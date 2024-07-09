<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<spring:eval var="env" expression="@environment.getActiveProfiles()"></spring:eval>
<c:set var="envs" value="${StringUtils.join(env, ',')}" />
<html lang="KR">
<head>
    <meta charset="UTF-8">
    <meta name="format-detection" content="telephone=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>대한체육회챗봇</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/default.css?ver=202404">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/lib/slick.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/lib/slick-theme.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/chatbot.css?ver=202404">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/lib/datepicker.css?ver=2">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/lib/swiper-bundle.min.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/dev.css?ver=<%=System.currentTimeMillis()%>">
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath }/resources/img/favicon.ico?ver=202404" />
</head>

<body onblur="window.focus()">
<input type="hidden" id="projectId" name="projectId" value="<c:out value='${projectId}'/>">
<input type="hidden" id="userId" name="userId" value="<c:out value='${sessionScope.loginVO.userId}'/>">
<input type="hidden" id="startUserId" name="startUserId" value="<c:out value='${sessionScope.loginVO.userId}'/>">
<input type="hidden" id="channel" name="channel" value="<c:out value='${sessionScope.channel}'/>">
<!-- 스킵네비게이션 -->
<div id="skip" class="skip">
    <a href="#sentence"><span>채팅대화창 바로가기</span></a>
</div>
<!-- //스킵네비게이션 -->
<!-- chat_wrap -->
<div class="chat_wrap">
    <!-- chat_box -->
    <div class="chat_box">
        <!-- chat_top -->
        <div class="chat_top">
            <h1 class="h_tit">
                    <span class="chat_name">
                        <a href="javascript:clear();" title="챗봇 첫화면으로 이동">알리</a>
                    </span>
            </h1>
            <div class="top_btn left">
                <button type="button" onclick="appendMainMenu();" class="menu left"><span title="처음으로" class="ico_comm ico_home">홈</span></button>
            </div>
            <div class="top_btn right">
                <button type="button" onclick="openPopup('guide');" class="menu right"><span title="이용안내" class="ico_comm ico_guide">챗봇 이용안내</span></button>
                <button type="button" onclick="openPopup('survey');" class="menu right"><span class="ico_comm ico_question">최근 문의 목록</span></button>
                <!-- 
                <c:if test="${feedbackQuestion eq null || feedbackQuestion.QUESTION_YN eq 'N'}">
                    <button type="button" onclick="finish();" class="menu right"><span title="닫기" class="ico_comm ico_list">챗봇 만족도평가</span></button>
                </c:if>
                -->
            </div>
        </div>
        <!--  //chat_top -->

        <!-- chat_cont_wrap -->
        <div class="chat_cont_wrap">
            <!-- chat_cont -->
            <div class="chat_cont">

                <div class="bot_wrap menu">
                    <span class="ico_comm ico_logo"></span>
                    <div class="bot_box" id="first_menu">
                        <div class="bot_txt">
                            <div class="chat_talk font">
                                <c:if test="${sessionScope.loginVO ne null}"><c:out value="${sessionScope.loginVO.userName}" />님 </c:if>
                                안녕하세요, 저는 대한체육회 AI 상담사 알리에요!<br>
                                체육인 등록, 증명서 발급 등의 정보를 안내할 수 있어요.<br>
                                <br>
                                질문을 직접 입력하거나 아래 메뉴를 선택해보세요~
                            </div>
                        </div>
                        <ul class="chat_menu font" id="chat_menu">
                            <c:forEach var="item" items="${quickMenuList}">
                                <li>
                                    <a href="#" onclick="ichat_question('${item.MAINUSERQUERY}'); return false;" class="icon_box">
                                        <div class="icon_outer">
                                            <div class="icon_inner" style="height:97px;">
                                                <span class="icon"><img src="${pageContext.request.contextPath}/resources/upload/${item.IMAGE_FILESERVERNAME}"/></span>
                                                <div class="m_txt"><c:out value="${item.QUICKMENU_NM}"/></div>
                                            </div>
                                        </div>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                        <span class="time"><span id="main_time" class="screen_out">대화전송시간</span>오후 2:00</span>
                    </div>
                </div>
                <c:if test="${not empty loginVO.userId and (fn:contains(envs, 'local') or fn:contains(envs, 'dev')) and false}">
                <div class="test_box">
                    <div style="color: #878787; font-weight: bold; margin-bottom: 10px;">연계 테스트 버튼</div>
                    <button type="button" onclick="ichat_question('경기인 신청 이력 조회');">#신청이력</button>
                    <button type="button" onclick="ichat_question('훈·포장 신청 이력 조회');">#훈포장</button>
                    <button type="button" onclick="ichat_question('증명서 발급 이력 조회');">#증명서(19년)</button>
                    <button type="button" onclick="ichat_question('기타 신청 이력 조회');">#기타이력(22년)</button>
                    <button type="button" onclick="ichat_question('경기인 등록 이력 조회');">#등록이력</button>
                    <button type="button" onclick="ichat_question('훈련 이력 조회');">#훈련이력(23년)</button>
                    <button type="button" onclick="ichat_question('선수실적 조회');">#선수실적</button>
                    <button type="button" onclick="ichat_question('나의 대회 경기 영상 조회');">#경기영상(19년)</button>
                    <button type="button" onclick="ichat_question('온라인 교육 수료 현황');">#교육</button>
                    <button type="button" onclick="ichat_question('대한체육회 교육이력 조회');">#기타교육</button>
                    <button type="button" onclick="ichat_question('이적 신청 이력 조회');">#이적이력</button>
                    <button type="button" onclick="ichat_question('경기인 재등록 방법 알려줘'); return false;">#재등록</button>
                    <button type="button" onclick="ichat_question('경기인 재등록 선수 등록정보 확인하기');">#재등록(선수)</button>
                    <button type="button" onclick="ichat_question('경기인 재등록 지도자 등록정보 확인하기');">#재등록(지도자)</button>
                    <button type="button" onclick="ichat_question('경기인 재등록 심판 등록정보 확인하기');">#재등록(심판)</button>
                    <button type="button" onclick="ichat_question('경기인 재등록 선수관리담당자 등록정보 확인하기');">#재등록(담당자)</button>
                </div>
                </c:if>
            </div>
            <!--// chat_cont -->
        </div>
        <!--// chat_cont_wrap -->

        <!-- chat_bottom -->
        <div class="chat_bottom">
            <div class="bottom_inner">
                <div class="font_control">
                    <button onclick="openFont(); return false;" type="button" class="btn_font"><span class="ico_comm ico_font">글자크기 작게/크게</span></button>
                </div>
                <div class="inp_box">
                    <div class="inp_txt">
                        <input type="text" class="inp" id="sentence" name="sentence" onkeypress="if(event.keyCode==13) {ichat_question(); return false;}" placeholder="질문을 입력해주세요" title="문의내용" />
                        <div class="btn_send">
                            <button type="button" onclick="ichat_question(); return false;"><span class="ico_comm ico_send active">문의 내용 전송</span></button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 자동완성 : 자동완성시 li만 사용(li.first 제외) -->
            <div id="auto_keyword" class="auto_keyword">
                <!--
                 <ul class="keyword_box">
                      <li>
                         <a href="#" class="sch_keyword on"><strong>선수등록</strong> 방법을 안내해주세요</a>
                     </li>
                     <li>
                         <a href="#" class="sch_keyword"><strong>선수등록</strong> 준비서류는 무엇인가요</a>
                     </li>
                 </ul>
                  -->
            </div>
            <!-- //자동완성 -->
            <div class="font_wrap font_layer" style="display:none;">
                <ul class="font_size_list">
                    <li><a href="#" onclick="fontControl('1'); return false;" class="btn_font"><span class="fz1">가</span></a></li>
                    <li><a href="#" onclick="fontControl('2'); return false;" class="btn_font on"><span class="fz2">가</span></a></li>
                    <li><a href="#" onclick="fontControl('3'); return false;" class="btn_font"><span class="fz3">가</span></a></li>
                    <li><a href="#" onclick="fontControl('4'); return false;" class="btn_font"><span class="fz4">가</span></a></li>
                    <li><a href="#" onclick="fontControl('5'); return false;" class="btn_font"><span class="fz5">가</span></a></li>
                </ul>
            </div>
        </div>
        <!-- //chat_bottom -->
    </div>
    <!-- //chat_box -->
</div>
<!-- //chat_wrap -->

<!-- layer -->
<div class="load_dimed" style="display:none"></div>
<div class="dimed" style="display:none"></div>

<div class="popup survey alert" style="display:none;">
    <div class="layer_cont">
        <div class="survey_box">
            <strong class="survey_tit popup_tit" id="alert_tit">경고창</strong>
            <div class="popup_txt_box">
                <span id="alert_cont">경고 내용</span>
            </div>
        </div>
        <div class="pop_btn_group w_half">
            <button type="button" id="alert_btn" style="width:100%;" onclick="window.close();" class="pop_btn gray">닫기</button>
        </div>
    </div>
</div>

<div class="popup survey confirm" style="display:none;">
    <div class="layer_cont">
        <div class="survey_box">
            <strong class="survey_tit popup_tit" id="confirm_tit">확인창</strong>
            <button type="button" onclick="closePopup();" class="btn_pop_close"><span class="ico_comm ico_pop_close">팝업닫기</span></button>
            <div class="popup_txt_box">
                <span id="confirm_cont">확인 내용</span>
            </div>
        </div>
        <div class="pop_btn_group w_half">
            <button type="button" id="confirm_btn" class="pop_btn blue">확인</button>
            <button type="button" onclick="closePopup();" class="pop_btn gray">취소</button>
        </div>
    </div>
</div>

<!-- 설문조사  -->
<div class="popup custom survey popupsurvey" style="display:none;">
    <div class="layer_cont">
        <div class="survey_box">
            <strong class="survey_tit">최근 문의 목록</strong>
            <ul id="recentIntent" style="text-align:left;">
                <li>증명서 발급 어떻게 하나요</li>
            </ul>
        </div>
        <div class="pop_btn_group w_half">
            <button type="button" style="width:100%;" onclick="closePopup();" class="pop_btn gray">닫기</button>
        </div>
    </div>
</div>


<!-- layer_pop guide -->
<div class="popup survey guide popupguide" style="display:none;">
    <div class="layer_cont">
        <div class="survey_box">
            <strong class="survey_tit">이용안내</strong>
            <button type="button" class="btn_pop_close" onclick="closePopup();"><span class="ico_comm ico_pop_close">팝업닫기</span></button>
            <div class="pop_swipe_wrap">
                <div class="pop_swipe_box swipe_img_box">
                    <div class="pop_img_box">
                        <img src="${pageContext.request.contextPath}/resources/img/chat_img_guide.png" class="img_guide" alt="이용 가이드, 자세한 설명은 아래를 참고하세요">
                    </div>
                    <div class="screen_out">
                        <p>"이용안내" 버튼은 이용안내 화면이 열려요.</p>
                        <p>"최근문의목록" 버튼은 최근 챗봇에 문의하신 질문을 표시합니다. </p>
                        <p>"메뉴" 버튼을 클릭하시면 답변을 드려요.</p>
                        <p>하단영역 왼쪽 "글자크기" 버튼은 글자크기를 크거나 작게 조절할 수 있어요.</p>
                        <p>"입력창"은 텍스트를 입력하시면 답변이 출력돼요. 입력시 자동완성 창이 오픈되며, 선택시 질의가 출력돼요.</p>
                    </div>
                </div>
                <div class="pop_swipe_box guide_txt">
                    <div class="ly_cont_top">
                        <strong class="user_cont_tit"><span>질문가이드</span></strong>
                    </div>
                    <div class="pop_swipe_scroll">
                        <div class="tip_box">
                            <ul class="tip_txt_list">
                                <li><span>증명서 발급 절차 알려줘</span></li>
                                <li><span>선수 신청은 어떻게 하나요</span></li>
                                <li><span>증명서 종류는 무엇인가요</span></li>
                                <li><span>경기인 신청기간은 언제인가요</span></li>
                                <li><span>동호인 클럽은 어떻게 신청하나요</span></li>
                            </ul>
                        </div>
                        <div class="tip_example_box">
                            <strong class="example_tit">정확한 답변을 받으시려면 아래와 같이 질문해주세요.</strong>
                            <ul class="example_list">
                                <li><span>문장형으로 완성하시면 더욱 정확한 답변을 드릴 수 있어요.</span></li>
                                <li><span>맞춤법을 정확히 확인해 주세요.</span></li>
                                <li><span>검색어의 단어 수를 줄이거나, 보다 일반적인 단어로 다시 질문해보세요.</span></li>
                                <li><span>두 단어 이상인 경우, 띄어쓰기를 확인해 보세요.</span></li>
                            </ul>
                        </div>
                        <div class="tip_example_box">
                            <strong class="example_tit">이용문의가 있으신 경우 고객센터로 연락 주세요.</strong>
                            <ul class="example_list">
                                <li><span>고객센터 : 02-2144-8141</span></li>
                                <li><span>고객센터 운영시간<br>오전 9:00 ~ 오후 6:00 (주말/공휴일 휴무)</span></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="pop_btn_group w_half">
            <button type="button" onclick="closePopup();" class="pop_btn gray">닫기</button>
            <button type="button" onclick="nextSlide(this);" class="pop_btn blue">다음</button>
        </div>
    </div>
</div>

<div class="popup survey loading" style="display:none;width:70%;">
    <div class="layer_cont no_bg">
        <div class="survey_box">
            <div class="loader"></div>
            <!-- 텍스트만 사용할 경우 -->
            <div class="popup_txt_box">
                처리 중입니다.<br>
                잠시만 기다려 주세요.
            </div>
            <!-- //텍스트만 사용할 경우 -->
        </div>
    </div>
</div>
<!--// layer_pop guide -->

<div class="popup survey info popupagreement" style="display: none;"></div>
<div class="popup survey popupcontinueConfirm no_scroll" style="display: none;"></div>
<div class="popupuserInfo user_ly_pop " style="display: none;"></div>

<script src="${pageContext.request.contextPath}/resources/js/lib/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-3.3.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-ui.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/lodash-4.17.15.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/slick.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/swiper-bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/ichat2.js?ver=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/resources/js/common.js?ver=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/resources/js/ajax.js?ver=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.touchSlider.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/handlebars-v4.7.8.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/api.js?ver=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/resources/js/api/helper.js?ver=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/resources/js/reRegist/reRegist.js?ver=<%=System.currentTimeMillis()%>"></script>

<c:forEach var="filename" items="${scripts}">
<script src="${pageContext.request.contextPath}/resources/js/api/${filename}?ver=<%=System.currentTimeMillis()%>"></script>
</c:forEach>

<c:forEach var="templat" items="${templates}">
<script id="${templat.id}" type="text/html">
${templat.contents}
</script>
</c:forEach>

<script type="text/javascript">
    self.window.focus();
</script>

<!-- pop_swipe_wrap -->
<script>
    $(document).ready(function() {
        $('.pop_swipe_wrap').slick({
            slidesToShow: 1,
            slidesToScroll: 1, //스크롤 한번에 움직일 컨텐츠 개수
            arrows: false, // 옆으로 이동하는 화살표 표시 여부
            dots: true, // 하단 paging버튼 노출 여부
            infinite: false, // 양방향 무한 모션
            fade: false, //페이드모션 실행 여부
            accessibility: true, // 탭 이동 및 화살표 키 탐색 활성화
            variableWidth: true, // 가변 너비 슬라이드
            cssEase: 'ease-out' //css easing 모션 함수
        }).on('afterChange', function(event, slick, direction){
            var btn = $(event.target).parents('.popup').eq(0).find('.pop_btn.blue')
            var txt = '다음'
            if (slick.getSlideCount() !== slick.getCurrent() + 1) {
                txt = '이전'
            }
            btn.text(txt)
        });
    });
</script>
<!--// pop_swipe_wrap -->

<script>
    $(function() {
        $("#sentence").on('input keydown', function() {
            if ($("#sentence").val() == ''){
                $("#img_btn").attr("disabled", true);
                $(".btn_send").css("cursor", "default");
                $("#img_btn > img").attr("src", "${pageContext.request.contextPath}/resources/img/ico_send.png");
            }else{
                $("#img_btn").attr("disabled", false),
                    $(".btn_send").css("cursor", "pointer");
                $("#img_btn > img").attr("src", "${pageContext.request.contextPath}/resources/img/ico_send_on.png");
            }
        });
    });


    $(document).ready(function() {
        init();
        $("#sentence").focusout(function() {
            $("#sentence").css("border", "none");
        });
        $("#main_time").text(getHour());

        $('.star_rating_box .ico_star').click(function() {
            $(this).parent().children('.ico_star').removeClass('is_selected');
            $(this).addClass('is_selected').prevAll('.ico_star').addClass('is_selected');
            return false;
        });

        $('#opinion').on('keyup', function() {
            if($(this).val().length > 200) {
                $(this).val($(this).val().substring(0, 200));
            }
        });
    })

</script>
</body>

</html>