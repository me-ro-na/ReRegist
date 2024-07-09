package egovframework.iChat.web.ctrl.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.iChat.common.model.CommandMap;
import egovframework.iChat.ichat.service.PropertiesService;
import egovframework.iChat.web.service.AutoService;

@Controller
public class AutoAjaxController extends AjaxController {

	@Autowired AutoService autoService;
	@Autowired PropertiesService propertiesService;
	// ㄱ      ㄲ      ㄴ      ㄷ      ㄸ      ㄹ      ㅁ      ㅂ      ㅃ      ㅅ      ㅆ      ㅇ      ㅈ      ㅉ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
    final static char[] CHOSUNG   = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
    // ㅏ      ㅐ      ㅑ      ㅒ      ㅓ      ㅔ      ㅕ      ㅖ      ㅗ      ㅘ      ㅙ      ㅚ      ㅛ      ㅜ      ㅝ      ㅞ      ㅟ      ㅠ      ㅡ      ㅢ      ㅣ
    final static char[] JWUNGSUNG = { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };
    // ㄱ      ㄲ      ㄳ      ㄴ      ㄵ      ㄶ      ㄷ      ㄹ      ㄺ      ㄻ      ㄼ      ㄽ      ㄾ      ㄿ      ㅀ      ㅁ      ㅂ      ㅄ      ㅅ      ㅆ      ㅇ      ㅈ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
    final static char[] JONGSUNG  = { 0,      0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

	
	
	@RequestMapping(value = "/ajax/getAutoComplete")
	@ResponseBody
	public Object getAutoComplete(CommandMap commandMap, HttpServletRequest request) throws Exception {
		request.setCharacterEncoding("UTF-8");
		String value = request.getParameter("value");
		String projectId= commandMap.get("projectId").toString();
		
		  int a, b, c; // 자소 버퍼: 초성/중성/종성 순
	        String result = "";
	        
	        if(value != null) {
		        for (int i = 0; i < value.length(); i++) {
		            char ch = value.charAt(i);
		            if (ch >= 0xAC00 && ch <= 0xD7A3) { // "AC00:가" ~ "D7A3:힣" 에 속한 글자면 분해
		                c = ch - 0xAC00;
		                a = c / (21 * 28);
		                c = c % (21 * 28);
		                b = c / 28;
		                c = c % 28;
		                result = result + CHOSUNG[a] + JWUNGSUNG[b];
		                if (c != 0) result = result + JONGSUNG[c] ; // c가 0이 아니면, 즉 받침이 있으면
		            } else {
		                result = result + ch;
		            }
		        }
	        }
	        
	        HashMap<String,Object> param = new HashMap<>();
	        param.put("result", result);
	        param.put("projectId",projectId);
	        
	        List<HashMap<String, String>> dblist = autoService.autoComplete(param);
		
		
			JSONArray arrayObj = new JSONArray();
			JSONObject jsonObj = null;
			ArrayList<String> resultlist = new ArrayList<String>();
			
			for (HashMap<String, String> str : dblist) {
				String name = str.get("auto_expr");
				resultlist.add(name);
			}
			for (String str : resultlist) {
				jsonObj = new JSONObject();
				jsonObj.put("data", str);
				arrayObj.add(jsonObj);
				
			}
			String s = arrayObj.toString();
			return makeResponse(commandMap, s);
		}

}

