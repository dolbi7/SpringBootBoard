package com.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {
	
	@GetMapping("/board/chat")
	public void getChat() throws Exception {
		System.out.println("==================채팅 진행 중==================");
	}
	

}
