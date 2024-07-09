package egovframework.iChat.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.ichat.model.ResultBuilderVO;
import egovframework.iChat.web.dao.CachedDao;
import egovframework.iChat.web.service.CachedService;

@Service("cachedService")
public class CachedServiceImpl implements CachedService {
	
	@Autowired CachedDao cachedDao;
	
	
	@Override
	public List<String> selectScenarioList(String projectId){
		return cachedDao.selectScenarioList(projectId);
	}
	
	@Override
	public ResultBuilderVO selectScenarioId(IchatVO pVO){
		return cachedDao.selectScenarioId(pVO);
	}
}
