package egovframework.iChat.web.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.iChat.common.dao.AbstractDao;
import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.ichat.model.ResultBuilderVO;

@Repository("cachedDao")
public class CachedDao extends AbstractDao {
	
	@SuppressWarnings("unchecked") //미확인 오퍼레이션과 관련된 경고를 억제합니다.
	
	public List<String> selectScenarioList(String projectId){
		return selectList("cached.selectScenarioList", projectId);
	}
	
	public ResultBuilderVO selectScenarioId(IchatVO pVO){
		return (ResultBuilderVO) selectOne("cached.selectScenarioId", pVO);
	}
}