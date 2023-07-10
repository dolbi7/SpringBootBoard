package com.shop.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CartDTO {
	
	private int cart_id;
	private String cart_email;
	private int cart_quantity;
	private int cart_amount;
	private long cart_totalAmount;
	private String cart_status;
	private LocalDateTime cart_regdate;	
	
	private String invoice;
	private String type;
	
	private int p_id;
	private String p_stored_image;
	private String p_name;
	private String p_manufacturer;
	private int p_price;
	private String p_stock;	

}
