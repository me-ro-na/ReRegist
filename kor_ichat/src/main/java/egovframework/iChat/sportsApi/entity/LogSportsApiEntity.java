package egovframework.iChat.sportsApi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogSportsApiEntity {
	
	//순번
	private int logSeq;
	
	//로그일시
	private String logDate;
	
	//대한체육회 로그인 사용자ID
	private String logUserId;
	
	//프로젝트ID
	private String projectId;
	
	//API 타입
	private String apiType;
	
	//API 요청 파라미터
	private String requestParam;
	
	//결과코드
	private String resultCode;
	
	//결과메세지
	private String resultMsg;
	
	//API(재등록) 챗봇 내 진행중인 단계
	private String apiStep;
}
