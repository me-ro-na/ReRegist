package egovframework.iChat.web.service;

import org.apache.ibatis.exceptions.PersistenceException;

import java.util.List;
import java.util.Map;

public interface AutoService {
	List autoComplete(Map<String, Object> map) throws PersistenceException;
	
}
