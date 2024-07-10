package egovframework.iChat.sportsApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.stereotype.Repository;

import egovframework.iChat.common.dao.AbstractDao;
import egovframework.iChat.sportsApi.entity.ApiCodeEntity;
import egovframework.iChat.sportsApi.entity.LogSportsApiEntity;


@Repository("sportsApiDao")
public class SportsApiDao extends AbstractDao{
	
	
	/**
	 * API 목록 조회
	 */
	@SuppressWarnings("unchecked") //미확인 오퍼레이션과 관련된 경고를 억제
	public List<ApiCodeEntity> selectApiInfo(Map<String, Object> map) throws PersistenceException {
		List<ApiCodeEntity> list = selectList("sportsApi.selectApiInfo", map);
		return list;
	}
	
	
	/**
	 * API 로그 기록
	 */
	
	public void insertSportsApiLog(LogSportsApiEntity log) throws PersistenceException {
		insert("sportsApi.insertSportsApiLog", log);
	}

	/**
	 * API STEP Update
	 */
	public void updateSportsApiStep(LogSportsApiEntity entity) throws PersistenceException {
		update("sportsApi.updateSportsApiStep", entity);
	}

	public LogSportsApiEntity selectSportsApiStep(LogSportsApiEntity entity) throws PersistenceException {
		return (LogSportsApiEntity) selectOne("sportsApi.selectSportsApiStep", entity);
	}
}
