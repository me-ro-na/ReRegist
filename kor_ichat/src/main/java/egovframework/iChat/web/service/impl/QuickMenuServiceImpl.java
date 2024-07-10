package egovframework.iChat.web.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.web.dao.QuickMenuDao;
import egovframework.iChat.web.service.QuickMenuService;

@Service("quickMenuService")
public class QuickMenuServiceImpl implements QuickMenuService {
	
	@Autowired QuickMenuDao quickMenuDao;
	
	@Override
	public List<Map<String, String>> selectQuickMenuList(Map<String, Object> map) throws PersistenceException {
		return quickMenuDao.selectQuickMenuList(map);
	}
}
