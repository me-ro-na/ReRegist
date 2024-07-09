package egovframework.iChat.ichat.service;

import java.net.URI;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import egovframework.iChat.sportsApi.dto.LoginVO;
import egovframework.iChat.web.dao.SearchLogDao;
import egovframework.iChat.web.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import egovframework.iChat.common.exception.IChatException;
import egovframework.iChat.common.exception.IChatParsingException;
import egovframework.iChat.common.model.CommandMap;
import egovframework.iChat.common.service.CommonService;
import egovframework.iChat.common.util.RestTemplateConnectionPooling;
import egovframework.iChat.ichat.Intent.IntentType;
import egovframework.iChat.ichat.model.IChatResp;
import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.ichat.model.ResponseIchat;
import egovframework.iChat.ichat.model.ResultBuilderVO;
import egovframework.iChat.ichat.model.UploadImageVO;
import egovframework.iChat.ichat.vo.LangCodeType;
import egovframework.iChat.web.service.CachedService;
import egovframework.iChat.web.service.ChatLogService;
import egovframework.iChat.web.service.ConvertService;
import egovframework.iChat.web.service.FeedbackService;
import egovframework.iChat.web.service.FileService;
import lombok.extern.slf4j.Slf4j;
import egovframework.iChat.sf1.service.SearchService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Service("iChatService")
public class IChatService {

	@Autowired
	private PropertiesService propertiesService;

	@Autowired
	private ChatLogService chatLogService;

	@Autowired
	private ConvertService convertService;

	@Autowired
	private FileService fileService;
	
	@Autowired
	private FeedbackService feedbackService;
	/*@Autowired
	private IChatService iChatService;*/

	@Autowired
	private CachedService cachedService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SearchLogDao searchLogDao;

	private final ConcurrentHashMap<String, String> userSessionKeyMap = new ConcurrentHashMap<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(IChatService.class);

	public static <T1, T2> T1 getRestPost(String url, T2 obj, Class<T1> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> param = new HttpEntity<String>(obj.toString(), headers);
		RestTemplate restTemplate = new RestTemplate(RestTemplateConnectionPooling.getInstance().getRequestFactory());
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate.postForObject(URI.create(url), param, responseType);
	}

	/*
	 * 프로젝트 목록조회
	 */
	public String getDmApiProjectList() {
		String url = propertiesService.dmProtocol + propertiesService.dmIp + ":" + propertiesService.dmPort
				+ propertiesService.dmApiCommonProjectList;

		String param1 = "{ \"pageNum\" :1, \"countPerPage\" : 20, \"order\" : \"ASC\"}";

		JSONObject obj = new JSONObject();
		obj.put("pageNum", 1);
		obj.put("countPerPage", 20);
		obj.put("order", "ASC");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> param = new HttpEntity<String>(obj.toString(), headers);

		RestTemplate restTemplate = new RestTemplate(RestTemplateConnectionPooling.getInstance().getRequestFactory());
		String result = restTemplate.postForObject(URI.create(url), param, String.class);
		log.debug(result);
		return result;
	}

	/*
	 * 세션키 발급
	 */
	public String getDmApiCommonSessionRequest(String uniqueKey) {
		userSessionKeyMap.remove(uniqueKey);

		String url = propertiesService.dmProtocol + propertiesService.dmIp + ":" + propertiesService.dmPort
				+ propertiesService.dmApiCommonSessionRequest;
		String param = "{}";

		RestTemplate restTemplate = new RestTemplate(RestTemplateConnectionPooling.getInstance().getRequestFactory());
		ResponseIchat result = restTemplate.postForObject(URI.create(url), param, ResponseIchat.class);
		return result.getSessionKey();
	}

	/*
	 * 세션키 삭제
	 */
	public String delDmApiCommonSessionRequest(String uniqueKey) {
		userSessionKeyMap.remove(uniqueKey);

		String url = propertiesService.dmProtocol + propertiesService.dmIp + ":" + propertiesService.dmPort
				+ propertiesService.dmApiCommonSessionDelRequest;
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("sessionKey", uniqueKey);

		RestTemplate restTemplate = new RestTemplate(RestTemplateConnectionPooling.getInstance().getRequestFactory());
		ResponseIchat result = restTemplate.postForObject(URI.create(url), paramMap, ResponseIchat.class);

		return result.getStatus();
	}
	
	/*
	 * 세션키 삭제
	 */
	public int saveIntentFeedback(Map<String,Object> pMap) throws Exception {
		LoginVO lVo = (LoginVO) RequestContextHolder.getRequestAttributes()
				.getAttribute("loginVO", RequestAttributes.SCOPE_SESSION);
		if (lVo != null && StringUtils.isNotBlank(lVo.getUserId())) {
			pMap.put("userId", lVo.getUserId());
		}
		if(pMap.get("seq").toString().equals("-1")) {
			//답변 가져오기
			String url = propertiesService.dmProtocol + propertiesService.dmIp + ":" + propertiesService.dmPort
					+ propertiesService.dmApiCommonWiseIChatResponse;

			JSONObject obj = new JSONObject();
			
			obj.put("projectId", pMap.get("projectId").toString());
			obj.put("sessionKey", pMap.get("sessionKey").toString());
			obj.put("isDebug", true);
			obj.put("customKey", pMap.get("sessionKey").toString());
			Map<String, Object>  queryMap = convertService.selectQueryConvert(pMap);
			
			
			String convQuery = "";
			
			if(queryMap != null) {
				convQuery = (String) queryMap.get("CONV_RESULT");
			}

			if ("".equals(convQuery)) {
				obj.put("query", pMap.get("query").toString());
			} else {
				obj.put("query", convQuery);
			}

			IChatResp iChatResp = new IChatResp();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> param = new HttpEntity<String>(obj.toString(), headers);
			RestTemplate restTemplate = new RestTemplate(RestTemplateConnectionPooling.getInstance().getRequestFactory());
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

			iChatResp = restTemplate.postForObject(URI.create(url), param, IChatResp.class);
			
			String answer = iChatResp.getAnswer().toUpperCase();
			answer += "<br><br>";
			pMap.put("answer", answer);
			
			return feedbackService.insertIntentFeedback(pMap);
		}else {
			//XSS 처리
			String detail = pMap.get("detail").toString();
			pMap.put("detail", xssReplace(detail));
			
			return feedbackService.updateIntentFeedback(pMap);
		}
		
	}

	/*
	 * 세션키 Validation
	 */
	public String getDmApiCommonSessionValidation(String uniqueKey) {
		String url = propertiesService.dmProtocol + propertiesService.dmIp + ":" + propertiesService.dmPort
				+ propertiesService.dmApiCommonSessionValidation;

		log.debug("url >>>>>>>>>>>>>> " + url);
		Map<String, String> paramMap = new HashMap<>();
		if (userSessionKeyMap == null) {
			// userSessionKeyMap.remove(uniqueKey);
			paramMap.put("sessionKey", "");
		} else {
			userSessionKeyMap.putIfAbsent(uniqueKey, "");
			paramMap.put("sessionKey", userSessionKeyMap.get(uniqueKey));
		}

		log.debug("userSessionKeyMap :: userSessionKey: " + uniqueKey);

		RestTemplate srestTemplate = new RestTemplate(RestTemplateConnectionPooling.getInstance().getRequestFactory());
		ResponseIchat result = srestTemplate.postForObject(URI.create(url), paramMap, ResponseIchat.class);

		String userSessionKey = "";

		if (userSessionKeyMap != null) {
			userSessionKey = (result.getIsValid()) ? userSessionKeyMap.get(uniqueKey)
					: getDmApiCommonSessionRequest(uniqueKey);
			userSessionKeyMap.put(uniqueKey, userSessionKey);
		}

		log.debug("uniqueKey :: userSessionKey: " + uniqueKey + " :: " + userSessionKey);

		return userSessionKey;
	}

	/*
	 * 사용자질의에 대한 답변 요청
	 */
	public OutData getChatResponse(String projectId, String userQuery, String convQuery, String uniqueKey, Map<String, Object> logMap, boolean isMobile)
			throws IChatException, IChatParsingException, Exception {

		String userSessionKey = this.getDmApiCommonSessionValidation(uniqueKey);
		String url = propertiesService.dmProtocol + propertiesService.dmIp + ":" + propertiesService.dmPort
				+ propertiesService.dmApiCommonWiseIChatResponse;

		String tempAnswer = "";
		String intentNm = "";
		OutData output = new OutData();

		JSONObject obj = new JSONObject();

		obj.put("projectId", projectId);
		obj.put("sessionKey", userSessionKey);
		obj.put("isDebug", true);
		obj.put("customKey", userSessionKey);

		if ("".equals(convQuery)) {
			obj.put("query", userQuery);
		} else {
			obj.put("query", convQuery);
		}

		log.info("[input] ==>" + obj.toJSONString());
		IChatResp iChatResp = new IChatResp();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> param = new HttpEntity<String>(obj.toString(), headers);
		RestTemplate restTemplate = new RestTemplate(RestTemplateConnectionPooling.getInstance().getRequestFactory());
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

		iChatResp = restTemplate.postForObject(URI.create(url), param, IChatResp.class);

		// 인텐트 파싱
		intentNm = iChatResp.getResponse().get("topIntentName").toString().trim();
		intentNm = "".equals(intentNm) ? "Default_Fallback_Intent" : intentNm;

		output.setSearchKeyword((searchService.getIChatMorph((obj.get("query")).toString())));
		
		String responseType = iChatResp.getResponse().get("responseType").toString().trim();
		if ("DEFAULT_FALLBACK".equals(responseType)) {
			//makeSearchResult(iChatResp, output, userQuery);
		}
		
		String reformatAnswer = "";

		// dynamicAnswerProcess..
		Map<String, String> result = commonService.dynamicAnswerProcess(iChatResp.getAnswer(), "", LangCodeType.ko,
				output);
		reformatAnswer = result.get("contentForTemplate");

		if (reformatAnswer != "") {
			iChatResp.setAnswer(reformatAnswer);
		}

		if(isMobile && iChatResp.getAnswer().toUpperCase().indexOf("<MIMG>") != -1) {
			String imgAnswer = mImgTagChange(iChatResp.getAnswer());
			iChatResp.setAnswer(imgAnswer);
		}else if (iChatResp.getAnswer().toUpperCase().indexOf("<IMG>") != -1) {
			String imgAnswer = imgTagChange(iChatResp.getAnswer());
			iChatResp.setAnswer(imgAnswer);
		}
		
		
		if(iChatResp.getAnswer().toLowerCase().indexOf("<attach>") != -1) {
			String attachAnswer = attachTagChange(iChatResp.getAnswer());
			iChatResp.setAnswer(attachAnswer);
		}
		
		if (iChatResp.getAnswer().toUpperCase().indexOf("<A>") != -1) {
			String linkAnswer = linkTagChange(iChatResp.getAnswer());
			iChatResp.setAnswer(linkAnswer);
		}
		
		if (iChatResp.getAnswer().toUpperCase().indexOf("<ABTN>") != -1) {
			String linkAnswer = abtnTagChange(iChatResp.getAnswer());
			iChatResp.setAnswer(linkAnswer);
		}
		
		if(iChatResp.getAnswer().toUpperCase().indexOf("<AP>") != -1) {
			String telAnswer = apTagChange(iChatResp.getAnswer());
			iChatResp.setAnswer(telAnswer);
		}

		iChatResp.setAnswer(iChatResp.getAnswer().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>").trim());
		log.info("[IChatResp]############################################### " + url);
		log.info(iChatResp.toString());

		if ("error".equals(iChatResp.getStatus())) {
			throw new IChatException("ChatBot Response Error : " + iChatResp.getStatus());
		}

		output.setType(DataType.TALK); // default

		tempAnswer = iChatResp.getAnswer().toUpperCase();
		boolean isButton = isButtonAnswer(tempAnswer);
		boolean isRButton = isRButtonAnswer(tempAnswer);

		// 파라미터 VO
		IchatVO pVO = new IchatVO();
		IchatVO tempVo = new IchatVO();
		String subAnswer = "";
		List<ResultBuilderVO> listVO = new ArrayList<ResultBuilderVO>();

		if (!iChatResp.getResponse().get("responseType").toString().trim().equals("REQUESTION")) {
			pVO.setIntentNm(intentNm);
			pVO.setType("IN");
			pVO.setProjectId(projectId);
			listVO = convertService.selectResultMapByScenario(pVO);
		}
		
		// JSON 
		JSONParser parser = new JSONParser();
		JSONObject jo = new JSONObject();
		//JSONObject joResult = new JSONObject();
		JSONArray jaResult = new JSONArray();
		Gson gson = new Gson();

		if (listVO != null) {
			for(ResultBuilderVO builder : listVO) {
				jo = (JSONObject)parser.parse(gson.toJson(builder));
				jaResult.add(jo);
			}
			
			subAnswer = jaResult.toString();
		}else if(listVO == null && iChatResp.getAnswer().toUpperCase().indexOf("<SBTN>") != -1){

			Map<String,String> sbtnAnswer = STagChange(iChatResp.getAnswer(), intentNm);
			iChatResp.setAnswer(sbtnAnswer.get("answer"));
			subAnswer = sbtnAnswer.get("subAnswer");

		}

		if (isButton) {
			String type = "BTN";
			String requery = iChatResp.getResponse().get("responseType").toString().trim();
			String btnAnswer = getParamsForDynamicAnswer(tempAnswer, type, requery);
			makeButtonResult(iChatResp, output, btnAnswer, type);
		} else if (isRButton) {
			String type = "RBTN";
			String btnAnswer = getParamsForDynamicAnswer(tempAnswer, type, "");
			makeButtonResult(iChatResp, output, btnAnswer, type);
		} else {
			makeChatResult(iChatResp, output);
		}
		output.setSubAnswer(subAnswer);
		
		String categoryNm = (String) iChatResp.getResponse().get("categoryName");
		pVO.setIntentNm(intentNm);
		pVO.setProjectId(projectId);
		ResultBuilderVO scenarioId = cachedService.selectScenarioId(pVO);
		if(iChatResp.getResponse().get("categoryName").equals("") || iChatResp.getResponse().get("categoryName").equals("NONE")) {
			categoryNm = "undefined";
		}
		
		ChatResult reResult =  output.getResult();
		reResult.setIntentNm(intentNm);
		
		// 로그 등록
		logMap.put("logQuery", userQuery);
		logMap.put("logAnswer", output.getResult().getMessage());
		logMap.put("logIntentName", intentNm);
		logMap.put("logCategoryName", categoryNm);
		logMap.put("projectId", projectId);

		chatLogService.insertDetailLog(logMap);
		if(scenarioId!=null) {
			logMap.put("scenarioId", scenarioId.getScenarioId());
			chatLogService.insertScenarioLog(logMap);
			reResult.setMainUserQuery(scenarioId.getMainUserQuery());
		}
		
		output.setResult(reResult);
		return output;
	}

	public boolean isButtonAnswer(String answer) {
		boolean result = false;
		if (answer.toUpperCase().contains("<BTN>")) {
			result = true;
		}

		return result;
	}

	public boolean isRButtonAnswer(String answer) {
		boolean result = false;
		if (answer.toUpperCase().contains("<RBTN>")) {
			result = true;
		}

		return result;
	}


	
	public String attachTagChange(String answer) {
		Pattern p = Pattern.compile("<attach>(.*?)</attach>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(answer);
		String Aval="";
		// 이미지 값 있을 시 변환
		//fnDoFileDownload('a8fa9380-a9f4-4ab1-a307-e0edf397914b.html');
		try {
			while(m.find()) {
				Aval =m.group().replaceAll("(?i)<attach>", "").replaceAll("(?i)</attach>", "");
				FileVO fVO = fileService.selectAttachFileDetail(Aval);
				String attachBtn = "";
				attachBtn += "<a href=\"javascript:;\" class=\"btn_inner link\" title=\"" + fVO.getTitle() + " 다운로드\" onclick=\"fileDownload('" + fVO.getSaveFileName() + "');\">"+ fVO.getTitle()+"다운로드</a>";
			
			
//				
				answer = answer.replace("<attach>"+Aval+"</attach>", attachBtn);
			}
		} catch (NullPointerException e) {
			LOGGER.error("[ERROR] Exception : IChatService.attachTagChange() -> can't get attach File");			
		} catch (PersistenceException e) {
			LOGGER.error("[ERROR] Exception : IChatService.attachTagChange()");			
		}
		
		return answer;
		
	}
	
/*
	public boolean isPhoneAnswer(String answer) {
		boolean result = false;
		if (answer.contains("<PHONE>")) {
			result = true;
		}

		return result;
	}
*/
	/* 동적답변 여부 체크 */
	public boolean isDynamicAnswer(String answer) {
		boolean result = false;
		if (answer.contains("♠") && answer.contains("♠")) {
			result = true;
		}
		return result;
	}

	// im tag 이미지 변경
	public String imgTagChange(String answer) {
		System.out.println("imgTag ::::: " + answer);
		Pattern p = Pattern.compile("<IMG>(.*?)</IMG>",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(answer);
		String imgval = "";
		// String strResult = new String("");
		UploadImageVO pVO = new UploadImageVO();
		UploadImageVO rVO = new UploadImageVO();

		// 이미지 값 있을 시 변환
		try {
			while (m.find()) {

				imgval = m.group().replaceAll("(?i)<IMG>", "").replaceAll("(?i)</IMG>", "");
				pVO.setImageId(imgval);
				rVO = convertService.selectUploadImage(pVO);
				if (rVO != null) {

					LOGGER.debug("[CHECK] strResult : " + rVO.getImageFileservername());
					answer = answer.replace(imgval, rVO.getImageFileservername() + "^" + rVO.getImageAttribute());
				}

			}
		} catch (NullPointerException e) {
			LOGGER.error("[ERROR] Exception : NullPointerException");
		} catch (PersistenceException e) {
			LOGGER.error("[ERROR] Exception : Exception");
		}
		
		Pattern mp = Pattern.compile("<MIMG>(.*?)</MIMG>",Pattern.CASE_INSENSITIVE);
		Matcher mm = mp.matcher(answer);
		// IMG 내용 삭제
		try {
			while (mm.find()) {
				answer = answer.replace( mm.group(), "");
			}
		} catch (NullPointerException e) {
			LOGGER.error("[ERROR] Exception : NullPointerException");
		} catch (PersistenceException e) {
			LOGGER.error("[ERROR] Exception : Exception");
		}

		return answer;
	}

	// 모바일용 img tag 이미지 변경
		public String mImgTagChange(String answer) {
			Pattern p = Pattern.compile("<IMG>(.*?)</IMG>",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(answer);
			String imgval = "";
			// IMG 내용 삭제
			try {
				while (m.find()) {
					imgval = m.group();
					answer = answer.replace(imgval, "");
				}
			} catch (NullPointerException e) {
				LOGGER.error("[ERROR] Exception : NullPointerException");
			} catch (PersistenceException e) {
				LOGGER.error("[ERROR] Exception : Exception");
			}
			
			answer = answer.replaceAll("(?i)<MIMG>", "<IMG>").replaceAll("(?i)</MIMG>", "</IMG>");
			
			return imgTagChange(answer);
		}
		
	// link tag (a tag url형)
		public String linkTagChange(String answer) {
			Pattern p = Pattern.compile("<A>.*</A>",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(answer);
			
			String linkval = "";
			try {
				while (m.find()) {
					linkval = m.group().replaceAll("(?i)<A>", "").replaceAll("(?i)</A>", "");
					
					// jsp 태그에 맞게 수정해야하는 부분
					String temp_link = "";
					//for (int i = 0; i < btnList.length; i++) {
						//String[] btnOption = btnList[i].trim().split("\\^");
						temp_link += "<a href=\"javascript:;\" onclick=goLink(\"" + linkval
								+ "\"); return false; style=\"color:blue\">" + linkval + "</a>";
						
//						temp_link += "<a class=\"btn btn_block link\" onclick=goLink(\"" + btnOption[1]
//								+ "\"); return false;>" + btnOption[0] + "<i class=\"fas fa-angle-right\"></i></a>";
					//}
					answer = answer.replace(m.group(), temp_link);
				}
			} catch (NullPointerException e) {
				LOGGER.error("[ERROR] NullPointerException");
			} catch (PersistenceException e) {
				LOGGER.error("[ERROR] Exception");
			}
			
			return answer;
		}
	
	// a tag 버튼형
		public String abtnTagChange(String answer) {
			Pattern p = Pattern.compile("<ABTN>.*</ABTN>",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(answer);
			String linkval = "";
			
			try {
				while (m.find()) {
					linkval = m.group().replaceAll("(?i)<ABTN>", "").replaceAll("(?i)</ABTN>", "");

					String[] btnList = linkval.split("\\|");
					
					// jsp 태그에 맞게 수정해야하는 부분
					String temp_link = "";
					for (int i = 0; i < btnList.length; i++) {
						String[] btnOption = btnList[i].trim().split("\\^");
//						temp_link += "<a href=\"" + btnOption[1]
//								+ "\"; style=\"color:blue\">" + btnOption[0] + "</a>";
						
						//temp_link += "<a class=\"btn btn_block link\" onclick=goLink(\"" + btnOption[1]
						//		+ "\"); return false;>" + btnOption[0] + "<i class=\"fas fa-angle-right\"></i></a>";
						
						temp_link += "<a href=\"javascript:;\" class=\"btn_inner link\" onclick=goLink(\"" + btnOption[1]
								+ "\"); return false; title=\"사이트 바로가기\">" + btnOption[0] + "</a>";
					}
					answer = answer.replace(m.group(), temp_link);
					
				}
			} catch (NullPointerException e) {
				LOGGER.error("[ERROR] NullPointerException");
			} catch (PersistenceException e) {
				LOGGER.error("[ERROR] Exception");
			}

			return answer;
		}
	
	// AP tag 변경
	public String apTagChange(String answer) {
		Pattern p = Pattern.compile("<AP>.*</AP>",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(answer);
		String linkval = "";
		// 이미지 값 있을 시 변환
		try {
			while(m.find()) {
				linkval =m.group().replaceAll("(?i)<AP>", "").replaceAll("(?i)</AP>", "");
				String[] btnList = linkval.split("\\|");
				
				StringBuffer temp_link = new StringBuffer();
				for (int i = 0; i < btnList.length; i++) {
					temp_link.append("<a class=\"call\" href=\"tel:").append(btnList[i]).append("\" title=\"").append(btnList[i]).append(" 전화걸기\">").append(btnList[i]).append("</a>");
				}
				answer = answer.replace(m.group(), temp_link.toString());
			}
		} catch (NullPointerException e) {
			LOGGER.error("[ERROR] Exception : IChatService.apTagChange() -> NullPointerException");			
		} catch (PersistenceException e) {
			LOGGER.error("[ERROR] Exception : IChatService.apTagChange() -> Exception");			
		}
			
		return answer;
	}
	

	public String FTagChange(String answer) {
		// System.out.println("in!!");
		Pattern p = Pattern.compile("<FBTN>.*</FBTN>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(answer);
		String fval = "";

		try {
			while (m.find()) {
//				System.out.println("start!!");
				fval = m.group().replaceAll("(?i)<FBTN>", "").replace("(?i)</FBTN>", "");
//				System.out.println("btn::" + fval);
				String[] btnList = fval.split("\\|");
				String temp_link = "";
				for (int i = 0; i < btnList.length; i++) {
					String[] btnOption = btnList[i].trim().split("\\^");
					temp_link += "<a href=\"javascript:;\" class=\"btn\" onclick=showButton(); return false;>" + btnOption[0] + "</a>";
					temp_link += "<div class=\"txt font\" style=\"display:none;\">" + btnOption[1] + "</div>";
				}
				answer = answer.replace(m.group(), temp_link);
			}
		} catch (NullPointerException e) {
			LOGGER.error("[ERROR] Exception : NullPointerException");
		} catch (PersistenceException e) {
			LOGGER.error("[ERROR] Exception : Exception");
		}

		return answer;
	}

	/* 
		답변 내 시나리오 버튼(<SBTN></SBTN>) 태그 파싱
		<SBTN>버튼명^질의문|버튼명^질의문...</SBTN>
	*/
	public Map<String,String> STagChange(String answer, String intentNm) {
		// System.out.println("in!!");
		answer = answer.replaceAll("(?i)<br><SBTN>", "<SBTN>");
		Pattern p = Pattern.compile("<SBTN>.*</SBTN>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(answer);
		String fval = "";
		Map<String, String> result = new HashMap<String, String>();
		JSONObject jsonObject = new JSONObject();
		//wise_resultmap -> BUILD_CONTENT 필드 데이터 생성, {"intentName" : "intentName", "dataArray" : []}
		JSONArray dataArrayValueList = new JSONArray();
		try {
			while (m.find()) {
				//				System.out.println("start!!");
				fval = m.group().replaceAll("(?i)<SBTN>", "").replaceAll("(?i)</SBTN>", "");
				//				System.out.println("btn::" + fval);
				String[] btnList = fval.split("\\|");
				
				for (int i = 0; i < btnList.length; i++) {
					String[] btnOption = btnList[i].trim().split("\\^");
					JSONObject btnOpt = new JSONObject();
					btnOpt.put("parent", intentNm);
					btnOpt.put("ref", "");
					btnOpt.put("imageId", "");
					btnOpt.put("name", btnOption[0]);
					btnOpt.put("linkType", "IL");
					btnOpt.put("mainUserQuery", btnOption[1]);
					btnOpt.put("key", "");
					btnOpt.put("dispType", "BB");
					dataArrayValueList.add(btnOpt);
				}
				answer = answer.replace(m.group(), "");
			}
			jsonObject.put("intentName", intentNm);
			jsonObject.put("dataArray", dataArrayValueList);
		} catch (NullPointerException e) {
			LOGGER.error("[ERROR] Exception : NullPointerException");
		} catch (PersistenceException e) {
			LOGGER.error("[ERROR] Exception : Exception");
		}
		result.put("answer", answer);
		result.put("subAnswer", jsonObject.toJSONString());
		return result;
	}

	public String getParamsForDynamicAnswer(String dynamicAnswer, String type, String requry) {
		int startFilterParamIndex = 0;
		int endFilterParamIndex = 0;
		int index = 0;
		String getParam = "";
		String answer = dynamicAnswer.toUpperCase();
		// dynamicAnswer = dynamicAnswer.toUpperCase();

		// params..
		// if(dynamicAnswer.contains("<LBTN>")) {
		startFilterParamIndex = answer.indexOf("<" + type + ">");
		endFilterParamIndex = answer.lastIndexOf("</" + type + ">");

		if (type.toUpperCase().equals("BTN")) {
			index = 5;
		} else if (type.toUpperCase().equals("A")) {
			index = 3;
		} else {
			index = 6;
		}

		getParam = dynamicAnswer.substring(startFilterParamIndex + index, endFilterParamIndex); // 숫자 2를 체크하세요 -> ♠
																								// 중괄호2개

		if (requry.equals("REQUESTION")) {
			getParam += "|처음으로 가기^newsession";
		}
		log.debug("getParam: " + getParam);
		// }

		return getParam;
	}

	// 인텐트 조회
	public IntentType getIntentType(String intent) {
		IntentType intentType = null;
		for (IntentType type : IntentType.values()) {
			if (type.getName().contains(intent)) {
				intentType = type;
				return intentType;

			}
		}
		return intentType;
	}

	// 일상Talk result
	public void makeChatResult(IChatResp response, OutData output) {
		ChatResult result = new ChatResult();
		String answer = response.getAnswer();
		/*if (answer.contains("♣")) {
			int replaceIdx = answer.indexOf("♣");
			result.setMessage(replaceIdx > 0 ? answer.substring(0, replaceIdx) : answer);
		} else {
			result.setMessage(response.getAnswer());
		}*/
		result.setMessage(response.getAnswer());

		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());
		result.setNodeType(IntentType.CommonTalk);
		result.setTimeStamp(timeStamp);
		output.setResult(result);
	}

	/*
	 * 링크도 버튼으로 통합 됨 "^"값으로 구분이 되며 IN 인경우 챗봇으로 값을 보냄 OUT 인경우 새창을 띄워서 해당 URL을 오픈함
	 * ♠버튼: 버튼명1^값1^IN | 버튼명2^값2^IN | ... ♠ ♠버튼: 버튼명1^값1^IN | 링크명1^URL1^OUt | ... ♠
	 */
	public void makeButtonResult(IChatResp response, OutData output, String params, String type) throws Exception {
		JSONParser parser = new JSONParser();
		ChatResult result = new ChatResult();

		String[] btnList = params.split("\\|");
		Option[] optionList = new Option[btnList.length];
		for (int i = 0; i < btnList.length; i++) {

			Option option = new Option();
			option.setId(Integer.toString(i));
			String[] btnOption = btnList[i].trim().split("\\^");
			option.setLabel(btnOption[0].trim());
			option.setValue(btnOption[1].trim());
			// option.setType (btnOption[2].trim().toUpperCase()); // IN:챗봇으로 , OUT: 새창으로
			option.setType(type);
			option.setOrder(Integer.toString(i));
			optionList[i] = option;

		}
		result.setOptionList(optionList);

		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());
		result.setNodeType(IntentType.BUTTON);
		result.setTimeStamp(timeStamp);
		String answer = response.getAnswer();
		int replaceIdx = answer.toUpperCase().indexOf("<" + type + ">");

		result.setMessage(replaceIdx > 0 ? answer.substring(0, replaceIdx) : answer);

		output.setResult(result);

	}

	public void makeSearchResult(IChatResp response, OutData output, String userQuery) throws Exception {
		output.setResult(new ChatResult());
//		List<Map> searchList = searchService.getSearchResult("sample_bbs", userQuery, output);
//		String resMsg = output.getResult().getMessage();
		String resMsg = "제가 답하기 어려운 질문이네요." 
				+ "<br>보다 자세한 문의를 원하시면 아래 링크에서 연락처를 확인하신 후 연락해주세요." 
				+ "<br>또는 상세한 검색을 원하시면 검색 버튼을 클릭하여 직접 검색해 보실 수 있어요."
				+ "<abtn>전화번호 안내 바로가기^https://g1.sports.or.kr/guide/contactList.do</abtn>";
//		if (searchList != null && !searchList.isEmpty()) {
			String keyword = output.getSearchKeyword();
			String btnName = "스포츠 지원포털 검색 > " + userQuery;
			String linkUrl = "https://portal.sports.or.kr/search/result.do?searchQuery=" + keyword;
			resMsg += "<a href=\"javascript:;\"" +
					  " class=\"btn_inner link\"" +
					  " onclick=\"goLink('" + linkUrl + "'); saveSearchLog('" + userQuery + "'); return false;\"" +
					  " title=\"통합검색 바로가기\">" + btnName + "</a>";
//		}
		if (StringUtils.isNotBlank(resMsg)) {
			response.setAnswer(resMsg);
		}
	}
	
	public static String xssReplace(String param) {
		
		param = param.replaceAll("&", "&amp;");
		param = param.replaceAll("\"", "&quot;");
		param = param.replaceAll("'", "&apos;");
		param = param.replaceAll("<", "&lt;");
		param = param.replaceAll(">", "&gt;");
		param = param.replaceAll("\r", "<br>");
		param = param.replaceAll("\n", "<p>");

		return param;
	}
	
	public void saveLogSearch(Map<String, Object> sMap) throws Exception {
		LOGGER.info(sMap.toString());

		searchLogDao.insertLogSearch(sMap);

	}

}
