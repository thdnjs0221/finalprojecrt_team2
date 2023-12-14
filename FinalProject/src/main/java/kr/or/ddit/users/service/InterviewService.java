package kr.or.ddit.users.service;

import kr.or.ddit.company.vo.InterviewSchdVO;
import kr.or.ddit.paging.vo.PaginationInfo;
import kr.or.ddit.users.vo.InterviewReviewVO;

public interface InterviewService {
	
	/**
	 * 면접일정 리스트 조회
	 * @param paging
	 */
	public void retrieveInterviewSchdList(PaginationInfo<InterviewSchdVO> paging);
	
	public InterviewSchdVO retrieveInfo(String intrNo);
	
	public void createInterviewReview(InterviewReviewVO interviewReviewVO);
	

}
