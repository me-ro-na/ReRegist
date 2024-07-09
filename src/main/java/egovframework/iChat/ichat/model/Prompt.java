package egovframework.iChat.ichat.model;

import java.util.ArrayList;
import java.util.List;

public class Prompt {
	private int maxReactionCount = 0;
	private String answerOrder = "";
	private List<String> promptSentenceList = new ArrayList<>();
	
	public int getMaxReactionCount() {
		return maxReactionCount;
	}
	public void setMaxReactionCount(int maxReactionCount) {
		this.maxReactionCount = maxReactionCount;
	}
	public String getAnswerOrder() {
		return answerOrder;
	}
	public void setAnswerOrder(String answerOrder) {
		this.answerOrder = answerOrder;
	}
	public List<String> getPromptSentenceList() {
		return promptSentenceList;
	}
	public void setPromptSentenceList(List<String> promptSentenceList) {
		this.promptSentenceList = promptSentenceList;
	}
	
	
}
