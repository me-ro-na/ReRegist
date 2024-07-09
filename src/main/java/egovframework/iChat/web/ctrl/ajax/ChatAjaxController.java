package egovframework.iChat.web.ctrl.ajax;


import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import egovframework.iChat.sportsApi.dto.LoginVO;
import egovframework.iChat.sportsApi.service.SportsDbService;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.iChat.common.exception.IChatException;
import egovframework.iChat.common.exception.IChatParsingException;
import egovframework.iChat.common.model.CommandMap;
import egovframework.iChat.common.util.AES256Util;
import egovframework.iChat.common.util.SequenceUtil;
import egovframework.iChat.ichat.Intent.IntentType;
import egovframework.iChat.ichat.service.IChatService;
import egovframework.iChat.ichat.service.PropertiesService;
import egovframework.iChat.sf1.service.SearchService;
import egovframework.iChat.web.model.ChatResult;
import egovframework.iChat.web.model.OutData;
import egovframework.iChat.web.service.ChatLogService;
import egovframework.iChat.web.service.ConvertService;
import egovframework.iChat.web.service.ImageService;

@Controller
public class ChatAjaxController extends AjaxController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatAjaxController.class);

	@Autowired
	IChatService iChatService;
	
	@Autowired
	ImageService imageService; 
	
	@Autowired
	ChatLogService chatLogService;

	@Autowired
	ConvertService convertService; 
	
	@Autowired 
	PropertiesService propertiesService;
	
	@Autowired
	SearchService searchService;

	@Autowired
	private Environment environment;

	@Autowired
	SportsDbService sportsDbService;


	public static String randomHangulName() {
		List<String> lNm = Arrays.asList("김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서", "신",
				"권", "황", "안", "송", "류", "전", "홍", "고", "문", "양", "손", "배", "조", "백", "허", "유", "남", "심",
				"노", "정", "하", "곽", "성", "차", "주", "우", "구", "신", "임", "나", "전", "민", "유", "진", "지", "엄",
				"채", "원", "천", "방", "공", "강", "현", "함", "변", "염", "양", "변", "여", "추", "노", "도", "소", "신",
				"석", "선", "설", "마", "길", "주", "연", "방", "위", "표", "명", "기", "반", "왕", "금", "옥", "육", "인",
				"맹", "제", "모", "장", "남", "탁", "국", "여", "진", "어", "은", "편", "구", "용");
		List<String> fNm = Arrays.asList("가", "강", "건", "경", "고", "관", "광", "구", "규", "근", "기", "길", "나", "남",
				"노", "누", "다", "단", "달", "담", "대", "덕", "도", "동", "두", "라", "래", "로", "루", "리", "마", "만",
				"명", "무", "문", "미", "민", "바", "박", "백", "범", "별", "병", "보", "빛", "사", "산", "상", "새", "서",
				"석", "선", "설", "섭", "성", "세", "소", "솔", "수", "숙", "순", "숭", "슬", "승", "시", "신", "아", "안",
				"애", "엄", "여", "연", "영", "예", "오", "옥", "완", "요", "용", "우", "원", "월", "위", "유", "윤", "율",
				"으", "은", "의", "이", "익", "인", "일", "잎", "자", "잔", "장", "재", "전", "정", "제", "조", "종", "주",
				"준", "중", "지", "진", "찬", "창", "채", "천", "철", "초", "춘", "충", "치", "탐", "태", "택", "판", "하",
				"한", "해", "혁", "현", "형", "혜", "호", "홍", "화", "환", "회", "효", "훈", "휘", "희", "운", "모", "배",
				"부", "림", "봉", "혼", "황", "량", "린", "을", "비", "솜", "공", "면", "탁", "온", "디", "항", "후", "려",
				"균", "묵", "송", "욱", "휴", "언", "령", "섬", "들", "견", "추", "걸", "삼", "열", "웅", "분", "변", "양",
				"출", "타", "흥", "겸", "곤", "번", "식", "란", "더", "손", "술", "훔", "반", "빈", "실", "직", "흠", "흔",
				"악", "람", "뜸", "권", "복", "심", "헌", "엽", "학", "개", "롱", "평", "늘", "늬", "랑", "얀", "향", "울",
				"련");
		Collections.shuffle(lNm);
		Collections.shuffle(fNm);
		return lNm.get(0) + fNm.get(0) + fNm.get(1);
	}

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final SecureRandom random = new SecureRandom();

	public static String generateRandomId(int length) {
		StringBuilder randomId = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			randomId.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}
		return randomId.toString();
	}

	@RequestMapping(value = "/ajax/setLogin")
	@ResponseBody
	public Object setLogin(@RequestBody LoginVO reqSso) throws Exception {
		if (Arrays.stream(environment.getActiveProfiles()).anyMatch(e->"local".equals(e) || "dev".equals(e))) {
			if (StringUtils.equals(reqSso.getUserId(), "luon12")) {
				reqSso.setCi("Ww7HMxS4siPlt0IUtgOu8AX7y/23jvTY7FYo8eF5FO7IJ9ftfwVyMjIQ8bZSPEG06iooOjogTffywjrjI56nBA==");
			} else if (StringUtils.equals(reqSso.getUserId(), "psgaca")) {
				reqSso.setCi("g+2zzLkL15ORWzGSlASVcqhlObjRf187hPOwlUQ3H2dgqzfSVR1T+I8JkmD/M+W/lKqGgpYA9BykSnGX6xplQA==");
			} else if (StringUtils.equals(reqSso.getUserId(), "subina")) {
				reqSso.setCi("HKVegnVs6mT1zwe+tVkbOa3xEqD20NwcrQ+zY6E2B1ebH6i5EZiOsRm2v6d/Ch8w/bFsNg+iT6kw5XgzpKhAfA==");
			} else {
				reqSso.setCi(generateRandomId(25) + "/" + generateRandomId(60) + "==");
			}
			LoginVO sso = LoginVO.builder()
					.userId(reqSso.getUserId())
					.sessionId(reqSso.getSessionId())
					.ci(reqSso.getCi())
					.userName(randomHangulName())
					.birthDt(reqSso.getBirthDt())
					.genderCode("1")
					.ipinVno(" ")
					.certMethod("IPIN")
					.mobileNo("01000001234")
					.nationalInfo("0")
					.targetUrl("/ichat/iChat.do")
					.build();
			sportsDbService.makeSso(sso);
		}

		return true;
	}

	@ResponseBody
	@RequestMapping(value="/auth/makeSso.do", method = RequestMethod.POST)
	public Map<String, Object> authMakeSSoAjax (
			@RequestBody  Map<String, String>  vo,
			HttpServletRequest request) throws Exception {

		Map<String, Object> jsonList = new HashMap<String, Object>();
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String channel = (String)request.getSession().getAttribute("channel");
		String adaptor = "", targetUrl = "";

		try {

			if(loginVO==null) {
				jsonList.put("resultCode", "3000200");
				jsonList.put("resultMsg", "세션이 만료되었습니다. 로그인 후 사용할 수 있습니다.");
				return jsonList;
			}
			loginVO.setSessionId(request.getSession().getId().split("\\.")[0]);
			if (vo != null) {
				if ("P".equals(vo.get("sysGb"))) {
					// 스포츠지원포털
					adaptor = propertiesService.apiUrlG1Adaptor;
					if (StringUtils.equals(vo.get("gubun"), "REG_RES")) {
						targetUrl = propertiesService.apiUrlG1TargetRegRes;
					} else if (StringUtils.equals(vo.get("gubun"), "CERT")) {
						// 내 생애주기 - 증명서 발급신청이력
						targetUrl = propertiesService.apiUrlG1TargetCert;
					} else {
						// 내 생애주기 - 선수실적
						targetUrl = propertiesService.apiUrlG1Target;
					}

					loginVO.setTargetUrl(targetUrl);
				} else if ("PINFO".equals(vo.get("sysGb"))) {
					// 경기인등록 시스템
					if ("MO".equals(channel)) {
						// 모바일
						adaptor = propertiesService.apiUrlPinfoAdaptorMobile;
					} else {
						// 데스크탑
						adaptor = propertiesService.apiUrlPinfoAdaptorWeb;
					}
					// 종목
					if (StringUtils.isNotBlank(vo.get("classCd"))) {
						loginVO.setPclassCd(vo.get("classCd"));
					} else {
						throw new IllegalArgumentException("classCd");
					}
					// 경기인 유형
					if(StringUtils.isNotBlank(vo.get("gubun"))){
						targetUrl = getRegistUrl(vo.get("gubun"));
					} else {
						throw new IllegalArgumentException("gubun");
					}

					loginVO.setTargetUrl(targetUrl);
				} else if ("HNR".equals(vo.get("sysGb"))) {
					// 훈·포장 시스템
					adaptor = propertiesService.apiUrlHnrAdaptor;
					targetUrl = propertiesService.apiUrlHnrTarget;
					loginVO.setTargetUrl(targetUrl);
				} else {
					throw new IllegalArgumentException("sysGb");
				}
			} else {
				throw new IllegalArgumentException();
			}

			sportsDbService.makeSso(loginVO);

			jsonList.put("resultCode", "0000000");
			jsonList.put("resultMsg", "");
			jsonList.put("targetUrl", adaptor);
			jsonList.put("ssid", loginVO.getSessionId());

		} catch(PersistenceException e) {
			jsonList.put("resultCode", "70005000");
			jsonList.put("resultMsg", "sso처리 중 오류가 발생했습니다.");
		}

		return jsonList;
	}

	@RequestMapping(value = "/ajax/setLogout")
	@ResponseBody
	public Object setLogout(HttpServletRequest request, HttpSession session) {
		session.invalidate();
		HttpSession newSession = request.getSession(true);
		return newSession.getId();
	}

	/*
	 * 전체 project의 이름과 상세 설명을 조회하기 위한 API
	 */
	@RequestMapping(value = "/ajax/getProjectList")
	@ResponseBody
	public Object getProjectList(CommandMap commandMap, HttpServletRequest request) throws Exception {

		String projectList = iChatService.getDmApiProjectList();
		LOGGER.info("projectList : " + projectList);

		return makeResponse(commandMap, projectList);
	}

	@RequestMapping(value = "/ajax/getApiSession")
	@ResponseBody
	public Object getApiSession(CommandMap commandMap, HttpServletRequest request) throws Exception {
		LOGGER.info("----------------------------->");
		LOGGER.info(commandMap.toString());

		String sessionKey = null;
		HttpSession session = request.getSession();
		if (session.getAttribute("userSessionKey") != null) {
			if(iChatService.getDmApiCommonSessionValidation((String) session.getAttribute("userSessionKey"))==null){
				sessionKey = "NULL";
			} else {
				sessionKey = iChatService.getDmApiCommonSessionValidation((String) session.getAttribute("userSessionKey"));
			}
		} else {
			sessionKey = iChatService.getDmApiCommonSessionValidation("NULL");
			if(sessionKey==null){
				session.setAttribute("userSessionKey", "NULL");
			} else {
				session.setAttribute("userSessionKey", sessionKey);
			}
		}

		LOGGER.info("sessionKey : " + sessionKey);
		LOGGER.info("----------------------------->");
		return makeResponse(commandMap, sessionKey);
	}
	
	@RequestMapping(value = "/ajax/delApiSession")
	@ResponseBody
	public Object delApiSession(CommandMap commandMap, HttpServletRequest request) throws Exception {
		LOGGER.info("----------------------------->");
		LOGGER.info(commandMap.toString());
		String sessionKey = request.getParameter("sessionKey");
		
		sessionKey = iChatService.delDmApiCommonSessionRequest(sessionKey);
		
		return makeResponse(commandMap, sessionKey);
	}
	
	@RequestMapping(value = "/ajax/saveIntentFeedback")
	@ResponseBody
	public Object saveIntentFeedback(CommandMap commandMap, HttpServletRequest request) throws Exception {
		LOGGER.info("[saveIntentFeedback]----------------------------->");
		LOGGER.info(commandMap.toString());
		String sessionKey = "";
		String projectId = commandMap.get("projectId").toString();
		String feedbackNo = "";
		int result = 0;
		
		HttpSession session = request.getSession();
		if(session.getAttribute("userSessionKey")==null){
			sessionKey = "";
		} else {
			sessionKey = (String) session.getAttribute("userSessionKey");
			
			if(!sessionKey.equals("")) {
				Map<String, Object> pMap = new HashMap<String, Object>();
				pMap.put("intent", commandMap.get("intent"));
				pMap.put("score", commandMap.get("score"));
				pMap.put("seq", commandMap.get("seq"));
				pMap.put("query", commandMap.get("query"));
				pMap.put("detail", commandMap.get("detail"));
				pMap.put("sessionKey", sessionKey);
				pMap.put("projectId", projectId);
				
				result = iChatService.saveIntentFeedback(pMap);
				if(result == 1) {
					feedbackNo = pMap.get("seq").toString();
				}
			}
		}
		
		return makeResponse(commandMap, feedbackNo);
	}


	@RequestMapping(value = "/ajax/getAnswer")
	@ResponseBody
	public Object getAnswer(CommandMap commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("[getAnswer]----------------------------->");
		LOGGER.info(commandMap.toString());
		String sessionKey = "";
		String projectId = commandMap.get("projectId").toString();
		
		HttpSession session = request.getSession();
		if(session.getAttribute("userSessionKey")==null){
			sessionKey = null;
		} else {
			sessionKey = (String) session.getAttribute("userSessionKey");
		}
		
		OutData outdata = new OutData();
		try {
			
			LOGGER.info("[LOG_SEQ]11___session logSeq : ===============["+session.getAttribute("logSeq")+"]");
			
			if(session.getAttribute("logSeq") == null || "".equals(session.getAttribute("logSeq"))) {
				LOGGER.error("IChatException Error: session.getAttribute (logSeq) is null");			
				StringBuffer rsJsonBuf = new StringBuffer();
				rsJsonBuf.append("저와 마지막으로 대화하신 후 시간이 많이 지났어요. 대화를 종료 하시겠어요?<br/>");
				rsJsonBuf.append("<div class=\"btn_group w_half\">");
				rsJsonBuf.append("<button type=\"button\" class=\"btn\" onclick=\"ichatReload();\">새로고침</button>");
				rsJsonBuf.append("<button type=\"button\" class=\"btn\" onclick=\"finish();\">종료</button>");
				rsJsonBuf.append("</div>");

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

				return makeErrorResponse(commandMap, rsJsonBuf.toString());
			}
			
			String logSeq = session.getAttribute("logSeq") == null? "null":(String) session.getAttribute("logSeq");
			Map<String, Object> logMap = new HashMap<String, Object>();
			LoginVO loginVO = (LoginVO) session.getAttribute("loginVO");
			String userId = null;
			if (loginVO != null) {
				userId = loginVO.getUserId();
			}
			logMap.put("logSessionKey", sessionKey);
			logMap.put("logUser",  logSeq);
			logMap.put("logUserId", userId);
			logMap.put("logIp", request.getRemoteAddr());
			logMap.put("type", session.getAttribute("type"));
			
			String rawQuery = (String) commandMap.get("query");
			commandMap.put("projectId", projectId);
			commandMap.put("userId", userId);
			Map<String, Object>  queryMap = convertService.selectQueryConvert(commandMap.getMap());
			
			//모바일 확인
			//String userAgent = request.getHeader("User-Agent").toUpperCase();
			boolean isMobile =  Boolean.valueOf(commandMap.get("isMobile").toString());
		    //if(userAgent.indexOf("MOBILE") > -1) {
		    	//isMobile = true;
		    //}
			
			if(queryMap != null) {
				String convQuery = (String) queryMap.get("CONV_RESULT");
				LOGGER.info("raw Query : " + rawQuery + " -->  converted Query : " + convQuery);
				//logMap.put("logConvQuery", convQuery);
				outdata = iChatService.getChatResponse(projectId, rawQuery, convQuery, sessionKey, logMap, isMobile);
			}else {
				//logMap.put("logConvQuery", "");
				outdata = iChatService.getChatResponse(projectId, rawQuery, "",sessionKey, logMap, isMobile);
			}
		} catch (IChatException ie) {
			
			LOGGER.error("IChatException Error:" +  ie.getMessage());			
			//ichat
			String rsJson = "챗봇에 일시적인 장애가 발생하였습니다.<br>반복적인 오류가 발생하면 관리자에게 문의해 주세요.";
			return makeErrorResponse(commandMap, rsJson);
			
		} catch (IChatParsingException pe) {
			
			LOGGER.error("IChatParsingException Error:" +  pe.getMessage());			
			//파싱에러
			String rsJson = "챗봇에 치명적인 파싱오류가 발생되었습니다.<br>반복적인 오류가 발생하면 관리자에게 문의해 주세요.";
			return makeErrorResponse(commandMap, rsJson);
			
		}
		return makeResponse(commandMap, outdata);
	}
	
	
	/**
	 * 이미지 리스트
	 */
	@RequestMapping(value = "/ajax/getImage")
	@ResponseBody
	public Object getImage(CommandMap commandMap, HttpServletRequest request) throws Exception {
		String projectId = commandMap.get("projectId").toString();
		List<String> imgList = imageService.imageList(projectId);
		
		commandMap.put("list", imgList);
		
		return makeResponse(commandMap,"");
	}

	/**
	 * sessionId가 없을때 localstorage의 sessionId 추가
	 */
	@RequestMapping(value = "/ajax/getSessionId")
	@ResponseBody
	public Object getSessionId(CommandMap commandMap, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) commandMap.getMap();

		String chatId = (String) map.get("sessionId");
		URLCodec codec = new URLCodec();
		AES256Util aes256 = new AES256Util();
		LOGGER.info("chatId: " + chatId);
		LOGGER.info("chatId: " + aes256.aesDecode(codec.decode(chatId)));
		String decodedId = aes256.aesDecode(codec.decode(chatId));

		if (session.getAttribute("logSeq") == null || "".equals(session.getAttribute("logSeq"))) {
			session.setAttribute("logSeq", decodedId);
			String logSeq ="";
			Map<String, Object> mstMap = new HashMap<String, Object>();
			if(SequenceUtil.getDBKey()==null){
				String rsJson = "죄송합니다 <br>일시적인 시스템 장애입니다.<br>잠시 후 다시 이용해 주시기 바랍니다.";
				return makeErrorResponse(commandMap, rsJson);
			} else {
				logSeq = SequenceUtil.getDBKey();
			}
			mstMap.put("chatId", logSeq);
			mstMap.put("year",  new SimpleDateFormat("yyyy",Locale.KOREA).format(new Date()));
			mstMap.put("month", new SimpleDateFormat("MM",Locale.KOREA).format(new Date()));
			mstMap.put("day",   new SimpleDateFormat("dd",Locale.KOREA).format(new Date()));
			if(session.getAttribute("userId")==null || "".equals(session.getAttribute("userId"))){
				mstMap.put("userId", "NOT_LOGIN");
			} else {
				mstMap.put("userId", session.getAttribute("userId"));
			}
			mstMap.put("ip",   request.getRemoteAddr());
			//chatLogService.insertMasterLog(mstMap);
			
		}
		return makeResponse(commandMap, (String)map.get("query"));
	}
	
		
	// 일상Talk result
	public void makeResult(String answer, OutData output) {
		ChatResult result = new ChatResult();
		result.setMessage(answer);
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",Locale.KOREA).format(new Date());
		result.setNodeType(IntentType.CommonTalk);
		result.setTimeStamp(timeStamp);
		output.setResult(result);
	}
	
	@RequestMapping(value = "/ajax/saveLogSearch")
	@ResponseBody
	public Object saveLogSearch(CommandMap commandMap, HttpServletRequest request) throws Exception {
		LOGGER.info("[saveLogSearch]----------------------------->");
		LOGGER.info(commandMap.toString());
		String sessionKey = "";
		String projectId = commandMap.get("projectId").toString();

		HttpSession session = request.getSession();
		if(session.getAttribute("userSessionKey")==null){
			sessionKey = "";
		} else {
			sessionKey = (String) session.getAttribute("userSessionKey");

			if(!sessionKey.equals("")) {
				Map<String, Object> sMap = new HashMap<String, Object>();
				String logSeq = session.getAttribute("logSeq") == null? "null":(String) session.getAttribute("logSeq");
				sMap.put("logUser", logSeq);
				sMap.put("logQuery", commandMap.get("logQuery"));
				sMap.put("logMorphAnalResult", commandMap.get("logMorphAnalResult"));
				sMap.put("logSessionKey", sessionKey);

				sMap.put("logPlatformType", commandMap.get("logPlatformType"));
				sMap.put("logEnterType", commandMap.get("logEnterType"));

				LoginVO loginVO = (LoginVO) session.getAttribute("loginVO");
				if (loginVO != null) {
					sMap.put("logUserId", loginVO.getUserId());
				} else {
					sMap.put("logUserId", "");
				}

				sMap.put("projectId", projectId);


				iChatService.saveLogSearch(sMap);

			}
		}

		return makeResponse(commandMap, "");
	}
	
	
	//형태소 분석
	@RequestMapping(value = "/ajax/getMorphWord")
	@ResponseBody
	public Map<String, Object> getMorphWord(String query, HttpServletRequest request) throws Exception {
		Map<String, Object> morphStr = new HashMap<>();
		LOGGER.info("[getMorphWord]----------------------------->");

		if (StringUtils.isNotBlank(query)) {
			try {
				morphStr.put("searchKeyword", searchService.getIChatMorph(query));
			} catch (UnsupportedOperationException | ClassCastException | NullPointerException |
					 IllegalArgumentException e) {
				LOGGER.error("getMorphWord Error:" + e.getMessage());
				String rsJson = "챗봇에 일시적인 장애가 발생하였습니다.<br>반복적인 오류가 발생하면 관리자에게 문의해 주세요.";
				morphStr.put("errorMsg", rsJson);
				return morphStr;
			}
		}
		
		return morphStr;
	}

	private String getRegistUrl(String gubun){
		switch (gubun){
			case "RP":
				return propertiesService.apiUrlPinfoRTargetPlayer;
			case "RO":
				return propertiesService.apiUrlPinfoRTargetOfficer;
			case "RR":
				return propertiesService.apiUrlPinfoRTargetReferee;
			case "RM":
				return propertiesService.apiUrlPinfoRTargetManager;
			case "AP":
				return propertiesService.apiUrlPinfoATargetPlayer;
			case "AO":
				return propertiesService.apiUrlPinfoATargetOfficer;
			case "AR":
				return propertiesService.apiUrlPinfoATargetReferee;
			case "AM":
				return propertiesService.apiUrlPinfoATargetManager;
			default:
				return "";
		}
	}

}
