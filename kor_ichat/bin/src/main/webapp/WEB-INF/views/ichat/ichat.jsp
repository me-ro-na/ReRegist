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
    <title>대한체육회챗봇</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/default.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/lib/slick.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/lib/slick-theme.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/chatbot.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/dev.css">
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath }/resources/img/favicon.ico" />
    
    <script src="${pageContext.request.contextPath}/resources/js/lib/jquery.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-3.3.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-ui.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/lib/slick.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/ichat2.js?ver=<%=System.currentTimeMillis()%>"></script>
	<script src="${pageContext.request.contextPath}/resources/js/common.js?ver=<%=System.currentTimeMillis()%>"></script>
	<script src="${pageContext.request.contextPath}/resources/js/ajax.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/jquery.touchSlider.js"></script>
	
	<script src="https://cdn.jsdelivr.net/npm/swiper/swiper-bundle.min.js"></script>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper/swiper-bundle.min.css"/>
	
	<script type="text/javascript">
    	self.window.focus();
	</script>
	
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
</head>

<body onblur="window.focus()">
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
            <div class="chat_top">
                <h1 class="h_tit">
                    <span class="chat_name">
                        <a href="${pageContext.request.contextPath}/iChat.do" title="챗봇 첫화면으로 이동">알리</a>
                    </span>
                </h1>
                <div class="top_btn left">
					<button type="button" onclick="appendMainMenu();" class="menu left"><span title="처음으로" class="ico_comm ico_home">홈</span></button>
				</div>
                <div class="top_btn right">
                    <!-- <button type="button" onclick="openPopup('guide');" class="menu right"><span title="이용안내" class="ico_comm ico_guide">챗봇 이용안내</span></button> -->
                    <c:choose>
                        <c:when test="${feedbackQuestion eq null || feedbackQuestion.QUESTION_YN eq 'N'}">
                            <button type="button" onclick="finish();" class="menu right"><span title="닫기" class="ico_comm ico_list">챗봇 만족도평가</span></button>
                        </c:when>
                        <c:otherwise>
                            <button type="button" onclick="openPopup('survey');" class="menu right"><i class="fa fa-history fa-2x" aria-hidden="true"></i></button>
                        </c:otherwise>
                    </c:choose>
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
                                    		<div class="icon_inner">
                                    			<span class="icon"><img src="${pageContext.request.contextPath}/resources/upload/${item.IMAGE_FILESERVERNAME}"/></span>
                                    			<div class="m_txt">${item.QUICKMENU_NM}</div>
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
	<div class="dimed" style="display:none"></div>

	<!-- 설문조사  -->
	<div class="popup survey popupsurvey" style="display:none;width:70%;">
		<div class="layer_cont">
			<div class="survey_box">
				<strong class="survey_tit">최근 문의 목록</strong>
				<ul id="recentIntent" style="text-align:left;">
					<li>증명서 발급 어떻게 하나요</li>
				</ul>
			</div>
			<div class="pop_btn_group w_half">
				<button type="button" style="width:100%;"onclick="closePopup();" class="pop_btn gray">닫기</button>
			</div>
		</div>
	</div>
	<%-- <div class="popup guide popupguide">
		<button class="layer_close" onclick="closePopup();"><span class="ico_comm ico_w_close">레이어팝업 닫기</span></button>
		<div class="layer_cont">
			<img alt="챗봇 이용안내 이미지" src="${pageContext.request.contextPath}/resources/img/chat_guide.png">
            <div class="screen_out">
				<p>헬프봇 이용안내입니다.</p>
				<p>우측 상단에 챗봇 화면을 컨트롤할 수 있는 3개의 버튼이 있습니다.</p>
				<p>첫 번째 버튼은 "챗봇 메인 메뉴 불러오기" 버튼이며, 주요 문의 메뉴를 불러옵니다.</p>
				<p>두 번째 버튼은 "이용안내" 버튼이며, 챗봇 이용 방법에 대한 이미지를 보여줍니다.</p>
				<p>세 번째 버튼은 "만족도 평가 후 챗봇 닫기" 버튼으로, 챗봇 사용 만족도 평가 후 챗봇을 종료합니다.</p>
				<p>아래쪽에는 "대화 메뉴" 버튼이 나열되어 있으며, 각 메뉴를 클릭하시면 답변을 확인할 수 있습니다.</p>
				<p>챗봇 창 가장 아래쪽에는 글자 크기 확대 및 축소 버튼이 있으며, 글씨가 작은 경우 확대하여 사용하실 수 있습니다.</p>
				<p>그 옆에는 입력창이 있으며, 입력창에 텍스트를 입력하면 답변이 출력됩니다. 또한, 입력창에 질의 입력 시 자동완성 창이 오픈되며, 자동완성 문장을 클릭하면 해당 질의가 출력됩니다.</p>
			</div>
		</div>
	</div> --%>
	

</body>

</html>