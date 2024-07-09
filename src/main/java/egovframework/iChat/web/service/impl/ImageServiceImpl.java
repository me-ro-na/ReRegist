package egovframework.iChat.web.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.web.dao.AutoDao;
import egovframework.iChat.web.dao.ImageDao;
import egovframework.iChat.web.service.AutoService;
import egovframework.iChat.web.service.ImageService;

@Service("imageService")
public class ImageServiceImpl implements ImageService {
	
	@Autowired ImageDao imageDao;
	
	@Override
	public List imageList(String param) throws PersistenceException {
		return imageDao.imageList(param);
	}
}
