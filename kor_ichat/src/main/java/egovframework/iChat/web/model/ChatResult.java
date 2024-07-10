package egovframework.iChat.web.model;

import egovframework.iChat.ichat.Intent.IntentType;
import lombok.Data;

@Data
public class ChatResult {

	IntentType nodeType;
	String timeStamp;
	String message;
	String intentNm;
	String mainUserQuery;
	Option[] optionList;
}
