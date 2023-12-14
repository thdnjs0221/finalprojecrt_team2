package kr.or.ddit.users.service;

import kr.or.ddit.paging.vo.PaginationInfo;
import kr.or.ddit.users.vo.InterviewReviewVO;

public interface InterviewReviewService {
	
	public void retrieveReviewList(PaginationInfo<InterviewReviewVO> paging);
	
	public InterviewReviewVO retrieveReview(String intrevNo);
	
	public void modifyReview(InterviewReviewVO interviewReviewVO);
}
