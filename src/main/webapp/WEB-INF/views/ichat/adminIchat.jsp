<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html lang="KR">
<head>
	<meta charset="UTF-8">
	<meta name="format-detection" content="telephone=no">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>대한체육회 관리자 챗봇</title>
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

<body>
<input type="hidden" id="projectId" name="projectId" value="<c:out value='${projectId}'/>">
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
            <div class="chat_top manager">
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
                </div>
            </div>
            <!--  //chat_top -->
    
            <!-- chat_cont_wrap -->
            <div class="chat_cont_wrap manager">
            <!-- chat_cont -->
            <div class="chat_cont manager">
                
                <div class="bot_wrap menu">
                    <span class="ico_comm ico_logo" id="first_menu"></span>
                    <div class="bot_box">
                        <div class="bot_txt">
                            <div class="chat_talk font">
			                            안녕하세요!<br>
							대한체육회 관리자 챗봇은 체육정보시스템에서 경기인 관리, 증명서 발급 등과 관련된 정보를 안내하는 인공지능 상담 챗봇입니다.<br><br> 
							아래 메뉴를 선택하거나 직접 질문을 입력해보세요. 
                            </div>
                        </div>
                        <ul class="chat_menu adm_menu" id="chat_menu">
                            <c:forEach var="item" items="${quickMenuList}">
								<li>
                               		<a href="#" onclick="ichat_question('${item.MAINUSERQUERY}'); return false;" class="icon_box">
                                    	<div class="icon_outer">
                                    		<div class="icon_inner">
                                    			<span class="icon"><img src="${pageContext.request.contextPath}/resources/upload/${item.IMAGE_FILESERVERNAME}"/></span>
                                    			<div class="m_txt"><c:out value="${item.QUICKMENU_NM}" /></div>
                                    		</div>
                                    	</div>
                                	</a>
                            	</li>
							</c:forEach>
                        </ul>
                        <span class="time"><span id="main_time" class="screen_out">대화전송시간</span>오후 2:00</span>
                    </div>
                </div>
            </div>
            <!--// chat_cont -->
            </div>
             <!--// chat_cont_wrap -->
            
            <!-- chat_bottom -->
            <div class="chat_bottom manager">
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
	<div class="dimed" style="display:none"></div>

	<!-- 설문조사  -->
	<div class="popup manager survey popupsurvey" style="display:none;width:70%;">
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
<div class="popup survey guide manager popupguide" style="display:none;width:70%;">
    <div class="layer_cont">
        <div class="survey_box">
            <strong class="survey_tit">이용안내</strong>
            <button type="button" class="btn_pop_close" onclick="closePopup();"><span class="ico_comm ico_pop_close">팝업닫기</span></button>
            <div class="pop_swipe_wrap">
                <div class="pop_swipe_box swipe_img_box">
                    <div class="pop_img_box">
                        <img src="${pageContext.request.contextPath}/resources/img/chat_img_guide01.png" class="img_guide" alt="이용 가이드, 자세한 설명은 아래를 참고하세요">
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
<!--// layer_pop guide -->



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

<script>
    self.window.focus();

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