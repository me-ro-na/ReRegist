package egovframework.iChat.ichat.model;

public class ResponseIchat {
	private String answer = "";
	private String sessionKey = "";
	private String api = "";
	private String status = "";
	private int errorCode = 0;
	private String errorMessage = "";
	private Boolean isValid = false; 
	private Intent intent = new Intent();
	private LangCodeType userLangCode = LangCodeType.ko;
	private String conversationType = "F";
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Boolean getIsValid() {
		return isValid;
	}
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	public LangCodeType getUserLangCode() {
		return userLangCode;
	}
	public void setUserLangCode(LangCodeType userLangCode) {
		this.userLangCode = userLangCode;
	}
	public String getConversationType() {
		return conversationType;
	}
	public void setConversationType(String conversationType) {
		this.conversationType = conversationType;
	}
	
	
}
