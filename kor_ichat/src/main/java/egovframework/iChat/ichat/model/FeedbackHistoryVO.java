package egovframework.iChat.ichat.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FeedbackHistoryVO  {
	
	//등록일
	String regDate;
	//만족도 조사 타입(C : 코멘트, S : 만족도)
	String feedbackType;
	//만족도 조사 질문
	String query;
	//코멘트
	String feedbackContent;
	//만족도 조사 질문에 대한 점수
	String feedbackScore;
	String feedbackSessionKey;
	String userId;
	//프로젝트 ID
	String projectId;
	
	// 날짜 범위 검색을 위한 시작일, 종료일
	String startDate;
	String endDate;
}
