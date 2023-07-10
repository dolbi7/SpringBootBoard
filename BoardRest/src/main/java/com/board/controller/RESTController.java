package com.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.board.dto.BoardDTO;
import com.board.dto.FileDTO;
import com.board.dto.MemberDTO;
import com.board.dto.ReplyInterface;
import com.board.entity.BoardEntity;
import com.board.entity.LikeEntity;
import com.board.entity.repository.BoardRepository;
import com.board.entity.repository.MemberRepository;
import com.board.service.BoardService;
import com.board.service.MemberService;
import com.board.util.JWTUtil;
import com.board.util.PageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@CrossOrigin(originPatterns = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class RESTController {

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder pwdEncoder; 
	private final MemberService mservice;
	private final BoardService bservice;
	
	//JWT 관리 객체 의존성 주입
	private final JWTUtil jwtUtil;
	
	//전체 게시물 목록 보기
	@GetMapping("/restapi/listAll")
	public List<BoardDTO> getListAll() {
		
		List<BoardDTO> boardDTOs = new ArrayList<>();		
		boardRepository.findAll().stream().forEach(list-> boardDTOs.add(new BoardDTO(list)));
		return boardDTOs;
	}
	
	//게시물 목록 보기
	@GetMapping("/restapi/list")
	public  Page<BoardEntity> getList(@RequestParam("page") int pageNum, @RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception{
		int postNum = 5; //한 화면에 보여지는 게시물 행의 갯수		
		return bservice.list(pageNum, postNum, keyword);
	}
	
	//게시물 목록 페이지 리스트 보기
	@GetMapping("/restapi/pagelist")
	public String getPageList(@RequestParam("page") int pageNum, @RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception {
		int postNum = 5; //한 화면에 보여지는 게시물 행의 갯수
		int pageListCount = 5; //화면 하단에 보여지는 페이지리스트 내의 페이지 갯수
		
		Page<BoardEntity> list = getList(pageNum, keyword);
		
		PageUtil page = new PageUtil();
		return "{\"pagelist\":\"" + page.getPageList(pageNum, postNum, pageListCount, (int)list.getTotalElements(), keyword) + "\"}";
	}
	
	//게시물 내용 상세 보기 
	@GetMapping("/restapi/view")
	public BoardDTO getView(@RequestParam("seqno") Long seqno,@RequestParam("email") String email) throws Exception {
		return bservice.view(seqno); 
	}

	//조회수 증가
	
	//게시물 이전 보기
	@GetMapping("/restapi/preseqno")
	public String getPreseqno(@RequestParam("seqno") Long seqno,
	@RequestParam(name="keyword",required=false) String keyword) throws Exception {		
		return "{\"pre_seqno\":\"" + bservice.pre_seqno(seqno, keyword) + "\"}";		
	}
	
	//게시물 다음 보기
	@GetMapping("/restapi/nextseqno")
	public String getNextseqno(@RequestParam("seqno") Long seqno, 
	@RequestParam(name="keyword",required=false) String keyword) throws Exception {		
		return "{\"next_seqno\":\"" + bservice.next_seqno(seqno, keyword) + "\"}"; 		
	}
	
	//첨부 파일 없는 게시물 등록
	@PostMapping("/restapi/write")
	public String PostWrite(BoardDTO board) throws Exception{

		Long seqno = bservice.getSeqnoWithNextval();
		board.setSeqno(seqno);
		bservice.write(board);
		return "{\"message\":\"good\"}";

	}
	
	//첨부파일 있는 게시물 등록
	@PostMapping("/restapi/fileUpload")
	public String postFileUpload(BoardDTO board,@RequestParam("SendToFileList") List<MultipartFile> multipartFile, 
			@RequestParam("kind") String kind,@RequestParam(name="deleteFileList", required=false) Long[] deleteFileList) throws Exception{

		String path = "c:\\Repository\\file\\"; 
		Long seqno =0L;
		
		if(kind.equals("I")) { //게시물 등록 시 게시물 등록 
			seqno = bservice.getSeqnoWithNextval();
			board.setSeqno(seqno);
			bservice.write(board);
		}
		
		if(kind.equals("U")) { //게시물 수정 시 게시물 수정
			seqno = board.getSeqno();
			bservice.modify(board);
			
			if(deleteFileList != null) {
				
				for(int i=0; i<deleteFileList.length; i++) {
					
					//파일 테이블에서 파일 정보 삭제
					//게시물 수정에서 삭제할 파일 목록이 전송되면 이 값을 받아서 tbl_file내에 있는 파일 정보를 하나씩 삭제하는 deleteFileList 실행

					bservice.deleteFileList(deleteFileList[i],"F"); 
					
				}
			}	
		}
		
		if(!multipartFile.isEmpty()) {//파일 등록 및 수정 시 파일 업로드
			File targetFile = null; 
			
			for(MultipartFile mpr:multipartFile) {
				
				String org_filename = mpr.getOriginalFilename();	
				String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
				String stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
				long filesize = mpr.getSize() ;

				targetFile = new File(path + stored_filename);
				mpr.transferTo(targetFile);
				
				FileDTO fileDTO = FileDTO.builder()
								.seqno(seqno)
								.email(board.getEmail().getEmail())
								.org_filename(org_filename)
								.stored_filename(stored_filename)
								.filesize(filesize)
								.checkfile("Y")
								.build();
				
				bservice.fileInfoRegistry(fileDTO);
	
			}
		}	
		
		return "{\"message\":\"good\"}";
}
	
	//좋아요/싫어요 상태 보기
	@GetMapping("/restapi/likeCheckView")
	public String getLikeCheckView(@RequestParam("seqno") Long seqno, @RequestParam("email") String email) throws Exception {
		
		LikeEntity likeCheckView = bservice.likeCheckView(seqno, email);
		String result = "";
		
		//초기에 좋아요/싫어요 등록이 안되어져 있을 경우 "N"으로 초기화		
		if(likeCheckView == null) { 
			result = "{\"myLikeCheck\":\"N\",\"myDislikeCheck\":\"N\"}";
		} else if(likeCheckView != null) 
			result = "{\"myLikeCheck\":\"" + likeCheckView.getMylikecheck()
			+ "\",\"myDislikeCheck\":\"" + likeCheckView.getMydislikecheck() + "\"}";
		System.out.println("좋아요/싫어요 상태 = " + result);
		return result;	
	}
	
	//좋아요/싫어요 상태 변경
	@PostMapping(value = "/restapi/likeCheck")
	public String postLikeCheck(@RequestParam("seqno") Long seqno, @RequestParam("email") String email,
			@RequestParam("myLikecheck") String mylikeCheck,@RequestParam("myDislikecheck") String mydislikeCheck,
			@RequestParam("checkCnt") String checkCnt) throws Exception {
	
		//현재 날짜, 시간 구해서 좋아요/싫어요 한 날짜/시간 입력 및 수정
		String likeDate = "";
		String dislikeDate = "";
		if(mylikeCheck.equals("Y")) 
			likeDate = LocalDateTime.now().toString();
		else if(mydislikeCheck.equals("Y")) 
			dislikeDate = LocalDateTime.now().toString();

		LikeEntity likeCheckView = bservice.likeCheckView(seqno, email);
		if(likeCheckView == null) {
			System.out.println("좋아요/싫어요 등록");
			System.out.println("seqno = " + seqno + ",email = " + email + ", mylikeCheck = " + mylikeCheck + ", mydislikeCheck = " + mydislikeCheck + ",checkCnt = " + checkCnt);
			bservice.likeCheckRegistry(seqno,email,mylikeCheck,mydislikeCheck,likeDate,dislikeDate);
		}
			else {
				System.out.println("좋아요/싫어요 업데이트");
				System.out.println("seqno = " + seqno + ",email = " + email + ", mylikeCheck = " + mylikeCheck + ", mydislikeCheck = " + mydislikeCheck + ",checkCnt = " + checkCnt);
				bservice.likeCheckUpdate(seqno,email,mylikeCheck,mydislikeCheck,likeDate,dislikeDate);
			}

		//TBL_BOARD 내의 likecnt,dislikecnt 입력/수정 
		BoardDTO board = bservice.view(seqno);
		
		int likeCnt = board.getLikecnt();
		int dislikeCnt = board.getDislikecnt();
			
		switch(checkCnt){
	    	case "1" : likeCnt --; break;
	    	case "2" : likeCnt ++; dislikeCnt --; break;
	    	case "3" : likeCnt ++; break;
	    	case "4" : dislikeCnt --; break;
	    	case "5" : likeCnt --; dislikeCnt ++; break;
	    	case "6" : dislikeCnt ++; break;
		}

		bservice.boardLikeUpdate(seqno,likeCnt,dislikeCnt);

		return "{\"likeCnt\":\"" + likeCnt + "\",\"dislikeCnt\":\"" + dislikeCnt + "\"}";
	}

	
	//파일 목록 보기
	@GetMapping("/restapi/fileView")
	public List<FileDTO> getFileView(@RequestParam("seqno") Long seqno) throws Exception {
		List<FileDTO> fileDTOs = new ArrayList<>();
		bservice.fileListView(seqno).stream().forEach(file -> fileDTOs.add(new FileDTO(file)));
		return fileDTOs;
	}
	
	//파일 다운로드
	@GetMapping("/restapi/filedownload")
	public void filedownload(@RequestParam("fileseqno") Long fileseqno, HttpServletResponse rs) throws Exception {
		
		String path = "c:\\Repository\\file\\";
		
		FileDTO fileInfo = bservice.fileInfo(fileseqno);
		
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
	
	//게시물 수정
	@PostMapping("/restapi/modify")
	public String postModify(BoardDTO board,@RequestParam("page") int pageNum,
			@RequestParam(name="keyword", required=false) String keyword,
			@RequestParam(name="deleteFileList", required=false) Long[] deleteFileList) throws Exception {
	
		bservice.modify(board);
		
		if(deleteFileList != null) {
			
			for(int i=0; i<deleteFileList.length; i++) {
				
				//파일 테이블에서 파일 정보 삭제
				bservice.deleteFileList(deleteFileList[i],"F");
				
			}
		}
		
		return "{\"message\":\"good\"}";
	}
	
	//게시물 삭제
	@GetMapping("/restapi/delete")
	public String getDelete(@RequestParam("seqno") Long seqno) throws Exception {

		bservice.deleteFileList(seqno, "B");
		bservice.delete(seqno);
		return "{\"message\":\"good\"}";
	}
	
	//댓글 처리	
	@PostMapping("/restapi/reply")
	public List<ReplyInterface> postReply(ReplyInterface reply,@RequestParam("option") String option)throws Exception{
		
		switch(option) {
		
		case "I" : bservice.replyRegistry(reply); //댓글 등록
				   break;
		case "U" : bservice.replyUpdate(reply); //댓글 수정
				   break;
		case "D" : bservice.replyDelete(reply); //댓글 삭제
				   break;
		}

		return bservice.replyView(reply);
	}
	
	//로그인
	@PostMapping("/restapi/loginCheck")
	public String postLogIn(MemberDTO loginData,HttpSession session, 
			@RequestParam("autoLogin") String autoLogin, HttpServletRequest request) throws Exception {
		
		String authkey = "";
		String accessToken = "";
		String refreshToken = "";
		
		//로그인 시 자동 로그인 체크할 경우 신규 authkey 등록
		if(autoLogin.equals("NEW")) { 	 
			authkey = UUID.randomUUID().toString().replaceAll("-", ""); 
			loginData.setAuthkey(authkey);
			mservice.authkeyUpdate(loginData);	
		}
		
		//authkey가 클라이언트에 쿠키로 존재할 경우 로그인 과정 없이 세션 생성 후 게시판 목록 페이지로 이동  
		if(autoLogin.equals("PASS")) {		
			
			if(memberRepository.findByAuthkey(loginData.getAuthkey()) != null) {

				return "{\"message\":\"good\"}";
			}else 
				return "{\"message\":\"bad\"}";
		}
		
		//JWT 로그인
		if(autoLogin.equals("JWTNew")) {			
			Map<String,Object> data = new HashMap<>();
			data.put("email", loginData.getEmail());
			data.put("password", loginData.getPassword());
			
			//access token & refresh token 생성
			accessToken = jwtUtil.generateToken(data, 1);
			refreshToken = jwtUtil.generateToken(data, 5);
		}
				
		//아이디 존재 여부 확인
		if(mservice.idCheck(loginData.getEmail()) == 0)
			return "{\"message\":\"ID_NOT_FOUND\"}";
		
		//아이디가 존재하면 읽어온 email로 로그인 정보 가져 오기
		MemberDTO member = mservice.memberInfo(loginData.getEmail());
		
		//패스워드 확인
		String str = "";
		if(!pwdEncoder.matches(loginData.getPassword(),member.getPassword())) 
			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
		
		//최종적으로 클라이언트에 전달할 JSON 값 생성
		if(autoLogin.equals("JWTNew")) {
			str = "{\"message\":\"JWT\",\"accessToken\":\"" + accessToken + "\",\"refreshToken\":\"" + refreshToken + 
					"\",\"username\":\"" + URLEncoder.encode(member.getUsername(),"UTF-8") + "\",\"role\":\"" + member.getRole() + "\"}";
		} else {
			str = "{\"message\":\"good\",\"authkey\":\"" + member.getAuthkey() + "\",\"username\":\"" 
					+ URLEncoder.encode(member.getUsername(),"UTF-8") 	+ "\",\"role\":\"" + member.getRole() + "\"}";
		}	
		System.out.println("str = " + str);
		return str;

	}
	
	//토큰 유효성 검사
	@GetMapping("/restapi/validate")
	public String getValidate(HttpServletRequest request) {
		String token = jwtUtil.getTokenFromAuthorization(request);
		if(token.equals("JWT_NOT_FOUND")) 
			return "{\"message\":\"bad\"}";
		String jwtCheck = jwtUtil.validateToken(token);
		
		switch(jwtCheck) {			
			case "VALID_JWT" : return "{\"message\":\"VALID_JWT\"}";			
			case "EXPIRED_JWT" : return "{\"message\":\"EXPIRED_JWT\"}";
			case "INVALID_JWT":
			case "UNSUPPORTED_JWT":
			case "EMPTY_JWT": return "{\"message\":\"INVALID_JWT\"}";													
		}
		return null;
	}
	
	//refreshToken 관리
	@PostMapping("/restapi/refreshToken")
	public String postRefreshToken(MemberDTO memberDTO, @RequestParam("refreshToken") String refreshToken ) {
	
		Map<String,Object> data = new HashMap<>();
		data.put("email", memberDTO.getEmail());
		data.put("password", memberDTO.getPassword());
		
		//access token & refresh token 생성		
		return "{\"accessToken\":\"" + jwtUtil.generateToken(data, 1) + ",\"refreshToken\":\"" + jwtUtil.generateToken(data, 5) + "\"}";
	}
	
	//아이디 중복 확인
	@PostMapping("/restapi/idCheck")
	public String getIdCheck(@RequestParam("email") String email) {	
		System.out.println("email = " + email);
		return mservice.idCheck(email) == 0 ? "{\"status\":\"good\"}":"{\"status\":\"bad\"}";	
	}
	
	@GetMapping("/restapi/visit")
	public String getRandomVisit() {
		
		//숫자 + 영문대소문자 7자리 생성
		StringBuffer tempPW = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 7; i++) {
		    int rIndex = rnd.nextInt(3);
		    switch (rIndex) {
		    case 0:
		        // a-z : 아스키코드 97~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 97));
		        break;
		    case 1:
		        // A-Z : 아스키코드 65~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 65));
		        break;
		    case 2:
		        // 0-9
		    	tempPW.append((rnd.nextInt(10)));
		        break;
		    }
		}
		return "{\"rvalue\":\"" + tempPW.toString() + "\"}";
	}
	
	//회원 등록
	@PostMapping("/restapi/signup")
	public String postSignup(MemberDTO member, @RequestParam("imgProfile") MultipartFile mpr) throws Exception {
		
		String path = "c:\\Repository\\profile\\"; 
		String org_filename = "";
		long filesize = 0L;
		
		if(!mpr.isEmpty()) {
			File targetFile = null; 
				
			org_filename = mpr.getOriginalFilename();	
			String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
			String stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
			filesize = mpr.getSize();
			targetFile = new File(path + stored_filename);
			mpr.transferTo(targetFile);	//raw data를 targetFile에서 가진 정보대로 변환
			member.setOrg_filename(org_filename);
			member.setStored_filename(stored_filename);
			member.setFilesize(filesize);
		}
		
		member.setPassword(pwdEncoder.encode(member.getPassword()));
		
		mservice.signup(member);		
		return "{\"username\":\"" + URLEncoder.encode(member.getUsername(),"UTF-8") + "\",\"status\":\"good\"}";
		
	}
	
}
