package kr.or.ddit.users.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import kr.or.ddit.company.vo.InterviewSchdVO;
import kr.or.ddit.paging.BootstrapPaginationRenderer;
import kr.or.ddit.paging.vo.PaginationInfo;
import kr.or.ddit.paging.vo.SearchVO;
import kr.or.ddit.users.service.InterviewReviewService;
import kr.or.ddit.users.service.InterviewService;
import kr.or.ddit.users.vo.InterviewReviewVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
public class InterviewController {

	@Inject
	private InterviewService service;
	
	@Inject
	private InterviewReviewService reviewService;
	
	//면접일정 조회
	@GetMapping("interviewScheduleList")
	public String interviewScheduleList(
		Model model
		, @ModelAttribute("simpleCondition") SearchVO simpleCondition
		, @RequestParam(value="page", required = false, defaultValue = "1") int currentPage
		, @SessionAttribute("authId") String usersId	
	) {
		log.info("아이디 : " + usersId);
		PaginationInfo<InterviewSchdVO> paging = new PaginationInfo<>();
		paging.setSimpleCondition(simpleCondition); // 키워드 검색 조건
		paging.setCurrentPage(currentPage);
		Map<String, Object> variousCondition = new HashMap<>();
		variousCondition.put("usersId", usersId);
		
		paging.setVariousCondition(variousCondition);
	
		service.retrieveInterviewSchdList(paging);
	
		paging.setRenderer(new BootstrapPaginationRenderer());
		model.addAttribute("paging", paging);
				
		return "users/member/interviewSchedule";
	}
	
	//면접후기 조회
	@GetMapping("interviewReviewList")
	public String interviewReviewList (
			Model model
			, @ModelAttribute("simpleCondition") SearchVO simpleCondition
			, @RequestParam(value="page", required = false, defaultValue = "1") int currentPage 
			, @SessionAttribute("authId") String usersId
			) {
		log.info("아이디 : " + usersId);
		PaginationInfo<InterviewReviewVO> paging = new PaginationInfo<>();
		paging.setSimpleCondition(simpleCondition); // 키워드 검색 조건
		paging.setCurrentPage(currentPage);
		Map<String, Object> variousCondition = new HashMap<>();
		variousCondition.put("usersId", usersId);
		
		paging.setVariousCondition(variousCondition);
		
		reviewService.retrieveReviewList(paging);
		
		paging.setRenderer(new BootstrapPaginationRenderer());
		model.addAttribute("paging", paging);
		
		return "users/member/interviewReviewList";
	}
	
	//면접후기 등록
	@GetMapping("interview/reviewForm/{intrNo}")
	public String interviewReiviewForm(
			@PathVariable String intrNo
			, @SessionAttribute("authId") String usersId
			, Model model
			) {
		InterviewSchdVO interview = service.retrieveInfo(intrNo);
		model.addAttribute("interview", interview);
		return "users/member/interviewReviewForm";
	}
	
	@PostMapping("interview/review/newPost")
	public String interviewReviewNewPost(
			InterviewReviewVO interviewReviewVO
			, HttpSession session
			) {
		service.createInterviewReview(interviewReviewVO);
		return "redirect:/user/interviewReviewList";
	}
	
	//면접후기 수정
	@GetMapping("interview/reviewEdit/{intrevNo}")
	public String interviewReiviewEdit(
			@PathVariable String intrevNo
			, @SessionAttribute("authId") String usersId
			, Model model
			) {
		InterviewReviewVO review = reviewService.retrieveReview(intrevNo);
		model.addAttribute("review", review);
		return "users/member/interviewReviewEdit";
	}
	
	@PostMapping("interview/review/editPost")
	public String interviewReviewEditPost(
			InterviewReviewVO interviewReviewVO
			, HttpSession session
			) {
		reviewService.modifyReview(interviewReviewVO);
		return "redirect:/user/interviewReviewList";
	}
	
}
