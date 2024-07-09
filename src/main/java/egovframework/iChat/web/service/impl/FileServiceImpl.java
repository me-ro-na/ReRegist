package egovframework.iChat.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.iChat.web.dao.FileDao;
import egovframework.iChat.web.model.FileVO;
import egovframework.iChat.web.service.FileService;

@Service("FileService")
public class FileServiceImpl implements FileService {
	
	@Autowired
	FileDao fileDao;
	
	@Override
	public FileVO selectAttachFileDetail(String seq) {
		return fileDao.selectAttachFileDetail(seq);
	}

}
