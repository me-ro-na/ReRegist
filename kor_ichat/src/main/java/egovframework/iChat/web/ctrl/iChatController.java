package egovframework.iChat.web.ctrl;

//import static com.spring.ichat.service.common.WNUtils.getCheckReqXSS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import egovframework.iChat.common.exception.IChatException;
import egovframework.iChat.common.service.CommonService;
import egovframework.iChat.common.util.AES256Util;
import egovframework.iChat.common.util.SequenceUtil;
import egovframework.iChat.common.view.FileDownloadView;
import egovframework.iChat.ichat.service.IChatService;
import egovframework.iChat.ichat.service.PropertiesService;
import egovframework.iChat.sportsApi.dto.LoginVO;
import egovframework.iChat.sportsApi.service.SportsDbService;
import egovframework.iChat.web.service.ConvertService;
import egovframework.iChat.web.service.FeedbackService;
import egovframework.iChat.web.service.QuickMenuService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class iChatController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(iChatController.class);

	@Autowired IChatService iChatService;
	@Autowired ConvertService convertService;
	@Autowired PropertiesService propertiesService;
	@Autowired CommonService commonService;
	@Autowired FeedbackService feedbackService;
	@Autowired QuickMenuService quickMenuService;
	@Autowired SportsDbService sportsDbService;
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/ichat", method = RequestMethod.GET)
	public String home(Device device, HttpServletRequest request, Locale locale, Model model) {
		LOGGER.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);		
		String formattedDate = dateFormat.format(date);	
		
		model.addAttribute("serverTime", formattedDate );
		String userAgent = request.getHeader("User-Agent");
		if (device != null && (device.isMobile() || device.isTablet() || isMobileDevice(userAgent))) {
			return "ichatMoTest";
		} else {
			return "ichatPcTest";
		}
		//return "ichatIndex";
	}

	private boolean isMobileDevice(String userAgent) {
		if (userAgent == null) {
			return false;
		}
		// User-Agent 문자열에 모바일 장치와 관련된 키워드가 있는지 확인
		String[] mobileKeywords = {"Mobi", "Android", "iPhone", "iPad", "iPod"};
		for (String keyword : mobileKeywords) {
			if (userAgent.contains(keyword)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "/iChat", method = {RequestMethod.POST, RequestMethod.GET})
	public String iChat(Device device, Model model, HttpServletRequest request, HttpSession session) throws Exception {
		String type = request.getParameter("type");
		String key = request.getParameter("key");
		LoginVO sso = (LoginVO) session.getAttribute("loginVO");
		String userId = "NOT_LOGIN";
		AES256Util aes256 = new AES256Util();

		List<String> scripts = new ArrayList<>();
		List<Map<String, String>> templates = new ArrayList<>();
		ServletContext context = request.getSession().getServletContext();
		String filePath = context.getRealPath("resources/js/api");
		for (File file : FileUtils.listFiles(new File(filePath), new String[]{"js"}, false)) {
			if(file.isFile() && !"helper.js".equals(file.getName())) {
				scripts.add(file.getName());
			}
		}
		model.addAttribute("scripts", scripts);
		filePath = context.getRealPath("resources/api_html");
		for (File file : FileUtils.listFiles(new File(filePath), new String[]{"html"}, false)) {
			HashMap<String, String> template = new HashMap<>();
			template.put("id", file.getName().split("\\.")[0]);
			template.put("contents", FileUtils.readFileToString(file, StandardCharsets.UTF_8));
			templates.add(template);
		}
		model.addAttribute("templates", templates);
		
		boolean cbotStatus = true;

		URLCodec codec = new URLCodec();
		
		String view = "ichat/ichat";
		try {
			if (device != null && (device.isMobile() || device.isTablet())) {
				session.setAttribute("channel", "MO");
			} else {
				session.setAttribute("channel", "PC");
			}
			if (key != null) {
				if (sso != null && !key.equals(sso.getSessionId())) {
					session.invalidate();
					session = request.getSession();
				}
				//LOGGER.info(">>>>>> key is : {}", key);
				LoginVO tryKey = new LoginVO();
				tryKey.setKey(key);
				LoginVO tmpSso = null;
				try {
					tmpSso = sportsDbService.ssoProcess(tryKey, request.getRemoteAddr());
					//LOGGER.info(">>>>>> key sso : {}", sso);
				} catch (IChatException e) {
					LOGGER.info(">>>>>> key error : {} - {}", tmpSso, e.getMessage());
				}
				if (tmpSso != null) {
					sso = tmpSso;
				}
				session.setAttribute("loginVO", sso);
				if (sso != null) {
					userId = sso.getUserId();
				}
			}

			session.setAttribute("encUserId", aes256.aesEncode(userId));
			session.setAttribute("userId", userId);

			String logSeq = (String) session.getAttribute("logSeq");
			LOGGER.info(">>>>>> session is : {}", logSeq);
			//세션에 log Sequence 값이 없을 경우;
			if (logSeq == null) {
				logSeq = SequenceUtil.getDBKey();
				session.setAttribute("logSeq", logSeq);
				LOGGER.info(">>>>>> new logSeq is : {}", logSeq);
			}

			if (type != null) {
				session.setAttribute("type", type);
			} else {
				session.setAttribute("type", "");
			}

			model.addAttribute("logSeq", logSeq);
			model.addAttribute("type", type);

			String storageDateTime = new SimpleDateFormat("yyyyMMddHHmmss",Locale.KOREA).format(new Date());

			model.addAttribute("cbotStatus",  cbotStatus);
			if (logSeq != null) {
				model.addAttribute("encChatLog",  codec.encode(aes256.aesEncode(logSeq)));
			} else {
				model.addAttribute("encChatLog",  "");
			}
			
			//프로젝트  관리자 / 사용자 구분
			String code = request.getParameter("code");
			String projectId = propertiesService.dmProjectId;
			String projectCode = propertiesService.chatProjectCode + storageDateTime.substring(0, 8);
			//String projectCode = "대한진진20211123";
			//projectCode = sha256Encrypt(projectCode);
			projectCode = getSHA256(projectCode);
			if(code != null && !"null".equals(code) ) {
				LOGGER.debug(code + ":  code is not null");
				LOGGER.debug(projectCode + ":  projectCode is not null");
				if(code.equals(projectCode)) {
					projectId = propertiesService.dmAdminProjectId;
					view = "ichat/adminIchat";
					model.addAttribute("code", code);
				}
			}
			
			Map<String, Object> qMap = new HashMap<>();
			qMap.put("projectId", projectId);
			List<Map<String, String>> quickMenuList = quickMenuService.selectQuickMenuList(qMap);
			System.out.println("quickMenu :::::::::: " +  quickMenuList.size());
			model.addAttribute("quickMenuList", quickMenuList);
			model.addAttribute("projectId", projectId);
			
			//model.addAttribute("encChatLog",  codec.encode(aes256.aesEncode(logSeq)));
			model.addAttribute("serverTime",  storageDateTime);
			//model.addAttribute("userId",  session.getAttribute("encUserId"));
			if (sso != null) {
				model.addAttribute("userId",  sso.getUserId());
			} else {
				model.addAttribute("userId",  "");
			}
			Map<String, String> feedbackQuestion = feedbackService.selectFeedbackSettingDetail(projectId);
			model.addAttribute("feedbackQuestion", feedbackQuestion);
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException
				 | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				 | BadPaddingException | EncoderException | NullPointerException e)
		{
			LOGGER.error("[iChat page Error] page : /iChat");
		}
		
		return view;
	}
	
	@Autowired
	FileDownloadView fileDownloadView;
	
	@RequestMapping("/fileManageDownload")
	public ModelAndView ichatIntraFileDownload(HttpServletRequest request, HttpSession session) throws Exception{
		ModelAndView mav = new ModelAndView(fileDownloadView);
		
		String profiles = System.getProperty("spring.profiles.active");
		String[] notLocalEnvs = {"dev","test","prod"};
		
		String saveFileName = StringUtils.trimToEmpty(request.getParameter("saveFileName"));
//		saveFileName = saveFileName.replaceAll("[/\\\\.&]","");
		
		if (saveFileName == null || !saveFileName.matches("^\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}[.]\\w+$")) {
			throw new FileNotFoundException("File can't read(파일을 찾을 수 없습니다)");
		}
		
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		String projectId = request.getParameter("projectId");
		map.set("projectId", projectId);
		map.set("saveFileName", saveFileName);
		HttpEntity<MultiValueMap<String, Object>> reqMap = new HttpEntity<MultiValueMap<String, Object>>(map, new HttpHeaders());
		Map resultMap = null;
		try {
				String managerUrl = propertiesService.managerUrl; 
				StringBuffer sb = new StringBuffer(managerUrl);
				sb.append("/wiseichat/fileManage/getFileInfo.do");
				
				LOGGER.debug(managerUrl + ":<  managerUrl");
				resultMap = new RestTemplate().postForObject(sb.toString(), reqMap, Map.class);
		} catch (RestClientException e) {
			throw new FileNotFoundException("File can't read(파일을 찾을 수 없습니다)");
		}
		if (resultMap != null && !StringUtils.isBlank((String) resultMap.get("orgFileName"))) {
			String uploadPath = (String) resultMap.get("uploadPath");
			LOGGER.debug("fileInfo : " + resultMap.toString());
			LOGGER.debug("FilePath : " + uploadPath);
			LOGGER.debug("FileName : "+ saveFileName);
			/*
			 * if (profiles.contains("local") ||
			 * Arrays.asList(notLocalEnvs).stream().allMatch(e->!profiles.contains(e))) {
			 * uploadPath = "C:\\fileupload"; }
			 */
			String orgFileName = (String) resultMap.get("orgFileName");
			
			File downloadFile = new File(uploadPath, saveFileName);
			if(!downloadFile.canRead()) {
				throw new FileNotFoundException("File can't read(파일을 찾을 수 없습니다)");
			}
			mav.addObject("downloadFile", downloadFile);
			mav.addObject("orgFileName", orgFileName);
		}
		
		return mav;
	}

	public String sha256Encrypt(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.reset();
		md.update(propertiesService.salt.getBytes());
        md.update(text.getBytes());

        return bytesToHex(md.digest());
    }
	
	private String bytesToHex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
		    builder.append(String.format("%02x", b));
		}
		
		return builder.toString();
	}
	
	public String getSHA256(String text) {
		try{

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			//digest.reset();
			//digest.update(propertiesService.salt.getBytes());
			byte[] hash = digest.digest(text.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			System.out.println("텍스트 : " + text);
			System.out.println("암호화 : " + hexString.toString());
			return hexString.toString();
			
		} catch(GeneralSecurityException | UnsupportedEncodingException ex){
			throw new RuntimeException(ex);
		}
	}
	
	
}
