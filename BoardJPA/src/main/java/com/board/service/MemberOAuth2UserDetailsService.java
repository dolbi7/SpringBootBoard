package com.board.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.board.dto.MemberOAuth2DTO;
import com.board.entity.MemberEntity;
import com.board.entity.repository.MemberRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberOAuth2UserDetailsService extends DefaultOAuth2UserService{
	
	private final MemberRepository memberRepository;
	private final PasswordEncoder pwdEncoder;
	private final HttpSession session;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		
		//구글에서 인증 후에 보내주는 데이터를 가져옴. 데이터는 key,value 구조 되어있음
		String provider = userRequest.getClientRegistration().getRegistrationId();
		String providerId = oAuth2User.getAttribute("sub");
		String email = oAuth2User.getAttribute("email");
		
		System.out.println("********************* provider = " + provider);
		System.out.println("********************* providerId = " + providerId);
		System.out.println("********************* email = " + email);
		
		oAuth2User.getAttributes().forEach((k,v) -> {System.out.println(k+":" + v);});
		
		MemberEntity memberEntity = saveSocialMember(email);
		
		//회원 role 값을 가져와서 User 객체에 저장
		List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
		SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(memberEntity.getRole());
		grantedAuthorities.add(grantedAuthority);
		
		MemberOAuth2DTO memberOAth2DTO =new MemberOAuth2DTO();
		memberOAth2DTO.setAttributes(oAuth2User.getAttributes());
		memberOAth2DTO.setAuthorities(grantedAuthorities);
		memberOAth2DTO.setName(memberEntity.getUsername());
		
		session.setAttribute("email", email);
		session.setAttribute("username", memberEntity.getUsername());
		session.setAttribute("role", memberEntity.getRole());
		session.setMaxInactiveInterval(3600*24*7);
		
		return memberOAth2DTO;
		
	}
	
	//구글 인증 통과 후에 tbl_member에 존재하는 회원이면
	//tbl_member에서 회원 정보를 읽어서 리턴하고 
	//존재하지 않는 구글 회원이면 임시 회원 정보를 등록
	private MemberEntity saveSocialMember(String email) {
		
		Optional<MemberEntity> result = memberRepository.findById(email);
		if(result.isPresent()) // 아이디가 email인 회원이 존재하면?
			return result.get();
		
		// 아이디가 email인 회원이 존재하지 않으면?
		MemberEntity memberEntity = MemberEntity.builder()
												.email(email)
												.username(email)
												.password(pwdEncoder.encode("12345"))
												.role("USER")
												.regdate(LocalDateTime.now())
												.FromSocial("Y")
												.build();
		
		memberRepository.save(memberEntity);
		
		
		return memberEntity;
	}
	
	

}
