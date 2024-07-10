package egovframework.iChat.web.model;

import egovframework.iChat.ichat.Intent.IntentType;
import lombok.Data;

@Data
public class ChatAppend {

	IntentType nodeType;
	String timeStamp;
	String message;
	Option[] optionList;

}
