package egovframework.iChat.sportsApi.config;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

public enum SportsApiConfig {
	//컨피그명(설명, Value)
	UNKNOWN("일치하는 Config가 없습니다."
			, "UNKNOWN"),
	UNVALID_USER("로그인 정보가 유효하지 않습니다."
			, "UNVALID_USER")
	
	;
	
	@Getter
	private String desc;
	@Getter
	private String value;

	private SportsApiConfig(String desc, String value) {
		this.desc = desc;
		this.value = value;
	}
	
	/**
	 * value값으로 Config 찾을 때 사용하기 위한 MAP
	 * 
	 * @return SportsApiConfig
	 * @example CONFIG_MAP.get("0000000") -> SportsApiConfig.RC_SUCCESS
	 */
	private static final Map<String, SportsApiConfig> CONFIG_MAP = Collections.unmodifiableMap(
																					Stream.of(values())
	            																	.collect(Collectors.toMap(SportsApiConfig::getValue, Function.identity())));
	/**
	 * value값으로 Config 조회
	 * 
	 * @return SportsApiConfig
	 * @example SportsApiConfig.getConfigByValue("0000000") -> SportsApiConfig.RC_SUCCESS
	 */
	public static SportsApiConfig getConfigByValue(final String value) {
		return Optional.ofNullable(CONFIG_MAP.get(value)).orElse(UNKNOWN);
	}
	
	/**
	 * Config 존재 여부 확인 및 조회
	 * 
	 * @param code - Config명
	 * @return SportsApiConfig
	 * @example SportsApiConfig.of(RC_SUCCESS) -> SportsApiConfig.RC_SUCCESS
	 * @example SportsApiConfig.of(RC_SUCCESS123123) -> SportsApiConfig.UNKNOWN
	 */
	public static SportsApiConfig of(final String code) {
		return Optional.ofNullable(SportsApiConfig.valueOf(code)).orElse(UNKNOWN);
	}
	
}
