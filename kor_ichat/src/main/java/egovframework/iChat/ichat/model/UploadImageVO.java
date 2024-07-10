package egovframework.iChat.ichat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UploadImageVO {

	/* SEQ */
	private String imageSeq;
	
	/* 이미지ID */
	private String imageId;
	
	/* 이미지명 */
	private String imageName;
	
	/* 이미지파일명 */
	private String imageFilename;
	
	/* 이미지서버파일명 */
	private String imageFileservername;
	
	/* 이미지 default YN */
	private String imageDefaultYn;

	/* 이미지 속성 */
	private String imageAttribute;
}
