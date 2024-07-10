package egovframework.iChat.ichat.model;

import java.util.ArrayList;
import java.util.List;

public class Response {
	private List<String> answerList = new ArrayList<>();
	private String userValue = ""; 
	private String value = "";
	
	public List<String> getAnswerList() {
		return answerList;
	}
	public void setAnswerList(List<String> answerList) {
		this.answerList = answerList;
	}
	public String getUserValue() {
		return userValue;
	}
	public void setUserValue(String userValue) {
		this.userValue = userValue;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
