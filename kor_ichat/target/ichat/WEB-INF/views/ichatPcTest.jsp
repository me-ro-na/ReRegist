<%@ page contentType="text/html; charset=utf-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="ko">
<head>
    <title>iChat</title>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <style>
        #box1 {background-image: url(https://g1.sports.or.kr/comn/g1/p/img/main/main_icon_1.svg);}
        #box2 {background-image: url(https://g1.sports.or.kr/comn/g1/p/img/main/main_icon_2.svg);}
        #box3 {background-image: url(https://g1.sports.or.kr/comn/g1/p/img/main/main_icon_3.svg);}
        #box4 {background-image: url(https://g1.sports.or.kr/comn/g1/p/img/main/main_icon_4.svg);}
        #box5 {background-image: url(https://g1.sports.or.kr/comn/g1/p/img/main/main_icon_1.svg);}
    </style>
</head>
<body style="margin: unset;">

<script	src="${pageContext.request.contextPath}/resources/js/lib/sha256.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-3.3.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-ui.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/ajax.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/main.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/lib/datepicker.css">
<script>

    $(document).ready(function () {
        var m_slide_idx = $('.main-slider ul li').length;
        var m_slide_interval = setInterval(function () {
            m_itv_slide('right');
        }, 5000);
        var m_slide_count = 0;
        var m_moving = $('.main-slider ul');
        var m_slide_width = $('.main-slider ul li').width();
        var m_slide_event = false;

        $(window).on('resize', function () {
            // $('.main-slider ul li').css('width',$(window).width());
           // m_slide_width = $('.main-slider ul li').width();
            //m_itv_slide('right');
        });

        function m_itv_slide(event) {
            if (event == 'right') {
                if (m_slide_count < m_slide_idx - 1) {
                    m_slide_count++;
                    m_slide_r();

                } else {
                    m_slide_count = 0;
                    m_slide_r();
                }
            } else if (event == 'left') {
                if (m_slide_count > 0) {
                    m_slide_count--;
                    m_slide_l();
                } else {
                    m_slide_count = m_slide_idx - 1;
                    m_slide_l();
                }
            }
        }

        function m_arrow_left() {
            if (m_slide_count == 0) {
                $('.main .arrow-left').removeClass('active');
            } else {
                $('.main .arrow-left').addClass('active');

            }

        }

        function m_slide_r() {
            m_arrow_left();
            $(m_moving).stop().animate({
                left: -m_slide_width * 2
            }, {
                duration: 700,
                complete: function () {
                    $(m_moving).css({
                        "left": -m_slide_width * 1
                    });

                    $(m_moving).append($(m_moving).children()[0]);
                    /* comment 2022.03.17
                    $('.page').text((m_slide_count + 1) + ' / 4');
                    */
                    if (m_slide_count == 0 || m_slide_count == 2)
                        $('.page').text('1 / 2');
                    else
                        $('.page').text('2 / 2');

                    m_slide_event = false;
                }
            });
        }

        function m_slide_l() {
            m_arrow_left();
            $(m_moving).stop().animate({
                left: 0
            }, {
                duration: 700,
                complete: function () {
                    $(m_moving).css({
                        "left": -m_slide_width * 1
                    });
                    $(m_moving).prepend($(m_moving).children()[m_slide_idx - 1]);
                    /* comment 2022.03.17
                    $('.page').text((m_slide_count + 1) + ' / 4');
                    */
                    if (m_slide_count == 0 || m_slide_count == 2)
                        $('.page').text('1 / 2');
                    else
                        $('.page').text('2 / 2');

                    m_slide_event = false;
                }
            });
        }
    });
</script>
<script>
    $(document).ready(function() {
        $('#birthDt').datepicker({
            /*minDate: "-5Y",*/ maxDate: "+0d",
            dateFormat: "yy.mm.dd",
            buttonImage: "./resources/img/ico_calendar.png",
            beforeShow: function (el, inst) {
                //console.log({'b': {"el": el, "inst": inst}})
            },
            onSelect: function (txt, inst) {
                console.log({'s': {"txt": txt, "inst": inst}})
            }
        }).datepicker("setDate", '1990.01.23');
    });

    function createUser(type) {
        switch (type) {
            case 1:
                setId('luon12');
                setBirth('1990.01.01');
                break;
            case 2:
                setId('psgaca');
                setBirth('1990.01.01');
                break;
            case 3:
                setId('psgaca');
                setBirth('2020.01.01');
                break;
            case 4:
                setId('subina');
                setBirth('1990.01.01');
                break;
        }
        createSession(true);
    }

    function clientPopup(isWindow) {
        var sportsForm = document.querySelector('#sports_form');
        if (isWindow) {
            sportsForm.target = "sports_form";
            window.open('', 'sports_form', 'width=500,height=800,resizable=0,scrollbars=1');
        }
        setTimeout(() => {
            sportsForm.submit();
        }, 200);

    }

    function adminPopup() {
        var code = "대한진진" + yyyymmdd(new Date());
        code = sha256(code);

        window.open('${pageContext.request.contextPath}/iChat.do?code='+code, '_blank2', 'width=500,height=800,resizable=0,scrollbars=1');
    }
    function getCookie(name) {
        var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
        return value? decodeURIComponent(value[2]) : null;
    }
    function createSession(isWindow) {
        var xhr = new XMLHttpRequest(); // XMLHttpRequest 객체 생성
        var url = '${pageContext.request.contextPath}/ajax/setLogin.do'; // API 엔드포인트 URL

        xhr.open('POST', url, true); // POST 요청 설정
        xhr.setRequestHeader('Content-Type', 'application/json'); // 요청 헤더 설정

        xhr.onreadystatechange = function() { // 요청 상태 변화 이벤트 핸들러
            if (xhr.readyState === 4 && xhr.status === 200) { // 요청이 완료되고 응답이 성공적으로 돌아왔을 때
                var response = JSON.parse(xhr.responseText); // 응답 데이터를 JSON 형태로 파싱
                clientPopup(isWindow)
            } else if (xhr.readyState === 4 && xhr.status != 200) {
                alert('생성실패!')
            }
        };

        var jsonData = JSON.stringify({
            userId: sports_form.userId.value,
            sessionId: sports_form.key.value,
            birthDt: $.datepicker.formatDate("yymmdd",$("#birthDt").datepicker("getDate"))
        }); // 요청할 데이터를 JSON 문자열로 변환
        xhr.send(jsonData); // 요청 보내기
    }

    function logout() {
        var xhr = new XMLHttpRequest();
        var url = '${pageContext.request.contextPath}/ajax/setLogout.do';
        xhr.open('POST', url, true); // POST 요청 설정
        xhr.setRequestHeader('Content-Type', 'application/json'); // 요청 헤더 설정
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                clientPopup()
            }
        };
        xhr.send("{}"); // 요청 보내기
    }

    function generateRandomId(length) {
        const characters1 = 'bcdfghjklmnpqrstvwxyz';
        const characters2 = 'aeiou';
        const characters3 = '0123456789';
        let randomId = '';
        for (let i = 0; i < length; i++) {
            let charset = characters1;
            if (i%2 == 0) {
                charset = characters1
            } else {
                charset = characters2
            }
            if (i >= length - 2) {
                charset = characters3;
            }
            randomId += charset.charAt(Math.floor(Math.random() * charset.length));
        }
        return randomId;
    }

    function generateRandomSessionId(length) {
        const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        let randomId = '';
        for (let i = 0; i < length; i++) {
            randomId += charset.charAt(Math.floor(Math.random() * charset.length));
        }
        return randomId;
    }

    function setId(userId) {
        document.querySelector('#userId').value = userId;
    }

    function setBirth(birthDt) {
        document.querySelector('#birthDt').value = birthDt;
    }

    window.onload = function () {
        document.querySelector('#userId').value = generateRandomId(4 + Math.floor(Math.random() * 6));
        document.querySelector('#key').value = generateRandomSessionId(32);
    }
</script>

<form id="sports_form" method="post" action="${pageContext.request.contextPath}/iChat.do" target="sports_form">
    <style>
        button, label, input, select, li, a {transform : rotate(0.04deg);}
        li, a {color: #3b5075; font-weight: bold;font-size: smaller;}
        #sports_form {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
        }
        #sports_form label, #sports_form input, #sports_form select, #sports_form button {
            padding: 5px 10px 5px 10px;
            color: #3b5075;
            height: 30px;
        }
        #sports_form .holder {
            width: 300px;
            margin: 0 auto;
        }
        #sports_form label{
            display: block;
            float: left;
            width: 90px;
            text-align: right;
            padding-left: 0px;
            font-weight: bold;
        }
        #sports_form input, #sports_form select {
            width: calc(100% - 100px);
            border: 1px solid #c4c4c4;
            border-radius: 5px;
        }
        #sports_form select {
            width: inherit;
        }
        #sports_form button {
            width: 100%;
            font-weight: bold;
            border: 1px solid #c4c4c4;
            border-radius: 5px;
            background-color: #d4e5ea;
            cursor: pointer;
            font-size: 13pt;
            height: 40px;
        }
        #sports_form button:hover {
            filter: contrast(0.8);
        }
        #sports_form button + button {margin-top: 10px;}
        .ui-datepicker-trigger {position: absolute;right: 20px;width: 20px;margin-top: 5px;}
    </style>
    <div class="holder" style="display: none;">
        <div style="border-radius: 10px; border: 2px solid #d4e5ea; padding: 10px; margin-bottom: 10px;">
            <div style="margin-bottom: 10px;">
                <label for="userId">사용자ID</label>
                <input type="text" id="userId" name="userId" value="" style="ime-mode:disabled;"/>
            </div>
            <div style="margin-bottom: 10px;">
                <label for="key">세션ID</label>
                <input type="text" id="key" name="key" value="" style="ime-mode:disabled;"/>
            </div>
            <div style="margin-bottom: 10px;">
                <label for="birthDt">생년월일</label>
                <input type="text" id="birthDt" name="birthDt" value="19900123" style="ime-mode:disabled;"/>
            </div>
            <ul>
                <li>조회 테스트ID : <a href="#" onclick="setId('luon12')">luon12</a></li>
                <li>재등록 테스트ID : <a href="#" onclick="setId('psgaca');setBirth('1990.01.01')">psgaca</a></li>
                <li>재등록 테스트ID(14세 미만) : <a href="#" onclick="setId('psgaca');setBirth('2020.01.01')">psgaca</a></li>
                <li>가입만 한 테스트ID : <a href="#" onclick="setId('subina')">subina</a></li>
            </ul>
        </div>
        <button type="button" class="btn" onclick="createSession(true); return false;">로그인(새창)</button>
        <button type="button" class="btn" onclick="createSession(); return false;" style="margin-bottom: 20px;">로그인(새탭)</button>
        <button type="button" class="btn" onclick="clientPopup(true); return false;">iChat Test(사용자)</button>
        <button type="button" class="btn" onclick="adminPopup(); return false;">iChat Test(관리자)</button>
        <button type="button" class="btn" style="background-color: #c0d5f1;" id="admin" onclick="window.open('http://211.39.140.199:17400/wiseichat','_blank'); return false;">챗봇 관리도구</button>
        <button type="button" class="btn" id="swag" onclick="window.open('${pageContext.request.contextPath}/swagger-ui.html','_blank'); return false;">API 테스트(Swagger)</button>
    </div>
</form>
<div class="wrap sports-main">
    <div class="header active" style="display: none">
        <%--<div class="login">
            <ul class="join">
                <li>
                    <a href="./swagger-ui.html" target="_blank">
                        API 테스트(Swagger)
                    </a>
                </li>
            </ul>
        </div>--%>
        <div class="fixed-top">
            <div class="fixed-top__left">
                <a href="#" title="홈으로" tabindex="3"></a>
                <div class="logo" style="width: 0px"></div>
            </div>
            <ul class="fixed-top__mid">
                <li class=""><a href="http://211.39.140.199:17400/wiseichat" target="_blank" tabindex="4">챗봇 관리도구</a></li>
            </ul>
        </div>
    </div>
    <div class="section main">
        <div class="main-slider">
            <ul>
                <li class="first"></li>
                <li class="second"></li>
                <li class="first"></li>
                <li class="second"></li>
            </ul>
        </div>
        <div class="inner">
            <div class="inner-contents">
                <div class="inner-contents__center">
                    <div class="text">
                        <h1 class="NotoSerif"><span class="NotoSerifTC">統合</span><br>통합된 하나의 길</h1>
                        <p>대회전산운영 및 경기실적 관리체계 통합(2차)<br>챗봇 테스트 페이지입니다!<br>
                            <span style="font-size: 20px; line-height: 3; color: #eee;">
								<i>입력 데이터: 증명서(19년), 기타이력(22년), 훈련이력(23년), 경기영상(19년)</i>
							</span>
                        </p>
                        <a href="http://211.39.140.199:17400/wiseichat" target="_blank" class="chatbot_admin" tabindex="4">챗봇 관리도구 바로가기</a>
                    </div>

                    <div class="boxs" style="top: 0px;">
                        <div class="boxs-top">
                            <div class="box" id="box1" tabindex="120">
                                <span>사용자 챗봇</span>
                                <a href="javascript:clientPopup(true)" id="link1"></a>
                            </div>

                            <div class="box" id="box2" tabindex="121">
                                <span>관리자 챗봇</span>
                                <a href="javascript:adminPopup()" id="link2"></a>
                            </div>
                        </div>
                        <div class="boxs-top">
                            <div class="box" id="box3" tabindex="122">
                                <span>조회 사용자</span>
                                <a href="javascript:createUser(1)" id="link3"></a>
                            </div>
                            <div class="box" id="box6" tabindex="125">
                                <span>신규 사용자</span>
                                <a href="javascript:createUser(4)" id="link6"></a>
                            </div>
                        </div>
                        <div class="boxs-bottom">
                            <div class="box" id="box4" tabindex="123">
                                <span>재등록 사용자</span>
                                <a href="javascript:createUser(2)" id="link4"></a>
                            </div>
                            <div class="box" id="box5" tabindex="124">
                                <span>14세 미만 사용자</span>
                                <a href="javascript:createUser(3)" id="link5"></a>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
</body>
</html>
