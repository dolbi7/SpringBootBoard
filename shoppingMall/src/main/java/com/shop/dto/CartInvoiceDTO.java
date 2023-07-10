package com.shop.dto;

//비동기 방식으로 장바구니에 담겨져 있는 상품을 tbl_order 테이블로 옮기기 위한 DTO
import java.util.List;

import lombok.*;

@Getter
@Setter
public class CartInvoiceDTO {

	private List<Integer> cartIds;
	private String invoice;
	private String type;
	
}
