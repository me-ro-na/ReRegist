package egovframework.iChat.ichat.model;

public class Parameter {
	private String postfix = "";
	private int askedCount = 0;
	private String value = "";
	private Prompt prompt = new Prompt();
	private Boolean required = true;
	private String parameterName = "";
	private String entityName = "";
	
	public String getPostfix() {
		return postfix;
	}
	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}
	public int getAskedCount() {
		return askedCount;
	}
	public void setAskedCount(int askedCount) {
		this.askedCount = askedCount;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Prompt getPrompt() {
		return prompt;
	}
	public void setPrompt(Prompt prompt) {
		this.prompt = prompt;
	}
	public Boolean getRequired() {
		return required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	
}
