package egovframework.iChat.web.service;

import java.util.Map;

import egovframework.iChat.ichat.model.ChatLogVO;
import egovframework.iChat.ichat.model.IchatVO;
import org.apache.ibatis.exceptions.PersistenceException;

public interface ChatLogService {
	
	public void insertDetailLog(Map<String, Object> map) throws PersistenceException ;
	public void insertApiLog(Map<String, Object> map) throws PersistenceException;
	public void insertScenarioLog(Map<String, Object> map) throws PersistenceException ;

	
	
	
}
