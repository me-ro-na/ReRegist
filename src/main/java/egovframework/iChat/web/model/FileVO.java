package egovframework.iChat.web.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileVO {

	/* SEQ */
	private String fileSeq;
	
	/* 등록날짜 */
	private String regDate;
	
	/* 게시판ID */
	private String boardId;
	
	/* 제목 */
	private String title;
	
	/* 원본파일이름 */
	private String orgFileName;
	
	/* 저장파일이름 */
	private String saveFileName;
	
	/* 저장파일경로 */
	private String filePath;
	
	/* 파일크기 */
	private String fileSize;
	
	/* 파일확장자 */
	private String fileExt;
	
	/* 프로젝트ID */
	private String projectId;
	
	/* 검색어 */
	private String searchVal;
	
	/* 파일 확장자 선택값 */
	private String fileExtVal;
}
