package egovframework.iChat.web.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.web.dao.AutoDao;
import egovframework.iChat.web.service.AutoService;

@Service("autoService")
public class AutoServiceImpl implements AutoService {
	
	@Autowired AutoDao autoDao;
	
	@Override
	public List autoComplete(Map<String, Object> map) throws PersistenceException {
		return autoDao.autoComplete(map);
	}
}
