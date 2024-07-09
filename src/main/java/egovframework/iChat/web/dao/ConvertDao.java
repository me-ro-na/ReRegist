package egovframework.iChat.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Repository;

import egovframework.iChat.common.dao.AbstractDao;
import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.ichat.model.ResultBuilderVO;
import egovframework.iChat.ichat.model.UploadImageVO;

@Repository("convertDao")
public class ConvertDao extends AbstractDao {

	public Map<String, Object> selectQueryConvert(Map<String, Object> map) throws PersistenceException, TooManyResultsException {
		return (Map<String, Object>) selectOne("convert.selectQueryConvert", map);
	}
	
	public Map<String, Object> selectResultConvert(Map<String, Object> map) throws PersistenceException, TooManyResultsException {
		return (Map<String, Object>) selectOne("convert.selectResultConvert", map);
	}

	public List<Map<String, Object>> selectChatHistory(String logChatId) {
		return (List<Map<String, Object>>) selectList("convert.selectChatHistory", logChatId);
	}
	
	public List<Map<String, Object>> selectChatHistoryMap(Map<String, Object> map) {
		return (List<Map<String, Object>>) selectList("convert.selectChatHistoryMap", map);
	}
	
	public List<Map<String, Object>> selectChatHistoryId(String logChatId) {
		return (List<Map<String, Object>>) selectList("convert.selectChatHistoryId", logChatId);
	}
	
	public IchatVO selectResultMapping(IchatVO pVO) throws PersistenceException {
		return (IchatVO) selectOne("convert.selectResultMapping",pVO);
	}
	
	public UploadImageVO selectUploadImage(UploadImageVO pVO) throws PersistenceException {
		return (UploadImageVO) selectOne("convert.selectUploadImage", pVO);
	}

	public List<ResultBuilderVO> selectResultMapByScenario(IchatVO pVO) {
		return (List<ResultBuilderVO>) selectList("convert.selectResultMapByScenario", (Object) pVO);
	}
	
}
