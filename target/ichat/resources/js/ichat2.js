var sessionKey = "";
var chatState = false;  //채팅 상태 체크
var font = 14; //처음폰트사이즈
var jbOffset;	
var cnt = 0;
var initMinute;
var remainSecond;
var rating = 10;    // 만족도 평가
var rating2 = 10;   // 정확도 평가
var rating3;        // 상담 후기
var elementClass; //상단메뉴 고정시 스크롤 이동 방지
var fontLeft=68;
var fontSize = font + "px";
var detailAnswer = "";
var isHelp ="Y";
var _feedbackBtn;

 var searchKeyword ="";
var host = location.host;
var paths = location.pathname.split('/');
var path = paths[1];
var sldCnt = 0;

var THROTTLE_TICK = 100;

function resizer(el) {
	var $layerCont = $(el).find('.layer_cont:visible');
	if ($layerCont.length > 0) {
		el.style.setProperty('--pop-width', Math.ceil($layerCont.width()) + 'px');
		el.style.setProperty('--pop-height', Math.ceil($layerCont.height()) + 'px');
	}
}

function clear() {
	$('.chat_cont > div:not(:first)').remove();
}

//초기화
$(document).ready(function() {

	$(window).on('resize', _.throttle(function () {
		$('.popup').each(function (i, c) {
			resizer(c);
		})
	}, THROTTLE_TICK));

	if (document.querySelector("#userId") && document.querySelector("#userId").value) {
		// 창 닫기시 로그아웃 처리
		window.addEventListener('beforeunload', function (e) {
			var xhr = new XMLHttpRequest();
			xhr.open('POST', './ajax/setLogout.do', true);
			xhr.setRequestHeader('Content-Type', 'application/json');
			xhr.send("{}"); // 요청 보내기
		});
	}
	
	if (sessionKey == "") {
		js_util_fnAjax('./ajax/getApiSession', '', getSeesionKey);
	}
	
	// a태그 상단이동 막기
	$('a[href="#"]').attr('href','javascript:;')
		/*.click(function(e) {
		e.preventDefault();
	});*/

	function getSeesionKey(response) {
		sessionKey = response.result.message;
	}

	if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
		fontControl('3');
	}else{
		fontControl('2');
	}

	getRecentQuery();

});

//자동완성
$(document).on('focus keyup',"#sentence",function() {
		$("#sentence").autocomplete({
		source : function(request, response) {
			var autocom = $.ajax({
				type : 'post',
				url : "./ajax/getAutoComplete",
				dataType : "json",
				data : {
					value : request.term,
					projectId : $("#projectId").val()
				},
				success : function(result) {
					// 서버에서 json 데이터 response 후 목록에 뿌려주기 위함
					data = JSON.parse(result.outdata.result.message);
					response($.map(data, function(item) {
						return {
							label : item.data,
							value : item.data
						}
					}));
					var auto_width = parseInt($('.ui-autocomplete').css('top'));
					//새로운 변수에 연산 후 보관
					auto_width = auto_width - 1;
					$('.ui-widget-content').css('background','none');
					$('.ui-widget.ui-widget-content').css('border','none');
					$('.ui-autocomplete').css('border-top','1px solid #e8e8e8');
					$('.ui-autocomplete').css('top',/*auto_width+'px'*/'0');
					$('.ui-autocomplete').css('left','0');
					$('.ui-autocomplete').css('padding-bottom','0px');
					//$('.ui-autocomplete').css('font-size','16px');
					//$('.ui-autocomplete').css('max-height','190px');  //자동완성 높이
					$('.ui-autocomplete').css('overflow-style','none'); //IE scroll display none
					$('.ui-autocomplete').css('overflow-y','hidden'); //스크롤 세로기능 열기
					$('.ui-autocomplete').css('overflow-x','hidden'); //스크롤 가로기능 열기
					
				}
			});
		},
		
		minLength : 1,
		maxResults : 5,
		select : function(event, ui) {
			sessionCheck(ui.item.label);
			$("#sentence").val("");
			$('.ui-autocomplete').css('display','none');
			return false;
		},
		focus : function(event, ui) {
			return false;
		},
		position : {
			my : "left bottom",
			at : "left top",
			collision : "flip"
		},
		open: function(event, ui) {
	        $('.ui-autocomplete').off('menufocus hover mouseover mouseenter');
	    },
	    /*appendTo:"#keyword_box",*/
	    classes:{
	    	"ui-autocomplete": "keyword_box"
	    }
	    
	    });
		$.ui.autocomplete.prototype._renderItem = function(ul, item) {
			ul.appendTo($('.auto_keyword'));
	//		var t = String(item.value).replace(new RegExp(this.term, "i"),
	//				"<span>$&</span>");
			var t = String(item.value).replace(new RegExp(this.term, "i"),
			"<strong>$&</strong>");
			return $("<li class='ui-menu-item'></li>").data("item.autocomplete", item).append("<a href=\"javascript:;\" class=\"sch_keyword font\">" + t + "</a>").appendTo(ul);
		};
		//$('.ui-autocomplete').css('top', '609px!important');
});

$(document).on('click', '.btn_answer', function(e){
	_feedbackBtn = $(this);
	setFeedback(_feedbackBtn);
});

$(document).on('click', '.btn_feedback_detail:not(.btn_api)', function(e){
	var btn = $(this).parent().parent().find('.btn_answer.on');
	var intent = btn.parent().data("intent");
	var seq = btn.parent().attr("data-seq") || -1;
	var score = btn.data("score");
	var query =btn.parents('.bot_wrap').prev().find('.user_txt').text();
	var detail = btn.parents('.chat_talk').find('.feedback_opinion').val() || "";
	
	if(score==3){
		detail = "";
	}
	
	btn.parent().find(".on").removeClass("on");
	btn.addClass("on");
	
	var param={
			"intent" : intent,
			"score"  : score,
			"seq"	 : seq,
			"query"  : query,
			"detail" : detail
		}
	
	js_util_fnAjax('./ajax/saveIntentFeedback', param, cb_feedbackdetail);
	
});

$(document).on('keyup', '.feedback_detail textarea', function (e) {
	var txtcontent = $(this).val();
    // 글자수 제한
    if (txtcontent.length > 500) {
        $(this).val($(this).val().substring(0, 500));
        // 500자 넘으면 알림창 뜨도록
        alert('글자수는 500자까지 입력 가능합니다.');
    };
});

function cb_feedbackdetail(outdata, indata){
	/*
	if (indata && indata.data && indata.data.map && indata.data.map.seq) {
		var $detail = $('.chat_talk:has(.chat_answer[data-seq='+indata.data.map.seq+']) .feedback_detail')
		$detail.find('.btn_answer,.feedback_opinion,.btn_feedback_detail')
			.attr('disabled', true);
		$('<p>', {text:'* 제출되었습니다. 소중한 의견 감사드립니다.',class:'info_pop_txt'})
			.insertAfter($detail.find('.btn_feedback_detail'))
	} else {*/
		alert("제출되었습니다.");
	/*
	}
	 */
}

function openDetailInput(btn, query){
	var chatBox = btn.parent().parent().parent().parent();
	var detailBox = "";
	if(chatBox.find("input").length == 0){
		detailBox += "<div class='feedback_detail'>";
		detailBox += "<input type=\"text\" class=\"inp_detail feedback_opinion\" placeholder=\"의견을 들려주세요.\">";
		detailBox += "<button type=\"button\" class=\"btn_feedback_detail\">전송</button>";
		detailBox += setSearchLink(query);
		detailBox += "</div>";
		chatBox.append(detailBox);		
	}
}

function closeDetailInput(btn){
	var chatBox = btn.parent().parent().parent().parent();
	if(chatBox.find("input").length != 0){
		chatBox.find('.feedback_detail').remove();
	}
}

function setFeedback(btn){
	var intent = btn.parent().data("intent");
	var seq = btn.parent().attr("data-seq") || -1;
	var score = btn.data("score");
	var query =btn.parents('.bot_wrap').prev().find('.user_txt').text();
	var detail = btn.parents('.chat_talk').find('.feedback_opinion').val() || "";
	
	btn.parent().find(".on").removeClass("on");
	btn.addClass("on");
	
	if(score == 3){
		detail = "";
	}
	
	var param={
			"intent" : intent,
			"score"  : score,
			"seq"	 : seq,
			"query"  : query,
			"answer" : "", 
			"detail" : detail
		}
	
	js_util_fnAjax('./ajax/saveIntentFeedback', param, updateFeedbackSeq);
	
	if(score != "3"){
		if(score == "1"){
			//var msg = "딱 맞는 답변을 할 수 있을 때까지 더 열심히 학습할게요.<br>다시 아래 메뉴를 선택하거나 직접 질문을 입력해 보세요.";
			//appendMainMenu(msg);
		}
		openDetailInput(btn, query);
	}else{
		closeDetailInput(btn)
	}
}

function updateFeedbackSeq(result) {
	seq = result.result.message;
	var _par_div = _feedbackBtn.parent();
	_par_div.attr("data-seq", seq);
}

// 초기 인입 기본맨트
function init() {
	chatState = true;
	
	var UserAgent = navigator.userAgent;

	if (UserAgent.match(/iPhone|iPod|Android|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|Opera Mini|Opera Mobi|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null 
			|| UserAgent.match(/LG|SAMSUNG|Samsung/) != null){
		$('#call_htn').hide();
	}
	
	setTimeBox();
	
}

//질문 하기전 세션ID값 확인.
function ichat_question(query){
	closePopup();
	param = query;
	$('.keyword_box').hide();
	var param={
			"query" : query,
			"sessionId"  : "",
			"button:"	:  "y"
		}
	
	js_util_fnAjax('./ajax/getSessionId', param, question);
	
}

//질문 하기전 세션ID값 확인.
function sessionCheck(query){
	param = query;
	
	var param={
			"query" : query,
			"sessionId"  : "",
			"button:"	:  "n"
		}
	
	js_util_fnAjax('./ajax/getSessionId', param, question);
	
}
var oriQuery;
//질문하기
function question(result) {
	param = result.result.message;
	//$('#sentence').blur();
	
	if(!chatState){
		return;
	}

	if(param==undefined){
		var userQuery = $('#sentence').val();
		if(userQuery==""){
			return;
		} 
		var regExp = /[\{\}\[\]|\)~*`^\+<>\#$%&\\\=\(\'\"]/gi;
		if(regExp.test(userQuery)){
			var param = userQuery.replace(regExp, "");
		}else{
			var param = userQuery;
		}	
		$("#sentence").val("");
	}

	//chatState = false;
//	$('.intent').parents('ul').remove();
	
	// 질문 박스
	oriQuery = param;
	userQuestion(param);
	param = maskingFunc.masking(param);
	var data={
			"query" : param,
			"isMobile" : isMobile()
		}
	typingMotionShow();
	setTimeout(function(){
		js_util_fnAjax('./ajax/getAnswer', data,getAnswer)
	}, 1000);
}

function setRecentQuery(mainUserQuery){
	var value = cookieFunc.get('rq');
	var userQuery = mainUserQuery || oriQuery;
	userQuery = oriQuery + "^" + userQuery;
	var vArr = [];
	var isNew = true;
	if(value == null){
		value = userQuery;
	}else{
		vArr = value.split('|');
		vArr.forEach(function(v){
			var querys = v.split("^");
			if(querys[0] == oriQuery){
				isNew = false; 
			}
		});
		
		if(isNew){
			if(vArr.length > 4){
				vArr = vArr.slice(0,-1);	
			}
			vArr.unshift(userQuery);
			value = vArr.join('|');
		}
		
	}
	cookieFunc.set('rq', value, 1);	
	getRecentQuery(value);
}

function getRecentQuery(val){
	$("#recentIntent").empty();
	var value = val || cookieFunc.get('rq');
	var vArr = [];
	if(value == null){
		$("#recentIntent").text("문의내역이 없습니다.");
	}else{
		vArr = value.split('|');
		vArr.forEach(function(v){
			var querys = v.split("^");
			var _li = $("<li>"
				+ "<a href='javascript:;'onclick=\"ichat_question('" + querys[0] + "')\")>"+ querys[0] + "</a>"
			+ "</li>");
			$("#recentIntent").append(_li);
		});
	}
}


function typingMotionShow(){
	typingMotionHide();
	var _div = $("<div>");
	_div.attr("id", "typingMotion");
//	_div.html(""
//	+'<div class="bot_wrap">'
//	+	'<span class="ico_comm ico_logo"></span>'
//	+	'<div class="bot_box">'
//	+		'<div class="bot_txt">'
//	+			'<div class="chat_talk font" style="font-size:14px">'
//	+				'<div class="spinner">'
//	+					'<div class="bounce1"></div>'
//	+					'<div class="bounce2"></div>'
//	+					'<div class="bounce1"></div>'
//	+				'</div>'
//	+			'</div>'
//	+		'</div>'
//	+	'</div>'
//	+'</div>');

	var spinner = $("<div class='spinner'><div class='bounce1'></div><div class='bounce2'></div><div class='bounce1'></div></div>");
	
	var talkContent = makeAnswerContent().find('.chat_talk').append(spinner).parent();

	talkContent = setBotChat().find('.bot_box').append(talkContent).parent();
	_div.html(talkContent);
	
	$(".chat_cont").append(_div);
	goLast();
}

function typingMotionHide(){
	$("#typingMotion").remove();
}

//답변 화면에 보여주기.
function getAnswer(response, request) {
	// searchKeyword = response.searchKeyword;
	var result = response.result;
	var msg = response.result.message;
	var intentNm = result.intentNm;
	var mainUserQuery = result.mainUserQuery;
//	msg = result.message.replaceAll(' ', '&nbsp;');
	var subAnswer = response.subAnswer;
	var caroAnswer = response.caro;
	var userId = null;

	setRecentQuery(mainUserQuery);

	if (document.querySelector('#userId')) {
		userId = document.querySelector('#userId').value;
	}

	var procNm = '';
	if (userId && _sportsApi.isApiIntent(intentNm)) {
		procNm = _sportsApi.getIntentApiType(intentNm);
		if (procNm.indexOf('G') == 0) {
			if(msg.indexOf('<USER>') != -1){
				msg = msg.substring(msg.indexOf('<USER>') + 6, msg.indexOf('</USER>'));
			}
			return apiHelper.init(procNm, msg, intentNm);
		}
	}

	var isRegist = (procNm.indexOf('R') === 0);
	if(result != null) { //답변 불러와지면 typingmotion 숨기기
		typingMotionHide();	
	}
	
	if(intentNm == null || intentNm == "Default_Fallback_Intent" || intentNm == "Default_Greeting_Intent"){
		isHelp = "N";
	}else {
		isHelp = "Y";
	}
	
		
	if(intentNm == "Default_Fallback_Intent"){
		msg += setSearchLink(request.data.map.query);
	}

	if(intentNm == "Default_Greeting_Intent"){
		appendMainMenu(msg);
	}else if(caroAnswer != '' && caroAnswer != null){
		makeCaro(response);
	}else if(result.nodeType == "BUTTON"){
		makeButton(response);
		
		var optionLth = response.result.optionList.length;
		if(optionLth > 1){
			slickActive();
		}
		
	}else{
		if(msg.indexOf('<USER>') != -1){
			if(userId){
				msg = msg.substring(msg.indexOf('<USER>')+6,msg.indexOf('</USER>'));		
			} else {
				msg = msg.substring(msg.indexOf('<DEFAULT>')+9,msg.indexOf('</DEFAULT>'));
				msg += '<a href="javascript:;" class="btn_inner link" onclick=\'goLoginPage();\' title="사이트 바로가기">' +
						'스포츠 지원포털 로그인 페이지로 이동' +
						'</a>';
				subAnswer = "";
			}
		}
		if(isRegist){
			var seqParam = {code:procNm, num:1, intentNm:intentNm, mainUserQuery:mainUserQuery};
			Regist.setAnswer(seqParam).done(function(result){
				if(result !== undefined && typeof result === "object" && result.isUnder14){
					msg = '만 14세미만일 경우 법정대리인의 본인명의 휴대폰인증 또는 아이핀인증을 통해 법정대리인의 정보를 확인해야 합니다.<br><br>스포츠지원포털로 이동하신 후 재등록을 진행해주세요.'
					msg += '<a href="javascript:;" class="btn_inner link" onclick=\'goLoginPage();\' title="사이트 바로가기">' +
						'경기인 등록 화면으로 이동' +
						'</a>';
					subAnswer = "";
					mainUserQuery = "";
					result = "";
				}

				setAnswer(msg,subAnswer,intentNm, mainUserQuery, result)

				if(isHelp == "Y"){
					setHelp(fontSize, intentNm);
				}

				goLast();
			});

		}else{
			setAnswer(msg,subAnswer,intentNm, mainUserQuery);
		}
	}
	
	if(isHelp == "Y" && !isRegist){
		setHelp(fontSize, intentNm);
	}
	
//	$(".talk_wrap").scrollTop($('.talk_wrap')[0].scrollHeight);
	goLast();
	
}

function showMore(id){
	$(id).hide();
	$(id).parent().nextAll("li").css("display","block");
}

function goLast() {
	$(".chat_cont").scrollTop($('.chat_cont')[0].scrollHeight);
	$link = $('.chat_cont .bot_wrap:last').find('a:visible,button:visible,input:visible,select:visible')
	if ($link.length > 0) {
		$link.eq(0).focus()
	}
}

function goLink(url){
	var option = 'height='+screen.height+',width='+ screen.width +',scrollbars=yes';
	url = encodeURI(url);
	
	if(url.indexOf(":mURL:") != -1){
		var uidx = url.indexOf(":mURL:");
		if(isMobile()){ //Mobile
			url = url.substring(uidx+6,url.length);
		}else{ 
			url = url.substring(0,uidx); //PC
		}
	}
	var openNewWindow = window.open("about:blank");
	openNewWindow.location.href = url
	/*window.open(url,'_cblank',option);*/
}

function isMobile(){
	var filterOs = "win16|win32|win64|mac|macintel";
	//PC 및 모바일 접속 확인 실시
	if(navigator.platform){
		if(0 > filterOs.indexOf(navigator.platform.toLowerCase())){
			return true;				
		}
		else {
			return false;   				
		}    			    				    			
	}
}

function finish(){
	var _ua = window.navigator.userAgent||window.navigator.vendor||window.opera; 

    if(_ua.toLocaleLowerCase().indexOf("kakaotalk") != -1) {
        window.location.href = (/iPad|iPhone|iPod/.test(_ua)) ? "kakaoweb://closeBrowser" : "kakaotalk://inappbrowser/close";
    }else{
    	window.close();
    }
}


function openFont(){
	//$('.bottom_btn_group').addClass('fz');
	$(".font_layer").show();
}

function closeFont(){
	$(".font_layer").hide();
	
	//$('.bottom_btn_group').removeClass('fz');
	
	$(".font").css("font-size", font);
	$(".user_txt").css("font-size", font);
	$('.ui-autocomplete').css('font-size',font);
}

function openGuide(){
	$('.guide_bg').show();
}

function closeGuide(){
	$('.guide_bg').hide();
}

function fontControl(size){
	$(".chat_menu .icon_outer .icon_inner").css("height", "");
	if(size == '1'){
		font = 12;
		fontLeft =0;
		
//		$(".slide_btn").css("left",fontLeft+"px");
//		$(".font_sample").css("font-size", font+'px');
	}else if(size=='2'){
		font = 14;
		fontLeft =68;
//		$(".slide_btn").css("left",fontLeft+"px");
//		$(".font_sample").css("font-size", font+'px');
	}else if(size=='3'){
		font = 16;
		fontLeft =136;
//		$(".slide_btn").css("left",fontLeft+"px");
//		$(".font_sample").css("font-size", font+'px');
	}else if(size=='4'){
		font = 18;
		fontLeft =204;
//		$(".slide_btn").css("left",fontLeft+"px");
//		$(".font_sample").css("font-size", font+'px');
	}else{
		font = 20;
		fontLeft =272;
//		$(".slide_btn").css("left",fontLeft+"px");
//		$(".font_sample").css("font-size", font+'px');
	}
	$('.btn_font').removeClass('on');
	$('.fz'+size).parent().addClass('on');
	closeFont();
	
	resizeQuickMenuHeight();
}

function ichatReload(){
	js_util_fnAjax('./ajax/delApiSession', 'sessionKey=' + encodeURIComponent(sessionKey),delSession);
	
}

function delSession(response){
	window.location.reload(true);
}
//챗봇 버튼 만들어주기.
function makeButton(response) {
	var result = response.result;
	var option = result.optionList;
	append ="";
	var msg = response.result.message;
	//msg = result.message.replaceAll(' ', '&nbsp;');
	var subAnswer = response.subAnswer;
	var optionLth = option.length;
	
	$.each(option,function(key,v) {
		if(v.type=="PHONE"){
			if(key == 0){
				append='<div class="btn_list">';
			}
				append+='<a href="javascript:;" class="btn" onclick="showPhone(\''+v.value+'\'); return false;">'+v.label+'</a>';
		}else if(v.type=="BTN"){
			if(key == 0){
				append='<div class="query_wrap">';
				
				if(optionLth == 1){
					append+='<div class="query_list variable-width slick-initialized slick-slider" style="margin-bottom: 10px;">';
					append+='<button class="slick-prev slick-arrow slick-disabled" aria-label="Previous" type="button" aria-disabled="true" style="">Previous</button>';
				}else{
					append+='<div class="query_list variable-width">';
				}
			}
			if(v.value == 'newsession'){
				append+='<a href="javascript:;" class="btn" onclick=\"ichatReload(); return false;\">#'+v.label+'</a>';
			}else{
				append+='<div class="query_box">';
				append+='<button type="button" class="word" onclick=\"ichat_question(\''+v.value+'\'); return false;\">'+v.label+'</button>';
				append+='</div>';
				if(optionLth == 1){
					append+='<button class="slick-next slick-arrow slick-disabled" aria-label="Next" type="button" style="" aria-disabled="true">Next</button>';
				}
			}
		}else if(v.type=="RBTN"){
			if(key == 0){
				append='<div class="btn_list">';
			}
			append+='<a href="javascript:;" class="btn" onclick=\"ichat_question(\''+v.value+'\'); return false;\">'+v.label+'</a>';
		}
	});
		
	append+="</div></div>"
	setAnswer(msg,subAnswer,result.intentNm,result.mainUserQuery);
	$(".bot_box .bot_txt:last").after(append);
}

function setSlider(){
	var swiper = new Swiper(".imgSlider", {
    	navigation: {
    		nextEl: ".swiper-button-next",
    		prevEl: ".swiper-button-prev"
    	},
    	pagination: {
	        el: ".swiper-pagination"
    	}
	});	
}



function showPhone(phone){
	
	window.open('./chatPhone?step2='+phone,'_dblank','width=414,height=736,resizable=1,scrollbars=1,toolbar=0,location=0, directories=0, status=0, menubar=0');
}



function openDetail(){
	$('.pop_layer .cont').html(detailAnswer);
	$('.pop_bg').show();
	$('.pop_layer').show();
}

function closeDetail(txt){
	$('.pop_layer .cont').html('');
	$('.pop_bg').hide();
	$('.pop_layer').hide();
}

function showButton(test){
	
	var accDt = $(test).parent();
	var accDD = accDt.next('dd');
	if (!accDt.hasClass('active')) {
		$('.accordion dt').removeClass('active');
		accDt.addClass('active');
		$('.accordion dd').hide();
		accDD.slideDown(400).show();
	} else {
		accDt.removeClass('active');
		accDD.hide();
	}

//$(test).parent().next().toggle();
}

function showStButton(test,strDivId){
	$('#'+strDivId).toggle();
	$(test).attr('style','display:none;');
}

function doImgPop(img){
	 img1= new Image();
	 img1.src=(img);
	 imgControll(img);
	}
	 
function imgControll(img){
 if((img1.width!=0)&&(img1.height!=0)){
    viewImage(img);
  }
  else{
     controller="imgControll('"+img+"')";
     intervalID=setTimeout(controller,20);
  }
}

function viewImage(img){
 W=img1.width;
 H=img1.height;
 O="width="+W+",height="+H+",scrollbars=yes";
 imgWin=window.open(img);
 /*imgWin=window.open("","",O);
 imgWin.document.write("<html><head><title>:*:*:*: 이미지상세보기 :*:*:*:*:*:*:</title></head>");
 imgWin.document.write("<body topmargin=0 leftmargin=0>");
 imgWin.document.write("<img src="+img+" onclick='self.close()' style='cursor:pointer;' title ='클릭하시면 창이 닫힙니다.'>");
 imgWin.document.close();*/
}

/*
function setAnswer(content,subAnswer,intentNm, mainUserQuery){
	var shortAnswer="";
	content = content.replace(/<cr><br>/g,'<cr>');
    
	var host = location.host;
	var paths = location.pathname.split('/');
	var path = paths[1];
	var hasSlider = false;
	
	if(mainUserQuery){
		var preQuery = "<ul class=\"word_box\">";
		preQuery += "<li><a class=\"word_link\" onclick=\"ichat_question('"+mainUserQuery+"'); return false;\">상위 질문으로 이동</a></li></ul>";
	}
	
	var linkContent = "";
	
	var answer_content = setBotChat();
	
	
	fontSize = font + "px";
	answer_html = $("<div>");
	
	var answerList = content.split('<cr>');
	for(i=0; i<answerList.length; i++){
		hasSlider = false;
		var answer_temp = answerList[i];
		
		if(answer_temp.toUpperCase().indexOf('<ST>') != -1){
			answer_temp = parseToggleDivision(answer_temp);
		}
		
		
		if(answer_temp.indexOf('<SLD>') != -1){
			hasSlider = true;
			var sld_txt = answer_temp.match(/<SLD>.+<\/SLD>/gi);
			var isOnlySlider = (sld_txt[0].length == answer_temp.trim().length) ? true : false;
			answer_temp = answer_temp.replace(sld_txt[0], sld_txt[0].replace(/<br>/gi, ""));
			sld_txt[0] = sld_txt[0].replace(/<br>/gi, "");
			var sld_title = []; //타이틀 따로 빼기 위한 변수
			sld_title = sld_txt[0].match(/<TIT>.+<\/TIT>/gi); //타이틀 찾기
			answer_temp = answer_temp.replace(sld_title[0], ""); //뺀 타이틀 지우기
			answer_temp = answer_temp.replace("<SLD>","").replace("</SLD>","");
			
			var sld_img = sld_txt[0].match(/<IMG>.*?<\/IMG>/gi); //슬라이더 안에 이미지 있을 경우
			var sldId = "sld_" + $('.chat_cont').find('.imgSlider').length;
			
			answer_temp = answer_temp.replace(/<IMG>.*?<\/IMG>/g, "");
			
			var temp_content = makeSlider(sldId, isOnlySlider, sld_txt[0]);			
			var sld_content = $("<div>");
			
			sld_content.html(answer_temp);
			
			
			if(!isOnlySlider){
				var sld_str ="";
				sld_content.append("<div class = 'word_box'>");
				if(sld_title.length > 0){ 
					sld_str = $('<a class="word_link '+sldId+'" onclick=\"openSlider(\''+sldId+'\'); return false;\">'+sld_title+'<div class="triangle top">');
					answer_temp = answer_temp.replace(sld_title[0], "");
				}else{
					sld_str = $('<a class="word_link '+sldId+'" onclick=\"openSlider(\''+sldId+'\'); return false;\">자세히 보기<div class="triangle top">');
				}
				sld_content.find('.word_box').append(sld_str).parent();
				
				if(mainUserQuery){
					sld_content.append(preQuery);
				}
			}
			answer_temp = sld_content.html();

		}
		
		if(answer_temp.indexOf('IMG') != -1){
			//var rep_img = content.match(/<IMG[^<]*>([^\/]*)<\/IMG>/gi);
			var rep_img = answer_temp.match(/<IMG>.*?<\/IMG>/gi);
			var real_img = "";
			var imgAttr = "";
			for(j=0; j<rep_img.length;j++){
				var temp_img = "" ;
				var imgVO = rep_img[j].replace("<IMG>","").replace("</IMG>","");
				var imgVOArr = imgVO.split('^');
				real_img = imgVOArr[0];
				imgAttr = JSON.parse(imgVOArr[1]);
				temp_img += '<div class="img_box" onclick="doImgPop(\'http://'+host+'/' + path + '/resources/upload/'+real_img+'\')" style="cursor:pointer;">';
				temp_img += '<div class="" onclick="doImgPop(\'http://'+host+'/' + path + '/resources/upload/'+real_img+'\')" style="cursor:pointer;">';
				temp_img += '<a href="#" class="bot_img">';
				temp_img +=	'<img alt="'+imgAttr.description+'" class="img" src="http://'+host+'/' + path + '/resources/upload/' + real_img + '">';
				temp_img += '</a>';
				temp_img += '</div>';
				answer_temp = answer_temp.replace(rep_img[j],temp_img);
			}
		}
	
		if(answer_temp.toUpperCase().indexOf('FBTN') != -1){
			answer_temp = answer_temp.replace(/<\/fbtn><br>/g,'<\/fbtn>');
			var startIdx = answer_temp.toUpperCase().indexOf("<FBTN>");
			var endIdx = answer_temp.toUpperCase().indexOf("</FBTN>");
			var temp_fbtn = answer_temp.substring(startIdx+6,endIdx);		
			var fbtn_answer = "";
			var last_answer = "";
			var rep_fbtn = temp_fbtn.split('|');

			fbtn_answer += '<dl class="accordion">';
			for(k=0; k<rep_fbtn.length;k++){
				var temp =rep_fbtn[k].split('^'); 
                fbtn_answer += '<dt class="acc_dt">';
				fbtn_answer += "<a href=\"#n\" onclick=\"showButton(this); return false;\" class=\"arr\">"+temp[0].replace('<br>','')+"</a>";
                fbtn_answer += '</dt>';
				fbtn_answer += "<dd class=\"acc_dd\" style=\"display:none;\">"+temp[1]+"</dd>";
			}
            fbtn_answer += '</dl>';
			last_answer += answer_temp.substring(0,startIdx);
            last_answer += fbtn_answer;
            last_answer += answer_temp.substring(endIdx+7);

			answer_temp = last_answer;
		}
	
		if(!isOnlySlider || !hasSlider){
			var tempAnswerElse = makeAnswerContent();
			answer_html.append(tempAnswerElse.find('.chat_talk').append(answer_temp).parent());
		}
		
		if(hasSlider) { //슬라이더용 말풍선 추가되는 곳
			answer_html.append(temp_content);
		}
	}

	answer_content = answer_content.find('.bot_box').prepend(answer_html.html()).parent();
	$(".chat_cont").append(answer_content);
	
	if(answer_content.html().indexOf("accordion") != -1){
		$(".bot_wrap:last").addClass('accordion');
	}
	
	setTimeBox();
//	slickActive();
	if(subAnswer !="" && subAnswer !=null){
		makeBtn(subAnswer);
	}

	if(mainUserQuery && !hasSlider){
		$(".bot_txt .chat_talk:last").append(preQuery)
	}
	
	if(hasSlider){
		chkHasSlider(sldId, sld_img.length);
	}
	
}
*/
function setAnswer(content,subAnswer,intentNm, mainUserQuery,apiAnswerHtml){
	var shortAnswer="";
	content = content.replace(/<cr><br>/g,'<cr>');

	var hasSlider = false;
	
	if(mainUserQuery){
		var preQuery = "<br><ul class=\"word_box\">";
		preQuery += "<li><a href=\"javascript:;\" class=\"word_link\" onclick=\"ichat_question('"+mainUserQuery+"'); return false;\">상위 질문으로 이동</a></li></ul>";
	}
	
	var linkContent = "";
	var answer_content = setBotChat();
	sldCnt = 0;
	
	fontSize = font + "px";
	answer_html = $("<div>");
	
	var answerList = content.split('<cr>');
	var imgSliderValue = {
		sliderContent: "",
		isOnlySlider: false
	};
	
	for(i=0; i<answerList.length; i++){
		hasSlider = false;
		var answer_temp = answerList[i];
		
		if(answer_temp.toUpperCase().indexOf('<ST>') != -1){
			answer_temp = parseToggleDivision(answer_temp);
		}
		
		if(answer_temp.indexOf('<SLD>') != -1){
			hasSlider = true;
			imgSliderValue = createImgSlider(answer_temp); //<SLD></SLD> 슬라이더 이미지 생성
			answer_temp = createSliderBtn(answer_temp, mainUserQuery, preQuery, imgSliderValue.isOnlySlider); //<TIT>를 포함한 다른 내용
		}
		
		if(answer_temp.indexOf('IMG') != -1){
			answer_temp = makeImgBox(answer_temp);
		}
	
		if(answer_temp.toUpperCase().indexOf('FBTN') != -1){
			answer_temp = makeFbtn(answer_temp);
		}

		if(!imgSliderValue.isOnlySlider || !hasSlider){
			var tempAnswerElse = makeAnswerContent();
			tempAnswerElse.find('.chat_talk').append(answer_temp);
			if(apiAnswerHtml != null){
				tempAnswerElse.find('.chat_talk').append(apiAnswerHtml);
			}
			answer_html.append(tempAnswerElse);
		}
		
		if(hasSlider) { //슬라이더용 말풍선 추가되는 곳
			answer_html.append(imgSliderValue.sliderContent);
		}
		
	}

	answer_content = answer_content.find('.bot_box').prepend(answer_html.contents()).parent();
	$(".chat_cont").append(answer_content);
	
	if(answer_content.html().indexOf("accordion") != -1){
		$(".bot_wrap:last").addClass('accordion');
	}
	
	setTimeBox();
//	slickActive();
	if(subAnswer !="" && subAnswer !=null){
		makeBtn(subAnswer);
	}

	if(mainUserQuery && !hasSlider){
		$(".bot_txt .chat_talk:last").append(preQuery)
	}
	
	if(hasSlider){
		for(s=0;s<sldCnt;s++){
			var sliderId = "sld_" + ($('.chat_cont').find('.imgSlider').length - 1 - s);
			chkHasSlider(sliderId);
		}
	}
	
}

function openSlider(id){
	var _slider = $("#"+id);
	var _slider_box = _slider.parent().parent();
	var isShow = _slider_box.is(':visible');
	var isOn = _slider.hasClass('on');
	if(isShow && !isOn){
		_slider_box.hide();
		$("."+id + " > div").removeClass('bottom');
		$("."+id + " > div").addClass('top');
		$(".word_link" + "."+id).removeClass('opened');
	}else{
		$(".word_link" + "."+id).addClass('opened');
		_slider_box.show();
		$("."+id + " > div").removeClass('top');
		$("."+id + " > div").addClass('bottom');
		closeSlider(id);
	}
}

function setHelp(fontSize, intentNm){
	var help_html = "";
	help_html += '<div class="answer_box"><div class="answer_cont"> 도움이 되셨나요?';
	help_html += '<div class="chat_answer" data-intent="'+intentNm+'">';
	help_html += '<button type="button" class="btn_answer" data-score="3">';
	help_html += '<span class="ico_perfect"></span>';
	help_html += '<span class="answer_txt">정확해요</span>';
	help_html += '</button>';
	help_html += '<button type="button" class="btn_answer" data-score="2">';
	help_html += '<span class="ico_ambiguous"></span>';
	help_html += '<span class="answer_txt">애매해요</span>';
	help_html += '</button>';
	help_html += '<button type="button" class="btn_answer" data-score="1">';
	help_html += '<span class="ico_wrong"></span>';
	help_html += '<span class="answer_txt">틀렸어요</span>';
	help_html += '</button>';
	help_html += '</div></div></div>';
	
	var help_content = makeAnswerContent().find('.chat_talk').append(help_html).parent();

	$(".chat_cont .bot_box:last > div:last").after(help_content);	
}

function IsJsonString(str) {
	  try {
	    var json = JSON.parse(str);
	    return (typeof json === 'object');
	  } catch (e) {
	    return false;
	  }
	}

function makeCaro(response){
	var msg = response.result.message;
	//msg = result.message.replaceAll(' ', '&nbsp;');
	var caro = response.caro;
	var result = response.result;
	var subAnswer;
	
	msg = msg.replace(/<cr><br>/g,'<cr>');
	var answerList = msg.split('<cr>');

	answer_html = "";
	answer_html += '<div class="bot_wrap clearfix">';
	answer_html += '<div class="bot_img"></div>';
	answer_html += '<div class="bot_box">';
	answer_html += '<div class="txt font" style="font-size:'+fontSize+'">'+answerList[0]+'</div>';
	answer_html += '<div class="card_wrap">';
	answer_html += '<div class="menu_list">';
	
	$.each(caro,function(key,v) {
		subAnswer = v.content;
		if(IsJsonString(subAnswer)&&subAnswer!=null){
			subAnswer = JSON.parse(subAnswer);
			answer_html += '<div class="card">';
			answer_html += '<div class="card_tit">';
			answer_html += '<p>'+v.name+'</p>';
			answer_html += v.desc;
			answer_html += '</div><div>';
			
			$.each(subAnswer.dataArray, function(i, dataArray) {
				answer_html += "<a href=\"javascript:;\" onclick=\"ichat_question('"+dataArray.mainUserQuery+"'); return false;\" class=\"btn btn_block\">"+dataArray.name+"</a>";
			});
			answer_html += '</div></div>';
		}else{

			//subAnswer = JSON.parse(subAnswer);
			answer_html += '<div class="card">';
			answer_html += '<div class="card_tit">';
			answer_html += '<p>'+v.name+'</p>';
			//answer_html += v.desc;
			answer_html += '</div><div>';
			answer_html += v.desc;
			answer_html += '</div></div>';
		}

	});
	answer_html += '</div></div>';
	if(answerList.length > 1){
		answer_html += '<div class="txt font" style="font-size:'+fontSize+'">'+answerList[1]+'</div>';
	}
	
	if(result.nodeType == "BUTTON"){
		var option = result.optionList;
		var append = '';
		$.each(option,function(key,v) {
			if(v.type=="BTN"){
				if(key == 0){
					append='<div class="btn_list">';
				}
				if(v.value == 'newsession'){
					append+='<a href="javascript:;" class="btn" onclick=\"ichatReload(); return false;\">'+v.label+'</a>';
				}else{
					append+='<a href="javascript:;" class="btn" onclick=\"ichat_question(\''+v.value+'\'); return false;\">'+v.label+'</a>';
				}	
			}else if(v.type=="RBTN"){
				if(key == 0){
					append='<div class="btn_list">';
				}
				append+='<a href="javascript:;" class="btn" onclick=\"ichat_question(\''+v.value+'\'); return false;\">'+v.label+'</a>';
			}
		});
			append+="</div>"
			answer_html+=append;
	}
	
	answer_html += '<span class="time"></span>';
	answer_html += '</div>';
	answer_html += '</div>';
	
	$(".chat_cont").append(answer_html);
	setTimeBox();
	
	fnSlidemenu();
}
function makeBtn(subAnswer){
	
	subAnswer = JSON.parse(subAnswer);
		if(subAnswer != "" && subAnswer != null){
			var linkContent = "<br><ul class=\"word_box\">";
			var relationContent = "<div class=\"btn_list\">";
			
			$.each(subAnswer, function(i, dataArray) {
				switch (dataArray.linkType.toUpperCase()) {
					case "IL":	linkContent += "<li><a href=\"javascript:;\" class=\"word_link\" onclick=\"ichat_question('"+dataArray.mainUserQuery+"'); return false;\">"+dataArray.name+"</a></li>";
						break;
				//	case "RL":  relationContent += "<a class=\"btn\" onclick=\"ichat_question('"+dataArray.mainUserQuery+"'); return false;\">"+dataArray.name+"</a>";
				//		break;
				//	case "PA":	
				//				linkContent += "href=\"" + dataArray.ref + "\" target=\"_blank\"";
		//						if(devicetype == 'app' &&  dataArray.ref.indexOf('cyber')>0){
		//							linkContent = "href=\"fnAppLink('" + dataArray.ref + "')\"";
		//						}else{
		//							linkContent = "href=\"" + dataArray.ref + "\" target=\"_blank\"";
		//						}
				//				linkContent += "href=\"" + dataArray.ref + "\" target=\"_blank\"";	
				//				break;
				}
			
			});
			
			
			if(linkContent!="<br><ul class=\"keyword\">"){
				linkContent += "</ul>";
				$(".bot_txt .chat_talk:last").append(linkContent);		
			}
			
			if(relationContent!="<div class=\"btn_list\">"){
				relationContent += "</div>";
				$(".bot_box .txt:last").after(relationContent);
			}
	}

}

function fnSlidemenu() {
	
	$(".card_wrap").addClass('touchSlider');
	$(".ts-controls").css("display","block");
   
	var width_size = window.outerWidth;
	
	if(width_size >=450){
		$(".touchSlider").touchSlider({
			flexible : false,
			multiple : true,
			limit : true,
			useMouse:true,
			speed:250,
			transition:false,
			paging:true,
			view : 1,
			pleft: 230
		});
	}else{
		$(".touchSlider").touchSlider({
			flexible : false,
			multiple : true,
			limit : true,
			useMouse:true,
			speed:250,
			transition:false,
			paging:true,
			view : 1,
			pleft: 100
		});
	}
}

//12시간변환
function convert12H(a) {
	var time = a; // 'hh:mm' 형태로 값이 들어온다
	var getTime = time.substring(0, 2); // 시간(hh)부분만 저장
	var intTime = parseInt(getTime); // int형으로 변환

	if (intTime < 12) { // intTime이 12보다 작으면
		var str = '오전 '; // '오전' 출력
	} else { // 12보다 크면
		var str = '오후 '; // '오후 출력'
	}

	if (intTime == 12) { // intTime이 12면 변환 후 그대로 12
		var cvHour = intTime;
	} else { // 나머지경우엔 intTime을 12로 나눈 나머지값이 변환 후 시간이 된다
		var cvHour = intTime % 12;
	}

	// 'hh:mm'형태로 만들기
	var res = str + ('0' + cvHour).slice(-2) + time.slice(-3);
	return res;
}

//시간앞자리0맞춰주기
function checkTime(i) {
	if (i < 10) {
		i = "0" + i;
	}
	return i;
}

function getHour() {
	return convert12H(checkTime(new Date().getHours()) + ':'
			+ checkTime(new Date().getMinutes()));
}

//유저 질문 말풍선
function userQuestion(content){
	fontSize = font + "px";
	
	question_html = "";
	question_html += '<div class="user_wrap">';
	question_html += '<div class="user_box" style="font-size:'+fontSize+'">';
	question_html += '<div class="user_txt"></div>';
	question_html += '<span class="time user"></span>';
	question_html += '</div>';
	question_html += '</div>';
	$(".chat_cont").append(question_html);
	$(".user_box .time:last").text(getHour());
	$(".user_box .user_txt:last").text(content);			
}

//파일 다운로드
function fileDownload(saveFileName){
	location.href = "fileManageDownload.do?saveFileName=" + saveFileName + "&projectId=" + $("#projectId").val();
}

function parseToggleDivision(answer){
	var result = "";
	var arrToggleDiv = answer.split('<st>');
	
	var strDivId = Math.random().toString().substring(2,8);
	
	var toggleHtml = "";
	//result += arrToggleDiv[0].replace('<br>','');
	result += arrToggleDiv[0];
	result += "<a href=\"javascript:;\" class=\"btn_inner all\" title=\"상세내용보기\" style=\"cursor: pointer;\" onclick=showStButton(this,\"strDivId_"+strDivId+"\"); return false;><span>더보기</span></a>";
	result += "<div id=\"strDivId_" + strDivId + "\" style=\"display:none;\">" +arrToggleDiv[1] + "</div>"; 
	
	return result;
}

var resizeObserver = new ResizeObserver(function(entries) {
	$.each(entries, function (i, entry) {
		resizer($(entry.target).parents('.popup').get(0))
	})
})
function openPopup(popupStr){
	$('.dimed').show();
	if ($('.popup'+popupStr).length > 0) {
		$('.popup' + popupStr).show();
		$('.popup' + popupStr).attr('tabindex', '0').focus();
		$('.popup' + popupStr).each(function (i, el) {
			resizer(el)
			var target = $(el).find('.layer_cont').get(0)
			if (target) {
				resizeObserver.observe(target);
			}
		})
	}
//	$('.popupSurvey').show();
}
function closePopup(){
	if ($('.popup:visible:has(.pop_swipe_wrap)').length > 0) {
		$('.popup:visible:has(.pop_swipe_wrap)')
			.find('.pop_swipe_wrap.slick-initialized')
			.slick('slickGoTo', 0, false);
		$('.popup:visible:has(.pop_swipe_wrap)')
			.find('.pop_btn.blue').text('다음')
	}
	if ($('.popup:visible').length > 0) {
		$('.popup:visible .layer_cont').each(function (i, el) {
			resizeObserver.unobserve(el)
		})
	}
	$('.dimed').hide();
	$('body > div[class^="popup"]').hide();
	//$('.popup').hide();
	$('.ico_star').not('.is_selected').addClass('is_selected');
	$('#opinion').val('');
}
function nextSlide(target) {
	var methodNm = 'slickNext';
	var btnNm = '이전'
	if ($(target).text().indexOf('이전') >= 0) {
		methodNm = 'slickPrev';
		btnNm = '다음'
	}
	$(target).parents('.popup').eq(0).find('.pop_swipe_wrap').slick(methodNm);
	$(target).text(btnNm)
}

function insertFeedbackHistory(){
	var feedbackScore = $('.ico_star.is_selected').length;
	var feedbackContent = $('#opinion').val();
	var projectId = $("#projectId").val();
	var param = 'feedbackScore=' + encodeURIComponent(feedbackScore) + '&feedbackContent=' + encodeURIComponent(feedbackContent) + "&projectId=" + encodeURIComponent(projectId);
	js_util_fnAjax('./ajax/insertFeedbackHistory', param,finish());
}

function appendMainMenu(msg){
	var menuList = $('.chat_menu').html();
	var chatMenu = $('<ul>', {"class":"chat_menu font","style":"font-size:"+font+"px;"}).append(menuList);
	var time = $('<span>',{"class":"time"});
	var botBox = $('<div>',{"class":"bot_box"});
	var botWrap = $('<div>',{"class":"bot_wrap"});
	var msg_html = "";
	botWrap.append('<span class="ico_comm ico_logo"></span>');
	if(msg){
		var answerList = msg.split('<cr>');
		for(i=0; i<answerList.length; i++){
			var answer_temp = answerList[i];
//			msg_html += '<div class="bot_txt">';
//			msg_html += '<div class="chat_talk font" style="font-size:'+font+'px;">'+answer_temp+'</div>';
//			msg_html += '</div>';
			msg_html = makeAnswerContent().find('.chat_talk').append(answer_temp).parent();

		}
		
		botBox.append(msg_html);	    
	}else{
		botBox.append($(".bot_txt").first().clone());
	}
	
	botBox.append(chatMenu);
	botBox.append($('.test_box').prop("outerHTML"));
	botBox.append(time);
	botWrap.append(botBox);
	$(".chat_cont").append(botWrap);
	setTimeBox();
	goLast();
}

function slickActive(){
	$('.variable-width:last').slick({
		slidesToShow: 1,
		slidesToScroll: 1, //스크롤 한번에 움직일 컨텐츠 개수
		arrows: true, // 옆으로 이동하는 화살표 표시 여부
		dots: true, // 하단 paging버튼 노출 여부
		infinite: false, // 양방향 무한 모션
		fade: false, //페이드모션 실행 여부
		variableWidth: true,
		cssEase: 'ease-out' //css easing 모션 함수
	});
}

function resizeQuickMenuHeight(){
	var _lis = $("#chat_menu > li .icon_outer .icon_inner");
	
	var maxHeight = Math.max.apply(null, _lis.map(function (){
    	return $(this).height();
	}).get());
	
	$(".chat_menu .icon_outer .icon_inner").css("height", maxHeight);
}

function saveSearchLog(query, searchQuery){ //검색 통계 로그
	var isPlatform="M";
	var logEnterType = "S";
	if(!isMobile()){
		isPlatform="W";
	}
	if(isHelp == "Y"){
		logEnterType = "S";
	} else {
		logEnterType = "D";
	}

	let logKeyword = getSearchQuery(searchQuery, "log");

	var param = {
		"logQuery" : query,
		"logMorphAnalResult" : logKeyword,
		"logPlatformType" : isPlatform,
		"logEnterType" : logEnterType,
		"projectId" : $("#projectId").val()
	}
	js_util_fnAjax('./ajax/saveLogSearch', param, '');
}


function setSearchLink(query){ //만족도 조사, fallback에 따라 문구 및 스포츠 지원 포털 이동 버튼 생성
	var searchLinkBtn="";
	
	if(isHelp=="Y"){ //만족도 조사일 경우
		searchLinkBtn +="</br></br>만족스러운 답을 얻지 못하셨나요?</br>";
		searchLinkBtn += "보다 상세한 검색을 원하시면 검색버튼을 클릭하여 직접 검색해보실 수 있어요.";
	}
	
	searchLinkBtn +="<a href=\"javascript:;\" class='btn_inner link'";
	searchLinkBtn += "onclick=\"goSearch('"+query+"');\"";
	searchLinkBtn += "title='통합검색 바로가기'>통합검색 > "+query+"</a>";
	
	return searchLinkBtn;
}

function goSearch(query){ //스포츠 지원 포털 검색 이동, 로그 저장
	let morphedQuery = getMorphWord(query);
	let searchQuery = "";
	if(morphedQuery != ""){
		// searchKeyword = getMorphWord();
		searchQuery = getSearchQuery(morphedQuery, "search");
	}

	goLink('https://portal.sports.or.kr/search/result.do?searchQuery='+searchQuery + '&query=' + searchQuery);
	
	saveSearchLog(query, searchQuery);
	return false;
}

function openAlertPopup(tit, cont, isNotClose){
	$("#alert_tit").html(tit || '경고')
	$("#alert_cont").html(cont.replaceAll("\n","<br/>"))
	if (isNotClose === true) {
		typingMotionHide();
		$("#alert_btn").attr("onclick", "closePopup(); return false;");
		setAnswer("<b>"+tit+"</b><br/>"+cont, "[]", "API")
	} else {
		$("#alert_btn").attr("onclick", "window.close();");
	}
	openPopup(".alert");
}

function openConfirmPopup(tit, cont, callback){
	$("#confirm_tit").html(tit || '확인')
	$("#confirm_cont").html(cont.replaceAll("\n","<br/>"))
	$('#confirm_btn').off().on('click', function(){
		callback();
		closePopup();
	})
	openPopup(".confirm");
}

function goLoginPage(userSessionChk){ //로그인 페이지로 이동되는 confirm창
	var str="";
	if(userSessionChk == "Y"){
		str = "접속 시간이 길어져서 연결이 끊겼습니다.\n\n";
	}
	str += "로그인 페이지로 이동하며 챗봇은 종료됩니다.\n로그인 후 다시 챗봇을 실행하세요.\n\n챗봇 상담을 종료하겠습니까?";
	openConfirmPopup("로그인", str, function(){
		var origin = location.origin;
		if (location.origin.indexOf("g1.sports.or.kr") < 0) {
			origin = 'https://g1.sports.or.kr';
		}
		goLink(origin+'/member/login.do:mURL:'+origin+'/m/member/login.do');
		window.close();
	})
}

//형태소 분석
function getMorphWord(query){
	var morphStr ="";
	$.ajax({
		type:"post",
		url : "./ajax/getMorphWord",
		dataType : "json",
		data : {"query":query},
		async : false,
		success: function (result) {
			morphStr = result.searchKeyword;
		},
		error: function (result){
			alert(result.errorMsg);
			return false;
		}
	})
	return morphStr;
}

function getSearchQuery(query, mode) {
	let result;
    if (mode === "log") {
        // 공백을 "|"로 치환
        result = query.replace(/ /g, "|");
    } else if (mode === "search") {
        // 공백을 "*"로 치환
        result = query.replace(/ /g, "*");
    } else if(mode === "view") {
    	result = query.replace(/[*|]/g, " ");
    }
    return result;
}

/** 챗봇 말풍선 관련 기능 분리 **/

function setTimeBox(){ //bot_wrap마다 들어가 있는 time span에 시간 넣기
	$(".bot_box .time:last").text(getHour());
}

function makeAnswerContent(){ //답변 내용 들어가는 말풍선 bot_txt 만들기
	var answer_box = $('<div class="bot_txt">');
	var answer_txt = $('<div class="chat_talk font" style="font-size:'+fontSize+'">');
	return answer_box.append(answer_txt);
}

function setBotChat(){ //챗봇 측의 말풍선의 큰 틀 bot_wrap, bot_box 만들기
	var bot_wrap = $('<div class="bot_wrap">');
	var bot_logo = $('<span class="ico_comm ico_logo"></span>');
	var bot_box = $('<div class="bot_box">');
	var bot_time = $('<span class="time"></span>');
	bot_box.append(bot_time);
	
	return bot_wrap.append(bot_logo).append(bot_box);
}


function chkHasSlider(sldId){
	var options = {};
	var sld_img_cnt = $("#"+sldId).find('.swiper-slide').length;
		
    options = {
            on : {
		    init : function () {
				if(sld_img_cnt == 1 ){
	                $("#"+sldId).find(".swiper-button-next").addClass('swiper-button-hidden');
	                $("#"+sldId).find(".swiper-button-prev").addClass('swiper-button-hidden');
		            $("#"+sldId).find(".swiper-wrapper").addClass('swiper-no-swiping');
				}
		    	openSlider(sldId);
		    }
		},
		autoHeight:true,
		//loop:true,
    	navigation: {
    		nextEl: ".swiper-button-next",
    		prevEl: ".swiper-button-prev"
    	},
    	pagination: {
	        el: ".swiper-pagination",
		 	clickable: true

    	}
    }
    
	var swiper = new Swiper("#"+sldId, options);	
}


function makeImgBox(answer_temp){ //이미지 생성
	var rep_img = answer_temp.match(/<IMG>.*?<\/IMG>/gi);
	var real_img = "";
	var imgAttr = "";
	for(j=0; j<rep_img.length;j++){
		var temp_img = "" ;
		var imgVO = rep_img[j].replace("<IMG>","").replace("</IMG>","");
		var imgVOArr = imgVO.split('^');
		real_img = imgVOArr[0];
		imgAttr = JSON.parse(imgVOArr[1]);
		temp_img += '<div class="img_box" onclick="doImgPop(\'http://'+host+'/' + path + '/resources/upload/'+real_img+'\')" style="cursor:pointer;">';
		temp_img += '<a href="javascript:;" class="bot_img">';
		temp_img +=	'<img alt="'+imgAttr.description+'" class="img" src="http://'+host+'/' + path + '/resources/upload/' + real_img + '">';
		temp_img += '</a>';
		temp_img += '</div>';
		answer_temp = answer_temp.replace(rep_img[j],temp_img);
	}
	return answer_temp;
}
function makeFbtn(answer_temp){//fbtn버튼 내용
	answer_temp = answer_temp.replace(/<\/fbtn><br>/g,'<\/fbtn>');
	var startIdx = answer_temp.toUpperCase().indexOf("<FBTN>");
	var endIdx = answer_temp.toUpperCase().indexOf("</FBTN>");
	var temp_fbtn = answer_temp.substring(startIdx+6,endIdx);		
	var fbtn_answer = "";
	var last_answer = "";
	var rep_fbtn = temp_fbtn.split('|');

	fbtn_answer += '<dl class="accordion">';
	for(k=0; k<rep_fbtn.length;k++){
		var temp =rep_fbtn[k].split('^'); 
        fbtn_answer += '<dt class="acc_dt">';
		fbtn_answer += "<a href=\"javascript:;\" onclick=\"showButton(this); return false;\" class=\"arr\">"+temp[0].replace('<br>','')+"</a>";
        fbtn_answer += '</dt>';
		fbtn_answer += "<dd class=\"acc_dd\" style=\"display:none;\">"+temp[1]+"</dd>";
	}
    fbtn_answer += '</dl>';
	last_answer += answer_temp.substring(0,startIdx);	
    last_answer += fbtn_answer;
    last_answer += answer_temp.substring(endIdx+7);

	return last_answer;
}

function closeSlider(id){ //다른 word box 클릭시 열려지있는 word_box 닫기
	var chatTalkDiv = $("."+id).closest('.chat_talk');
	var wordLinks = chatTalkDiv.find('.word_link');
	
	wordLinks.each(function(){
		var otherSldStr = $(this).attr('class').split(' ');
		var otherSldId = otherSldStr.find(function(className){
			return className.startsWith("sld_");	
		});

		if(otherSldId != id && $(this).hasClass('opened')){
			$(this).removeClass('opened');
			$(this).find('div').removeClass('bottom');
			$(this).find('div').addClass('top');
			var _other_slider_box = $('#'+otherSldId).parent().parent();
			_other_slider_box.hide();
		}
	});
}

function createSliderBtn(answer_temp, mainUserQuery, preQuery, isOnlySlider){
	answer_temp = answer_temp.replace("<IMG>","<IMGBOX>").replace("</IMG>","</IMGBOX>");
	var sldList = answer_temp.match(/<SLD>.*?<\/SLD>/gi);
	var s_idx = sldList.length;
	for(s=0;s<sldList.length;s++){
		var sldId = "sld_" + ($('.chat_cont').find('.imgSlider').length+sldCnt-(s_idx--));
		answer_temp = answer_temp.replace(sldList[s], sldList[s].replace(/<br>/gi, ""));
		sldList[s] = sldList[s].replace(/<br>/gi, "");
		
		var sld_title = []; //타이틀 따로 빼기 위한 변수
		sld_title = sldList[s].match(/<TIT>.*?<\/TIT>/gi); //타이틀 찾기
		var hasTitle = false;
		if(sld_title != null){
			answer_temp = answer_temp.replace(sld_title[0], ""); //뺀 타이틀 지우기
			hasTitle = true;
		}
		
		var sld_content = $("<div>");
		if(!isOnlySlider){
			var sld_str ="";
			sld_content.append("<div class = 'word_box'>");
			if(hasTitle && sld_title.length > 0){ 
				sld_str = $('<a href="javascript:;" class="word_link '+sldId+'" onclick=\"openSlider(\''+sldId+'\'); return false;\">'+sld_title+'<div class="triangle top">');
			}else{
				sld_str = $('<a href="javascript:;" class="word_link '+sldId+'" onclick=\"openSlider(\''+sldId+'\'); return false;\">자세히 보기<div class="triangle top">');
			}
			sld_content.find('.word_box').append(sld_str).parent();
			
			if(mainUserQuery){
				sld_content.append(preQuery);
			}
			
			answer_temp = answer_temp.replace(answer_temp.match(/<SLD>.*?<\/SLD>/), sld_content.html());
		}

	}
		answer_temp = answer_temp.replace(/<imgbox>/gi,"<IMG>").replace(/<\/imgbox>/gi,"</IMG>");
		answer_temp = answer_temp.replace(/<br><div class="word_box"/g, '<div class="word_box"');
		return answer_temp;
}

function createImgSlider(answer_temp){
	var sld_txt = answer_temp.match(/<SLD>.*?<\/SLD>/gi);
	var result_content = $("<div>");
	var isOnlySlider = false;
	for(var s=0;s<sld_txt.length;s++){
		var sldId = "sld_" + ($('.chat_cont').find('.imgSlider').length+sldCnt+s);
		var sld_img = sld_txt[s].match(/<IMG>.*?<\/IMG>/gi);
		var sld_real_img = "";
		var sldImgAttr = "";
		isOnlySlider = (sld_txt[s].length == answer_temp.trim().length) ? true : false;
	
		var temp_content = $("<div>");
	
		var sld_temp ="";
		if(!isOnlySlider){
			sld_temp = $('<div class="swiper imgSlider" id="'+sldId+'">');
			temp_content.append(sld_temp);
		} else {
			sld_temp = $('<div class="swiper imgSlider on" id="'+sldId+'">');
			temp_content.append(sld_temp);
		}
	
		temp_content.append(temp_content.find('.imgSlider').append('<div class="swiper-wrapper">').parent());
				
		for(var i=0; i<sld_img.length;i++){
			var temp_sld = "" ;
			var sldVO = sld_img[i].replace("<IMG>","").replace("</IMG>","");
			var sldVOArr = sldVO.split('^');
			
			sld_real_img = sldVOArr[0];
			sldImgAttr = JSON.parse(sldVOArr[1]);
			
			temp_sld = $("<div class='swiper-slide'>");
			temp_sld.append('<img alt="'+sldImgAttr.description+'" src="http://'+host+'/' + path + '/resources/upload/' + sld_real_img + '">');
			temp_sld.append('<div class="slide_btn_box">');
			
			var slide_img_btn = $('<div class="slide_btn" onclick="doImgPop(\'http://'+host+'/' + path + '/resources/upload/'+sld_real_img+'\')"><i class="fa fa-search-plus fa-lg">').append("크게보기");
			temp_sld.append(temp_sld.find('.slide_btn_box').append(slide_img_btn));
			
			if(sldImgAttr.linkUrl != ""){
				var slide_link_btn = $('<div class="slide_btn" onclick="goLink(\''+sldImgAttr.linkUrl+'\')"><i class="fa fa-external-link fa-lg">').append("바로가기");
				temp_sld.append(temp_sld.find('.slide_btn_box').append(slide_link_btn));
			}
			temp_content.append(temp_content.find('.swiper-wrapper').append(temp_sld).parent());
		}
		temp_content.append(temp_content.find('.imgSlider').append('<div class="swiper-button-next">').parent());
		temp_content.append(temp_content.find('.imgSlider').append('<div class="swiper-button-prev">').parent());
		temp_content.append(temp_content.find('.imgSlider').append('<div class="swiper-pagination">').parent());
		
		result_content.append(makeAnswerContent().find('.chat_talk').append(temp_content.html()).parent());
	}
	sldCnt += sld_txt.length;
	
	return {
		isOnlySlider : isOnlySlider,
		sliderContent : result_content.html()
	} ;
	
}