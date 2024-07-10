<%@ page contentType="text/html; charset=utf-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="ko">
<head>
<title>iChat</title>
</head>
<body style="overflow-y: hidden; margin: unset;">

<script	src="https://cdnjs.cloudflare.com/ajax/libs/js-sha256/0.9.0/sha256.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/ajax.js"></script>
<script>
	function adminPopup() {
		var code = "대한진진" + yyyymmdd(new Date());
		code = sha256(code);
		
		window.open('${pageContext.request.contextPath}/iChat.do?code='+code, '_blank', 'width=500,height=800,resizable=0,scrollbars=1');
	}
</script>
	<div style="position: absolute; text-align: center; width: 100%; height: 100%;">
		<a 
			onclick="window.open('${pageContext.request.contextPath}/iChat.do', '_blank', 'width=500,height=800,resizable=0,scrollbars=1')"
			target="_balnk1" style="position: relative;display:
			block;text-decoration: none; width: fit-content; height:fit-content;left: 47%;right: 50%;top: 30%; cursor: pointer; color: blue;">
			<h1>iChat Test(사용자)</h1>
		</a>
		<a 
			onclick="adminPopup();"
			target="_balnk2" style="position: relative;display:
			block;text-decoration: none; width: fit-content; height:fit-content;left: 47%;right: 50%;top: 30%; cursor: pointer; color: blue;">
			<h1>iChat Test(관리자)</h1>
		</a>
		<%-- <a 
			onclick="window.open('${pageContext.request.contextPath}/iChatView.do', '_blank', 'width=500,height=800,resizable=0,scrollbars=1')"
			target="_balnk1" style="position: relative;display:
			block;text-decoration: none; width: fit-content; height:fit-content;left: 47%;right: 50%;top: 30%; cursor: pointer; color: blue;">
			<h1>iChat Test(시연)</h1>
		</a> --%>
		<a id="admin" href="http://211.39.140.199:17400/wiseichat" target="_balnk3"
			style="position: relative; display: block; 
			text-decoration: none; width: fit-content; right: 50%; left: 46%; top: 45%;">
			<h1>챗봇 관리도구</h1>
		</a>
		<!-- <h1 style="font-size: 5.0em;">테스트중입니다.</h1>
<h1></h1> -->
	</div>
</body>
</html>
