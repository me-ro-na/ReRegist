package egovframework.iChat.web.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import egovframework.iChat.ichat.model.IchatVO;
import egovframework.iChat.ichat.model.ResultBuilderVO;

public interface CachedService {
	
	@Cacheable("scenarioCache")
	public ResultBuilderVO selectScenarioId(IchatVO pVO);
	
	@Cacheable("scenarioCache")
	public List<String> selectScenarioList(String projectId);
	
}
