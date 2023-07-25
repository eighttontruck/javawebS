package com.spring.javawebS;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.javawebS.pagination.PageProcess;
import com.spring.javawebS.pagination.PageVO;
import com.spring.javawebS.service.AdminService;
import com.spring.javawebS.service.InquiryService;
import com.spring.javawebS.vo.InquiryReplyVO;
import com.spring.javawebS.vo.InquiryVO;
import com.spring.javawebS.vo.QrCodeVO;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	AdminService adminService;
	
	@Autowired
	PageProcess pageProcess;
	
	@Autowired
	InquiryService inquiryService;
	
	@RequestMapping(value = "/adminMain", method = RequestMethod.GET)
	public String adminMain() {
		return "admin/adminMain";
	}
	
	@RequestMapping(value = "/adminLeft", method = RequestMethod.GET)
	public String adminLeft() {
		return "admin/adminLeft";
	}
	
	@RequestMapping(value = "/adminContent", method = RequestMethod.GET)
	public String adminContent() {
		return "admin/adminContent";
	}
	
  // 관리자 1:1 리스트 보여주기
	@RequestMapping(value="/adInquiryList", method = RequestMethod.GET)
	public String adInquiryListGet(
			@RequestParam(name="part", defaultValue="전체", required=false) String part,
			@RequestParam(name="pag", defaultValue="1", required=false) int pag,
	    @RequestParam(name="pageSize", defaultValue="5", required=false) int pageSize,
			Model model) {
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "adminInquiry", part, "");
		
    ArrayList<QrCodeVO> vos = adminService.getInquiryListAdmin(pageVO.getStartIndexNo(), pageSize, part);
    
    model.addAttribute("vos", vos);
	  model.addAttribute("pageVO", pageVO);
	  model.addAttribute("part", part);
		
		return "admin/inquiry/adInquiryList";
	}
	
	// 관리자 답변달기 폼 보여주기(관리자가 답변글 수정/삭제처리하였을때도 함께 처리하고 있다.)
	@RequestMapping(value="/adInquiryReply", method = RequestMethod.GET)
	public String adInquiryReplyGet(int idx,
			@RequestParam(name="part", defaultValue="전체", required=false) String part,
			@RequestParam(name="pag", defaultValue="1", required=false) int pag,
	    @RequestParam(name="pageSize", defaultValue="5", required=false) int pageSize,
			Model model) {
		InquiryVO vo = adminService.getInquiryContent(idx);
		InquiryReplyVO reVO = adminService.getInquiryReplyContent(idx);
		model.addAttribute("part", part);
		model.addAttribute("pag", pag);
		model.addAttribute("vo", vo);
		model.addAttribute("reVO", reVO);
		return "admin/inquiry/adInquiryReply";
	}
	
	// 관리자 답변달기 저장하기
	@ResponseBody
	@RequestMapping(value="/adInquiryReplyInput", method = RequestMethod.POST)
	public String adInquiryReplyInputPost(InquiryReplyVO vo, Model model) {
		adminService.setInquiryInputAdmin(vo);
		adminService.setInquiryUpdateAdmin(vo.getInquiryIdx());
		
		return "admin/inquiry/adInquiryReply";
	}
	
	// 관리자 답변글 수정처리
	@ResponseBody
	@RequestMapping(value="/adInquiryReplyUpdate", method = RequestMethod.POST)
	public String adInquiryReplyUpdatePost(InquiryReplyVO reVO) {
		adminService.setInquiryReplyUpdate(reVO);	// 관리자가 답변글을 수정했을때 처리루틴
		return "";
	}
	
	// 관리자 답변글 삭제처리
	@ResponseBody
	@RequestMapping(value="/adInquiryReplyDelete", method = RequestMethod.POST)
	public String adInquiryReplyDeletePost(int reIdx, int inquiryIdx) {
		adminService.setInquiryReplyDelete(reIdx);	// 관리자가 답변글을 삭제했을때 처리루틴
		adminService.setInquiryUpdateAdmin2(inquiryIdx);
		return "";
	}
	
	// 관리자 원본글과 답변글 삭제처리
	@RequestMapping(value="/adInquiryDelete", method = RequestMethod.GET)
	public String adInquiryDeleteGet(Model model, int idx, String fSName, int reIdx, int pag) {
		adminService.setInquiryReplyDelete(reIdx);	// 관리자가 답변글을 삭제했을때 처리루틴
		inquiryService.setInquiryDelete(idx, fSName);
		model.addAttribute("pag", pag);
		return "redirect:/message/adInquiryDeleteOk";
	}

}
