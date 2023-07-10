package com.shop.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.dto.AddressDTO;
import com.shop.dto.MemberDTO;
import com.shop.dto.MemberLogDTO;
import com.shop.service.MemberService;
import com.shop.service.ShopService;
import com.shop.util.Page;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class MemberController {
	
	@Autowired
	MemberService service;
	
	@Autowired
	ShopService shopService;
	
	@Autowired //비밀번호 암호화 이존성 주입
	private PasswordEncoder pwdEncoder;
	
	//회원 등록 화면 보기
	@GetMapping("/member/signup")
	public void getMemberRegistry() throws Exception { }
	
	//회원 등록 처리
	@ResponseBody
	@PostMapping("/member/signup")
	public String postMemberRegistry(MemberDTO member) {

		String inputPassword = member.getPassword();
		String pwd = pwdEncoder.encode(inputPassword); 
		member.setPassword(pwd);			

		service.memberInfoRegistry(member);
		return "good";
	}
	
	//회원 등록 시 아이디 중복 확인
	@ResponseBody
	@PostMapping("/member/idCheck")
	public int idCheck(@RequestBody MemberDTO memberDTO) throws Exception{
		return service.idCheck(memberDTO.getEmail());
	}
	
	//로그인 화면 보기
	@GetMapping("/member/login")
	public void getLogIn(Model model,@RequestParam(name="message", required=false) String message) { 
		model.addAttribute("message", message);
	}

	//로그인 처리
	@ResponseBody
	@PostMapping("/member/login")
	public String postLogIn(MemberDTO loginData,Model model,HttpSession session) { 
		
		String authKey = "NO_AUTHKEY";
		
		//증명키로 로그인
		//1. 증명키 존재 여부 확인
		if(loginData.getAuthKey() != null) {
		
			//2. 존재하는 증명키를 가진 사용자가 있는지 확인 --> 증명키 유효성 확인...
			if(service.memberInfoAuthKeyValidate(loginData.getAuthKey()) == 1) {
				//3. 증명키로 사용자 정보 가져 오기
				MemberDTO member = service.memberInfoViewByAuthKey(loginData.getAuthKey());
				//4. 세션 생성
				createSession(session, member.getEmail(), member.getUsername(), member.getRole());	
				String json = "{\"message\":\"autoLogin\",\"authKey\":\"" + member.getAuthKey() + "\",\"role\":\"" + member.getRole() + "\"}";
				
				return json;
				
			} else 
				return "{\"message\":\"AUTHKEY_NOT_FOUND\"}";
		
		}	
		
		//아이디 존재 여부 확인
		if(service.idCheck(loginData.getEmail()) == 0) 
			return "{\"message\":\"ID_NOT_FOUND\"}";
		
		MemberDTO member = service.login(loginData.getEmail());
		
		//패스워드 확인
		if(!pwdEncoder.matches(loginData.getPassword(),member.getPassword())) {
			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
		}else {
		
			//로그인 날짜 등록			
			registerConnectionInfo(member.getEmail(),"login");
			
			//증명키 등록						
			if(loginData.getAutoLoginCheck().equals("O")) { 
				authKey = UUID.randomUUID().toString().replaceAll("-", "");
				member.setAuthKey(authKey);
				service.memberInfoUpdateAuthkey(member);
			}	
			
			//세션 생성
			createSession(session, member.getEmail(), member.getUsername(), member.getRole());
			
			String json = "{\"message\":\"formLogin\",\"authKey\":\"" + authKey + "\",\"role\":\"" + member.getRole() + "\"}";
			
			System.out.println("json = " + json);
			
			return json;					

		}
		
	}
	
	//세션 생성
	public void createSession(HttpSession session, String email, String username, String role) {
		
		session.setMaxInactiveInterval(3600*7);
		session.setAttribute("email", email);
		session.setAttribute("username", username);
		session.setAttribute("role", role);
		
	}
	
	//로그인/로그아웃 날짜 등록
	public void registerConnectionInfo(String email,String status) {
		
		MemberLogDTO memberLogDTO = new MemberLogDTO();
		memberLogDTO.setEmail(email);
		memberLogDTO.setLog_status(status);
		service.memberInfoConnection(memberLogDTO);
		
	}
	
	//로그 아웃
	@GetMapping("/member/logout")
	public String logout(HttpSession session) throws Exception{
	
		String email = (String)session.getAttribute("email");
		
		registerConnectionInfo(email,"logout");
		session.invalidate();
		
		return "redirect:/";
	}
	
	//회원정보 보기
	@GetMapping("/member/memberInfoView")
	public void getMemberInfo(Model model,HttpSession session, @RequestParam(name="email", defaultValue="", required=false) String email) throws Exception {
		
		if(email == null || email.equals("")) email = (String)session.getAttribute("email");
		model.addAttribute("pCartCount", shopService.pCartCount(email));
		model.addAttribute("member", service.memberInfoView(email));
	}
	
	//회원정보 수정
	@GetMapping("/member/memberInfoModify")
	public void getMemberInfoModify(Model model,HttpSession session) throws Exception {
		
		String email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");		
		model.addAttribute("member", service.memberInfoView(email));
		model.addAttribute("pCartCount", shopService.pCartCount(email));
	}
	
	//회원정보 수정
	@ResponseBody
	@PostMapping("/member/memberInfoModify")
	public String postMemberInfoModify(MemberDTO memberDTO) throws Exception {
		service.memberInfoModify(memberDTO);		
		return "good";
	}
	
	//회원 패스워드 수정
	@GetMapping("/member/memberInfoPasswordModify")
	public void gerMemberInfoPasswordModify(Model model,HttpSession session) throws Exception {
		String email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");
		model.addAttribute("pCartCount", shopService.pCartCount(email));
	}
	
	//회원 패스워드 수정
	@ResponseBody
	@PostMapping("/member/memberInfoPasswordModify")
	public String postMemberInfoPasswordModify(@RequestParam("old_password") String old_password,@RequestParam("new_password") String new_password, 
			HttpSession session) throws Exception {
		
		String email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");

		MemberDTO memberDTO = service.memberInfoView(email);
		if(pwdEncoder.matches(old_password, memberDTO.getPassword())) {
			memberDTO.setPassword(pwdEncoder.encode(new_password));
			service.memberInfoPasswordModify(memberDTO);
			return "good";
		} else {	
			return "bad";
		}		
				
	}
	
	//우편번호 검색
	@GetMapping("/member/addrSearch")
	public void getSearchAddr(@RequestParam("addrSearch") String addrSearch,
			@RequestParam("page") int pageNum,Model model) throws Exception {
		
		int postNum = 5;
		int startPoint = (pageNum -1)*postNum + 1; //테이블에서 읽어 올 행의 위치
		int endPoint = pageNum*postNum;
		int listCount = 5;
		
		Page page = new Page();
		
		int totalCount = service.addrTotalCount(addrSearch);
		
		List<AddressDTO> list = new ArrayList<>();
		list = service.addrSearch(startPoint, endPoint, addrSearch);

		model.addAttribute("list", list);
		model.addAttribute("pageListView", page.getPageAddress(pageNum, postNum, listCount, totalCount, addrSearch));
		
	}
	
}
