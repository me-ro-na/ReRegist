package egovframework.iChat.sportsApi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiCodeEntity {
	
	//API 타입
    private String apiType;
	
    //API 이름
	private String apiName;
	
	//API 코드
	private String apiCode;
	
	//API 구분
	private String apiGb;

	//API URL
	private String apiUrl;
	
	//API 설명
	private String apiDesc;
}
