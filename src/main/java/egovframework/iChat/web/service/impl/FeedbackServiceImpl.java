package egovframework.iChat.web.service.impl;

import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.common.model.CommandMap;
import egovframework.iChat.ichat.model.FeedbackHistoryVO;
import egovframework.iChat.web.dao.FeedbackDao;
import egovframework.iChat.web.service.FeedbackService;

@Service()
public class FeedbackServiceImpl implements FeedbackService {
	
	@Autowired
	FeedbackDao feedbackDao;
	
	@Override
	public Map<String, String> selectFeedbackSettingDetail(String projectId) throws PersistenceException {
		return feedbackDao.selectFeedbackSettingDetail(projectId);
	}

	@Override
	public void insertFeedbackHistory(FeedbackHistoryVO vo) throws PersistenceException  {
		feedbackDao.insertFeedbackHistory(vo);
	}
	
	@Override
	public int insertIntentFeedback(Map<String,Object> pMap) throws PersistenceException  {
		
		return feedbackDao.insertIntentFeedback(pMap);
	}
	
	@Override
	public int updateIntentFeedback(Map<String,Object> pMap) throws PersistenceException  {
		return feedbackDao.updateIntentFeedback(pMap);
	}

}
