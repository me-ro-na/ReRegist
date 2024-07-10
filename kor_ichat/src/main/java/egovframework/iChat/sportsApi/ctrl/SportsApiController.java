package egovframework.iChat.sportsApi.ctrl;

import egovframework.iChat.common.exception.IChatException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import egovframework.iChat.sportsApi.config.SportsApiConfig;
import egovframework.iChat.sportsApi.dto.RequestApiDto;
import egovframework.iChat.sportsApi.service.SportsApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Handles requests for the application home page.
 */
@Controller
@Api(tags = {"API Controller"})
@RequestMapping("/api")
public class SportsApiController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SportsApiController.class);
	
	@Autowired
	private SportsApiService sportsApiService;

	private HttpHeaders getHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	@PostMapping("/retrieve")
	@ApiOperation(value = "조회 관련 API", notes = "조회 관련 API")
	public ResponseEntity<Object> retrieveApi(@RequestBody RequestApiDto.Retrieve dto) throws Exception {
		return callApi(dto);
	}
	
	@PostMapping("/reRegist")
	@ApiOperation(value = "재등록 관련 API", notes = "재등록 관련 API")
	public ResponseEntity<Object> reRegistApi(@RequestBody RequestApiDto.ReRegist dto) throws Exception {
		return callApi(dto);
	}

	@PostMapping("/agree")
	@ApiOperation(value = "개인정보동의 관련 API", notes = "개인정보동의 관련 API")
	public ResponseEntity<Object> agreeApi(@RequestBody RequestApiDto.Agree dto) throws Exception {
		return callApi(dto);
	}

	@PostMapping("/updateStep")
	@ApiOperation(value = "이어하기용 Step 저장", notes = "이어하기용 Step 저장")
	public ResponseEntity<Object> updateStep(@RequestBody RequestApiDto.ReRegist dto) throws Exception {
		try {
			sportsApiService.updateStep(dto);
			return new ResponseEntity<Object>(dto, getHeader(), HttpStatus.OK);
		} catch (PersistenceException ex) {
			LOGGER.error("[updateStep] Errmsg : " + ex.getMessage(), ex);
			return new ResponseEntity<Object>(ex.getCause().getMessage(), getHeader(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/getStep")
	@ApiOperation(value = "이어하기용 Step 확인", notes = "이어하기용 Step 확인")
	public ResponseEntity<Object> getStep(@RequestBody RequestApiDto.ReRegist dto) throws Exception {
		try {
			return new ResponseEntity<Object>(sportsApiService.getStep(dto), getHeader(), HttpStatus.OK);
		} catch (PersistenceException ex) {
			return new ResponseEntity<Object>(ex.getCause().getMessage(), getHeader(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private ResponseEntity<Object> callApi(Object dto) {
		Object result;
		try {				
			result = sportsApiService.requestApi(dto);
		} catch (IChatException | PersistenceException ex) {
			if(ex.getCause().getMessage().contains(SportsApiConfig.UNVALID_USER.getValue())) {
				return new ResponseEntity<Object>(SportsApiConfig.UNVALID_USER.getValue(), getHeader(), HttpStatus.FORBIDDEN);
			}else {
				return new ResponseEntity<Object>(SportsApiConfig.UNKNOWN.getValue(), getHeader(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<Object>(result.toString(), getHeader(), HttpStatus.OK);
	}
}