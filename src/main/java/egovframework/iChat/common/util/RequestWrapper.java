package egovframework.iChat.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class RequestWrapper extends HttpServletRequestWrapper {

	public RequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if(values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for( int i = 0; i<count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		return encodedValues;
	}
	
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if(value == null) {
			return null;
		}
		return cleanXSS(value);
	}
	
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if(value == null) {
			return null;
		}
		return cleanXSS(value);
	}
	
	private String cleanXSS(String value) {
		
		String value2 =  value.replaceAll("<", "").replaceAll(">", "");
		String value3 =  value2.replaceAll("'", "");		
		String value4 =  value3.replaceAll("javascript", "");
		String value5 =  value4.replaceAll("eval", "");
		String value6 =  value5.replaceAll("script", "");
		
		return value;
	}

}
