package com.shop.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
public class LikeDTO {
	
	private int like_id;
	private String p_id;
	private String like_email;
	private LocalDateTime like_regdate;
	
	private String type;

}
