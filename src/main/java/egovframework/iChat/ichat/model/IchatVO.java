package egovframework.iChat.ichat.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IchatVO{
	
	
    /* test ID */
    private String selectNow;
    
    /* intent Name */
    private String intentNm;
    
    /* intent mapping type */
    private String type;
    
    /* intent mapping content */
    private String content;
    
    /* project Id */
    private String projectId;
}