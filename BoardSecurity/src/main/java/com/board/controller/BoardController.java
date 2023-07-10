package com.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.board.dto.BoardVO;
import com.board.dto.FileVO;
import com.board.dto.LikeVO;
import com.board.dto.ReplyVO;
import com.board.service.BoardService;
import com.board.util.Page;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class BoardController {
	
	@Autowired //비밀번호 암호화 의존성 주입
	private BCryptPasswordEncoder pwdEncoder; 
	
	@Autowired
	BoardService service; //의존성 주입
	
	//게시물 목록 보기
	@GetMapping("/board/list")
	public void getList(@RequestParam("page") int pageNum, @RequestParam(name="keyword",defaultValue="",required=false) String keyword, Model model) throws Exception{
		//model.addAttribute("list",mapper.list());
		int postNum = 10; //한 화면에 보여지는 게시물 행의 갯수
		int pageListCount = 10; //화면 하단에 보여지는 페이지리스트 내의 페이지 갯수
		int startPoint = (pageNum -1)*postNum + 1;
		int endPoint = pageNum * postNum;		
		
		Page page = new Page();
		
		model.addAttribute("list",service.list(startPoint,endPoint,keyword));
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageList", page.getPageList(pageNum, postNum, pageListCount, service.getTotalCount(keyword), keyword));
	}
	
	//게시물 등록(화면 보기)
	@GetMapping("/board/write")
	public void getWrite() {}

	//첨부 파일 없는 게시물 등록
	@ResponseBody
	@PostMapping("/board/write")
	public String PostWrite(BoardVO board) throws Exception{

		int seqno = service.getSeqnoWithNextval();
		board.setSeqno(seqno);
		service.write(board);
		return "{\"message\":\"good\"}";

	}
	
	//파일 업로드
	@ResponseBody
	@PostMapping("/board/fileUpload")
	public String postFileUpload(BoardVO board,@RequestParam("SendToFileList") List<MultipartFile> multipartFile, 
			@RequestParam("kind") String kind,@RequestParam(name="deleteFileList", required=false) int[] deleteFileList) throws Exception{

		String path = "c:\\Repository\\file\\"; 
		int seqno =0;
		
		if(kind.equals("I")) { //게시물 등록 시 게시물 등록 
			seqno = service.getSeqnoWithNextval();
			board.setSeqno(seqno);
			service.write(board);			
		}
		
		if(kind.equals("U")) { //게시물 수정 시 게시물 수정
			seqno = board.getSeqno();
			service.modify(board);
			
			if(deleteFileList != null) {
				
				for(int i=0; i<deleteFileList.length; i++) {

					//파일 삭제
					FileVO fileInfo = new FileVO();
					fileInfo = service.fileInfo(deleteFileList[i]);
					//File file = new File(path + fileInfo.getStored_filename());
					//file.delete();
					
					//파일 테이블에서 파일 정보 삭제
					//게시물 수정에서 삭제할 파일 목록이 전송되면 이 값을 받아서 tbl_file내에 있는 파일 정보를 하나씩 삭제하는 deleteFileList 실행
					Map<String,Object> data = new HashMap<>();
					data.put("kind", "F"); 
					data.put("fileseqno", deleteFileList[i]);
					service.deleteFileList(data); //deleteFileList는 Map타입의 인자값을 받도록 설정
					
				}
			}	
		}
		
		if(!multipartFile.isEmpty()) {//파일 등록 및 수정 시 파일 업로드
			File targetFile = null; 
			Map<String,Object> fileInfo = null;		
			
			for(MultipartFile mpr:multipartFile) {
				
				String org_filename = mpr.getOriginalFilename();	
				String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
				String stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
				long filesize = mpr.getSize() ;

				targetFile = new File(path + stored_filename);
				mpr.transferTo(targetFile);
				
				fileInfo = new HashMap<>();
				fileInfo.put("org_filename",org_filename);
				fileInfo.put("stored_filename", stored_filename);
				fileInfo.put("filesize", filesize);
				fileInfo.put("seqno", seqno);
				fileInfo.put("userid", board.getUserid());
				fileInfo.put("kind", kind);
				service.fileInfoRegistry(fileInfo);
	
			}
		}			
		return "{\"message\":\"good\"}";
}
	
	//게시물 내용 상세 보기 
	@GetMapping("/board/view")
	public void getView(@RequestParam("seqno") int seqno, @RequestParam("page") int pageNum,
			@RequestParam(name="keyword",required=false) String keyword,
			Model model,HttpSession session) throws Exception {
		
		String SessionUserid = (String)session.getAttribute("userid");
		BoardVO view = service.view(seqno);
		
		//mapper.hitno(board);
		
		//조회수 증가 처리
		if(!SessionUserid.equals(view.getUserid()))
			service.hitno(view);
		
		//좋아요, 싫어요 처리
		LikeVO likeCheckView = service.likeCheckView(seqno, SessionUserid);
		
		//초기에 좋아요/싫어요 등록이 안되어져 있을 경우 "N"으로 초기화		
		if(likeCheckView == null) {
			model.addAttribute("myLikeCheck", "N");
			model.addAttribute("myDislikeCheck", "N");
		} else if(likeCheckView != null) {
			model.addAttribute("myLikeCheck", likeCheckView.getMylikecheck());
			model.addAttribute("myDislikeCheck", likeCheckView.getMydislikecheck());
		}		
		
		//model.addAttribute("view", mapper.view(seqno));
		model.addAttribute("view", view);
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pre_seqno", service.pre_seqno(seqno, keyword));
		model.addAttribute("next_seqno", service.next_seqno(seqno, keyword));
		model.addAttribute("likeCheckView", likeCheckView);
		model.addAttribute("fileListView", service.fileListView(seqno));
		
	}
	
	//좋아요/싫어요 관리
	@ResponseBody
	@PostMapping(value = "/board/likeCheck")
	public String postLikeCheck(@RequestBody Map<String, Object> likeCheckData) throws Exception {
		
		int seqno = (int)likeCheckData.get("seqno");
		String userid = (String)likeCheckData.get("userid");
		int checkCnt = (int)likeCheckData.get("checkCnt");

		//현재 날짜, 시간 구해서 좋아요/싫어요 한 날짜/시간 입력 및 수정
		String likeDate = "";
		String dislikeDate = "";
		if(likeCheckData.get("mylikecheck").equals("Y")) 
			likeDate = LocalDateTime.now().toString();
		else if(likeCheckData.get("mydislikecheck").equals("Y")) 
			dislikeDate = LocalDateTime.now().toString();

		likeCheckData.put("likedate", likeDate);
		likeCheckData.put("dislikedate", dislikeDate);

		//TBL_LIKE 테이블 입력/수정
		LikeVO likeCheckView = service.likeCheckView(seqno,userid);
		if(likeCheckView == null) service.likeCheckRegistry(likeCheckData);
			else service.likeCheckUpdate(likeCheckData);

		//TBL_BOARD 내의 likecnt,dislikecnt 입력/수정 
		BoardVO board = service.view(seqno);
		
		int likeCnt = board.getLikecnt();
		int dislikeCnt = board.getDislikecnt();
			
		switch(checkCnt){
	    	case 1 : likeCnt --; break;
	    	case 2 : likeCnt ++; dislikeCnt --; break;
	    	case 3 : likeCnt ++; break;
	    	case 4 : dislikeCnt --; break;
	    	case 5 : likeCnt --; dislikeCnt ++; break;
	    	case 6 : dislikeCnt ++; break;
		}
		
		service.boardLikeUpdate(seqno,likeCnt,dislikeCnt);

		return "{\"likeCnt\":\"" + likeCnt + "\",\"dislikeCnt\":\"" + dislikeCnt + "\"}";
	}
	
	//게시물 수정(화면 보기)
	@GetMapping("/board/modify")
	public void getModify(@RequestParam("seqno") int seqno, @RequestParam("page") int pageNum, Model model,
			@RequestParam(name="keyword",required=false) String keyword
			) throws Exception{ 
		
		//model.addAttribute("view", mapper.view(seqno));
		
		System.out.println("keyword=" + keyword);
		model.addAttribute("view", service.view(seqno));
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);	
		model.addAttribute("fileListView", service.fileListView(seqno));
		
	}
	
	//게시물 수정
	@ResponseBody
	@PostMapping("/board/modify")
	public String postModify(BoardVO board,@RequestParam("page") int pageNum,
			@RequestParam(name="keyword", required=false) String keyword,
			@RequestParam(name="deleteFileList", required=false) int[] deleteFileList) throws Exception {
	
		service.modify(board);
		
		if(deleteFileList != null) {
			
			for(int i=0; i<deleteFileList.length; i++) {

				//파일 정보 삭제
				FileVO fileInfo = new FileVO();
				fileInfo = service.fileInfo(deleteFileList[i]);
				//File file = new File(path + fileInfo.getStored_filename());
				//file.delete();
				
				//파일 테이블에서 파일 정보 삭제
				Map<String,Object> data = new HashMap<>();
				data.put("kind", "F");
				data.put("fileseqno", deleteFileList[i]);
				service.deleteFileList(data);
				
			}
		}
		
		return "{\"message\":\"good\"}";
	}
	
	//게시물 삭제
	@GetMapping("/board/delete")
	public String getDelete(@RequestParam("seqno") int seqno) throws Exception {

		Map<String,Object> data = new HashMap<>();
		data.put("kind", "B"); //게시물 삭제시 tbl_file내의 파일 정보 수정
		data.put("seqno", seqno);
		service.deleteFileList(data);
		service.delete(seqno);
		return "redirect:/board/list?page=1";
	}
	
	//파일 다운로드
	@GetMapping("/board/filedownload")
	public void filedownload(@RequestParam("fileseqno") int fileseqno, HttpServletResponse rs) throws Exception {
		
		String path = "c:\\Repository\\file\\";
		
		FileVO fileInfo = service.fileInfo(fileseqno);
		
		byte fileByte[] = FileUtils.readFileToByteArray(new File(path+fileInfo.getStored_filename()));
		
		//헤드값을 Content-Disposition로 주게 되면 Response Body로 오는 값을 filename으로 다운받으라는 것임
		//예) Content-Disposition: attachment; filename="hello.jpg"
		rs.setContentType("application/octet-stream");
		rs.setContentLength(fileByte.length);
		rs.setHeader("Content-Disposition",  "attachment; filename=\""+URLEncoder.encode(fileInfo.getOrg_filename(), "UTF-8")+"\";");
		rs.getOutputStream().write(fileByte);
		rs.getOutputStream().flush();//버퍼에 있는 내용을 write
		rs.getOutputStream().close();
		
	}

	//댓글 처리
	@ResponseBody
	@PostMapping("/board/reply")
	public List<ReplyVO> postReply(@RequestBody ReplyVO reply,@RequestParam("option") String option)throws Exception{
		
		switch(option) {
		
		case "I" : service.replyRegistry(reply); //댓글 등록
				   break;
		case "U" : service.replyUpdate(reply); //댓글 수정
				   break;
		case "D" : service.replyDelete(reply); //댓글 삭제
				   break;
		}

		return service.replyView(reply);
	}

}
