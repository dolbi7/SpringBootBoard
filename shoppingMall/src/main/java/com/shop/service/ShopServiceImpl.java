package com.shop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.dto.CartDTO;
import com.shop.dto.LikeDTO;
import com.shop.dto.OrderInfoAddrDTO;
import com.shop.dto.OrderDTO;
import com.shop.dto.OrderInfoDTO;
import com.shop.dto.ProductDTO;
import com.shop.mapper.ShopMapper;

@Service
public class ShopServiceImpl implements ShopService{

	@Autowired
	ShopMapper mapper;
	
	//상품 목록 보기
	@Override
	public List<ProductDTO> pList(int startPoint, int endPoint, String keyword) {
		Map<String,Object> data = new HashMap<>();
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		data.put("keyword", keyword);
		return mapper.pList(data);
	}

	//상품 정보 상세 보기
	@Override
	public ProductDTO pView(int p_id) {
		return mapper.pView(p_id);
	}
	
	//상품 전체 목록 갯수 구하기
	@Override
	public int p_totalCount(String keyword) {
		return mapper.p_totalCount(keyword);
	}
	
	//관심 상품 등록 여부 확인
	@Override
	public int likeCheck(LikeDTO likeDTO) {
		return mapper.likeCheck(likeDTO);
	}
	
	//관심 상품 등록
	@Override
	public void likeRegistry(LikeDTO likeDTO) {
		mapper.likeRegistry(likeDTO);
	}
	
	//관심 상품 삭제
	@Override
	public void likeDelete(LikeDTO likeDTO) {
		mapper.likeDelete(likeDTO);
	}

	//관심상품 목록 보기
	@Override
	public List<ProductDTO> pLikeView(String like_email){
		return mapper.pLikeView(like_email);
	}
	
	//장바구니에 상품 담기
	@Override
	public void pCartInsert(CartDTO cartDTO) {

		mapper.pCartInsert(cartDTO);
	}
	
	//장바구니에 담긴 상품별 갯수 계산
	public int pCartQuantity(CartDTO cartDTO) {
		return mapper.pCartQuantity(cartDTO);
	}
	
	//장바구니 상품 갯수 업데이트
	public void pCartUpdate(CartDTO cartDTO) {
		mapper.pCartUpdate(cartDTO);
	}
	
	//카드에 담은 상품 갯수 계산
	@Override
	public int pCartCount(String cart_email) {
		return mapper.pCartCount(cart_email); 
	}
	
	//장바구니에 담긴 상품 목록 보기
	@Override
	public List<CartDTO> pCartView(String cart_email){
		return mapper.pCartView(cart_email);
	}
	
	//장바구니에 담긴 상품 삭제
	@Override
	public void pCartDelete(int cart_id) {
		mapper.pCartDelete(cart_id);
	}
	
	//장바구니 비우기
	@Override
	public void pCartDeleteAll(String cart_email) {
		mapper.pCartDeleteAll(cart_email);
	}
	
	//장바구니에서 선택한 상품을 tbl_order 테이블에 구매서 작성 중 상태로 저장
	@Override
	public void pCartInvoice(CartDTO cartDTO) {
		mapper.pCartInvoice(cartDTO);
	}
	
	//장바구니에서 선택한 상품을 tbl_order 테이블에 구매서 작성 중 상태로 저장
	@Override
	public void pOrderInvoice(int cart_id) {
		mapper.pOrderInvoice(cart_id);
	}
	
	//주문서 작성 내용 중 주문 상품 목록 보기
	@Override
	public List<CartDTO> invoiceProduct(String invoice){
		return mapper.invoiceProduct(invoice);
	}
	
	//주문서 작성 내용 중 이전 배송지 정보 가져 오기
	@Override
	public List<OrderInfoAddrDTO> invoiceOldAddr(String order_email){
		return mapper.invoiceOldAddr(order_email);
	}
	
	//결제
	@Override
	public void pOrderFulfilment(OrderInfoDTO orderInfoDTO) {
		mapper.pOrderFulfilment(orderInfoDTO);
	}
	
	//주문 이력 정보 관리를 위한 주문 정보 등록
	@Override
	public void pOrderInfoRegistry(OrderInfoDTO orderInfoDTO) {
		mapper.pOrderInfoRegistry(orderInfoDTO);
	}
	
	//주문 이력 정보 관리를 위한 배송지 정보 등록
	@Override
	public void pOrderInfoRVRegistry(OrderInfoAddrDTO orderAddrDTO) {
		mapper.pOrderInfoRVRegistry(orderAddrDTO);
	}	
	
	//주문 이력 정보 관리를 위한 배송지 정보 삭제
	@Override
	public void pOrderInfoRVDelete(OrderInfoAddrDTO orderAddrDTO) {
		mapper.pOrderInfoRVDelete(orderAddrDTO);
	}

	//주문 이력 정보 관리를 위한 주문 상품 목록 등록 
	@Override
	public void pOrderInfoProduct(String order_id) {
		mapper.pOrderInfoProduct(order_id);
	}
			
	//결제 완료 후 장바구니에서 구입한 항목 삭제
	@Override
	public void pOrderDeleteCart(String invoice) {
		mapper.pOrderDeleteCart(invoice);
	}
	
	//결제 완료 후 tbl_order 테이블 내에 주문 상태가 '01'(주문서 작성중)인 주문 항목 삭제
	@Override
	public void pOrderDeleteInvoice(String order_email) {
		mapper.pOrderDeleteInvoice(order_email);
	}
	
	//결제 완료 후 구입 항목 정보 읽어 오기(재고 조정용)
	@Override
	public List<OrderDTO> pStockInfo(String invoice){		
		return mapper.pStockInfo(invoice);
	}
	
	//결제 완료 후 상품 재고 조정
	@Override
	public void pStockUpdate(ProductDTO productDTO) {
		mapper.pStockUpdate(productDTO);
	}
	
	//주문 내역 보기
	@Override
	public List<OrderInfoDTO> pOrderInfoList(String order_email, int startPoint, int endPoint){
		
		Map<String,Object> data = new HashMap<>();
		data.put("order_email", order_email);
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		
		return mapper.pOrderInfoList(data);
	}
	
	//회원별 전체 주문 갯수 계산
	@Override
	public int pOrderCount(String order_email) {
		return mapper.pOrderCount(order_email);
	}
	
	//주문 내역에서 고객 이름, 주문건수, 주문금액 보기
	@Override
	public OrderInfoDTO pOrderTotalInfo(String order_email) {
		return mapper.pOrderTotalInfo(order_email);
	}
	
	//주문 내역에서 주문서 볼때 받는 주문 상품 목록 가져 오기
	@Override
	public List<OrderInfoDTO> pOrderInfoProductView(String order_id){
		return mapper.pOrderInfoProductView(order_id);
	}
	
	//주문 내역에서 주문서 볼때 받는 사람 정보 가져 오기
	@Override
	public OrderInfoAddrDTO pOrderInfoRVView(String order_id) {
		return mapper.pOrderInfoRVView(order_id);
	}

}
