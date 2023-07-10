package com.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.board.dto.MemberDTO;
import com.board.entity.MemberEntity;
import com.board.entity.AddressEntity;
import com.board.service.MemberService;
import com.board.util.PageUtil;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MemberController {
	
	
	private final MemberService service;
	private final BCryptPasswordEncoder pwdEncoder;

	
	//로그인
	@GetMapping("/member/login")
	public void getLogin(Model model) {}
	
	//로그인
	@PostMapping("/member/login")
	public void postLogin(Model model) {}
	
	
	@ResponseBody
	@PostMapping("/member/loginCheck")
	public String postLogIn(MemberDTO loginData,HttpSession session) {
	
		//아이디 존재 여부 확인
		if(service.idCheck(loginData.getEmail()) == 0)
			return "{\"message\":\"ID_NOT_FOUND\"}";
		
		//아이디가 존재하면 읽어온 email로 로그인 정보 가져 오기
		MemberDTO member = service.memberInfo(loginData.getEmail());
		
		//패스워드 확인
		if(!pwdEncoder.matches(loginData.getPassword(),member.getPassword())) 
			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
	
		return "{\"message\":\"good\"}";

		
		}
	
	
	//로그아웃
	@GetMapping("/member/logout")
	public String getLogout(HttpSession session) throws Exception {
		
		session.invalidate();
		return "redirect:/";
	}
	
	//회원 가입
	@GetMapping("/member/signup")
	public void getSignup() throws Exception { }
	
	//회원 가입
	@ResponseBody
	@PostMapping("/member/signup")
	public String postSignup(MemberDTO member, @RequestParam("fileUpload") MultipartFile mpr) throws Exception {
		
		String path = "c:\\Repository\\profile\\"; 
		String org_filename = "";
		long filesize = 0L;
		
		if(!mpr.isEmpty()) {
			File targetFile = null; 
				
			org_filename = mpr.getOriginalFilename();	
			String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
			String stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
			filesize = mpr.getSize();
			targetFile = new File(path + stored_filename);
			mpr.transferTo(targetFile);	//raw data를 targetFile에서 가진 정보대로 변환
			member.setOrg_filename(org_filename);
			member.setStored_filename(stored_filename);
			member.setFilesize(filesize);
		}
		
		member.setPassword(pwdEncoder.encode(member.getPassword()));
		
		service.signup(member);		
		return "{\"username\":\"" + URLEncoder.encode(member.getUsername(),"UTF-8") + "\",\"status\":\"good\"}";
		//{"username": "김철수", "status": "good"}
		
	}
	
	//회원정보 보기
	@GetMapping("/member/memberInfo")
	public void getUserInfo(Model model, HttpSession session) { 
		
		String session_email = (String)session.getAttribute("email");
		model.addAttribute("member", service.memberInfo(session_email));
		
	}
	
	//아이디 중복 체크
	@ResponseBody
	@PostMapping("/member/idCheck")
	public int getIdCheck(@RequestBody String email) {
		
		return service.idCheck(email);
		
	}
	
	//주소검색
	@GetMapping("/member/addrSearch")
	public void getSearchAddr(@RequestParam("addrSearch") String addrSearch,
			@RequestParam("page") int pageNum,Model model) throws Exception {
		
		int postNum = 5;
		int listCount = 10;
		
		PageUtil page = new PageUtil();
		
		Page<AddressEntity> list = service.addrSearch(pageNum,postNum,addrSearch);
		int totalCount = (int)list.getTotalElements();

		model.addAttribute("list", list);
		model.addAttribute("pageListView", page.getPageAddress(pageNum, postNum, listCount, totalCount, addrSearch));
		
	}

}
