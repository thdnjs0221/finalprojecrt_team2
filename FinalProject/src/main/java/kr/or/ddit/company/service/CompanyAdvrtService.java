package kr.or.ddit.company.service;

import java.util.List;

import kr.or.ddit.company.vo.CmpAdvrtPayVO;
import kr.or.ddit.company.vo.CompanyAdvrtVO;

public interface CompanyAdvrtService {
	
	/**
	 * 비동기로 db에 있는 기업 광고 목록 가져오기
	 * @return
	 */
	public List<CompanyAdvrtVO> selectCmpAdvrtList();
	
	/**
	 * 기업 광고 결제 db로 저장하기
	 * @param cmpAdvrtPayVO
	 */
    public void sendCmpAdPay(CmpAdvrtPayVO cmpAdvrtPayVO);

    /**
     * 사용중인 기업 광고 불러오기
     * @param id
     * @return
     */
	public List<CmpAdvrtPayVO> validCmpAdvrtList(String id);

	/**
	 * 사용중인 기업 광고 테이블 목록 불러오기
	 * @param id
	 * @return
	 */
	public List<CmpAdvrtPayVO> retriveMyCmpAdvrtList(String id);

	/**
	 * 오늘 날짜에 해당되는 기업 광고 가져오기
	 * @return
	 */
	public List<CmpAdvrtPayVO> selectCmpAdvrtIndex();

}
