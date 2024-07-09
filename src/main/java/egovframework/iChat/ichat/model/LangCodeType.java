package egovframework.iChat.ichat.model;

public enum LangCodeType {
	ko("한국어"), en("English"), ja("日本語"), zh("汉语");
	
	final private String name;
	
	private LangCodeType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
