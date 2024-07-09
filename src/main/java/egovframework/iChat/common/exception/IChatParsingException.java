package egovframework.iChat.common.exception;

/*
 * 챗봇 응답 파싱 오류
 */
public class IChatParsingException extends Exception {

	private static final long serialVersionUID = 5094684131446015380L;

	public IChatParsingException(String msg) {
		super(msg);
	}
	
	public IChatParsingException (Exception ex) {
		super(ex);
	}
}
