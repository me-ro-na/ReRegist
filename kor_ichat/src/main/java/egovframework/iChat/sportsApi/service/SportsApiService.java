package egovframework.iChat.sportsApi.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import egovframework.iChat.common.exception.IChatException;
import egovframework.iChat.sportsApi.dto.RequestApiDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.iChat.ichat.service.PropertiesService;
import egovframework.iChat.sportsApi.config.SportsApiConfig;
import egovframework.iChat.sportsApi.dao.SportsApiDao;
import egovframework.iChat.sportsApi.dto.LoginVO;
import egovframework.iChat.sportsApi.entity.ApiCodeEntity;
import egovframework.iChat.sportsApi.entity.LogSportsApiEntity;
import kr.or.sports.api.client.APIClient;

/**
 * ClassName : SportsApiService
 * <p>Description: 대한체육회 연계 처리 서비스 클래스</p>
 * <p>Date : 2024.02.18.</p>
 * @author : wisenut
 */

@Service
public class SportsApiService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SportsApiService.class);
	
   
    @Autowired
    private SportsApiDao apiDao;
    @Autowired
    private PropertiesService propertiesService;
    
    /**
     * 대한체육회 API 요청 및 결과 return
	 * <pre>
	 * <b>History:</b>
	 *   2024.02.18 최초 작성
	 * </pre>
	 * @param parameters (RequestApiDto.XXX - API 별 requestApiDto)
	 * @return result (API 결과 XML을 JSON 형태로 return)
	 * @throws Exception (api 실패 시, userid 검증 실패 시)
	 */
	public Object requestApi(Object parameters) throws IllegalArgumentException, IChatException {
		Object result = null;
		LOGGER.info("[requestApi] Start Param : " + parameters.toString());
		int apiLogSeq = 0;

		try {
			//DTO -> Map
			ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, Object> paramMap = objectMapper.convertValue(parameters, new TypeReference<Map<String, Object>>() {});
	        
	        Assert.hasText(Objects.toString(paramMap.get("userId"), ""), SportsApiConfig.UNVALID_USER.getValue());
			Assert.isTrue(checkUserId(Objects.toString(paramMap.get("userId"), "")), SportsApiConfig.UNVALID_USER.getValue());
			
			// API Arguments [URL, PARMAS]
			String[] args = makeArguments(paramMap);
			
			// 대한체육회 API Client
			APIClient m_client = null;
			String resultStr = "";
			JSONObject resultJson;
			
			// APIClient 인스턴스 생성
			m_client = APIClient.getInstance();
			
			// 작업 시작전 초기화 - 접속URL, 파라미터 셋팅
			m_client.init(args[0], args[1]);
			
			// 인스턴스를 호출하여 결과 수신
			/*
			//(개발) api 샘플 xml 파일 읽는거로 대체
			boolean isXmlFile = false;
			try {
				String xmlNm = paramMap.get("apiType").toString();
				if("R07".equals(xmlNm)){
					xmlNm += paramMap.get("gubun").toString();
				}

				ResourceLoader resourceLoader = new DefaultResourceLoader();
				Resource resource = resourceLoader.getResource("classpath:/apixml/" + xmlNm + ".xml");

				try (Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8")) {
					resultStr = FileCopyUtils.copyToString(reader);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				isXmlFile = true;
			}catch(Exception ee) {
				LOGGER.error("XML File Read Error.");
			}

			if(!isXmlFile) {
				resultStr = m_client.execute();
				Assert.hasText(resultStr, "Sports Api Unknown Error. Param : " + parameters.toString());
			}
			*/
			resultStr = m_client.execute();
			Assert.hasText(resultStr, "Sports Api Unknown Error. Param : " + parameters.toString());
			
			// XML -> JSON 변환
			resultJson = xmlToJson(resultStr);

			boolean isSuccess = propertiesService.apiSuccessCode.equals(resultJson.getString("RESULT_CODE"));
			String apiStep = "";
			if(isSuccess && paramMap.get("apiStep") != null){
				apiStep = paramMap.get("apiStep").toString();
			}

			// API History 이력 추가
			LogSportsApiEntity entity = LogSportsApiEntity.builder()
					.logUserId(Objects.toString(paramMap.get("userId"), ""))
					.projectId(propertiesService.dmProjectId)
					.apiType(Objects.toString(paramMap.get("apiType"),""))
					.resultCode(resultJson.getString("RESULT_CODE"))
					.resultMsg((propertiesService.apiSuccessCode).equals(resultJson.getString("RESULT_CODE")) ? propertiesService.apiSuccessMsg : resultJson.getString("RESULT_MSG"))
					.requestParam(args[1])
					.apiStep(apiStep)
					.build();
					
			apiLogSeq = insertApiHistory(entity);
			
			// 성공이 아닐 경우는 모두 에러 처리
			String errMsg = String.format("[API Error] Code : %s, Msg : %s, Params : %s"
					, resultJson.getString("RESULT_CODE")
					, resultJson.getString("RESULT_MSG")
					, parameters.toString());
			if (!resultJson.getString("RESULT_CODE").startsWith("70005")) {
				errMsg = resultJson.toString(4);
			}

			Assert.isTrue(isSuccess, errMsg);

			if(apiLogSeq > 0){
				resultJson.put("apiLogId", apiLogSeq);
			}

			result = resultJson;
			LOGGER.info("[API Result]  : Success");

		}catch(IllegalArgumentException ie){
            LOGGER.error("[requestApi] Errmsg : {}", ie.getMessage(), ie);
		}catch(Exception ex) {
            LOGGER.error("[requestApi] Errmsg : {}", ex.getMessage(), ex);
			throw new IChatException(ex);
		}

		return result;
	}
	
	/**
     * 대한체육회 API 요청 Parameter 설정(대한체육회 API 가이드 참고)
	 * <pre>
	 * <b>History:</b>
	 *   2024.02.18 최초 작성
	 * </pre>
	 * @param paramMap (RequestApiDto.XXX - API 별 requestApiDto를 Map으로 변환)
	 * @return args (API 요청 파라미터를 String[2]배열로 return - [Api URL, Api Parameters])
	 * @throws Exception (Db 조회 데이터 또는 파라미터 null)
	 */
	private String[] makeArguments(Map<String, Object> paramMap) throws IndexOutOfBoundsException {
        // Get Api Code Info
        Assert.notNull(paramMap.get("apiType"), "[makeArguments] ApiType required. Parameters : " + paramMap.toString());
        Assert.hasText(paramMap.get("apiType").toString(), "[makeArguments] ApiType required. Parameters : " + paramMap.toString());
        
        List<ApiCodeEntity> apiCodeList = getApiList(paramMap);
        Assert.notEmpty(apiCodeList, "[makeArguments] Unknown Api Type. Param : " + paramMap.toString());

        ApiCodeEntity apiCodeEntity = apiCodeList.get(0);
    	
    	//Set Api SiteKey, Api Code
        paramMap.put("siteKey", propertiesService.apiKeyValue);
        paramMap.put("apiCd", apiCodeEntity.getApiCode());
        paramMap.put("apiGb", apiCodeEntity.getApiGb());
        
        //Api Arguments = [URL, PARMAS]
        String[] args = new String[2];
		args[0] = propertiesService.apiProtocol + propertiesService.apiDomain + apiCodeEntity.getApiUrl();
		args[1] = makeParam(paramMap);

		LOGGER.debug("makeArguments: {} / {}", args[0], args[1]);
		
		return args;
	}
	
	/**
     * API Parameter를 QueryString 형태로 변환(대한체육회 API 가이드 참고)
	 * <pre>
	 * <b>History:</b>
	 *   2024.02.18 최초 작성
	 * </pre>
	 * @param paramList (API 요청 파라미터)
	 * @return string (QueryString return)
	 */
	private String makeParam(Map<String, Object> paramList) {
		StringBuffer buf = new StringBuffer();
		
		if(paramList==null || paramList.isEmpty())
			return "";
		
		int idx = 0;
		Set<String> keys = paramList.keySet();
        for (String key : keys) {
            if (paramList.get(key) != null) {
                idx++;
                String val = (String) paramList.get(key);
                buf.append(idx == 1 ? "" : "&")
					.append(key).append("=").append(val);
            }
        }
		
		return buf.toString();
	}
	
	/**
     * API 요청결과(XML)을 Json형태로 변환
	 * <pre>
	 * <b>History:</b>
	 *   2024.02.18 최초 작성
	 * </pre>
	 * @param xmlString ((String)API 결과 XML)
	 * @return json (JSONObject)
	 */
	private JSONObject xmlToJson(String xmlString){
		LOGGER.debug(xmlString);
        JSONObject json = XML.toJSONObject(xmlString, true).getJSONObject("RESULT");
        
        LOGGER.debug(json.toString(4));
        
		return json;
	}

	/**
	 * API 요청시, WISE_LOG_SPORTS_API에 삽입.
	 * <pre>
	 * <b>History:</b>
	 *   2024.02.18 최초 작성
	 * </pre>
	 * @param log (LogSportsApiEntity.class)
	 * @return id (Insert시, 생성된 seq)
	 */
	private int insertApiHistory(LogSportsApiEntity log) throws Exception {
		int id = 0;
		try {
			apiDao.insertSportsApiLog(log);
			id = log.getLogSeq();
			LOGGER.info("Api History Inserted.");
		} catch(PersistenceException ex) {
			LOGGER.error("[insertSportsApiLog] Errmsg : {} , param : {}", ex.getMessage(), log.toString());
			throw new IChatException(ex);
		}
		
		return id;
	}
	
	/**
     * 대한체육회 API 정보 DB 조회
	 * <pre>
	 * <b>History:</b>
	 *   2024.03.04 최초 작성
	 * </pre>
	 * @param paramMap (특정 ApiType으로 조회 또는 전체 조회 )
	 * @return list (DB에서 조회한 결과를 List<ApiCodeEntity> return)
	 * @throws Exception (sql 오류)_
	 */
	public List<ApiCodeEntity> getApiList(Map<String, Object> paramMap) throws PersistenceException{
		List<ApiCodeEntity> list = null;
		try {
			list = apiDao.selectApiInfo(paramMap);
		} catch(PersistenceException ex) {
			LOGGER.error("[getApiList] Errmsg : {} , param : {}", ex.getMessage(), paramMap);
			throw new PersistenceException(ex);
		}
		
		return list;
	}
	
	/**
     * 대한체육회 API 로그인유저 유효성 체크
	 * 웹에서 받은 id와 세션 내 id를 비교
	 * <pre>
	 * <b>History:</b>
	 *   2024.03.04 최초 작성
	 * </pre>
	 * @param userId (챗봇에서 전달 받은 UserId)
	 * @return boolean 
	 */
	public boolean checkUserId(String userId) {
        LoginVO lVo = (LoginVO) RequestContextHolder.getRequestAttributes()
                .getAttribute("loginVO", RequestAttributes.SCOPE_SESSION);
        if (lVo != null && StringUtils.equals(userId, lVo.getUserId())) {
            return true;
        }
        
        LOGGER.info(SportsApiConfig.UNVALID_USER.getDesc());
        return false;
    }

	/**
	 * 대한체육회 API 재등록 이어하기 Step Update
	 * <pre>
	 * <b>History:</b>
	 *   2024.03.04 최초 작성
	 * </pre>
	 * @param dto (logSeq, apiStep)
	 */
	public void updateStep(RequestApiDto.ReRegist dto) throws IChatException {
		LogSportsApiEntity entity = LogSportsApiEntity.builder()
				.logSeq(Integer.parseInt(dto.getApiLogSeq()))
				.apiStep(dto.getApiStep())
				.build();

		try {
			apiDao.updateSportsApiStep(entity);
			LOGGER.info("Api Step Updated.");
		} catch(PersistenceException ex) {
			LOGGER.error("[updateStep] Errmsg : {} , param : {}", ex.getMessage(), entity.toString());
			throw new IChatException(ex);
		}
	}

	/**
	 * 대한체육회 API 재등록 이어하기 Step 조회
	 * <pre>
	 * <b>History:</b>
	 *   2024.03.04 최초 작성
	 * </pre>
	 * @param dto (RequestApiDto.ReRegist)
	 * @return  LogSportsApiEntity 중 apiCd, siteKey, apiGb 제외한 데이터
	 */
	public Object getStep(RequestApiDto.ReRegist dto) throws IChatException {
		//만 14세 미만 여부 계산
		LoginVO lVo = (LoginVO) RequestContextHolder.getRequestAttributes()
				.getAttribute("loginVO", RequestAttributes.SCOPE_SESSION);
		String birthDt = lVo == null ? "" : lVo.getBirthDt();

		if(!StringUtils.isEmpty(birthDt)){
			LocalDate birthDate = LocalDate.of(Integer.parseInt(birthDt.substring(0,4)), Integer.parseInt(birthDt.substring(4,6)), Integer.parseInt(birthDt.substring(6,8)));
			LocalDate now = LocalDate.now();
			Period age = Period.between(birthDate, now);

			if(age.getYears() < 14) {
				return true;
			}
		}


		LogSportsApiEntity entity = LogSportsApiEntity.builder()
				.logUserId(dto.getUserId())
				.apiType(dto.getApiType())
				.build();

		try {
			LOGGER.info("Get Api Step.");
			LogSportsApiEntity result = apiDao.selectSportsApiStep(entity);
			if(result != null){
				String[] resultParam = Objects.toString(result.getRequestParam(),"").split("&siteKey");
				if(resultParam.length > 0){
					result =  LogSportsApiEntity.builder()
							.logSeq(result.getLogSeq())
							.apiStep(result.getApiStep())
							.resultCode(result.getResultCode())
							.resultMsg(result.getResultMsg())
							.requestParam(resultParam[0])
							.build();
				}
			}
			return result != null ? result : "{}";
		} catch(PersistenceException ex) {
			LOGGER.error("[getStep] Errmsg : {} , param : {}", ex.getMessage(), entity.toString());
			throw new IChatException(ex);
		}
	}
}