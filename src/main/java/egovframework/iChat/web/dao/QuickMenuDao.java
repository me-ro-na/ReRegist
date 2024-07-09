package egovframework.iChat.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;

import egovframework.iChat.common.dao.AbstractDao;

@Repository("quickMenuDao")
public class QuickMenuDao  extends AbstractDao {
	
	@SuppressWarnings("unchecked") //미확인 오퍼레이션과 관련된 경고를 억제합니다.
	
	public List selectQuickMenuList(Map<String, Object> map) throws PersistenceException {		
		return selectList("quickMenu.selectQuickMenuList", map);
	}
}
