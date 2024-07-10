package egovframework.iChat.web.model;

public class AjaxResponse {

	boolean success;
	String errorMsg;
	String redirectUrl;
	InData indata;
	OutData outdata;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getErrorMsg() {
		
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	public OutData getOutdata() {
		return outdata;
	}
	
	public void setOutdata(OutData outdata) {
		this.outdata = outdata;
	}
	public InData getIndata() {
		return indata;
	}
	public void setIndata(InData indata) {
		this.indata = indata;
	}
	
}
