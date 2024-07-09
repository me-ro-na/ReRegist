package egovframework.iChat.common.exception;

/*
 * 챗봇 오류
 */
public class IChatException extends Exception {

	private static final long serialVersionUID = -8698134657489361512L;

	public IChatException(String msg) {
		super(msg);
	}
	
	public IChatException (Exception ex) {
		super(ex);
	}
}
