package com.shop.service;

import java.util.List;

import com.shop.dto.AvenueDTO;
import com.shop.dto.MemberDTO;
import com.shop.dto.OrderDTO;
import com.shop.dto.OrderInfoAddrDTO;
import com.shop.dto.OrderInfoDTO;
import com.shop.dto.ProductDTO;

public interface MasterService {

	//상품 등록
	public void pRegistry(ProductDTO productDTO);
	
	//상품 목록 보기 
	public List<ProductDTO> pList(int startPoint, int endPoint, String keyword);
	
	//상품 상세 정보 보기
	public ProductDTO pView(int p_id);
	
	//상품 상세 정보 수정
	public void pModify(ProductDTO productDTO);
	
	//상품 삭제
	public void pDelete(int p_id);
	
	//상품 전체 목록 갯수 구하기
	public int p_totalCount(String keyword);
	
	//주문 내역 보기
	public List<OrderInfoDTO> pOrderInfoList(int startPoint, int endPoint, String keyword);
	
	//주문서 내역에서 주문서 볼때 상품 목록 보기
	public List<OrderInfoDTO> invoiceView(String invoice);
	
	//주문서 내역에서 주문서 볼때 받는 사람 정보 가져 오기
	public OrderInfoAddrDTO pOrderInfoRVView(String order_id);
	
	//주문서 상태 목록 보기
	public List<OrderInfoDTO> pOrderStatusList();
	
	//전체 주문 갯수 계산
	public int pOrderTotalCount(String keyword);
	
	//주문 상태 변경(tbl_order)
	public void pOrderUpdateOrder(OrderDTO orderDTO);
	
	//주문 상태 변경(tbl_order_info)
	public void pOrderUpdateOrderInfo(OrderDTO orderDTO);
	
	//회원관리
	public List<MemberDTO> pCustomer(int startPoint, int endPoint, String keyword);
	
	//회원 전체 수 계산
	public int pCustomerTotalCount(String keyword);
	
	//매출 발생 연도 출력
	public List<String> pAvenueYear();
	
	//매출 발생 일 목록 출력
	public List<String> pAvenueDayList(String yyyymm);
	
	//월/일 단위 매출 계산
	public AvenueDTO pAvenueDay(String yyyymmdd);
	
}
