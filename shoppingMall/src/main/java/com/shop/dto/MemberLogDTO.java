package com.shop.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
public class MemberLogDTO {
	
	private int log_id;
	private String email;
	private String log_status;
	private LocalDateTime log_regdate;

}
