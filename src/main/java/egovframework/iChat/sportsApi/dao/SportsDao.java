package egovframework.iChat.sportsApi.dao;

import egovframework.iChat.common.dao.AbstractDao;
import egovframework.iChat.sportsApi.dto.LoginVO;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Repository;

@Repository("loginDao")
public class SportsDao extends AbstractDao {
	
	@SuppressWarnings("unchecked") //미확인 오퍼레이션과 관련된 경고를 억제합니다.

	public void makeSso(LoginVO loginVO) throws PersistenceException {
		insert("sports.makeSso", loginVO);
	}

	public LoginVO selectSsoInfo(LoginVO loginVO) throws PersistenceException {
		return (LoginVO) selectOne("sports.selectSsoInfo", loginVO);
	}

	public void deleteSsoInfo(LoginVO loginVO) throws PersistenceException{
		delete("sports.deleteSsoInfo", loginVO);
	}

	public void insertAuthResult(LoginVO loginVO) throws PersistenceException{
		insert("sports.insertAuthResult", loginVO);
	}
}
