package kr.or.ddit.users.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.BoardNotFoundException;
import kr.or.ddit.common.controller.UploadController;
import kr.or.ddit.common.dao.AttachDao;
import kr.or.ddit.common.enumpkg.ServiceResult;
import kr.or.ddit.common.vo.AttachVO;
import kr.or.ddit.common.vo.MemberVO;
import kr.or.ddit.paging.vo.PaginationInfo;
import kr.or.ddit.users.dao.FrBoardAttatchDAO;
import kr.or.ddit.users.dao.FrBoardDAO;
import kr.or.ddit.users.vo.CommentVO;
import kr.or.ddit.users.vo.FrBoardAttatchVO;
import kr.or.ddit.users.vo.FrBoardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FrBoardServiceImpl implements FrBoardService {
	
	@Autowired
	AttachDao attachDao;
	
	private final FrBoardDAO frbDAO;
	
//	private final FrBoardAttatchDAO attatchDAO;
	
//	@Override
//	public void createFrboard(FrBoardVO frBoardVO) {
//		// TODO Auto-generated method stub
//		
//	}
	@Override
	public FrBoardVO retrieveFrBoard(String frbNo) {
		 FrBoardVO frBoardVO = frbDAO.selectFrBoard(frbNo);
	      if(frBoardVO==null)
	         throw new BoardNotFoundException(HttpStatus.NOT_FOUND, String.format("%d 해당 게시글이 없음.", frbNo));
	      
	      frbDAO.incrementHit(frbNo);
	      
	      return frBoardVO;
	}
//	@Override
//	public FrBoardAttatchVO retrieveAttatch(int frbAttNo) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	@Override
	public void retrieveFrBoardList(PaginationInfo<FrBoardVO> paging) {
		 int totalRecord = frbDAO.selectTotalRecord(paging);
	      paging.setTotalRecord(totalRecord);
	      List<FrBoardVO> dataList = frbDAO.selectFrBoardList(paging);
	      paging.setDataList(dataList);
	      
		
	}
	//자유게시판 등록
	@Override
	public int createPost(FrBoardVO frBoardVO) {
		//FrBoardVO(rnum=0, frbNo=null, usersId=az001, frbTitle=제목, frbWrite=내용, frbDate=null, frbLno=null, frbRep=null
		//, frbDelete=null, uploadFile=[org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@5f40f29a]
		//, comment=null, attachVOList=null, frBoardAttatchVOList=null)
		int result = this.frbDAO.createPost(frBoardVO);
		
		//파일업로드
		MultipartFile[] uploadFile = frBoardVO.getUploadFile();
		
		log.info("uploadFile[0] : " + uploadFile[0]);
		log.info("uploadFile[0].getOriginalFilename() : " + uploadFile[0].getOriginalFilename());
		log.info("uploadFile[0].getOriginalFilename().length() : " + uploadFile[0].getOriginalFilename().length());
		
		//파일이 있을때만 실행
		if(uploadFile[0].getOriginalFilename().length()>0) {
			this.uploadAjaxAction(uploadFile,frBoardVO.getFrbNo());
		}
		
		return result;
	}
	
	//자유게시판 상세
	@Override
	public FrBoardVO detail(FrBoardVO frBoardVO) {
		return this.frbDAO.detail(frBoardVO);
	}
	
	@Override
	public ServiceResult deletePost(FrBoardVO frBoardVO) {
		int cnt = frbDAO.deleteFrBoard(frBoardVO);
		return cnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}
	
	@Override
	public MemberVO getMemberVO(String usersId) {
		return this.frbDAO.getMemberVO(usersId);
	}
	@Override
	public int increaseFrbLno(FrBoardVO frBoardVO) {
		return this.frbDAO.increaseFrbLno(frBoardVO);
	}
	@Override
	public int frbRepPost(FrBoardVO frBoardVO) {
		return this.frbDAO.frbRepPost(frBoardVO);
	}
	@Override
	public int comment(CommentVO commentVO) {
		return this.frbDAO.comment(commentVO);
	}
	@Override
	public List<CommentVO> commentList(FrBoardVO frBoardVO) {
		return this.frbDAO.commentList(frBoardVO);
	}
	//댓글 신고
	@Override
	public int coNoPost(CommentVO commentVO) {
		return this.frbDAO.coNoPost(commentVO);
	}
	
	//공통 파일업로드(파일업로드 + ATTACH 테이블에 insert)
	public int uploadAjaxAction(MultipartFile[] uploadFile, String pkCd) {
		log.info("uploadAjaxAction에 왔다");
		log.info("uploadFile : " + uploadFile);
		
//			String uploadFolder 
//				= "D:\\finalprojectDoc\\uploadFiles\\frBoard";
		
		//make folder 시작 ---------------------------
//			File uploadPath = new File(uploadFolder, getFolder());
		File uploadPath = new File("C:\\tools\\eGovFrameDev-4.0.0-64bit\\workspace\\FinalProject\\src\\main\\webapp\\resources\\upload\\frBoard", getFolder());
		
		log.info("upload Path : " + uploadPath);
		
		//만약 년/월/일 해당 폴더가 없다면 생성
		if(uploadPath.exists()==false) {
			uploadPath.mkdirs();
		}
		//make folder 끝 ---------------------------
		
		int seq = 0;
		
		//배열로부터 하나씩 파일을 꺼내오자
		for(MultipartFile multipartFile : uploadFile) {
			if(multipartFile.getOriginalFilename().length()>0) {
				log.info("-----------------------");
				log.info("Upload File Name : " + multipartFile.getOriginalFilename());//파일명
				log.info("Upload File Size : " + multipartFile.getSize());//크기
				log.info("Upload MIME : " + multipartFile.getContentType());//이미지, 한글, 워드..
				
				//IE 처리 => 경로를 제외한 파일명만 추출 
				//c:\\upload\\image01.jpg => image01.jpg
				String uploadFileName = multipartFile.getOriginalFilename();
				uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1);
				
				log.info("only file name : " + uploadFileName);
				
				//------------- 같은날 같은 이미지를 업로드 시 파일 중복 방지 시작 -------------
				//java.util.UUID => 랜덤값 생성
				UUID uuid = UUID.randomUUID(); //임의의 값을 생성
				//원래의 파일 이름과 구분하기 위해 _를 붙임
				uploadFileName = uuid.toString() + "_" + uploadFileName;
				//------------- 같은날 같은 이미지를 업로드 시 파일 중복 방지 끝 -------------			
				
				//File 객체 설계(복사할 대상 경로 , 파일명)
	//				File saveFile = new File(uploadFolder, uploadFileName);
				//uploadPath : D:\\A_TeachingMaterial\\06_spring\\springProj\\src\\
				//					main\\webapp\\resources\\upload\\2022\\07\\22
				//uploadFileName : asdfjlsfdakfsd_개똥이.jpg
				File saveFile = new File(uploadPath, uploadFileName);
				
				try {
					//파일 복사가 일어남
					multipartFile.transferTo(saveFile);
					
					//이미지인지 체킹
	//					if(checkImageType(saveFile)) {	//이미지라면..
	//						FileOutputStream thumbnail = new FileOutputStream(
	//								new File(uploadPath, "s_" + uploadFileName)
	//								);
	//						//섬네일 생성
	//						Thumbnailator.createThumbnail(multipartFile.getInputStream(),thumbnail,100,100);
	//						thumbnail.close();
	//					}
					
					//4) productVO.setFilename(파일full경로+명);
	//					String filename = "/" + getFolder().replaceAll("\\\\","/") + "/" + uploadFileName;
					
					AttachVO attachVO = new AttachVO();
					attachVO.setPkCd(pkCd);
					attachVO.setSeq(++seq);
					attachVO.setFrbattOriginalnm(multipartFile.getOriginalFilename());
					attachVO.setFrbattSavename("/" + getFolder().replaceAll("\\\\","/") + "/" + uploadFileName);//웹경로
					attachVO.setFrbattMime(multipartFile.getContentType());
					attachVO.setFrbattFilesize(multipartFile.getSize());
					
					log.info("uploadAjaxAction->attachVO : " + attachVO);
					
					attachDao.insertAttach(attachVO);
					
				} catch (IllegalStateException e) {
					log.error(e.getMessage());
				} catch (IOException e) {
					log.error(e.getMessage());
				}//tnd catch
			}//end if
		}//end for
		return seq;
	}//uploadAjaxAction
	
	//년/월/일 폴더 생성
	public static String getFolder() {
		//2022-07-22 형식(format) 지정
		//간단한 날짜 형식
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//날짜 객체 생성(java.util패키지)
		Date date = new Date();
		//2022-07-22
		String str = sdf.format(date);
		
		return str.replace("-", File.separator);
	}
	
	//용량이 큰 파일을 섬네일 처리를 하지 않으면
	//모바일과 같은 환경에서 많은 데이터를 소비해야 하므로
	//이미지의 경우 특별한 경우가 아니면 섬네일을 제작해야 함.
	//섬네일은 이미지만 가능함.
	//이미지 파일의 판단
	public static boolean checkImageType(File file) {
		/*
		 .jpeg / .jpg(JPEG 이미지)의 MIME 타입 : image/jpeg
		 */
		//MIME 타입을 통해 이미지 여부 확인
		//file.toPath() : 파일 객체를 path객체로 변환
		try {
			String contentType = Files.probeContentType(file.toPath());
			log.info("contentType : " + contentType);
			//MIME 타입 정보가 image로 시작하는지 여부를 return
			return contentType.startsWith("image");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//이 파일이 이미지가 아닐 경우
		return false;
	}
	
	//자유게시판 글 삭제
	
//	@Override
//	public void modifyFrBoard(FrBoardVO frbBoardvo) {
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void removeFrBoard(FrBoardVO inputData) {
//		// TODO Auto-generated method stub
//		
//	}
	
	
	
}
