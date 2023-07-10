package com.shop.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {

	private String email;
	private String username;
	private String password;
	private String telno;
	private String zipcode;
	private String address;
	private LocalDateTime regdate;
	private String role;
	
	private String login_status;
	private String logout_status;
	private LocalDateTime log_regdate;
	
	private int order_count;
	private int order_pay_count;
	private long order_amount;
	
	private String autoLoginCheck;
	private String authKey;

}	