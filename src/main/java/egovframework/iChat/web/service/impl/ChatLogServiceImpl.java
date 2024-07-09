package egovframework.iChat.web.service.impl;

import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.common.util.SequenceUtil;
import egovframework.iChat.ichat.model.ChatLogVO;
import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.web.dao.ChatLogDao;
import egovframework.iChat.web.service.ChatLogService;

@Service("chatLogService")
public class ChatLogServiceImpl implements ChatLogService {

	@Autowired ChatLogDao chatLogDao;
	
	/*
	 * detail 로그테이블 insert (챗봇과 대화할 경우)
	 */
	public void insertDetailLog(Map<String, Object> map) throws PersistenceException {
		chatLogDao.insertDetailLog(map);
	}
	
	/*
	 * detail 로그테이블 insert (챗봇과 대화할 경우)
	 */
	public void insertApiLog(Map<String, Object> map) throws PersistenceException {		
		chatLogDao.insertApiLog(map);
	}

	/*
	 * 로그테이블 insert (최상위 시나리오 매핑 시)
	 */
	@Override
	public void insertScenarioLog(Map<String, Object> map) throws PersistenceException  {
		chatLogDao.insertScenarioLog(map);
	}
	
	
	
}
