<%@ page contentType="text/html; charset=utf-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ko">
<head>
    <title>iChat</title>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>

<script src="${pageContext.request.contextPath}/resources/js/lib/sha256.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-3.3.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/jquery-ui.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lib/slick.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/ajax.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/m_main.css">
<script>
    function createUser(type) {
        switch (type) {
            case 1:
                setId('luon12');
                setBirth('19900101');
                break;
            case 2:
                setId('psgaca');
                setBirth('19900101');
                break;
            case 3:
                setId('psgaca');
                setBirth('20200101');
                break;
            case 4:
                setId('subina');
                setBirth('19900101');
                break;
        }
        createSession(true);
    }

    function clientPopup(isWindow) {
        var sportsForm = document.querySelector('#sports_form');
        if (isWindow) {
            window.open('', 'sports_form', 'width=500,height=800,resizable=0,scrollbars=1');
            sportsForm.target = "sports_form";
        }
        setTimeout(() => {
            sportsForm.submit();
        },200);
    }

    function adminPopup() {
        var code = "대한진진" + yyyymmdd(new Date());
        code = sha256(code);

        window.open('${pageContext.request.contextPath}/iChat.do?code=' + code, '_blank2', 'width=500,height=800,resizable=0,scrollbars=1');
    }

    function getCookie(name) {
        var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
        return value ? decodeURIComponent(value[2]) : null;
    }

    function createSession(isWindow) {
        var xhr = new XMLHttpRequest(); // XMLHttpRequest 객체 생성
        var url = '${pageContext.request.contextPath}/ajax/setLogin.do'; // API 엔드포인트 URL

        xhr.open('POST', url, true); // POST 요청 설정
        xhr.setRequestHeader('Content-Type', 'application/json'); // 요청 헤더 설정

        xhr.onreadystatechange = function () { // 요청 상태 변화 이벤트 핸들러
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
            birthDt: sports_form.birthDt.value
        }); // 요청할 데이터를 JSON 문자열로 변환
        xhr.send(jsonData); // 요청 보내기
    }

    function generateRandomId(length) {
        const characters1 = 'bcdfghjklmnpqrstvwxyz';
        const characters2 = 'aeiou';
        const characters3 = '0123456789';
        let randomId = '';
        for (let i = 0; i < length; i++) {
            let charset = characters1;
            if (i % 2 == 0) {
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
    <input type="hidden" id="userId" name="userId" value="" style="ime-mode:disabled;"/>
    <input type="hidden" id="key" name="key" value="" style="ime-mode:disabled;"/>
    <input type="hidden" id="birthDt" name="birthDt" value="19900123" style="ime-mode:disabled;"/>
</form>
<div id="wrap">

    <!-- header -->

    <header id="header" class="mainHeader">
        <h1 class="logo">
            <%--<a href="https://g1.sports.or.kr/m/index.do">
                <img src="https://g1.sports.or.kr/comn/g1/m/assets/img/common/logo01.svg" alt="스포츠지원포털">
            </a>--%>
        </h1>
        <div class="left-head">
        </div>
    </header>
    <!--// header -->

    <!-- content -->
    <div id="contents" class="main">
        <div class="main-section01">
            <ul class="mainSlider">
                <li class="hideTxt" style="overflow: hidden; background-image: url(https://g1.sports.or.kr/comn/g1/m/assets/img/main/img_main01.png);">첫번째 배너</li>
            </ul>
            <div class="slogan-area">
                <h3>통합된<br>하나의 길</h3>
                <p class="mb10">
                    대회전산운영 및 경기실적 관리체계 통합(2차)<br>챗봇 테스트 페이지입니다!<br>
                    <span style="font-size: 10px; line-height: 3; color: #eee;">
                        <i>입력 데이터: 증명서(19년), 기타이력(22년), 훈련이력(23년), 경기영상(19년)</i>
                    </span>
                </p>
                <p>Sports life for everyone !</p>
            </div>
            <ul class="gateList" style="top: 455px;left: 50%;transform: translateX(-50%);">
                <li><a href="javascript:clientPopup(true);">사용자 챗봇</a></li>
                <li><a href="javascript:adminPopup();">관리자 챗봇</a></li>
                <li><a href="javascript:createUser(4);">신규 사용자</a></li>
            </ul>
            <ul class="gateList" style="top: 590px;left: 50%;transform: translateX(-50%);">
                <li><a href="javascript:createUser(1);">조회 연계</a></li>
                <li><a href="javascript:createUser(2);">재등록 연계</a></li>
                <li><a href="javascript:createUser(3);">14세 미만</a></li>
            </ul>
        </div>
    </div>
    <!--// content -->

    <!-- footer -->
    <footer id="footer" class="footer">
        <div class="footerTop">
            <div class="footerLogo">
                <%--<a href="javascript:;">
                    <img src="https://g1.sports.or.kr/comn/g1/m/assets/img/common/footerLogo.svg" alt="스포츠지원포털">
                </a>--%>
            </div>
            <div class="footMenu">
                <%--<ul class="clearFix">
                    <li><a href="https://g1.sports.or.kr/m/index.do">스포츠지원포털</a></li>
                </ul>--%>
            </div>
            <p class="address">서울특별시 송파구 올림픽로 424 올림픽회관</p>
            <p class="copyright">ⓒ 스포츠지원포털 챗봇 알리</p>
            <ul class="partner">
                <li><a href="https://www.sports.or.kr/" target="_blank" title="새창으로열기"><img
                        src="https://g1.sports.or.kr/comn/g1/m/assets/img/common/img_patner01.svg" alt="대한체육회"></a></li>
                <li><a href="https://www.mcst.go.kr/" target="_blank" title="새창으로열기"><img
                        src="https://g1.sports.or.kr/comn/g1/m/assets/img/common/img_patner02.svg" alt="문화체육관광부"></a></li>
                <li><a href="https://www.kspo.or.kr/" target="_blank" title="새창으로열기"><img
                        src="https://g1.sports.or.kr/comn/g1/m/assets/img/common/img_patner03.svg" alt="국민체육진흥공단"></a></li>
            </ul>
        </div>
        <div class="footerlower">
            <p class="txt">본 사업은<br>문화체육관광부와 국민체육진흥공단의<br>재정후원을 받고 있습니다.</p>
        </div>
        <!-- top -->
        <a href="javascript:;" class="topBtn hideTxt" title="맨 위로">맨 위로</a>
    </footer>
    <!-- footer -->
</div>
</body>
</html>