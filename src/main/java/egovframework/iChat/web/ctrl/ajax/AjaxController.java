package egovframework.iChat.web.ctrl.ajax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import egovframework.iChat.sportsApi.dto.LoginVO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import egovframework.iChat.common.model.CommandMap;
import egovframework.iChat.web.model.AjaxResponse;
import egovframework.iChat.web.model.ChatResult;
import egovframework.iChat.web.model.DataType;
import egovframework.iChat.web.model.InData;
import egovframework.iChat.web.model.OutData;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class AjaxController {

	protected LoginVO getLogin() {
		return (LoginVO) RequestContextHolder.getRequestAttributes().getAttribute("loginVO", RequestAttributes.SCOPE_SESSION);
	}
	
	public AjaxResponse makeResponse(CommandMap commandMap, OutData outdata) {
		AjaxResponse response = new AjaxResponse();
		InData indata = new InData();
		indata.setData(commandMap);
		
		response.setIndata(indata);
		response.setOutdata(outdata);
		response.setSuccess(true);

		LoginVO sso = getLogin();
		if (sso != null) {
			outdata.setUserId(sso.getUserId());
		} else {
			outdata.setUserId(null);
		}
		
		return response;
	}
	
	/*
	 * Exception 발생 할 경우 리턴 ajax 메시지
	 */
	public AjaxResponse makeErrorResponse(CommandMap commandMap, String msg) {
		AjaxResponse response = new AjaxResponse();
		InData indata = new InData();
		indata.setData(commandMap);
		
		OutData outdata = new OutData();		
		outdata.setType(DataType.TALK);
		
		ChatResult result = new ChatResult();
		result.setMessage(msg);
		outdata.setResult(result);
		
		response.setIndata(indata);
		response.setOutdata(outdata);
		response.setSuccess(true);
		return response;
	}
	
	/*
	 * 정상의 경우 리턴 ajax 메시지
	 */
	public AjaxResponse makeResponse(CommandMap commandMap, String message) {
		AjaxResponse response = new AjaxResponse();
		InData indata = new InData();
		indata.setData(commandMap);
		
		OutData outdata = new OutData();		
		outdata.setType(DataType.TALK);
//		outdata.setSession(session);
		ChatResult result = new ChatResult();
		result.setMessage(message);
		outdata.setResult(result);
		
		response.setIndata(indata);
		response.setOutdata(outdata);
		response.setSuccess(true);

		LoginVO sso = getLogin();
		if (sso != null) {
			outdata.setUserId(sso.getUserId());
		} else {
			outdata.setUserId(null);
		}

		return response;
	}
	
	
	public AjaxResponse makeInitResponse(CommandMap commandMap, String message) {
		AjaxResponse response = new AjaxResponse();
		InData indata = new InData();
		indata.setData(commandMap);
		
		ChatResult result = new ChatResult();
		result.setMessage(message);
		
		OutData outdata = new OutData();		
		outdata.setType(DataType.TALK);
		outdata.setResult(result);
		
		response.setIndata(indata);
		response.setOutdata(outdata);
		response.setSuccess(true);
		return response;
	}
	
	
	
	/*public AjaxResponse makeResponse(CommandMap commandMap, List msgList) {
		AjaxResponse response = new AjaxResponse();
		InData indata = new InData();
		indata.setData(commandMap);
		
		OutData outdata = new OutData();
		outdata.setDataType(DataType.LIST);
		outdata.setData(msgList);
		
		response.setIndata(indata);
		response.setOutdata(outdata);
		response.setSuccess(true);
		
		return response;
	}*/
	
	
	public List getDummyList(String str) {
		List list = new ArrayList();
		list.add(str);
		return list;
	}
	
	public List getDummy(Map map) {
		List list = new ArrayList();
		list.add(map);
		return list;
	}
	
	public JSONArray convertListToJson(List<Map<String, Object>> list) {
		JSONArray jsonArray = new JSONArray();
		for(Map<String, Object> map : list) {
			jsonArray.add(convertMapToJson(map));
		}
		return jsonArray;
	}
	
	public JSONObject convertMapToJson(Map<String, Object> map) {
		JSONObject json = new JSONObject();
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			json.put(key, value);
		}
		return json;
	}
}
