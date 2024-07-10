package egovframework.iChat.web.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.ichat.model.ResultBuilderVO;
import egovframework.iChat.ichat.model.UploadImageVO;
import egovframework.iChat.web.dao.ConvertDao;
import egovframework.iChat.web.service.ConvertService;

@Service("convertService")
public class ConvertServiceImpl implements ConvertService {

	@Autowired ConvertDao convertDao;
	/*
	 * Input 쿼리 Converter
	 * 챗봇으로 질의 시 처리
	 * (non-Javadoc)
	 * @see egovframework.iChat.web.service.ConvertService#selectQueryConvert(java.util.Map)
	 */
	@Override
	public Map<String, Object> selectQueryConvert(Map<String, Object> map) throws PersistenceException {
		// TODO Auto-generated method stub
		return convertDao.selectQueryConvert(map);
	}

	/*
	 * Output 답변 Converter
	 * 챗봇에서 나온 답변의 처리
	 * (non-Javadoc)
	 * @see egovframework.iChat.web.service.ConvertService#selectResultConvert(java.util.Map)
	 */
	@Override
	public Map<String, Object> selectResultConvert(Map<String, Object> map) throws PersistenceException  {
		// TODO Auto-generated method stub
		return convertDao.selectResultConvert(map);
	}

	
	@Override
	public List<Map<String, Object>> selectChatHistory(String logChatId) throws PersistenceException  {
		// TODO Auto-generated method stub
		return convertDao.selectChatHistory(logChatId);
	}
	
	@Override
	public List<Map<String, Object>> selectChatHistoryMap(Map<String, Object> map) throws PersistenceException  {
		// TODO Auto-generated method stub
		return convertDao.selectChatHistoryMap(map);
	}
	
	@Override
	public List<Map<String, Object>> selectChatHistoryId(String logChatId) throws PersistenceException  {
		// TODO Auto-generated method stub
		return convertDao.selectChatHistoryId(logChatId);
	}
	
	@Override
	public IchatVO selectResultMapping(IchatVO pVO) throws PersistenceException {
		
		return convertDao.selectResultMapping(pVO);
	}
	
	@Override
	public List<ResultBuilderVO> selectResultMapByScenario(IchatVO pVO) throws PersistenceException {
		
		return convertDao.selectResultMapByScenario(pVO);
	}
	
	@Override
	public UploadImageVO selectUploadImage(UploadImageVO pVO) throws PersistenceException {
		return convertDao.selectUploadImage(pVO);
	}

}
