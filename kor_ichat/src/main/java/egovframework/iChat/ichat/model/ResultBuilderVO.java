package egovframework.iChat.ichat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResultBuilderVO{

	private String key;
	private String buildSeq;
	private String buildNm;
	private String intentName;
	private String buildType;
	private String buildContents;
	private String buildYn;
	private String projectId;
	private String scenarioId;
	private String parentSeq;
	private String lvl;
	private String ord;
	private String ref;
	private String imageId;
	private String nodeNm;
	private String name;
	private String mainUserQuery;
	private String linkType;
	private String dispType;
	
}