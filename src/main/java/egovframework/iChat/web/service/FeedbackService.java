package egovframework.iChat.web.service;

import java.util.Map;

import egovframework.iChat.common.model.CommandMap;
import egovframework.iChat.ichat.model.FeedbackHistoryVO;
import org.apache.ibatis.exceptions.PersistenceException;

public interface FeedbackService {
	public Map<String, String> selectFeedbackSettingDetail(String projectId) throws PersistenceException ;
	public void insertFeedbackHistory(FeedbackHistoryVO vo) throws PersistenceException ;
	public int insertIntentFeedback(Map<String,Object> pMap) throws PersistenceException;
	public int updateIntentFeedback(Map<String,Object> pMap) throws PersistenceException ;
}
