package egovframework.iChat.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.ichat.Intent.IntentType;
import egovframework.iChat.ichat.vo.LangCodeType;
import egovframework.iChat.web.service.ChatLogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DynamicAnswerTemplateService {
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ChatLogService chatLogService;

	
	public Map<String,String> getDynamicAnswerTemplate(String staticDescription, String[] getParams, String[] getContents, String link, LangCodeType userLangCode) {
		Map<String,String> result = new HashMap<>();
		Map<String, Object> logMap = new HashMap<String, Object>();
		
		String intent = getParams[0].trim();
		log.debug("intent: " + intent);
		
		String[] mcategorynmArray = {};
		IntentType paramIntentType = commonService.getIntentType(intent);
		logMap.put("intentName", intent);
		logMap.put("apiName", paramIntentType);

		return result;
	}
}


















