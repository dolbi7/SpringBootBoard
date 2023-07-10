package com.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.dto.CartDTO;
import com.shop.dto.CartInvoiceDTO;
import com.shop.dto.LikeDTO;
import com.shop.dto.MemberDTO;
import com.shop.dto.OrderDTO;
import com.shop.dto.OrderInfoAddrDTO;
import com.shop.dto.OrderInfoDTO;
import com.shop.dto.ProductDTO;
import com.shop.service.MasterService;
import com.shop.service.MemberService;
import com.shop.service.ShopService;
import com.shop.util.Page;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class ShopController {

	@Autowired
	ShopService service;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	MasterService masterService;
	
	//메인페이지에서 상품 목록 보기
	@GetMapping("/shop/main")
	public void getPList(Model model, HttpSession session, @RequestParam(name="page",defaultValue="1",required=false) int pageNum, 
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword ) throws Exception{
		
		String email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");
		
		int pageCount = 5; //한 화면에 보여딜 페이지 수
		int postNum = 5; //페이지 하단에 보여질 페이지 리스트 내의 갯수
		int startPoint = (pageNum -1)*postNum + 1; 
		int endPoint = postNum*pageNum;
		
		Page page = new Page();
		int totalCount = service.p_totalCount(keyword);
		
		model.addAttribute("list", service.pList(startPoint, endPoint, keyword));
		model.addAttribute("pCartCount", service.pCartCount(email));
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageListView", page.getPageList("/shop/main",pageNum, postNum, pageCount, totalCount, keyword, ""));
	}
	
	//상품 상세 정보 보기
	@GetMapping("/shop/pView")
	public void getPView(Model model, HttpSession session, @RequestParam("p_id") int p_id, 
			@RequestParam(name="page",defaultValue="1",required=false) int pageNum,
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception {
		
		String email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");
		
		CartDTO cartDTO = new CartDTO();
		cartDTO.setCart_email(email);
		cartDTO.setP_id(p_id);
		
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("view", service.pView(p_id));
		model.addAttribute("pCartQuantity", service.pCartQuantity(cartDTO));
		model.addAttribute("pCartCount", service.pCartCount(email));
	}
	
	//관심 상품 목록 보기
	@GetMapping("/shop/pLike")
	public void getPLike(Model model, HttpSession session ) throws Exception {
		
		String email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");
		
		model.addAttribute("list", service.pLikeView(email));
		model.addAttribute("pCartCount", service.pCartCount(email));
		
	}
	
	//관심 상품 등록/삭제
	@ResponseBody
	@PostMapping("/shop/likeManage")
	public String postLikeRegistry(@RequestBody LikeDTO likeDTO, @RequestParam("type") String type) throws Exception{
		
		if(type.equals("undefined")) type = "load";

		switch(type) {
		
			case "load" : 
						if(service.likeCheck(likeDTO) == 0) return "bad";
							else return "good";
			case "check" : 
						if(service.likeCheck(likeDTO) == 0) {
							service.likeRegistry(likeDTO);
							return "good";					
						}else {
							service.likeDelete(likeDTO);
							return "bad";
						}						
		}

		return "end";
	}

	//카트에 저장된 상품 보기
	@GetMapping("/shop/pCart")
	public void getPCart(Model model, HttpSession session) throws Exception {
		
		String cart_email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");
		
		model.addAttribute("pCartCount", service.pCartCount(cart_email));
		model.addAttribute("list", service.pCartView(cart_email));
	}

	//카트로 상품 이동
	@ResponseBody
	@PostMapping("/shop/pCart")
	public int postPCart(@RequestBody CartDTO cartDTO) throws Exception {
		
		if(service.pCartQuantity(cartDTO) == 0 )
			service.pCartInsert(cartDTO);
		else 
			service.pCartUpdate(cartDTO);
		
		return service.pCartCount(cartDTO.getCart_email());
	}
	
	//카트 상품 삭제/주문서 작성 화면 이동 --> form문을 이용한 동기 처리용
	/*
	@PostMapping("/shop/pCartManage")
	public String postPCartManage(@RequestParam("cartItems")int[] cartIds, @RequestParam("type") String type,
		@RequestParam("invoice") String invoice) throws Exception{
		
		for(int cart_id : cartIds) {
			
			if(type.equals("delete")) { 
				service.pCartDelete(cart_id);
			}
			else {
					//카트에서 선택한 상품을 tbl_cart 테이블에 구매서 작성 중 상태로 전환
					CartDTO cartDTO = new CartDTO();
					cartDTO.setCart_id(cart_id);
					cartDTO.setInvoice(invoice);
					service.pCartInvoice(cartDTO);
					
					//카트에서 선택한 상품을 tbl_order 테이블에 구매서 작성 중 상태로 저장
					service.pOrderInvoice(cart_id);
			}	
		}	
	
		if(type.equals("delete")) return "redirect:/shop/pCart?page=" + pageNum + "&keyword=" + keyword;
			else return "redirect:/shop/pCartInvoice?page=" + pageNum + "&keyword=" + keyword + "&invoice=" + invoice;
	}
	*/
	
	//카트 내에서 삭제 버튼 클릭시 선택된 상품 삭제/주문서 작성 화면 이동 --> 비동기 처리용
	@ResponseBody
	@PostMapping("/shop/pCartManage")
	public String postPCartManage(@RequestBody CartInvoiceDTO cartInvoice, HttpSession session) throws Exception{
		
		String type = cartInvoice.getType();
		String invoice = cartInvoice.getInvoice();
		
		if(type.equals("deleteAll")) {
			
			String cart_email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");
			service.pCartDeleteAll(cart_email);
			return "good";
		}
		
		for(int i =0 ; i < cartInvoice.getCartIds().size(); i++) {
			
			int cart_id = cartInvoice.getCartIds().get(i);
			
			if(type.equals("delete")) { 
				service.pCartDelete(cart_id);
			}
			else {
					//카트에서 선택한 상품을 tbl_cart 테이블에 구매서 작성 중 상태로 전환
					CartDTO cartDTO = new CartDTO();
					cartDTO.setCart_id(cart_id);
					cartDTO.setInvoice(invoice);
					service.pCartInvoice(cartDTO);
					
					//카트에서 선택한 상품을 tbl_order 테이블에 구매서 작성 중 상태로 저장
					service.pOrderInvoice(cart_id);
			}	
		}	
		
		return "good";
	}
		
	//주문서 작성 및 보기
	@GetMapping("/shop/pCartInvoice")
	public void getPCartInvoice(Model model, HttpSession session, 
			@RequestParam("invoice") String invoice, 
			@RequestParam(name="type",defaultValue="00") String type) throws Exception {
		
		String order_email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");

		model.addAttribute("pCartCount", service.pCartCount(order_email));
		model.addAttribute("invoice", invoice);
		model.addAttribute("type", type);
		if(type.equals("00")) { //주문 전 주문서 작성시 이용할 정보 가져 오기 
			model.addAttribute("oldAddrList",service.invoiceOldAddr(order_email));
			model.addAttribute("list",service.invoiceProduct(invoice));
		} else if(type.equals("01")) { //주문 후 주문 내역 볼때 이용할 정보 가져 오기 
			model.addAttribute("member", service.pOrderInfoRVView(invoice));
			model.addAttribute("list",service.pOrderInfoProductView(invoice));
		}	
		
	}
		
	//결제하기
	@Transactional
	@ResponseBody
	@PostMapping("/shop/pOrder")
	public String postPOrder(@RequestBody OrderInfoDTO orderInfoDTO) throws Exception{
	
		//주문서 결제
		service.pOrderFulfilment(orderInfoDTO);
		
		//주문 이력 관리용 주문 정보 등록
		service.pOrderInfoRegistry(orderInfoDTO);
		
		//주문 이력 관리용 받는 사람 정보 등록
		if(orderInfoDTO.getNewAddrCheck().equals("O")) { //새로운 배송지일 경우만 tbl_order_info_rv_addr에 등록
		
			OrderInfoAddrDTO orderAddrDTO = new OrderInfoAddrDTO();
			orderAddrDTO.setOrder_email(orderInfoDTO.getOrder_email());
			orderAddrDTO.setRv_name(orderInfoDTO.getRv_name());
			orderAddrDTO.setRv_zipcode(orderInfoDTO.getRv_zipcode());
			orderAddrDTO.setRv_address(orderInfoDTO.getRv_address());
			orderAddrDTO.setRv_telno(orderInfoDTO.getRv_telno());
			orderAddrDTO.setRv_email(orderInfoDTO.getRv_email());
			service.pOrderInfoRVRegistry(orderAddrDTO);
			
		}
		
		//주문 이력 관리용 상품 목록 등록
		service.pOrderInfoProduct(orderInfoDTO.getOrder_id());
		
		//주문한 상품을 카트에서 삭제
		service.pOrderDeleteCart(orderInfoDTO.getOrder_id());
		
		//tbl_order 테이블내에 주문작성 중인 상품(status:01) 항목을 삭제 
		service.pOrderDeleteInvoice(orderInfoDTO.getOrder_email());	
				
		//주문요청한 제품 정보 가져 오기
		List<OrderDTO> list = service.pStockInfo(orderInfoDTO.getOrder_id());
		
		//재고 수량 조정(감소)
		for(OrderDTO order : list) {
			int p_id = order.getP_id();
			int p_stock = order.getP_stock();
			int order_quantity = order.getOrder_quantity();
			p_stock = p_stock - order_quantity;
			
			ProductDTO product = new ProductDTO();
			product.setP_id(p_id);
			product.setP_stock(p_stock);
			
			service.pStockUpdate(product);
						
		}
		
		return "good";
	}
	
	//주문취소 및 배송완료 처리
	@ResponseBody
	@PostMapping("/shop/pOrderChange")
	public String postPOrderChange(OrderDTO orderDTO) throws Exception {

		//type에 따라 주문테이블(tbl_order)과 주문정보테이블(tbl_order_info)의 order_status 값 변경
		masterService.pOrderUpdateOrder(orderDTO);//tbl_order의 orer_status 변경
		masterService.pOrderUpdateOrderInfo(orderDTO); //tbl_order_info의 order_status 변경
		
		//재고 수량 조정(증가)
		//1. 주문요청한 제품 정보 가져 오기
		List<OrderDTO> list = service.pStockInfo(orderDTO.getOrder_id());
		
		if(!orderDTO.getOrder_status().equals("07")) { //"반품요청"은 상태만 변경
		//2. 기존 재고에다가 "주문취소(06)" 또는 "반품완료(09)"한 상품의 갯수를 추가하여 업데이트
			for(OrderDTO order : list) {
				int p_id = order.getP_id();
				int p_stock = order.getP_stock();
				int order_quantity = order.getOrder_quantity();
				p_stock = p_stock + order_quantity;
				
				ProductDTO product = new ProductDTO();
				product.setP_id(p_id);
				product.setP_stock(p_stock);
				
				service.pStockUpdate(product);
							
			}
		}
		return "good";
	}
	
	//주문 이력 보기
	@GetMapping("/shop/pOrderHistory")
	public void getPOrderHistory(Model model, HttpSession session, @RequestParam("page") int pageNum,
			@RequestParam(name="order_email", defaultValue="", required=false) String order_email) throws Exception {
		
		if(order_email == null || order_email.equals("")) order_email = (String)session.getAttribute("email");
		
		int pageCount = 5; //한 화면에 보여딜 페이지 수
		int postNum = 5; //페이지 하단에 보여질 페이지 리스트 내의 갯수
		int startPoint = (pageNum -1)*postNum + 1; 
		int endPoint = postNum*pageNum;
		
		Page page = new Page();
		int totalCount = service.pOrderCount(order_email);
		
		model.addAttribute("pCartCount", service.pCartCount(order_email));
		model.addAttribute("list",service.pOrderInfoList(order_email, startPoint, endPoint));
		model.addAttribute("member", service.pOrderTotalInfo(order_email));
		model.addAttribute("page", pageNum);
		model.addAttribute("pageListView", page.getPageList("/shop/pOrderHistory",pageNum, postNum, pageCount, totalCount, "", "&order_email=" + order_email));
		
	}
	
	//배송지 관리
	@GetMapping("/shop/pRVAddrManage")
	public void getRVAddrManage(Model model,HttpSession session) throws Exception {
		
		String order_email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");		
		model.addAttribute("oldAddrList",service.invoiceOldAddr(order_email));
		model.addAttribute("pCartCount", service.pCartCount(order_email));
		
	}
	
	//배송지 관리
	@ResponseBody
	@PostMapping("/shop/pRVAddrManage")
	public String postRVAddrManage(OrderInfoAddrDTO orderInfoAddrDTO, @RequestParam("type") String type, 
			HttpSession session) throws Exception {
		
		String order_email = (String)session.getAttribute("email")==null?"":(String)session.getAttribute("email");	
		
		orderInfoAddrDTO.setOrder_email(order_email);
		
		if(type.equals("registry"))
			service.pOrderInfoRVRegistry(orderInfoAddrDTO);
		else if(type.equals("delete"))
			service.pOrderInfoRVDelete(orderInfoAddrDTO);
		
		return "good";
	}
}
