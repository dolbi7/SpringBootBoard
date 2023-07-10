package com.board.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
public class LikeVO {

	private int seqno;
	private String userid;
	private String mylikecheck;
	private String mydislikecheck;
	private String likedate;
	private String dislikedate;
	
	
	
}
