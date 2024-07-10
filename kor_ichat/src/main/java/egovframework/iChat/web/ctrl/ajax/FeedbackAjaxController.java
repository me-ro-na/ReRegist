package egovframework.iChat.web.ctrl.ajax;

import egovframework.iChat.sportsApi.dto.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.iChat.common.model.CommandMap;
import egovframework.iChat.ichat.model.FeedbackHistoryVO;
import egovframework.iChat.ichat.service.PropertiesService;
import egovframework.iChat.web.service.FeedbackService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Controller
public class FeedbackAjaxController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackAjaxController.class);
	
	@Autowired
	FeedbackService feedbackService;
	
	@Autowired
	PropertiesService propertiesService;
	
	@RequestMapping(value = "/ajax/insertFeedbackHistory")
	@ResponseBody
	public String insertFeedbackHistroyAjax(@ModelAttribute("fVO") FeedbackHistoryVO fVO) throws Exception {
		LoginVO lVo = (LoginVO) RequestContextHolder.getRequestAttributes()
				.getAttribute("loginVO", RequestAttributes.SCOPE_SESSION);
		if (lVo != null && StringUtils.isNotBlank(lVo.getUserId())) {
			fVO.setUserId(lVo.getUserId());
		}
		LOGGER.debug("insertFeedbackHistory VO : " + fVO.toString());
		//String projectId = propertiesService.dmProjectId;
		//fVO.setProjectId(projectId);
		feedbackService.insertFeedbackHistory(fVO);
		return "";
	}
	
}
