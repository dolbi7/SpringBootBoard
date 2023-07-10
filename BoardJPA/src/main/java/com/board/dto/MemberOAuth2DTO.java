package com.board.dto;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Setter;

@Setter
public class MemberOAuth2DTO implements OAuth2User {
	
	Map<String, Object> attributes; //구글에서 인증 후에 넘겨주는 값들
	Collection<? extends GrantedAuthority> authorities; //role
	String name; //아이디 

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	

}
