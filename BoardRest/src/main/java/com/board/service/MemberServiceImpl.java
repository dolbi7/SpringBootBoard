package com.board.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.board.dto.MemberDTO;
import com.board.entity.AddressEntity;
import com.board.entity.MemberEntity;
import com.board.entity.repository.AddressRepository;
import com.board.entity.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
	private final MemberRepository memberRepository;
	private final AddressRepository addresssRepository;

	//아이디 중복체크, 카운터가0이면 아이디 사용 가능, 1이면 기존 사용중인아이디
	@Override
	public int idCheck(String email) {
		//select * from ----
		return memberRepository.findById(email).isEmpty()?0:1;
	}
	
	//로그인 정보 가져 오기
	@Override
	public MemberDTO memberInfo(String email) {
		return memberRepository.findById(email).map(member-> new MemberDTO(member)).get();
	}
	
	//사용자등록
	@Override
	public void signup(MemberDTO member) {
		member.setRegdate(LocalDateTime.now());
		member.setRole("USER");
		memberRepository.save(member.dtoToEntity(member));
	}
	
	//사용자 자동 로그인을 위한 authkey 등록
	@Override
	public void authkeyUpdate(MemberDTO member) {
		MemberEntity memberEntity = memberRepository.findById(member.getEmail()).get();
		memberEntity.setAuthkey(member.getAuthkey());
		memberRepository.save(memberEntity);
	}
	
	//사용자 자동 로그인을 위한 authkey로 사용자 정보 가져 오기 
	@Override
	public MemberEntity memberinfoByAuthkey(String authkey) {
		return memberRepository.findByAuthkey(authkey);
	}

	/*
	 * //사용자 정보 보기
	 * 
	 * @Override public MemberDTO memberinfo(String userid) { // TODO Auto-generated
	 * method stub return null; }
	 */
	
	//주소 검색
	@Override
	public Page<AddressEntity> addrSearch(int pageNum, int postNum, String addrSearch) {
		
		PageRequest pageRequest = PageRequest.of(pageNum-1,postNum, Sort.by(Direction.ASC,"zipcode"));
		
		return addresssRepository.findByRoadContainingOrBuildingContaining(addrSearch, addrSearch, pageRequest);
	}

}
