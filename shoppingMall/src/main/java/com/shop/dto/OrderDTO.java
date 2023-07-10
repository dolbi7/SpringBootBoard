package com.shop.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
	
	private String order_id;
	private String order_email;
	private int cart_id;

	private int order_quantity;
	private int order_price;
	private String order_status;
	private long order_amount;
	private LocalDateTime order_regdate;
	
	private int p_id;
	private String p_stored_image;
	private int p_stock;
	private String p_name;
	private String p_manufacturer;

}
