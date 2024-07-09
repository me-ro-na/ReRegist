package egovframework.iChat.web.service;

import java.util.List;
import java.util.Map;

import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.ichat.model.ResultBuilderVO;
import egovframework.iChat.ichat.model.UploadImageVO;
import org.apache.ibatis.exceptions.PersistenceException;

public interface ConvertService {

	Map<String, Object> selectQueryConvert(Map<String, Object> map) throws PersistenceException ;
	
	Map<String, Object> selectResultConvert(Map<String, Object> map) throws PersistenceException ;
	
	List<Map<String, Object>> selectChatHistory(String logChatId) throws PersistenceException ;

	List<Map<String, Object>> selectChatHistoryMap(Map<String, Object> map) throws PersistenceException ;
	
	List<Map<String, Object>> selectChatHistoryId(String logChatId) throws PersistenceException ;
	
	IchatVO selectResultMapping(IchatVO pVO) throws PersistenceException;
	
	UploadImageVO selectUploadImage(UploadImageVO pVO) throws PersistenceException ;

	List<ResultBuilderVO> selectResultMapByScenario(IchatVO pVO) throws PersistenceException ; 
		
}