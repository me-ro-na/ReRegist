package egovframework.iChat.web.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;

import egovframework.iChat.common.dao.AbstractDao;

@Repository("searchLogDao")
public class SearchLogDao extends AbstractDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatLogDao.class);

	/*
	 * 검색 링크 클릭했을 경우
	 */
	public void insertLogSearch(Map<String, Object> map) throws PersistenceException {		
		insert("searchLog.insertLogSearch", map);
	}
}
