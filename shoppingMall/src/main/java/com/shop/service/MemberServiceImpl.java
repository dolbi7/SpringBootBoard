package com.shop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.dto.AddressDTO;
import com.shop.dto.MemberDTO;
import com.shop.dto.MemberLogDTO;
import com.shop.mapper.MemberMapper;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	MemberMapper mapper; 
	
	//아이디 확인
	@Override
	public int idCheck(String email) {
		return mapper.idCheck(email);
	}

	//로그인 정보 확인
	@Override
	public MemberDTO login(String email) {
		return mapper.login(email);
	}
	
	//증명키 등록
	@Override
	public void memberInfoUpdateAuthkey(MemberDTO memberDTO) {
		mapper.memberInfoUpdateAuthkey(memberDTO);
	}
	
	//증명키 확인
	@Override
	public int memberInfoAuthKeyValidate(String authKey) {
		return mapper.memberInfoAuthKeyValidate(authKey);
	}
	
	//증명키로 사용자 정보 보기
	@Override
	public MemberDTO memberInfoViewByAuthKey(String authKey) {
		return mapper.memberInfoViewByAuthKey(authKey);		
	}
	
	//회원 접속 정보 관리
	@Override
	public void memberInfoConnection(MemberLogDTO memberLogDTO) {
		mapper.memberInfoConnection(memberLogDTO);
	}

	//회원 등록
	@Override
	public void memberInfoRegistry(MemberDTO member) {
		mapper.memberInfoRegistry(member);
	}

	//회원 정보 보기
	@Override
	public MemberDTO memberInfoView(String email) {
		return mapper.memberInfoView(email);
	}
	
	//회원정보 수정
	@Override
	public void memberInfoModify(MemberDTO member) {
		mapper.memberInfoModify(member);		
	}

	//회원 패스워드 수정
	@Override
	public void memberInfoPasswordModify(MemberDTO member) {
		mapper.memberInfoPasswordModify(member);		
	}

	//우편번호 전체 갯수 가져 오기
	@Override
	public int addrTotalCount(String addrSearch) {
		return mapper.addrTotalCount(addrSearch);
	}

	//우편번호 검색
	@Override
	public List<AddressDTO> addrSearch(int startPoint, int endPoint, String addrSearch) {
		Map<String,Object> map = new HashMap<>();
		map.put("startPoint", startPoint);
		map.put("endPoint", endPoint);
		map.put("addrSearch", addrSearch);
		return mapper.addrSearch(map);
	}

}
