package egovframework.iChat.common.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import egovframework.iChat.ichat.Intent.IntentType;
import egovframework.iChat.ichat.service.IChatService;
import egovframework.iChat.ichat.service.PropertiesService;
import egovframework.iChat.ichat.vo.LangCodeType;
import egovframework.iChat.web.model.Carousel;
import egovframework.iChat.web.model.OutData;
import egovframework.iChat.web.service.ChatLogService;
import egovframework.iChat.ichat.Intent.IntentType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonService {
	@Autowired 
	PropertiesService propertiesService;

	@Autowired
	private DynamicAnswerTemplateService dynamicAnswerTemplateService;

	@Autowired 
	private IChatService ichatService;
	@Autowired
	private ChatLogService chatLogService;
	
	private ConcurrentHashMap<String, LangCodeType> userLanguageMap = new ConcurrentHashMap<>();
	
	public String excludeSpecialCharacters(String userQuery) {
		List<Character> charList = new ArrayList<>();
		String reformatUserQuery = "";
		for (int i = 0; i < userQuery.length(); i++) {
			if(!Character.isLetterOrDigit(userQuery.charAt(i))) {
				userQuery = userQuery.replace(userQuery.charAt(i), ' ');
				charList.add(userQuery.charAt(i));
			} else {
				charList.add(userQuery.charAt(i));
			}
		}
		
		for (Character character : charList) {
			reformatUserQuery =  reformatUserQuery.toString().concat(character.toString());
		}
		
		return reformatUserQuery;
	}
	
	//dynamicAnswer block definition: staticDescription [params] {contents} link
	public Map<String,String> dynamicAnswerProcess(String answer, String link, LangCodeType userLangCode, OutData output) {
		Map<String,String> result = this.getDynamicAnswer(answer, link, userLangCode,output);
		String dynamicAnswer = result.get("contentForTemplate");
		String reformatAnswer = "";
		
		switch (userLangCode) {
		/*
		case en:
			reformatAnswer = (dynamicAnswer.equals("not_dynamic_answer")) ? answer : (!StringUtils.isEmpty(dynamicAnswer)) ? dynamicAnswer : propertiesService.commonApiDynamicAnswerEmptyEn;
			break;
			
		case ja:
			reformatAnswer = (dynamicAnswer.equals("not_dynamic_answer")) ? answer : (!StringUtils.isEmpty(dynamicAnswer)) ? dynamicAnswer : propertiesService.commonApiDynamicAnswerEmptyJa;
			break;
			
		case zh:
			reformatAnswer = (dynamicAnswer.equals("not_dynamic_answer")) ? answer : (!StringUtils.isEmpty(dynamicAnswer)) ? dynamicAnswer : propertiesService.commonApiDynamicAnswerEmptyZh;			
			break;
			*/
			
		default:
			reformatAnswer = (dynamicAnswer.equals("not_dynamic_answer")) ? answer : (!StringUtils.isEmpty(dynamicAnswer)) ? dynamicAnswer : (link.equals("") ) ? propertiesService.commonApiDynamicAnswerAndLinkEmptyKo : propertiesService.commonApiDynamicAnswerEmptyKo;
			break;
		}
		
		result.put("contentForTemplate", reformatAnswer);
		
		return result;
	}
	
	public Map<String,String> getDynamicAnswer(String dynamicAnswer, String link, LangCodeType userLangCode, OutData output) {
		Map<String,String> result = new HashMap<>();
		Map<String, Object> logMap = new HashMap<String, Object>();
		
		String contentForTemplate = "not_dynamic_answer" ; //not yet dynamic answer.. 
		boolean isDynamicAnswer = false;
		
		int startFilterContentIndex = 0;
		int endFilterContentIndex = 0;
		String endAnswer = "";
		String getContent = "";
		String[] getContents = {};
		String[] getParams = {};
		IntentType paramIntentType;
		//result-content.. 
		if(dynamicAnswer.contains("{") && dynamicAnswer.contains("}")) {
			isDynamicAnswer = true;
			startFilterContentIndex = dynamicAnswer.indexOf("{");
			endFilterContentIndex = dynamicAnswer.lastIndexOf("}");
			getContent = dynamicAnswer.substring(startFilterContentIndex + 1, endFilterContentIndex);
			getContents = getContent.split(",");
			log.debug("getContent: " + getContent);
		}
		
		//dynamicAnswer..
		if(isDynamicAnswer) {
			endAnswer = this.getEndDescriptionForDynamicAnswer(dynamicAnswer);
			getParams = this.getParamsForDynamicAnswer(dynamicAnswer);
			paramIntentType = getIntentType(getParams[0].trim());
			
			logMap.put("intentName", getParams[0].trim());
			logMap.put("apiName", paramIntentType);
			
				try {
					chatLogService.insertApiLog(logMap);
				} catch (PersistenceException e) {
					// TODO Auto-generated catch block
					log.error("DyanmicAnswer Error: insert ApiLog");
				}
			
			
			
		if(paramIntentType != null) {
			result = dynamicAnswerTemplateService.getDynamicAnswerTemplate(this.getStaticDescriptionForDynamicAnswer(dynamicAnswer), this.getParamsForDynamicAnswer(dynamicAnswer), getContents, link, userLangCode);			
			if(endAnswer.trim()!="") {
				result.put("contentForTemplate", result.get("contentForTemplate")+endAnswer);
				
			}
		}
			log.debug("linkQueryStringForTemplate: " + result.get("linkQueryStringForTemplate"));
		} else {
			result.put("contentForTemplate", contentForTemplate);
		}
		
		return result;
	}
	
	public String getStaticDescriptionForDynamicAnswer(String dynamicAnswer) {
		int startFilterParamIndex = 0;
		String staticDescription = "";
		
		if(dynamicAnswer.contains("[") && dynamicAnswer.contains("]")) {
			startFilterParamIndex = dynamicAnswer.indexOf("[");
			staticDescription = dynamicAnswer.substring(0, startFilterParamIndex);
		}
		
		return staticDescription;
	}
	
	public String getAllShoppingDynamicAnswer(String dynamicAnswer) {
		int startFilterParamIndex = 0;
		int endFilterParamIndex = 0;
		String staticDescription = "";
		
		if(dynamicAnswer.contains("[") && dynamicAnswer.contains("]")) {
			startFilterParamIndex = dynamicAnswer.indexOf("[");
			endFilterParamIndex = dynamicAnswer.indexOf("<cr>");
			staticDescription = dynamicAnswer.substring(0, startFilterParamIndex);
			staticDescription += dynamicAnswer.substring(endFilterParamIndex);
		}
		
		return staticDescription;
	}
	
	public String getEndDescriptionForDynamicAnswer(String dynamicAnswer) {
		int startFilterParamIndex = 0;
		String staticDescription = "";
		
		if(dynamicAnswer.contains("{") && dynamicAnswer.contains("}")) {
			startFilterParamIndex = dynamicAnswer.indexOf("}");
			staticDescription = dynamicAnswer.substring(startFilterParamIndex+1);
		}
		
		return staticDescription;
	}
	
	public String[] getParamsForDynamicAnswer(String dynamicAnswer) {
		int startFilterParamIndex = 0;
		int endFilterParamIndex = 0;
		String getParam = "";
		String[] getParams = {};
		
		//params..
		if(dynamicAnswer.contains("[") && dynamicAnswer.contains("]")) {
			startFilterParamIndex = dynamicAnswer.indexOf("[");
			endFilterParamIndex = dynamicAnswer.lastIndexOf("]");
			getParam = dynamicAnswer.substring(startFilterParamIndex + 1, endFilterParamIndex);
			getParams = getParam.split(",");
			log.debug("getParam: " + getParam);
		}
		
		return getParams;
	}
	
	public String getCateId(String param){
		String cateId = "";
		IntentType caseType = getIntentType(param);
		
		switch (caseType) {
		case Shower:
		case Relax:
		case Napzone:
		case Spa:
		case Massage:
			cateId="12";
			break;
		case Book:
			cateId="44";
			break;
		case Tour:
			cateId="45";
			break;
		case FoodCourt:
			cateId="34";
			break;
		case InfoDesk:
			cateId="13";
			break;
		case Cafe:
		case Snack:
		case Dessert:
			cateId="36";
			break;
		case Comsmetic:
			cateId="41";
			break;
		case Fashion:
		case Accessory:
			cateId="42";
			break;
		case Souvenir:
			cateId="46";
			break;
		case Flower:
			cateId="47";
			break;
		case KoreanFood:
			cateId="32";
			break;
		case WesternFood:
			cateId="33";
			break;
		case AsianFood:
			cateId="35";
			break;
		case FastFood:
			cateId="31";
			break;
		case AdminService:
			cateId="18";
			break;
		case Internet:
			cateId="12";
			break;
		default:
			break;
		}
		
		return cateId;
	}
	
	public boolean isDynamicAnswer(String dynamicAnswer) {
		boolean result = false;
		if(dynamicAnswer.contains("[") && dynamicAnswer.contains("]") && dynamicAnswer.contains("{") && dynamicAnswer.contains("}")) {
			result = true;
		}
		
		return result;
	}
	
	public LangCodeType getUserLangCode(String userLanguage, String uniqueKey) {
		LangCodeType langCodeType = LangCodeType.ko;
		switch (userLanguage) {
		case "한국어":
			langCodeType = LangCodeType.ko;
			userLanguageMap.put(uniqueKey, langCodeType);
			break;
			
//		case "English":
		case "english_test":
			langCodeType = LangCodeType.en;		
			userLanguageMap.put(uniqueKey, langCodeType);
			break;
			
//		case "日本語":
		case "japanese_test":
			langCodeType = LangCodeType.ja;
			userLanguageMap.put(uniqueKey, langCodeType);
			break;
			
//		case "汉语":
		case "chinese_test":
			langCodeType = LangCodeType.zh;
			userLanguageMap.put(uniqueKey, langCodeType);
			break;
			
		default:
			userLanguageMap.putIfAbsent(uniqueKey, langCodeType);
			langCodeType = userLanguageMap.get(uniqueKey);
			break;
		}
		
		return langCodeType;
	}
	
	public String getMenuForLanguage(String userQuery) {
		String guideMent = "";
		switch (userQuery) {
		case "한국어":
		case "메뉴":
			guideMent = "안녕하세요. 만나서 반가워요.\r\n" + 
					"인천공항 챗봇 시범서비스 \'에어봇\' 입니다. ☺\r\n" + 
					"\r\n" + 
					"\'에어봇\'은 인천공항의 운항정보/쇼핑·식당/교통/공항서비스·시설/출·입국/환승/여행정보/편명검색 등에 대한 정보를 제공합니다.\r\n" + 
					"\r\n" + 
					"인천공항에 대해 궁금한 내용을 아래 예시처럼 물어봐주세요.\r\n" + 
					"\r\n" + 
					"\"운항 정보 확인해줘.\"\r\n" + 
					"\"출국장 혼잡도 알려줘.\"\r\n" + 
					"\"긴급 전화번호 안내\"\r\n" + 
					"\"스타벅스 어디에 있어?\"\r\n" + 
					"\r\n" + 
					"※질문 도중 처음으로 돌아오고 싶다면 '메뉴'를 입력해주세요.\r\n" +
					"※현재 시범서비스 진행중으로 답변이 부족한 점 양해 부탁드리며, 향후 학습을 통해 더 멋진 서비스로 찾아뵙겠습니다!";
//					"※질문 도중 처음으로 돌아오고 싶다면 '메뉴'를 입력해주세요.\r\n" + 
//					"\r\n" + 
//					"한국어 서비스를 원하시면 '한국어'라고 입력해주세요.\r\n" ;
//					"Please type in 'English' if you wish to chat in English.";
			break;
					
//		case "English":
//		case "english":
//		case "menu":
		case "english_test":
			guideMent = "Hello. Nice to meet you.\r\n" + 
					"This is Airbot, Incheon Airport Chatbot pilot service.\r\n" + 
					"\r\n" + 
					"The Airbot provides information on Flight Status/ shopping, dining/  transportation/ airport services, facilities/ Departure, Arrival/ transfer and travel.\r\n" + 
					"\r\n" + 
					"Please ask me the following example about Incheon Airport.\r\n" + 
					"\r\n" + 
					"\" Please check your flight information. \"\r\n" + 
					"\" Let me know the congestion of the departure areas, too. \"\r\n" + 
					"\" Emergency Phone Number Information \"\r\n" + 
					"\" Where is Starbucks? \"\r\n" + 
					"\r\n" + 
					"※ If you want to return to the first time during the question, please enter “menu”.\r\n" + 
					"\r\n" + 
					"한국어 서비스를 원하시면 '한국어'라고 입력해주세요.\r\n" + 
					"Please type in 'English' if you wish to chat in English."+
					"Select one of the following!Scenario,Free Conversation"
					;
			break;

//		case "日本語":
//		case "メニュー":
		case "japanese_test":
			guideMent = "おはようございます お会いできて嬉しいです。\r\n" + 
					"仁川(インチョン)空港チェッボッイプニダ。\r\n" + 
					"\r\n" + 
					"空港について知りたい内容を聞いてください。 例えば、こんなにです。\r\n" + 
					"\r\n" + 
					"\"運航情報確認してくれて。\"\r\n" + 
					"\"出国場に混雑も教えてくれて。\"\r\n" + 
					"\"緊急電話番号案内\"\r\n" + 
					"\r\n" + 
					"この他にも希望する質問を自由にできます。\r\n" + 
					"\"スターバックスどこにいるの?\"\r\n" + 
					"\r\n" + 
					"Please type in '한국어' if you wish to chat in Korean.\r\n" + 
					"Please type in 'English' if you wish to chat in English.\r\n" + 
					"Please type in '日本語' if you wish to chat in Japanese.\r\n" + 
					"Please type in '汉语' if you wish to chat in Chinese.";
			break;
			
//		case "汉语":
//		case "菜单":
		case "chinese_test":
			guideMent = "你好。 见到您很高兴。\r\n" + 
					"我是仁川机场聊天机器人。\r\n" + 
					"\r\n" + 
					"关于机场的疑问,请提问一下。 举例来说就是这样。\r\n" + 
					"\r\n" + 
					"\"请确认一下飞行信息。\"\r\n" + 
					"\"请告诉我出国通道的混乱情况。\"\r\n" + 
					"\"紧急电话号码指南\"\r\n" + 
					"\r\n" + 
					"除此之外,还可以自由地进行想要的提问。\r\n" + 
					"\"星巴克在哪里?\"\r\n" + 
					"\r\n" + 
					"Please type in '한국어' if you wish to chat in Korean.\r\n" + 
					"Please type in 'English' if you wish to chat in English.\r\n" + 
					"Please type in '日本語' if you wish to chat in Japanese.\r\n" + 
					"Please type in '汉语' if you wish to chat in Chinese.\r\n" + 
					"\r\n" + 
					"";
			break;
			
		default:
			guideMent = "empty";
			break;
		}
		
		return guideMent;
	}
	
	public String replaceLocaleUrl(String url, LangCodeType userLangCode) {
		String result = "";
		switch (userLangCode) {
		case en:
			result = url.replace("/ko/", "/en/");
			break;
		
		case ja:
			result = url.replace("/ko/", "/ja/");
			break;
		
		case zh:
			result = url.replace("/ko/", "/ch/");
			break;
			
		default:
			result = url;
			break;
		}
		
		return result;
	}
	
	public IntentType getIntentType(String intent) {
		IntentType intentType = null;
		for (IntentType type : IntentType.values()) {
			if(type.getName().contains(intent)) {
				intentType = type;
			}
		}
		
		return intentType;
	}
	
	public Map<String,String> getFilterLink(String answer) {
		Map<String,String> resultMap = new HashMap<>();
		String extractPattern = "";
		String reformatAnswer = "";
		
		String linkString = "";
		String filterLinkStrings = "링크@Link@リンク@链接";
		String[] filterLinkStringArray = filterLinkStrings.split("@");
		for (String filterLinkString : filterLinkStringArray) {
			if(answer.contains(filterLinkString)) {
				linkString = filterLinkString;
			}
		}
		
		switch (linkString) {
		case "링크":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryLinkKo)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryLinkKo)).trim();
			break;
			
		case "Link":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryLinkEn)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryLinkEn)).trim();
			break;
			
		case "リンク":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryLinkJa)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryLinkJa)).trim();
			break;
			
		case "链接":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryLinkZh)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryLinkZh)).trim();
			break;
			
		default:
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryLinkKo)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryLinkKo)).trim();
			break;
		}
		
		resultMap.put("extractPattern", extractPattern);
		resultMap.put("reformatAnswer", reformatAnswer);
		
		return resultMap;
	}
	
	public Map<String,String> filterImage(String answer) {
		Map<String,String> resultMap = new HashMap<>();
		String extractPattern = "";
		String reformatAnswer = "";
		
		String imageString = "";
		String filterImageStrings = "이미지@Image@イメージ@图像";
		String[] filterImageStringArray = filterImageStrings.split("@");
		for (String filterImageString : filterImageStringArray) {
			if(answer.contains(filterImageString)) {
				imageString = filterImageString;
			}
		}
		
		switch (imageString) {
		case "이미지":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryImageKo)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryImageKo)).trim();
			break;
			
		case "Image":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryImageEn)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryImageEn)).trim();
			break;
			
		case "イメージ":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryImageJa)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryImageJa)).trim();
			break;
			
		case "图像":
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryImageZh)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryImageZh)).trim();
			break;
			
		default:
			extractPattern = answer.substring(answer.indexOf(propertiesService.commonApiPatternRequeryImageKo)).trim();
			reformatAnswer = answer.substring(0, answer.indexOf(propertiesService.commonApiPatternRequeryImageKo)).trim();
			break;
		}
		
		resultMap.put("extractPattern", extractPattern);
		resultMap.put("reformatAnswer", reformatAnswer);
		
		return resultMap;
	}
	



	public String addGuide() {

		String answer = "고객님, 죄송하지만 문의주신 질문은 제가 답변하지 못하는 내용입니다. 추가 학습을 통해 보다 똑똑한 에어봇이 되도록 노력하겠으며,오타 및 일시적인 장애 등의 사유로 답변이 불가능할 수 있으니 양해 부탁드립니다. 공항에 대한 빠른 문의는 인천공항 고객센터 1577-2600을 이용해주시길 바랍니다.";

		return answer;
	}
	
	
	public String scenarioGuideMent( String selectedCategory, LangCodeType userLangCode) {
		String scenarioGuideMent = "";
		switch (userLangCode) {
		case en:
			scenarioGuideMent = scenarioGuideMent + selectedCategory +"has the following menus."+"\nPlease select one of the following!";
			break;
		
		case ja:
			scenarioGuideMent = scenarioGuideMent + selectedCategory +"には次のようなメニューがあります。"+"\n次のいずれかを選択してください!";
			break;
		
		case zh:
			scenarioGuideMent = scenarioGuideMent + selectedCategory +"下面有以下菜单。"+"\n请选择下列选项中!";
			break;
			
		default:
			scenarioGuideMent = scenarioGuideMent + selectedCategory +"에는 다음과 같은 메뉴가 있습니다." + "\n다음 중 하나를 선택해 주세요!";
			break;
		}
		
		return scenarioGuideMent;
	}
}















