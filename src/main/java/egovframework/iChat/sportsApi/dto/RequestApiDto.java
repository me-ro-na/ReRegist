package egovframework.iChat.sportsApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class RequestApiDto {
	

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@ApiModel(description="조회 관련")
	public static class Retrieve{
		@ApiModelProperty(value = "apiType - API 타입", example = "G01")
		private String apiType;
		@ApiModelProperty(value = "userId - 로그인 Id", example = "wisenut")
		private String userId;
		@ApiModelProperty(value = "regYear - 등록신청년도", example = "2024")
		private String regYear;
		@ApiModelProperty(value = "gubun - 상태구분코드", example = "2자리")
		private String gubun;
		@ApiModelProperty(value = "startDt - 신청시작일", example = "20240101")
		private String startDt;
		@ApiModelProperty(value = "endDt - 신청종료일", example = "20240101")
		private String endDt;
		@ApiModelProperty(value = "startYear - 신청시작년도", example = "2024")
		private String startYear;
		@ApiModelProperty(value = "endYear - 신청종료년도", example = "2024")
		private String endYear;
		@ApiModelProperty(value = "eventYear - 대회년도", example = "2024")
		private String eventYear;
		@ApiModelProperty(value = "eventNm - 대회명", example = "체육대회")
		private String eventNm;
		
	}
	
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@ApiModel(description="재등록 관련")
	public static class ReRegist{
		@ApiModelProperty(value = "apiType - API 타입", example = "R01")
		private String apiType;
		@ApiModelProperty(value = "userId - 로그인 Id", example = "wisenut")
		private String userId;
		@ApiModelProperty(value = "gubun - 신청 구분(선수 P,지도자 O,심판 R,선수관리담당자 M)", example = "M")
		private String gubun;
		@ApiModelProperty(value = "pclassCd - 종목 코드", example = "BB")
		private String pclassCd;
		@ApiModelProperty(value = "pclassNm - 종목명", example = "태권도")
		private String pclassNm;
		@ApiModelProperty(value = "personNo - 개인번호", example = "1234567")
		private String personNo;
		@ApiModelProperty(value = "regYear - 등록 연도", example = "2024")
		private String regYear;
		@ApiModelProperty(value = "apiLogSeq - Api History Log Seq", example = "1")
		private String apiLogSeq;
		@ApiModelProperty(value = "apiStep - API Step", example = "R07-1")
		private String apiStep;
	}
	
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@ApiModel(description="개인정보동의 관련")
	public static class Agree{
		@ApiModelProperty(value = "apiType - API 타입", example = "R01")
		private String apiType;
		@ApiModelProperty(value = "userId - 로그인 Id", example = "wisenut")
		private String userId;
		@ApiModelProperty(value = "pclassCd - 종목 코드", example = "BB")
		private String pclassCd;
		@ApiModelProperty(value = "termsVersion - 약관 버전", example = "1")
		private String termsVersion;
		@ApiModelProperty(value = "agreeYn - 개인정보(선택) 동의 여부", example = "N")
		private String agreeYn;
	}
	
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@ApiModel(description="SSO 관련")
	public static class Sso{
		@ApiModelProperty(value = "userId - 로그인 Id", example = "wisenut")
		private String userId;
		@ApiModelProperty(value = "targetUrl - 이동할 URL", example = "http://naver.com")
		private String targetUrl;
		@ApiModelProperty(value = "sysGb - 시스템 구분(챗봇 시스템 코드 - CB)", example = "CB")
		private String sysGb;
	}
}
