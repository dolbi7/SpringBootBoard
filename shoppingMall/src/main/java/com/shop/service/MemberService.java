package com.shop.service;

import java.util.List;
import java.util.Map;

import com.shop.dto.AddressDTO;
import com.shop.dto.MemberDTO;
import com.shop.dto.MemberLogDTO;

public interface MemberService {

	//아이디 확인
	public int idCheck(String userid); 

	//로그인 정보 확인
	public MemberDTO login(String userid); 
	
	//증명키 등록
	public void memberInfoUpdateAuthkey(MemberDTO memberDTO);
	
	//증명키 확인
	public int memberInfoAuthKeyValidate(String authKey);
	
	//증명키로 사용자 정보 보기
	public MemberDTO memberInfoViewByAuthKey(String authKey);
	
	//회원 접속 정보 관리
	public void memberInfoConnection(MemberLogDTO memberLogDTO);
	
	//회원 정보 등록
	public void memberInfoRegistry(MemberDTO member);
	
	//회원 정보 보기
	public MemberDTO memberInfoView(String email);
	
	//회원 정보 수정
	public void memberInfoModify(MemberDTO member);
	
	//패스워드 수정
	public void memberInfoPasswordModify(MemberDTO member);
	
	//주소 전체 갯수 계산
	public int addrTotalCount(String addrSearch);
	
	//주소 검색
	public List<AddressDTO> addrSearch(int startPoint, int endPoint, String addrSearch);
	
}
