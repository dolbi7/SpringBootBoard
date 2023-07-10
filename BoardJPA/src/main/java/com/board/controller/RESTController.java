package com.board.controller;

import java.util.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.board.dto.BoardDTO;
import com.board.dto.MemberDTO;
import com.board.entity.repository.BoardRepository;
import com.board.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@CrossOrigin(originPatterns = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class RESTController {
	
	private final BoardRepository boardRepository;
	private final MemberService service;
	private final BCryptPasswordEncoder pwdEncoder;
	
	@GetMapping("/restapi/list")
	public List<BoardDTO> getList() throws Exception {
		List<BoardDTO> boardDTOs = new ArrayList<>();
		boardRepository.findAll().stream().forEach(list -> boardDTOs.add(new BoardDTO(list)));
		return boardDTOs;
		}
	
	//로그인 처리
	@PostMapping("/restapi/loginCheck")
	public String postLogIn(MemberDTO loginData,HttpSession session) {
	
		//아이디 존재 여부 확인
		if(service.idCheck(loginData.getEmail()) == 0)
			return "{\"message\":\"ID_NOT_FOUND\"}";
		
		//아이디가 존재하면 읽어온 email로 로그인 정보 가져 오기
		MemberDTO member = service.memberInfo(loginData.getEmail());
		
		//패스워드 확인
		if(!pwdEncoder.matches(loginData.getPassword(),member.getPassword())) 
			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
	
		return "{\"message\":\"GOOD\"}";

		
		}
	
	@GetMapping("/restapi/visit")
	public String getRandomVisit() {

		//숫자 + 영문대소문자 10자리 임시패스워드 생성
		StringBuffer tempPW = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
		    int rIndex = rnd.nextInt(3);
		    switch (rIndex) {
		    case 0:
		        // a-z : 아스키코드 97~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 97));
		        break;
		    case 1:
		        // A-Z : 아스키코드 65~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 65));
		        break;
		    case 2:
		        // 0-9
		    	tempPW.append((rnd.nextInt(10)));
		        break;
		    }
		}
		
		
		return "{\"rvalue\":\"" + tempPW.toString()+ "\"}";
	}
	
}
