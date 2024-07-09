package egovframework.iChat.ichat.model;

import org.json.simple.JSONObject;

import lombok.Data;

@Data
public class Intent {
	
	private String errorMessage;
	private String status;
	private int errorCode;
	private String api;
	private String isSuccess;
	private JSONObject intent;
}
