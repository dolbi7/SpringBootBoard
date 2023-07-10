package com.shop.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.shop.dto.AvenueDTO;
import com.shop.dto.CartDTO;
import com.shop.dto.OrderDTO;
import com.shop.dto.OrderInfoDTO;
import com.shop.dto.ProductDTO;
import com.shop.service.MasterService;
import com.shop.util.Page;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class MasterController {
	
	@Autowired
	MasterService service;
	
	//상품 목록 보기
	@GetMapping("/master/main")
	public void GetPList(Model model, @RequestParam(name="page") int pageNum, 
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword ) throws Exception{
		
		int pageCount = 5;
		int postNum = 5; //한 페이지에 보여질 게시물 목록 갯수
		int startPoint = (pageNum -1)*postNum + 1; 
		int endPoint = postNum*pageNum;
		
		Page page = new Page();
		int totalCount = service.p_totalCount(keyword);
		
		model.addAttribute("list", service.pList(startPoint, endPoint, keyword));
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageListView", page.getPageList("/master/main",pageNum, postNum, pageCount, totalCount, keyword, ""));
	}
	
	//상품 등록
	@GetMapping("/master/pRegistry")
	public void getPRegistry() throws Exception { }
	
	//상품 등록
	@PostMapping("/master/pRegistry")
	public String postPRegistry(ProductDTO productDTO, @RequestParam("imageFile") MultipartFile imageFile) {
		
		String path = "c:\\Repository\\shop\\product\\";
		File targetFile;
		
		if(!imageFile.isEmpty()) {
			String org_filename = imageFile.getOriginalFilename();	
			String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
			String stored_filename =  UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
			
			try {
				targetFile = new File(path + stored_filename);				
				imageFile.transferTo(targetFile);
				productDTO.setP_org_image(org_filename);
				productDTO.setP_stored_image(stored_filename);
				
			} catch(Exception e) { e.printStackTrace(); }
			
		}
		
		service.pRegistry(productDTO);
		return "redirect:/master/main?page=1";
		
	}
	
	//상품 상세 정보 보기
	@GetMapping("/master/pView")
	public void getPView(Model model, @RequestParam("p_id") int p_id, @RequestParam(name="page", defaultValue="1") int pageNum, 
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword ) throws Exception {
		
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("view", service.pView(p_id));

	}

	//상품 수정
	@GetMapping("/master/pModify")
	public void getPModify(Model model, @RequestParam("p_id") int p_id) throws Exception {
		model.addAttribute("view",service.pView(p_id));
	}
	
	//상품 수정
	@ResponseBody
	@PostMapping("/master/pModify")
	public String postPModify(ProductDTO productDTO, @RequestParam("imageFile") MultipartFile imageFile ) throws Exception {
		
		String path = "c:\\Repository\\shop\\product\\";
		File targetFile;
		
		if(!imageFile.isEmpty()) {
			String org_filename = imageFile.getOriginalFilename();	
			String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
			String stored_filename =  UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
			
			try {
				targetFile = new File(path + stored_filename);				
				imageFile.transferTo(targetFile);
				productDTO.setP_org_image(org_filename);
				productDTO.setP_stored_image(stored_filename);
				
				//기존 상품 이미지 삭제
				ProductDTO pDTO = service.pView(productDTO.getP_id());
				File file = new File(path + pDTO.getP_stored_image());		
				file.delete();	
				
			} catch(Exception e) { e.printStackTrace(); }
			
		}

		service.pModify(productDTO);
		
		return "good";
	}
	
	//주문 이력 보기
	@GetMapping("/master/pOrder")
	public void getPOrderHistory(Model model,@RequestParam(name="page") int pageNum, 
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword) throws Exception {
		
		int pageCount = 5; //페이지 목록에 보여질 페이지 갯수
		int postNum = 5; //한 페이지에 보여질 게시물 목록 갯수
		int startPoint = (pageNum -1)*postNum + 1; 
		int endPoint = postNum*pageNum;
		
		Page page = new Page();
		int totalCount = service.pOrderTotalCount(keyword);
		
		model.addAttribute("list",service.pOrderInfoList(startPoint, endPoint, keyword));
		model.addAttribute("list_order_status", service.pOrderStatusList());
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageListView", page.getPageList("/master/pOrder",pageNum, postNum, pageCount, totalCount, keyword, ""));
		
	}
	
	//주문서 보기
	@GetMapping("/master/pOrderInvoice")
	public void getPCartInvoice(Model model, HttpSession session, @RequestParam("invoice") String invoice 
			) throws Exception {

		model.addAttribute("invoice", invoice);
		model.addAttribute("list",service.invoiceView(invoice));
		model.addAttribute("member", service.pOrderInfoRVView(invoice));
		
	}
	
	//주문 상태 변경
	@ResponseBody
	@PostMapping("/master/pOrderUpdate")
	public String postPOrderUpdate(OrderDTO orderDTO) throws Exception {
		
		service.pOrderUpdateOrder(orderDTO);
		service.pOrderUpdateOrderInfo(orderDTO);
		
		return "good";
	}
	
	//회원 관리
	@GetMapping("/master/pCustomer")
	public void getPCustomer(Model model,@RequestParam(name="page") int pageNum, 
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword) throws Exception {
		
		int pageCount = 5; //페이지 목록에 보여질 페이지 갯수
		int postNum = 5; //한 페이지에 보여질 게시물 목록 갯수
		int startPoint = (pageNum -1)*postNum + 1; 
		int endPoint = postNum*pageNum;
		
		Page page = new Page();
		int totalCount = service.pCustomerTotalCount(keyword);
		
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageListView", page.getPageList("/master/pCustomer",pageNum, postNum, pageCount, totalCount, keyword, ""));
		model.addAttribute("member", service.pCustomer(startPoint, endPoint, keyword));
		
	}
	
	//매출 관리
	@GetMapping("/master/pAvenue")
	public void getPAvenue(Model model,@RequestParam(name="yyyy", required=false) String yyyy,
			@RequestParam(name="mm", required=false) String mm) throws Exception{ 
		
		//현재 날짜를 양식에 맞춰 생성
		if(yyyy == null || yyyy.equals("")) {
			
			Date now = new Date();
			SimpleDateFormat SDF = new SimpleDateFormat("YYYY");
			yyyy = SDF.format(now);
		}
		
		if(mm == null || mm.equals("")) {
			
			Date now = new Date();
			SimpleDateFormat SDF = new SimpleDateFormat("MM");
			mm = SDF.format(now);
		}
		////////////////////////////////		
		
		//주문 내역 중 매출이 있는 날짜 계산(YYYYMMDD 포맷)
		List<String> dayList = service.pAvenueDayList(yyyy+mm);
		dayList.sort(Comparator.reverseOrder());
	
		List<AvenueDTO> avenue = new ArrayList<>();
		
		for(String day:dayList) {
			
			AvenueDTO avenueDay = new AvenueDTO();
			avenueDay = service.pAvenueDay(day);
			avenueDay.setDay(day);
			avenue.add(avenueDay);	
		}
		
		//월 출력(01 ~ 12) 
		List<String> months = new ArrayList<>();
		for(int i=1; i<=12; i++) {
			if(i<10) months.add("0" + Integer.toString(i));
			else months.add(Integer.toString(i));
		}
		
		model.addAttribute("years", service.pAvenueYear());
		model.addAttribute("months", months);
		model.addAttribute("yyyy", yyyy);
		model.addAttribute("mm", mm);
		model.addAttribute("avenue", avenue);
		
	}

}
