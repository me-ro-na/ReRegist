package egovframework.iChat.sportsApi.service;

import egovframework.iChat.common.exception.IChatException;
import egovframework.iChat.sportsApi.dao.SportsDao;
import egovframework.iChat.sportsApi.dto.LoginVO;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SportsDbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SportsDbService.class);

    @Autowired
    SportsDao sportsDao;

    public LoginVO ssoProcess(LoginVO loginVO, String remoteAddr) throws Exception {
        // 연계 키 조회
        String key = loginVO.getKey();
        if (key==null || key.equals("")) {
            throw new IChatException("연계키가 누락되었습니다.");
        }

        // 연계정보 조회
        LoginVO sso = sportsDao.selectSsoInfo(loginVO);

        // SSO 정보 삭제
        sportsDao.deleteSsoInfo(loginVO);

        if (sso==null) {
            throw new IChatException("연계정보가 없습니다.");
        }

        sso.setRemoteAddr(remoteAddr);

        // 로그인 이력 저장
        sportsDao.insertAuthResult(sso);

        return sso;
    }

    public void makeSso(LoginVO loginVO) throws PersistenceException {
        sportsDao.makeSso(loginVO);
    }
}
