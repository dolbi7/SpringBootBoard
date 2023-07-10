package com.board.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.board.entity.AddressEntity;
import com.board.service.MemberService;
import com.board.util.PageUtil;

import lombok.RequiredArgsConstructor;

@Controller
@CrossOrigin(originPatterns = "http://localhost:3000")
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService service;

	//주소검색
	@GetMapping("/member/addrSearch")
	public void getSearchAddr(@RequestParam("addrSearch") String addrSearch,
			@RequestParam("page") int pageNum,Model model) throws Exception {
		
		int postNum = 5;
		int listCount = 10;
		
		PageUtil page = new PageUtil();
		
		Page<AddressEntity> list = service.addrSearch(pageNum, postNum, addrSearch);
		int totalCount = (int)list.getTotalElements();

		model.addAttribute("list", list);
		model.addAttribute("pageListView", page.getPageAddress(pageNum, postNum, listCount, totalCount, addrSearch));
		
	}
	
}
