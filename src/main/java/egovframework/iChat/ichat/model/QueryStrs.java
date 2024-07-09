package egovframework.iChat.ichat.model;

import java.util.ArrayList;
import java.util.List;

public class QueryStrs {
	private String userQuery = "";
	private List<HighlightInfo> highlightInfo = new ArrayList<>();
	
	public String getUserQuery() {
		return userQuery;
	}
	public void setUserQuery(String userQuery) {
		this.userQuery = userQuery;
	}
	public List<HighlightInfo> getHighlightInfo() {
		return highlightInfo;
	}
	public void setHighlightInfo(List<HighlightInfo> highlightInfo) {
		this.highlightInfo = highlightInfo;
	}
	
	
}
