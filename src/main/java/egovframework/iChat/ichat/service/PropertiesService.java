package egovframework.iChat.ichat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("propertiesService")
public class PropertiesService {

	@Value("#{dmProp['salt']}")
	public String salt;

	// dm
	@Value("#{dmProp['dm.protocol']}")
	public String dmProtocol;

	@Value("#{dmProp['dm.ip']}")
	public String dmIp;

	@Value("#{dmProp['dm.port']}")
	public String dmPort;

	@Value("#{dmProp['dm.project.id']}")
	public String dmProjectId;
	
	@Value("#{dmProp['dm.adminProject.id']}")
	public String dmAdminProjectId;
	
	@Value("#{dmProp['chat.projectCode']}")
	public String chatProjectCode;

	@Value("#{dmProp['dm.project.scenario.id']}")
	public String dmProjectScenarioId;

	@Value("#{dmProp['dm.api.common.dmApiCommonProjectList']}")
	public String dmApiCommonProjectList;

	@Value("#{dmProp['dm.api.common.sessionRequest']}")
	public String dmApiCommonSessionRequest;

	@Value("#{dmProp['dm.api.common.sessionDelRequest']}")
	public String dmApiCommonSessionDelRequest;

	@Value("#{dmProp['dm.api.common.sessionValidation']}")
	public String dmApiCommonSessionValidation;

	@Value("#{dmProp['dm.api.common.wiseIChatResponse']}")
	public String dmApiCommonWiseIChatResponse;

	@Value("#{dmProp['dm.api.intent.detail']}")
	public String dmApiIntentDetail;

	@Value("#{dmProp['dm.api.demo.simulation']}")
	public String dmApiDemoSimulation;

	@Value("#{dmProp['dm.api.history.daily']}")
	public String dmApiHistorySDaily;
	
	//manager
	@Value("#{dmProp['manager.url']}")
	public String managerUrl;

	// common
	@Value("#{dmProp['common.api.message.check.protocol']}")
	public String commonApiMessageCheckProtocol;

	@Value("#{dmProp['common.api.pattern.requery.text.ko']}")
	public String commonApiPatternRequeryTextKo;

	@Value("#{dmProp['common.api.pattern.requery.text.en']}")
	public String commonApiPatternRequeryTextEn;

	@Value("#{dmProp['common.api.pattern.requery.text.ja']}")
	public String commonApiPatternRequeryTextJa;

	@Value("#{dmProp['common.api.pattern.requery.text.zh']}")
	public String commonApiPatternRequeryTextZh;

	@Value("#{dmProp['common.api.pattern.requery.link.ko']}")
	public String commonApiPatternRequeryLinkKo;

	@Value("#{dmProp['common.api.pattern.requery.link.en']}")
	public String commonApiPatternRequeryLinkEn;

	@Value("#{dmProp['common.api.pattern.requery.link.ja']}")
	public String commonApiPatternRequeryLinkJa;

	@Value("#{dmProp['common.api.pattern.requery.link.zh']}")
	public String commonApiPatternRequeryLinkZh;

	@Value("#{dmProp['common.api.pattern.requery.image.ko']}")
	public String commonApiPatternRequeryImageKo;

	@Value("#{dmProp['common.api.pattern.requery.image.en']}")
	public String commonApiPatternRequeryImageEn;

	@Value("#{dmProp['common.api.pattern.requery.image.ja']}")
	public String commonApiPatternRequeryImageJa;

	@Value("#{dmProp['common.api.pattern.requery.image.zh']}")
	public String commonApiPatternRequeryImageZh;

	@Value("#{dmProp['common.api.requery.text.split.charcter']}")
	public String commonApiRequeryTextSplitCharcter;

	@Value("#{dmProp['common.api.requery.text.split.charcter.ja']}")
	public String commonApiRequeryTextSplitCharcterJa;

	@Value("#{dmProp['common.api.requery.image.split.charcter']}")
	public String commonApiRequeryImageSplitCharcter;

	@Value("#{dmProp['common.api.requery.link.split.charcter']}")
	public String commonApiRequeryLinkSplitCharcter;

	@Value("#{dmProp['common.api.dynamic.answer.empty.ko']}")
	public String commonApiDynamicAnswerEmptyKo;

	@Value("#{dmProp['common.api.dynamic.answer.country.empty.ko']}")
	public String commonApiDynamicAnswerCountryEmptyKo;

	@Value("#{dmProp['common.api.dynamic.answer.link.empty.ko']}")
	public String commonApiDynamicAnswerAndLinkEmptyKo;

	@Value("#{dmProp['common.api.dynamic.answer.empty.FlightName.ko']}")
	public String commonApiDynamicAnswerEmptyFlightNameKo;

	@Value("#{dmProp['common.api.dynamic.answer.empty.en']}")
	public String commonApiDynamicAnswerEmptyEn;

	@Value("#{dmProp['common.api.dynamic.answer.empty.ja']}")
	public String commonApiDynamicAnswerEmptyJa;

	@Value("#{dmProp['common.api.dynamic.answer.empty.zh']}")
	public String commonApiDynamicAnswerEmptyZh;

	@Value("#{dmProp['common.api.freeconversation.guide.en']}")
	public String commonApiFreeConversationGuideEn;

	@Value("#{apiProp['api.url.g1.adaptor']}")
	public String apiUrlG1Adaptor;
	@Value("#{apiProp['api.url.g1.target']}")
	public String apiUrlG1Target;
	@Value("#{apiProp['api.url.g1.target.reg_res']}")
	public String apiUrlG1TargetRegRes;
	@Value("#{apiProp['api.url.g1.target.cert']}")
	public String apiUrlG1TargetCert;

	@Value("#{apiProp['api.url.pinfo.adaptor.web']}")
	public String apiUrlPinfoAdaptorWeb;
	@Value("#{apiProp['api.url.pinfo.adaptor.mobile']}")
	public String apiUrlPinfoAdaptorMobile;
	@Value("#{apiProp['api.url.pinfo.r.target.player']}")
	public String apiUrlPinfoRTargetPlayer;
	@Value("#{apiProp['api.url.pinfo.r.target.officer']}")
	public String apiUrlPinfoRTargetOfficer;
	@Value("#{apiProp['api.url.pinfo.r.target.referee']}")
	public String apiUrlPinfoRTargetReferee;
	@Value("#{apiProp['api.url.pinfo.r.target.manager']}")
	public String apiUrlPinfoRTargetManager;
	@Value("#{apiProp['api.url.pinfo.a.target.player']}")
	public String apiUrlPinfoATargetPlayer;
	@Value("#{apiProp['api.url.pinfo.a.target.officer']}")
	public String apiUrlPinfoATargetOfficer;
	@Value("#{apiProp['api.url.pinfo.a.target.referee']}")
	public String apiUrlPinfoATargetReferee;
	@Value("#{apiProp['api.url.pinfo.a.target.manager']}")
	public String apiUrlPinfoATargetManager;

	@Value("#{apiProp['api.url.hnr.adaptor']}")
	public String apiUrlHnrAdaptor;
	@Value("#{apiProp['api.url.hnr.target']}")
	public String apiUrlHnrTarget;
	
	//Sports API
    @Value("#{apiProp['api.protocol']}")
    public String apiProtocol;
    
    @Value("#{apiProp['api.domain']}")
    public String apiDomain;
    
    @Value("#{apiProp['api.key.value']}")
    public String apiKeyValue;
    
    @Value("#{apiProp['api.success.code']}")
    public String apiSuccessCode;
    
    public String apiSuccessMsg = "정상";

}
