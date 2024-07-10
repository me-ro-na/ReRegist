package egovframework.iChat.web.dao;


import org.springframework.stereotype.Repository;

import egovframework.iChat.common.dao.AbstractDao;
import egovframework.iChat.web.model.FileVO;

@Repository("FileDao")
public class FileDao extends AbstractDao {
	@SuppressWarnings("unchecked") //미확인 오퍼레이션과 관련된 경고를 억제합니다.
	
	public FileVO selectAttachFileDetail(String seq) {
		return (FileVO) selectOne("file.selectAttachFileDetail", seq);
	}
}
