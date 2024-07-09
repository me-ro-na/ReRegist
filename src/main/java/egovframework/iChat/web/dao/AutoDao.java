package egovframework.iChat.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Repository;

import egovframework.iChat.common.dao.AbstractDao;

@Repository("autoDao")
public class AutoDao  extends AbstractDao {

	
	
	@SuppressWarnings("unchecked") //미확인 오퍼레이션과 관련된 경고를 억제합니다.
	
	public List autoComplete(Map<String, Object> map) throws PersistenceException {
		return selectList("auto.selectList", map);
	}
}
