package egovframework.iChat.web.model;

import java.util.List;

import egovframework.iChat.ichat.Intent.IntentType;
import egovframework.iChat.ichat.model.IchatVO;
import lombok.Data;

@Data
public class OutData {

	DataType type; //일상대화, 특정대화, 기타
	String   session;
	ChatResult result;
	ChatAppend append;
	//IchatVO subAnswer;
	String searchKeyword;
	String subAnswer;
	Carousel[] caro;
	Boolean request = false;
	String userId;
}
