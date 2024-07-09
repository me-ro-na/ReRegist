package egovframework.iChat.ichat.model;

import org.json.simple.JSONObject;

import lombok.Data;

@Data
public class IChatResp {
	
	private String errorMessage;
	private String status;
	private int errorCode;
	private String answer;
	private String api;
	private String sessionKey;
	private JSONObject response;
	
}
