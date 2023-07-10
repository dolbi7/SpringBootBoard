package com.shop.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
public class OrderInfoDTO {

	private int seq;
	private String order_id;
	private LocalDateTime order_regdate;
	private int p_id;
	private String p_name;
	private String order_name;
	private int order_quantity;
	private long order_amount;
	private String order_status;
	private String order_status_id;
	private String order_status_name;
	private String order_email;
	private String order_total_amount;
	private int order_count;
	
	private String rv_name;
	private String rv_zipcode;
	private String rv_address;
	private String rv_telno;
	private String rv_email;
	
	private String newAddrCheck;
	
}
