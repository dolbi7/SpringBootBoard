package com.shop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.dto.AvenueDTO;
import com.shop.dto.MemberDTO;
import com.shop.dto.OrderDTO;
import com.shop.dto.OrderInfoAddrDTO;
import com.shop.dto.OrderInfoDTO;
import com.shop.dto.ProductDTO;
import com.shop.mapper.MasterMapper;

@Service
public class MasterServiceImpl implements MasterService{
	
	@Autowired
	MasterMapper mapper;

	//상품 등록
	@Override
	public void pRegistry(ProductDTO productDTO) {
		mapper.pRegistry(productDTO);	
	}

	//상품 목록 보기
	@Override
	public List<ProductDTO> pList(int startPoint, int endPoint, String keyword) {
		
		Map<String,Object> data = new HashMap<>();
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		data.put("keyword", keyword);
		return mapper.pList(data);
	}

	//상품 상세 정보 보기
	@Override
	public ProductDTO pView(int p_id) {
		return mapper.pView(p_id);
	}

	//상품 상세 정보 수정
	@Override
	public void pModify(ProductDTO productDTO) {
		mapper.pModify(productDTO);		
	}

	//상품 삭제
	@Override
	public void pDelete(int p_id) {
		
		
	}
	
	//상품 전체 목록 갯수 구하기
	@Override
	public int p_totalCount(String keyword) {		
		return mapper.p_totalCount(keyword);		
	}

	//주문 내역 보기
	@Override
	public List<OrderInfoDTO> pOrderInfoList(int startPoint, int endPoint, String keyword){
		
		Map<String,Object> data = new HashMap<>();
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		data.put("keyword", keyword);
		return mapper.pOrderInfoList(data);
	}
	
	//전체 주문 갯수 계산
	@Override
	public int pOrderTotalCount(String keyword) {
		return mapper.pOrderTotalCount(keyword);
	}
	
	//주문서 내역에서 주문서 볼때 상품 목록 보기
	@Override
	public List<OrderInfoDTO> invoiceView(String invoice){
		return mapper.invoiceView(invoice);
	}
	
	//주문서 내역에서 주문서 볼때 받는 사람 정보 가져 오기
	@Override
	public OrderInfoAddrDTO pOrderInfoRVView(String order_id) {
		return mapper.pOrderInfoRVView(order_id);
	}
	
	//주문서 상태 목록 보기
	@Override
	public List<OrderInfoDTO> pOrderStatusList(){
		return mapper.pOrderStatusList();
	}
	
	//주문 상태 변경(tbl_order)
	@Override
	public void pOrderUpdateOrder(OrderDTO orderDTO) {
		mapper.pOrderUpdateOrder(orderDTO);
	}
	
	//주문 상태 변경(tbl_order_info)
	@Override
	public void pOrderUpdateOrderInfo(OrderDTO orderDTO) {
		mapper.pOrderUpdateOrderInfo(orderDTO);
	}	

	//회원관리
	@Override
	public List<MemberDTO> pCustomer(int startPoint, int endPoint, String keyword){
		
		Map<String,Object> data = new HashMap<>();
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		data.put("keyword", keyword);
		return mapper.pCustomer(data);
	}
	
	//회원 전체 수 계산
	@Override
	public int pCustomerTotalCount(String keyword) {
		return mapper.pCustomerTotalCount(keyword);
	}
	
	//매출 발생 연도 출력
	@Override
	public List<String> pAvenueYear(){
		return mapper.pAvenueYear();
	}
	
	//매출 발생 일 목록 출력
	@Override
	public List<String> pAvenueDayList(String yyyymm){
		return mapper.pAvenueDayList(yyyymm);
	}
	
	//월/일 단위 매출 계산
	@Override
	public AvenueDTO pAvenueDay(String yyyymmdd){
		return mapper.pAvenueDay(yyyymmdd);
	}
}
