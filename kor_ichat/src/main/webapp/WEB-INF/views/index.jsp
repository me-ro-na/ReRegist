<%@ page contentType="text/html; charset=utf-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="ko">
<head>
<title>iChat</title>
</head>
<body style="overflow-y: hidden; margin: unset;">
	<div
		style="position: absolute; text-align: center; width: 100%; height: 100%;">
		<a href="javascript:;"
			onclick="window.open('${pageContext.request.contextPath}/iChat.do', '_blank', 'width=500,height=800,resizable=0,scrollbars=1')"
			target="_balnk1" style="position: relative;display:
			block;text-decoration: none; width: fit-content; height:fit-content;left: 47%;right: 50%;top: 30%; cursor: pointer; color: blue;">
			<h1>iChat Test(사용자)</h1>
		</a>
		<a href="http://61.82.137.77:17400/wiseichat" target="_balnk2"
			style="position: relative; display: block; 
			text-decoration: none; width: fit-content; right: 50%; left: 46%; top: 45%;">
			<h1>챗봇 관리도구</h1>
		</a>
		<!-- <h1 style="font-size: 5.0em;">테스트중입니다.</h1>
<h1></h1> -->
	</div>
</body>
</html>
