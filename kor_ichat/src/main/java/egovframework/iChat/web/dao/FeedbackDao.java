package egovframework.iChat.web.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;

import egovframework.iChat.common.dao.AbstractDao;
import egovframework.iChat.ichat.model.FeedbackHistoryVO;

@Repository("FeedbackDao")
public class FeedbackDao extends AbstractDao{
	@SuppressWarnings("unchecked") //미확인 오퍼레이션과 관련된 경고를 억제합니다.
	
	public Map<String, String> selectFeedbackSettingDetail(String projectId) throws PersistenceException {
		return (Map<String, String>) selectOne("feedback.selectFeedbackSettingDetail", projectId);
	}
	
	public void insertFeedbackHistory(FeedbackHistoryVO vo) throws PersistenceException {		
		insert("feedback.insertFeedbackHistory", vo);
	}
	
	public int insertIntentFeedback(Map<String,Object> pMap) throws PersistenceException {		
		return (int) insert("feedback.insertIntentFeedback", pMap);
	}
	
	public int updateIntentFeedback(Map<String,Object> pMap) throws PersistenceException {	
		return (int) update("feedback.updateIntentFeedback", pMap);
	}
}
