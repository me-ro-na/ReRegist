package egovframework.iChat.sportsApi.dto;

import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class LoginVO implements Serializable {
    private static final long serialVersionUID = 7201668417299081153L;
    String sessionId;
    String userId;
    String ci;
    String userName;
    String birthDt;
    String genderCode;
    String sex;
    String ipinVno;
    String certMethod;
    String mobileNo;
    String nationalInfo;
    String pclassCd;
    String registerGb;
    String targetUrl;
    String orgCd;
    String orgNm;
    String key;
    String remoteAddr;
}