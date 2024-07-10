package egovframework.iChat.ichat.model;

import java.util.ArrayList;
import java.util.List;

public class Action {
	private List<String> additionalArgumentList = new ArrayList<>();
	private String name = "";
	private List<String> requiredArgumentList = new ArrayList<>();
	public List<String> getAdditionalArgumentList() {
		return additionalArgumentList;
	}
	public void setAdditionalArgumentList(List<String> additionalArgumentList) {
		this.additionalArgumentList = additionalArgumentList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getRequiredArgumentList() {
		return requiredArgumentList;
	}
	public void setRequiredArgumentList(List<String> requiredArgumentList) {
		this.requiredArgumentList = requiredArgumentList;
	}
	
	
}
