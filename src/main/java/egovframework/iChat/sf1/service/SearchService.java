package egovframework.iChat.sf1.service;

import egovframework.iChat.ichat.model.IChatResp;
import egovframework.iChat.ichat.service.IChatService;
import egovframework.iChat.ichat.service.PropertiesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Configuration
public class SearchService {

    @Autowired
    private PropertiesService propertiesService;

    private String getDmUrl() {
        return propertiesService.dmProtocol + propertiesService.dmIp + ":" + propertiesService.dmPort;
    }

    public String getIChatMorph(String modQry) {
        List<String> nounsList = new ArrayList<>();

        String url = getDmUrl() + propertiesService.dmApiDemoSimulation;
        JSONObject obj = new JSONObject();
        obj.put("projectId", propertiesService.dmProjectId.trim());
        obj.put("query", modQry);

        JSONObject simulResp = IChatService.getRestPost(url, obj, IChatResp.class).getResponse();
        if (simulResp == null) {
            return "";
        }

        String morphAnalysis = (String) simulResp.get("morphAnalysis");
        if (morphAnalysis != null) {
            { // 띄어쓰기 혹은 구분이 없는 연속된 명사 하나로 추출
                int curQueryPos = 0;

                Iterator<String> it = Arrays.stream(morphAnalysis.split(" ")).iterator();
                String nouns = null;
                while (it.hasNext()) {
                    String temp = it.next();

                    String word = temp.split("/")[0];
                    String morph = "";
                    if (temp.split("/").length > 1) {
                        morph = temp.split("/")[1];
                    }

                    boolean isNouns = false;
                    for (String checkMorph : new String[]{"NN"/*명사*/,"XSN"/*명사 파생 접미사*/,"XR"/*어근*/, "SL"/*외국어 존재시 필요*/}) {
                        if (morph.startsWith(checkMorph)) {
                            isNouns = true;
                        }
                    }
                    for (String checkMorph : new String[]{"NNB"/*의존 명사*/,"NNM"/*단위 의존 명사*/}) {
                        if (morph.startsWith(checkMorph)) {
                            isNouns = false;
                        }
                    }
                    if (isNouns && StringUtils.startsWith(modQry.substring(curQueryPos), "*")) {
                        isNouns = false;
                    }

                    // 구분이 있으면 해당 명사 종결
                    String checkQuery = modQry.substring(curQueryPos);

                    if (!checkQuery.startsWith(word)) {
                        if (nouns != null) {
                            nounsList.add(nouns);
                            nouns = null;
                        }
                        // 다음 매칭까지 질의 처리 위치 이동
                        while (!modQry.substring(curQueryPos).startsWith(word)) {
                            curQueryPos++;
                        }
                        curQueryPos += word.length();
                    } else {
                        curQueryPos += word.length();
                    }

                    // 명사면 처리중 명사에 붙이기
                    if (isNouns) {
                        nouns = StringUtils.join(nouns, word);
                    }

                    // 현재가 명사가 아니고 이전에 명사였으면 추가하고 초기화
                    if (!isNouns && nouns != null) {
                        nounsList.add(nouns);
                        nouns = null;
                    }

                    // 마지막인데 추가할 내용이 있으면 추가
                    if (!it.hasNext() && nouns != null) {
                        nounsList.add(nouns);
                        break;
                    }
                }
            }
        }
        nounsList = nounsList.stream().distinct().collect(Collectors.toList());
        return StringUtils.join(nounsList, " ");
    }

}
