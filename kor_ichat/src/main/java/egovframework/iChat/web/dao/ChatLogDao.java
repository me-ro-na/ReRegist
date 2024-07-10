package egovframework.iChat.web.dao;

import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import egovframework.iChat.common.dao.AbstractDao;
import egovframework.iChat.ichat.model.ChatLogVO;
import egovframework.iChat.ichat.model.IchatVO;

@Repository("chatLogDao")
public class ChatLogDao  extends AbstractDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatLogDao.class);

	/*
	 * detail 로그테이블 insert (챗봇과 대화할 경우)
	 */
	public void insertDetailLog(Map<String, Object> map) throws PersistenceException {
		insert("chatLog.insertDetailLog", map);
	}
	

	/*
	 * api 로그테이블 insert (챗봇과 대화할 경우)
	 */
	public void insertApiLog(Map<String, Object> map) throws PersistenceException {
		insert("chatLog.insertApiLog", map);
	}
	
	
	/*
	 * 로그테이블 insert (최상위 시나리오 매핑 시)
	 */
	public void insertScenarioLog(Map<String, Object> map) throws PersistenceException {
		insert("chatLog.insertScenarioLog", map);
	}
	
	
}
