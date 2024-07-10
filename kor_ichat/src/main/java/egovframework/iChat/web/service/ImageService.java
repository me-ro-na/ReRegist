package egovframework.iChat.web.service;

import org.apache.ibatis.exceptions.PersistenceException;

import java.util.List;
import java.util.Map;

public interface ImageService {
	List imageList(String param) throws PersistenceException;
	
}
