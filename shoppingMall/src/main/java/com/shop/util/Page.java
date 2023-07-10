package com.shop.util;

import java.net.URLEncoder;

public class Page {

	public String getPageList(String methodName, int pageNum, int postNum, int pageCount, int totalCount, String keyword, String option){

	/*
	 * 	methoName --> 게시물을 실행할 메소드 경로 및 이름
	 *  pageNum --> 현재 페이지
	 *  postNum --> 한 페이지에 보여질 게시물 갯수
	 *  pageCount --> 페이지 하단에 보여질 페이지 리스트 내의 갯수
	 *  section --> 하나의 페이지 리스트에서 처리하는 페이지 범위(예 : 페이지의 리스트 [1,2,3,4,5] --> section 1, [6,7,8,9,10] --> section2)
	 *  totalSection --> 전체 section 갯수   
	 *  totalPage --> 전체 페이지 갯수 
	 */
		
	int totalPage = (int) Math.ceil(totalCount/(double)postNum); 
	int totalSection = (int) Math.ceil(totalPage/(double)pageCount); 
	
	pageNum = pageNum-1; //계산에 사용되는 페이지 번호는 0부터 시작
	int section = (int) Math.ceil(pageNum/pageCount); //현재 위치한 섹션 번호	

	String pageList = "";
	
	if(totalPage != 1 )
	{
		for(int i=0; i < pageCount ; i++){ 
			if(section > 0 && i == 0) 
				pageList +=	"<a href=" + methodName + "?page=" + Integer.toString((section-1)*pageCount+(pageCount)) + "&keyword=" + keyword + option + ">◀</a> ";
			if(totalPage == (i+section*pageCount)){  break; }
			if(pageNum != (section*pageCount + i))
				pageList += " <a href=" + methodName + "?page=" + Integer.toString(i+section*pageCount+1) + "&keyword=" + keyword + option + ">" + Integer.toString(i+section*pageCount+1) + "</a> ";
			else pageList += " <span style='font-weight: bold'>" + Integer.toString(section*pageCount+i+1) + "</span>";
			if(totalSection >1 && i==(pageCount-1) && totalPage != (i+section*pageCount+1)) 
				pageList += "<a href=" + methodName + "?page=" + Integer.toString((section+1)*pageCount+1) + "&keyword=" + keyword + option + ">▶</a>";
			
		}
 	} 
	return pageList == ""? "":("[" + pageList + "]"); 
	}
	
	public String getPageAddress(int pageNum, int postNum, int pageCount, int totalCount, String addrSearch) throws Exception {

	int totalPage = (int) Math.ceil(totalCount/(double)postNum); //전체 페이지 갯수
	int totalSection = (int) Math.ceil(totalPage/(double)pageCount); //전체 섹션 개수
	
	pageNum = pageNum-1; //계산에 사용되는 페이지 번호는 0부터 시작
	int section = (int) Math.ceil(pageNum/pageCount); //현재 위치한 섹션 번호	

	String pageList = "";
	
	if(totalPage != 1 )
	{
		for(int i=0; i < pageCount ; i++){ 
			if(section > 0 && i == 0) 
				pageList +=	"<a href=addrSearch?page=" + Integer.toString((section-1)*pageCount+(pageCount)) + "&addrSearch=" + URLEncoder.encode(addrSearch,"UTF-8" ) + ">◀</a> ";
			if(totalPage == (i+section*pageCount)){  break; }
			if(pageNum != (section*pageCount + i))
				pageList += " <a href=addrSearch?page=" + Integer.toString(i+section*pageCount+1) + "&addrSearch=" + URLEncoder.encode(addrSearch,"UTF-8" ) + ">" + Integer.toString(i+section*pageCount+1) + "</a> ";
			else pageList += " <span style='font-weight: bold'>" + Integer.toString(section*pageCount+i+1) + "</span>";
			if(totalSection >1 && i==(pageCount-1) && totalPage != (i+section*pageCount+1)) 
				pageList += "<a href=addrSearch?page=" + Integer.toString((section+1)*pageCount+1) + "&addrSearch=" + URLEncoder.encode(addrSearch,"UTF-8" ) + ">▶</a>";
			
		}
 	} 
	return pageList = "[페이지] " + pageList; 
	}
	
}
