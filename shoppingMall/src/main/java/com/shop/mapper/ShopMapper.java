package com.shop.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.shop.dto.CartDTO;
import com.shop.dto.LikeDTO;
import com.shop.dto.OrderInfoAddrDTO;
import com.shop.dto.OrderDTO;
import com.shop.dto.OrderInfoDTO;
import com.shop.dto.ProductDTO;

@Mapper
public interface ShopMapper {

	//상품 목록 보기 
	public List<ProductDTO> pList(Map<String,Object> data);
	
	//상품 상세 정보 보기
	public ProductDTO pView(int p_id);
	
	//상품 목록 전체 갯수
	public int p_totalCount(String keyword);
	
	//관심 상품 등록 여부 확인
	public int likeCheck(LikeDTO likeDTO);
	
	//관심 상품 등록
	public void likeRegistry(LikeDTO likeDTO);
	
	//관심 상품 삭제
	public void likeDelete(LikeDTO likeDTO);
	
	//관심상품 목록 보기
	public List<ProductDTO> pLikeView(String like_email);
	
	//장바구니에 상품 담기
	public void pCartInsert(CartDTO cartDTO);
	
	//장바구니에 담긴 상품별 갯수 계산
	public int pCartQuantity(CartDTO cartDTO);
	
	//장바구니 상품 갯수 업데이트
	public void pCartUpdate(CartDTO cartDTO);

	//장바구니에 담긴 상품 갯수 계산
	public int pCartCount(String cart_email);
	
	//장바구니에 담긴 상품 목록 보기
	public List<CartDTO> pCartView(String cart_email);
	
	//장바구니에 담긴 상품 삭제
	public void pCartDelete(int cart_id);
	
	//장바구니 비우기
	public void pCartDeleteAll(String cart_email);
	
	//장바구니에서 선택한 상품을 tbl_cart 테이블에 구매서 작성 중 상태로 전환
	public void pCartInvoice(CartDTO cartDTO);
	
	//장바구니에서 선택한 상품을 tbl_order 테이블에 구매서 작성 중 상태로 저장
	public void pOrderInvoice(int cart_id);
	
	//주문서 작성 내용 중 주문 상품 목록 보기
	public List<CartDTO> invoiceProduct(String invoice);

	//주문서 작성 내용 중 이전 배송지 정보 가져 오기
	public List<OrderInfoAddrDTO> invoiceOldAddr(String order_email);
		
	//결제
	public void pOrderFulfilment(OrderInfoDTO orderInfoDTO);
	
	//주문 이력 정보 관리를 위한 주문 정보 등록
	public void pOrderInfoRegistry(OrderInfoDTO orderInfoDTO);
	
	//주문 이력 정보 관리를 위한 배송지 정보 등록
	public void pOrderInfoRVRegistry(OrderInfoAddrDTO orderAddrDTO);
	
	//주문 이력 정보 관리를 위한 배송지 정보 삭제
	public void pOrderInfoRVDelete(OrderInfoAddrDTO orderAddrDTO);

	//주문 이력 정보 관리를 위한 주문 상품 목록 등록 
	public void pOrderInfoProduct(String order_id);
	
	//결제 완료 후 장바구니에서 구입한 항목 삭제
	public void pOrderDeleteCart(String invoice);
	
	//결제 완료 후 tbl_order 테이블 내에 주문 상태가 '01'(주문서 작성중)인 주문 항목 삭제 
	public void pOrderDeleteInvoice(String order_email);
	
	//결제 완료 후 구입 항목 정보 읽어 오기(재고 조정용)
	public List<OrderDTO> pStockInfo(String invoice);
	
	//결제 완료 후 상품 재고 조정
	public void pStockUpdate(ProductDTO productDTO);

	//주문 내역 보기
	public List<OrderInfoDTO> pOrderInfoList(Map<String,Object> data);
	
	//주문 내역에서 고객 이름, 주문건수, 주문금액 보기
	public OrderInfoDTO pOrderTotalInfo(String order_email);
	
	//회원별 전체 주문 갯수 계산
	public int pOrderCount(String order_email);
	
	//주문 내역에서 주문서 볼때 받는 사람 정보 가져 오기
	public OrderInfoAddrDTO pOrderInfoRVView(String order_id);	
	
	//주문 내역에서 주문서 볼때 받는 주문 상품 목록 가져 오기
	public List<OrderInfoDTO> pOrderInfoProductView(String order_id); 

	
}
