package com.shop.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {

	private int p_id;
	private int seq;
	private String p_name;
	private String p_manufacturer;
	private int p_price;
	private int p_stock;
	private String p_description;
	private String p_org_image;
	private String p_stored_image;
	private LocalDateTime p_regdate;
	private String p_status;
	
}
