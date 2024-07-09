package egovframework.iChat.ichat.model;

import lombok.Data;

@Data
public class ChatLogVO {
	String detailSeq;
	String logChatid;
	String logSession;
	String logQuery;
	String logAnswer;
    String logIntent;
    String logType;
    String logAnswerMsg;
    String logAnswerOpt;
    String logIp;
    String regDate;
    
    String type;
    String message;
    String optionList;
}
