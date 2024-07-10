((config) => {

	config.RESULT_SUCCESS_CODE = "0000000";
	
	// api 콜 인입 버튼
	config.INIT_BTNS = {
			"R01": "선수 등록 정보",
			"R02": "지도자 등록 정보",
			"R03": "심판 등록 정보",
			"R04": "선수관리담당자 등록 정보"
	};
	
	config.REGISTER_GB_CODE = {
		"R01": "P",
		"R02": "O",
		"R03": "R",
		"R04": "M",
	};

	config.REGISTER_GB_NM = {
		"P":"선수",
		"O":"지도자",
		"R":"심판",
		"M":"선수관리담당자",
	};

	// 프로세스 코드값 정의
	config.TOTAL_PROCESS_API_CODE = ['R01', 'R02', 'R03', 'R04', 'R05', 'R06', 'R07', 'R08'];

	// 프로세스 시퀀스값 구분자
	config.DEFAULT_SEQ_SEPERATOR = "-";

	// 초기값
	config.INIT_PROCESS_CODE = {
		code: Object.keys(reRegistConfig.TOTAL_PROCESS_API_CODE)[0],
		num: 1
	}

	// 프로세스 시퀀스값 총 코드 갯수(api code, seq 두개) - 코드값 검증시 사용
	config.DEFAULT_SEQ_MAX_SIZE = Object.keys(reRegistConfig.INIT_PROCESS_CODE).length;

	// api call시 필수 파라미터 셋팅
	config.DEFAULT_API_PARAM = {
		apiType: reRegistConfig.TOTAL_PROCESS_API_CODE[0]
	}

	// (데이터 변경 예정) 등록 이력 리스트 상태 코드값에 따른 버튼
	config.BUTTON_CLASS_OF_STATUS = {
		"1": {desc:"등록가능", title: "재등록", data: {action: "reRegist", reRegGb: "R"}},
		"2": {desc:"진행중", title: "신청 화면 이동", data: {action: "link", reRegGb: "R"}},
		"3": {desc:"승인중", title: "신청 화면 이동", data: {action: "link", reRegGb: "A"}},
		"5": {desc:"승인완료", title: "신청 화면 이동", data: {action: "link", reRegGb: "A"}},
	}

	// 개인정보 약관 필수 동의 필드
	config.AGREEMENT_ESSENTIAL_FIELD = ["AGREE_YN"];

})(window.reRegistConfig = window.reRegistConfig || {});

/**
 * 실제 구동
 */
((regist) => {

//	목록 조회
	regist.setAnswer = function(seqParam) {
		try {
			Regist.codes.setProcessCode(seqParam.code);

			let $dfd = $.Deferred();

			Regist.continueService.process().done(function(isUnder14) {
				if(isUnder14){
					$dfd.resolve({isUnder14:isUnder14});
				}else if(seqParam.code === 'R00'){
					$dfd.resolve();
				}else{
					let registerGbCode = reRegistConfig.REGISTER_GB_CODE[Regist.codes.getCurrentProcessCode()];
					Regist.codes.setInfo({apiType: Regist.codes.getCurrentProcessCode(), gubun: registerGbCode});
					
					Regist.apiCall(Regist.codes.getInfo(["apiType", "gubun"])).done(function (data){
						$dfd.resolve(Regist.reRegistListView.process(data));
					});
				}
			});

			return $dfd.promise();
		} catch (e) {
			console.log(e);
		}
	};
	
//	시퀀스에 해당하는 동작 수행
	regist.process = function(param) {
		let data = {};
		try {
			this.apiCall(param).done(function (data){
				return data});
		} catch (e) {
			console.log(e);
		}
		//return data;
	};
	
//	url call ajax 파라미터 들어갈 부분
	regist.apiCall = function(param, url, callback) {

		let $dfd = $.Deferred();
		try {
			if(url === undefined || url.length === 0){
				_sportsApi.getData(param, function (data){
					if(callback === undefined){
						$dfd.resolve(data);
					}else{
						$dfd.resolve(callback(data));
					}
				});
			}else{
				_sportsApi.fnAjax(url, param, function (data){
					if(callback === undefined){
						$dfd.resolve(data);
					}else{
						$dfd.resolve(callback(data));
					}
				});
			}

		} catch (e) {
			console.log(e);
		}
		return $dfd.promise();
	}

//	url call ajax 파라미터 들어갈 부분
	regist.setApiCallParam = function(param) {
		let result = {};
		try {
			let defaultParam = reRegistConfig.DEFAULT_API_PARAM;
			result = $.extend(defaultParam, param);
		} catch (e) {
			console.log(e);
		}
		return result;
	}
	
	regist.openLink = function(link) {
//		외부 링크 열기
		try {
			goLink(link);
		} catch (e) {
			console.log(e);
		}
	}

	regist.changeHandler = function(event) {
		// $btn.on('click.regist', {action:'agree'}, this.changeHandler);
		closePopup();
		const data = event.data;
		Regist.codes.setInfo(data);
		switch (data.action){
			case 'agree':
				Regist.provisionAgreement.process();
				break;
			case 'law':
				Regist.prerequisiteAcademicPolicies.process();
				break;
			case 'detail':
				Regist.userInfo.process();
				break;
			case 'reRegist':
				Regist.continueService.process(true);
				break;
			case 'continue':
				Regist.continueService.process();
				break;
			case 'link':
				if(data.reRegGb !== undefined){
					Regist.codes.setInfo(data);
				}
				_sportsApi.moveSite('PINFO', Regist.codes.getInfo().reRegGb + Regist.codes.getInfo().gubun, Regist.codes.getInfo().pclassCd);
				// let link = data.link;
				// Regist.openLink(link);
				break;
			case 'cancel':
				Regist.deleteStep();
				break;
			case 'close':
				break;
		}
	}

	regist.createAgreementPopup = function(options) {
		const popup = $(".popupagreement");
		const settings = $.extend(true, {
			title: "",
			contents: [],
			onOpen: function () {
				openPopup("agreement");
			},
			buttons: [],
		}, options);

		function initPopup() {
			popup.hide();
			popup.empty();
			popup.append(`
				 <div class="layer_cont">
					<div class="survey_box">
						<strong class="survey_tit title"></strong>
						<div class="ly_privacy_cont contents"></div>
					</div>
					<div class="btns pop_btn_group"></div>
				</div>
			`);
		}

		function setPopupContents() {
			//close
			popup.find('strong.title').after(Regist.getCloseBtn('close'));	

			//title
			popup.find('strong.title').first().text(settings.title);

			//contents
			settings.contents.forEach(function (el, index) {
				const $subDiv = $("<div>", {class: "privacy_wrap sub-content"});

				//title
				if (el.title) {
					$subDiv.append($("<strong>", {class: "cont_tit_info sub-content-title", text: el.title}));
				}
				//content
				if (el.content) {
					$subDiv.append($("<div>", {class: "privacy_cont sub-content-cont", html: el.content}));
					if(settings.contents.length === 1) $subDiv.find("div.sub-content-cont").addClass("people");
				}

				//input
				if (el.subContent) {
					el.subContent.forEach(function (subEl, inputIdx) {
						const $wrapSubContent = $("<div>", {class: "ly_cont_box privacy"});
						const $wrapText = $("<strong>", {class: "tit privacy"})
							.append($("<span>", {class: "label", text: subEl.text}));
						$wrapSubContent.append($wrapText);

						const $wrapInput = $("<div>", {class: "cont_btn_group agreement_select_box"});
						if (subEl.requiredFlag) $wrapInput.addClass("required");

						if (subEl.inputs) {
							subEl.inputs.forEach(function (inputEl) {
								let $input = $("<input>", inputEl.attr);
								if (inputEl.label) $input = $("<label>").append($input).append($("<span>", {
									class: "terms_name",
									text: inputEl.label
								}));
								$wrapInput.append($input);
							});
						}

						$wrapSubContent.append($wrapInput);
						$subDiv.append($wrapSubContent);
					});
				}

				popup.find('div.contents').first().append($subDiv);
			});

			// buttons
			const $btnDiv = popup.find('div.btns').first();
			$btnDiv.append(Regist.btns.draw(settings.buttons));
			if($btnDiv.find("button").length === 2) $btnDiv.addClass("w_half");
		}

		initPopup();
		setPopupContents();
		settings.onOpen();
	}

	regist.updateStep = (param) => {
		let defaultParam = {
			apiLogSeq : Regist.codes.getLogSeq(),
			apiStep : Regist.codes.getCurrentProcessSeqForSave()
		}
		_sportsApi.fnAjax('./api/updateStep', $.extend({}, defaultParam, param));
	}

	regist.deleteStep = () => {
		Regist.updateStep({apiStep:''});
	}

	regist.getCloseBtn = (action) => {
		return Regist.btns.draw({text:'<span class="ico_comm ico_pop_close">팝업닫기</span>', class:"btn_pop_close", data:{action:action}, onClick:Regist.changeHandler })
	}
	
	
	((codes) => {
		let thisProcessCode;
		let initCode;	//	인입버튼 저장
		let info = {};	// param 저장
		let logSeq;
		
//		현재 API 코드값
		codes.getCurrentProcessCode = () => { return thisProcessCode; }
//		인입 API 코드값
		codes.getInitProcessCode = () => { return initCode; }

//		save용(이어하기)
		codes.getCurrentProcessSeqForSave = (seq) => {
			let seperator = reRegistConfig.DEFAULT_SEQ_SEPERATOR;
			return `${thisProcessCode}${seperator}${String(seq).padStart(2, "0")}`;
		};

//		파라미터 셋팅하기 위해
		codes.setInfo = (data) => {
			info = $.extend(true, info, data);
		}
		
//		파라미터 셋팅하기 위해 필요한 key값을 list형태로 받아, 그에 해당하는 키-값을 object형태로 return
//		key-value형태로 받으려면 getInfo(key); / value만 받고싶으면 getInfo().key;
		codes.getInfo = (keyList) => {
			if (typeof keyList === "undefined") {
				return info;  // keyList가 undefined일 경우, info 전체를 반환합니다.
			}

			keyList = $.makeArray(keyList);
			return keyList.reduce((newMap, key) => {
		        if (key in info) {
		            newMap[key] = info[key];
		        }
		        return newMap;
		    }, {});
		}

		//check
		codes.setLogSeq = (data) => {
			logSeq = data;
		}
		//check
		codes.getLogSeq = (data) => {
			return logSeq;
		}
		//check
		codes.setCodes = (data) => {
			info = data;
		}
		//check
		codes.getCodes = () => {
			return info;
		}
		
//		시퀀스값 셋팅
		codes.setProcessCode = (code) => {
			
			if(apiCodeVerification(code)) {
				thisProcessCode = code;
			}
		};
//		API CODE 검증
		let apiCodeVerification = (code) => { return _sportsApi.isApiType(code); }


		
		
	})(Regist.codes = Regist.codes || {});
	
	
	
	((reRegistListView) => {
//	const reRegistListView = (function() {
		reRegistListView.process = function(data) {
			return drawList(data);
		}

//		리스트 작성
		let drawList = (data) => {
			const templateHTML = `
				<div class="tbl_box_wrap">
					<div>
						<table class="tbl_basic">
							<caption class="screen_out">세부종목,소속,상태</caption>
							<thead></thead>
							<tbody></tbody>
						</table>
					</div>
				</div>
			`;
			const listData= data.DATA.REG;
			let $template = $(templateHTML).clone();
			const theadArr = [
				{title:'세부종목', value:'PCLASS_NM', width:'20%'},
				{title:'소속', value:'TEAM_NM', width:'23%'},
				{title:'상태', value:'RE_REG_GB_NM', width:'57%'}
			];
			if(listData){
				//set header
				let $thead = $template.find('thead');
				$thead.append($('<colgroup>')).append($('<tr>'));

				theadArr.forEach(function (el){
					$thead.find('colgroup').append($('<col>', {style:`width:${el.width}`}));
					$thead.find('tr').append($('<th>', {text:el.title}));
				});

				//set list
				$.makeArray(listData).forEach(function (el){
					let $newTr = $('<tr>');
					let aIdx = 0;
					let btnOpt = "";
					if(reRegistConfig.BUTTON_CLASS_OF_STATUS[el['RE_REG_GB']] !== undefined){
					btnOpt = regist.btns.draw({
							text: reRegistConfig.BUTTON_CLASS_OF_STATUS[el['RE_REG_GB']].title,
							class: "btn_info",
						data: $.extend({}, reRegistConfig.BUTTON_CLASS_OF_STATUS[el['RE_REG_GB']].data, {code:regist.codes.getInfo().code, val:el, pclassNm:el['PCLASS_NM'], pclassCd: el['PCLASS_CD'], personNo: el['PERSON_NO'], regYear: el['REG_YEAR'], gubun:reRegistConfig.REGISTER_GB_CODE[Regist.codes.getCurrentProcessCode()]}),
							onClick: Regist.changeHandler
						});
					}
					$newTr.append($('<td>').append($('<span>', {text:el[theadArr[aIdx++].value]})));
					$newTr.append($('<td>', {text:el[theadArr[aIdx++].value]}));
					$newTr.append($('<td>', {class:'ta_left'})
						.append($('<span>', {text:el[theadArr[aIdx++].value]}))
						.append(btnOpt)
					);
					$template.find('tbody').append($newTr);
				});
			}else{
				$template.find('tbody').html('<tr><td>등록 이력이 없습니다.</td></tr>');
			}
			
			return $template;
		}

//		return {
//			process: process,
//		}
//	})();
	})(Regist.reRegistListView = Regist.reRegistListView || {});
	
	((continueService) => {
//	const continueService = (function() {
		let info = {
			popup : $(".popupcontinueConfirm"),
			continueData : {}
		};

		continueService.process = function(isRegist) {
			let $dfd = $.Deferred();
			setTimeout(function(){
				selectRegistData().done(function() {
					if (isContinue()) {
						continuePopup();
					} else {
						console.log(isRegist);
						if (isRegist) {
							Regist.changeHandler({data:{action:'agree'}});
						}
					}
					$dfd.resolve();
				});
			}, 100);

			return $dfd.promise();

		}
		
//		WISE_LOG_SPORTS_API테이블에 이어하기 데이터 select(공통화)
		let selectRegistData = () => {
			let $deferred = $.Deferred();
			setTimeout(function(){
				 _sportsApi.fnAjax('./api/getStep', {apiType : 'R07'}, function(data){
					 info.continueData = data;
					 $deferred.resolve(data);
				 });
			}, 100);

			return $deferred.promise();
		}

		let isContinue = () => {
			return !$.isEmptyObject(info.continueData);
		}

		let loadUserInfo = (event) => {
			let data = event.data;
			Regist.deleteStep();
			Regist.changeHandler({data: data});
		}

		let continuePopup = () => {
			let continuesData = info.continueData;

			if(Object.keys(continuesData).length !== 0) {
				Regist.codes.setLogSeq(continuesData.logSeq);

				const continueConfirmHtml = `
				    <div class="layer_cont">
				        <div class="survey_box">
				            <strong class="survey_tit">재등록 이어하기 알림</strong>
				            <div class="popup_txt_box">
				            	경기인 재등록 진행한 이력이 있습니다.<br>
				            	이전 진행한 단계부터 진행하시겠어요?<br>
				            	<div class="step_txt">
				            		* "아니오"를 선택하시면 첫 단계부터 시작됩니다.
				            		<span class="txt_blue">&lt; 아니오를 선택하시면 기존 저장된 항목이 삭제됩니다. &gt;</span>
				            	</div>
				            </div>
				        </div>
				        <div class="continue-btn pop_btn_group w_half">
				        </div>
				    </div>
				`;

				const popup = info.popup;
				popup.hide();
				popup.empty();

				let $confirm = $(continueConfirmHtml).clone();
				//close
				$confirm.find('strong.survey_tit').after(Regist.getCloseBtn('cancel'));

				const apiParam = Object.fromEntries( new URLSearchParams(continuesData.requestParam));
				Regist.codes.setInfo($.extend(true, {}, apiParam, {apiStep:continuesData.apiStep}));
				
				let btnOptions = [{
		            text: "아니오",
		            class: "pop_btn gray",
					data: {action: "cancel"},
		            onClick: Regist.changeHandler
		        }, {
		            text: "불러오기",
		            class: "pop_btn blue",
		            //data: $.extend(true, {}, {action: "detail", continueSeq: continueSeq, registerGb: registerGbCode}),
					data: {action: "detail"},
		            onClick: continueService.loadUserInfo
		        }];
				$confirm.find(".continue-btn").append(Regist.btns.draw(btnOptions));

				popup.append($confirm);
				openPopup("continueConfirm");
			}
		}

//		return {
//			selectRegistData: selectRegistData,
//			continuePopup: continuePopup,
//			loadUserInfo: loadUserInfo,
//			process: process,
//		}
//	})();
	})(Regist.continueService = Regist.continueService || {});

	// 개인정보 동의 조회 / 저장
	((provisionAgreement) => {
//	const provisionAgreement = (function() {
//		api call
		provisionAgreement.process = () => {
			Regist.codes.setInfo({apiType: 'R05'});

			Regist.apiCall(Regist.codes.getInfo(["pclassCd", "personNo", "apiType"]))
				.done(function(result){
					const rData = result.DATA;
					if(isAgree(rData)){
						//let evt = this.event;
						//evt.data = $.extend({}, data, {action:'law'});
						Regist.changeHandler({data:{action:'law'}});
					}else{
						drawAgreement(rData);
					}
				});
		}
		
//		개인정보 필수사항 동의여부 파싱
		let isAgree = (data) => {
//			필수여부 데이터 파싱
			let result = true;
			let essentialFields = reRegistConfig.AGREEMENT_ESSENTIAL_FIELD;
			
			essentialFields.forEach(function(fieldName, idx) {
				if(data.hasOwnProperty(fieldName)) {
					let yn = data[fieldName].toLocaleUpperCase();
					switch (yn) {
						case "Y":
							result = true;
							break;
						case "N":
							result = false;
							break;
						default:
							result = false;
							break;
					}
				}
			});
			return result;
		}

//		약관 출력 및 기존 동의내역 출력
		let drawAgreement = (data) => {
			let title = "이용약관, 개인정보 제3자 제공동의";
			let contents = [];

			// 개인정보 수집 및 이용
			contents.push({
				title:"개인정보 수집 및 이용",
				content: data.CONTENTS1,
				subContent:[
					{
						text:"위와 같이 개인정보 필수사항에 대해 수집‧이용하는데 동의하십니까?",
						requiredFlag:true,
						inputs:[
							{attr:{type:"radio", name:"agree1", value:"Y", class: "inp_radio", title:"개인정보 필수사항 수집 이용 동의"}, label:"예"},
							{attr:{type:"radio", name:"agree1", value:"N", class: "inp_radio", title:"개인정보 필수사항 수집 이용 동의안함"}, label:"아니오"}
						]
					},
					{
						text:"위와 같이 개인정보 선택사항에 대해 수집‧이용하는데 동의하십니까?",
						requiredFlag:false,
						inputs:[
							{attr:{type:"radio", name:"agree2", value:"Y", class: "inp_radio", title:"개인정보 선택사항 수집 이용 동의"}, label:"예"},
							{attr:{type:"radio", name:"agree2", value:"N", class: "inp_radio", title:"개인정보 선택사항 수집 이용 동의안함"}, label:"아니오"}
						]
					},
				]
			});

			// 개인정보 제 3자 제공
			contents.push({
				title:"개인정보 제 3자 제공",
				content: data.CONTENTS2,
				subContent:[
					{
						text:"위와 같이 개인정보 필수사항에 대해 수집‧이용하는데 동의하십니까?",
						requiredFlag:true,
						inputs:[
							{attr:{type:"radio", name:"agree3", value:"Y", class: "inp_radio", title:"개인정보 필수사항 수집 이용 동의"}, label:"예"},
							{attr:{type:"radio", name:"agree3", value:"N", class: "inp_radio", title:"개인정보 필수사항 수집 이용 동의안함"}, label:"아니오"}
						]
					}
				]
			});

			let buttonOptions = [
				{
					text: "취소",
					class: "pop_btn gray",
					data: {action: 'close'},
					onClick: Regist.changeHandler
				},
				{
					text: "확인",
					class: "pop_btn blue",
					data: data,
					onClick: provisionAgreement.saveAgreement
				}
			];

			Regist.createAgreementPopup({
				title: title,
				contents: contents,
				buttons: buttonOptions
			});
		}

		let isCheckboxAgreed = () => {
			const $agreeBox = $(".popupagreement .agreement_select_box");

			// 필수 동의 사항 확인
			const $required = $agreeBox.filter(".required");
			const $inputRequired = $required.find("input[type='radio'][value='Y']");
			if($required.length > 0 && $required.length !== $inputRequired.filter(":checked").length){
				const agreeName = $inputRequired.not(":checked").first().attr("name");
				switch(agreeName){
					case "agree1":
						alert("개인정보 수집 및 이용 필수사항에 대한 동의가 필요합니다.");
						break;
					case "agree3":
						alert("개인정보 제3자 제공 및 활용에 대해 동의가 필요합니다.");
						break;
				}
				return false;
			}

			// 선택 동의 사항 확인
			if($agreeBox.length > 0 && $agreeBox.length !== $agreeBox.find("input[type='radio']:checked").length){
				alert("개인정보 수집 및 이용 선택사항에 체크가 필요합니다.");
				return false;
			}

			return true;
		}
		
		let saveAgreement = (e) => {
			let data = e.data;
			if(isCheckboxAgreed()) {
				let param = {
					apiType: 'R06',
					termsVersion: data.TERMS_VERSION,
					pclassCd : data.pclassCd,
					agreeYn : $("input[type='radio'][name='agree2']:checked").val()
				};

				Regist.codes.setInfo(param);
				let result = Regist.apiCall(Regist.codes.getInfo(["apiType", "termsVersion", "pclassCd", "agreeYn"]));

				if(Object.keys(result).length !== 0){
					Regist.changeHandler({data:$.extend({}, data, {action:'law'})});
				}
			}
		}
		
//		return {
//			process: process,
//			isAgree: isAgree,
//			isCheckboxAgreed: isCheckboxAgreed,
//			saveAgreement: saveAgreement,
//		}
	})(Regist.provisionAgreement = Regist.provisionAgreement || {});
	
	((prerequisiteAcademicPolicies) => {
//	const prerequisiteAcademicPolicies = (function() {
		prerequisiteAcademicPolicies.process = () => {
			drawLaw();
		}

		// 경기인 등록 규정(하드코딩) 내용 출력(같은 템플릿 사용)
		let drawLaw = () => {
			let title = "";
			let contentTitle = "";
			let contentText = "";
			let action = {};
			let step = Regist.codes.getInfo().step || 1;
			let nextStep = 1;

			if(step == 1){
				title = "선수용(전문/생활 공통)";
				contentText = `
						<strong class="cont_tit_info">경기인 등록 규정 제14조(등록 결격사유 등)</strong>
						<table class="privacy_cont people">
							<tbody>
								<tr>
									<td style="border:1px solid #A1A1A1; padding:10px;">
										<p>① 다음 각 호의 하나에 해당하는 사람은 선수로 등록할 수 없다.</p>
										<p>1. 선수·심판·지도자·단체임원·선수관리담당자로서 스포츠공정위원회규정 제27조에 따라 제명의 징계를 받은 사람</p>
										<p>2. 체육회 관계단체로부터 제명의 징계를 받은 사람</p>
										<p>3. 자격정지 징계를 받고 그 처분이 종료되지 않은 사람</p>
										<p>4. 강간, 유사강간 및 이에 준하는 성폭력의 죄를 범하여 학교폭력예방 및 대책에 관한법률 제17조 제1항 제9호의 퇴학처분 조치를 받고 10년이 지나지 아니한 사람</p>
										<p>5. 제14조 제1항 제4호 이외의 사유로 학교폭력예방 및 대책에 관한법률 제17조 제1항 제9호의 퇴학처분 조치를 받고 5년이 지나지 아니한 사람</p>
										<p>6. 체육회 관계단체가 주최·주관하는 경기의 결과에 영향을 미치는 승부조작에 가담하여 「국민체육진흥법」 제47조제1호, 제48조제1호 또는 같은 조 제2호에 따른 죄를 범하여 유죄의 판결이 확정된 사람</p>
										<p>7. 체육회 관계단체로부터 해임 징계를 받고 10년이 지나지 아니한 사람</p>
									</td>
								</tr>
							</tbody>
						</table>
						<p>※ 관계단체: 국민체육진흥법 제2조제9호 가목부터 다목까지의 체육단체</p>
						<table style="margin-top:10px;">
							<tbody>
								<tr>
									<td style="border:1px solid #A1A1A1; padding:10px;">
										<p>제2조(정의) 이 법에서 사용하는 용어의 뜻은 다음과 같다.</p>
										<p>9. “체육단체”란 체육에 관한 활동이나 사업을 목적으로 설립된 다음 각 목의 어느 하나에 해당하는 법인이나 단체를 말한다.</p>
										<p>&nbsp;가. 제5장에 따른 대한체육회, 시ㆍ도체육회 및 시ㆍ군ㆍ구체육회(이하 “지방체육회”라 한다), 대한장애인체육회, 시ㆍ도장애인체육회 및 시ㆍ군ㆍ구장애인체육회(이하 “지방장애인체육회”라 한다), 한국도핑방지위원회, 서울올림픽기념국민체육진흥공단</p>
										<p>&nbsp;나. 제11호에 따른 경기단체</p>
										<p>&nbsp;다. 「태권도 진흥 및 태권도공원 조성 등에 관한 법률」 제19조에 따른 국기원 및 같은 법 제20조에 따른 태권도진흥재단</p>
										<p>11. “경기단체”란 특정 경기 종목에 관한 활동과 사업을 목적으로 설립되고 대한체육회나 대한장애인체육회에 가맹된 법인이나 단체 또는 문화체육관광부장관이 지정하는 프로스포츠 단체를 말한다.</p>
									</td>
								</tr>
							</tbody>
						</table>
						<p>※ 종목별 세부사항에 대해서 등록하려는 회원종목단체 경기인 등록 규정 필독</p>`;
				action = {action: 'law'};
				nextStep = 2;
			}else if(step == 2){
				title = "경기인의 교육 의무";
				contentText = `<strong class="cont_tit_info" style="line-height: 1.5">대상: 경기인(회원종목단체에 등록한 선수(전문/생활), 지도자, 심판, 선수관리담당자)</strong>
						<table style="margin-top:10px;">
							<tbody>
								<tr>
									<td style="border:1px solid #A1A1A1; padding:10px;">
										<p>「경기인 등록 규정」 제6조의2(경기인의 교육)</p>
										<p> ① 경기인은 국민체육진흥법 시행규칙 제30조의4에 따라 성폭력 등 폭력 예방교육을 이수해야한다.</p>
										<p> ② 경기인은 한국도핑방지위원회가 제공하는 도핑방지 교육을 이수해야 한다.</p>
									</td>
								</tr>
							</tbody>
						</table>
						<br>
						<p>◆ 제①항의 교육은 등록 과정 중의 동영상(인권)교육과 별개로 매년 edu.k-sec.or.kr ‘스포츠윤리 런(Learn)’에서 “성폭력 등 폭력 예방교육”을 반드시 이수해야 함.(각 역할별 신규 등록 시에는 등록 후 2개월 이내 교육 이수 필요)</p>
						<p><a href="https://edu.k-sec.or.kr" target="_blank" style="color:#035AC6 !important;" title="새창으로열기">☞ 스포츠윤리 런 바로가기</a></p>
						<br>
						<p>◆ 제②항의 교육은 등록 과정 중 “도핑방지교육” 동영상을 시청한 경우 이수 인정.
						(다만, 별도로 이수증 발급이 필요한 경우 edu.kada-ad.or.kr 온라인 도핑방지 교육센터에서 별도 이수하여 이수증을 발급 받을 수 있음.)</p>
						<p><a href="https://edu.kada-ad.or.kr" target="_blank" style="color:#035AC6 !important;" title="새창으로열기">☞ 이수증 발급 교육 바로가기</a></p>
						`;
				action = {action: 'detail'}; //comment: userInfo에서 필요한 data 추가,  data에서 꺼내면 됨 예) data.pclassCd
				nextStep = 0;
			}

			let buttonOptions = [
				{
					text: "확인",
					class: "pop_btn blue",
					data: action,
					onClick: Regist.changeHandler
				}
			];
			if(nextStep) {
				Regist.codes.setInfo({step: nextStep});
			}
			Regist.createAgreementPopup({
				title: title,
				contents: [
					{
						title:contentTitle,
						content:contentText
					}
				],
				buttons: buttonOptions
			});
		}
//		return {
//			process: process,
//		}
//	})();
	})(Regist.prerequisiteAcademicPolicies = Regist.prerequisiteAcademicPolicies || {});
	
	// 상세정보 조회
	((userInfo) => {
//	const userInfo = (function() {
		let info = {
			popup : $(".popupuserInfo"),
			parsedData : [],
			seq : 0,
			maxCount : 1,
			gData : {}
		};

		let setDataInfo = (data) => {
			let pData = parseData(data);
			info.maxCount = pData.length;
			info.parsedData = pData;
			info.seq = parseInt(data.apiStep);
			info.gData = data;
			// 마지막 페이지 추가
			if(info.maxCount > 0){
				pData.push({
					groupNm : "재등록을 위한 정보 확인을 완료했습니다.",
					items : [`경기인 재등록 완료를 위해서는 스포츠지원포털로 이동하여 사진등록 후 신청서를 제출해야 합니다.<br>아래 완료 버튼을 클릭하시면 임시저장 후 스포츠지원포털로 이동합니다.`]
				})
			}else{
				alert("조회된 데이터가 없습니다.");
				return false;
			}
		}

		let getSeqData = (seq) => {
			let s = seq || info.seq;
			return info.parsedData[s];
		}
		
		Regist.userInfo.process = (data) => {
			if(data){
				drawPopup(getSeqData());
			}else{
				let apiStep = "0";
				if(Regist.codes.getInfo().apiStep && Regist.codes.getInfo().apiStep.length != ""){
					apiStep = Regist.codes.getInfo().apiStep;
				}
				Regist.codes.setInfo({apiType: 'R07', apiStep: apiStep});
//				data.apiStep = apiStep;
				Regist.apiCall(Regist.codes.getInfo(["apiType", "gubun", "pclassCd", "personNo"]))
				.done(function(result){
					if(!$.isEmptyObject(result)){
						setDataInfo(result.DATA);
						Regist.codes.setLogSeq(result.apiLogId);
						if(result.DATA !== ""){
							drawPopup(getSeqData());
						}else{
							Regist.deleteStep();
						}
					}
				});
			}
		}
		
		let parseData = (data) => {
			// 처리 결과를 저장할 객체
			let result = [];
			let pGroups = {};
			let eGroups = {};

			// PINFO 처리
			if (data.PINFO) {
				let pArr = $.makeArray(data.PINFO);
				pGroups = pArr.reduce((acc, item) => {
					if (!acc[item.INFO_GB]) acc[item.INFO_GB] = [];
					acc[item.INFO_GB].push({
						label: item.INFO_NM,
						value: item.INFO_CONT,
						groupNm: item.INFO_GB,
					});
					return acc;
				}, {});
			}

			// EXTRA_INFO 처리
			if (data.EXTRA_INFO) {
				let extraArr = $.makeArray(data.EXTRA_INFO);
				eGroups = extraArr.reduce((acc, item) => {
					const itemInfo = item.INFO;
					const itemGb = itemInfo[0].INFO_GB;
					if (!acc[itemGb]) acc[itemGb] = [];
					let eInfoArr = [];
					itemInfo.forEach(eInfo => {
						eInfoArr.push({
							label: eInfo.INFO_NM,
							value: eInfo.INFO_CONT,
							groupNm: eInfo.INFO_GB
						});
					})
					acc[itemGb].push(eInfoArr);
					return acc;
				}, {});
			}

			const groups = $.extend({}, pGroups, eGroups);
			for(key in groups){
				result.push({
					groupNm:key,
					items:groups[key]
				});
			}

			return result;
		}
		
		let drawPopup = (data) => {
			info.popup.empty();
			const userInfoMainHtml = `
				<div class="user_ly_top">
					<strong class="user_pop_tit">경기인 재등록</strong>
					<p class="tit_sub_txt"></p>
				</div>
				<div class="user_ly_cont">
					<div class="ly_cont_top">
						<strong class="user_cont_tit"><span></span></strong>
					</div>
					<div class="ly_over_scroll">
						<div class="ly_pop_cont">
							<div class="list_wrap"></div>
						</div>
					</div>
					<div class="user_info_bottom">
						<p class="info_pop_txt user_info_desc">* 변경사항이 있는 경우 스포츠 지원포털 신청페이지로 이동하여 내용을 수정해주세요.</p>
						<a class="btn_inner link" title="사이트 바로가기"><span>경기인 등록 페이지로 이동</span></a>
					</div>
				</div>
				<div class="layer_bottom w_half new user_info_btn"></div>`;

			let $template;
			if(typeof data.items != "undefined") {
				if(data.items.length > 0) {
					$template = $(userInfoMainHtml).clone();

					//bottom button
					$template.find(".user_info_bottom > a.btn_inner.link").first().on('click.regist', { action: "done"}, userInfo.userInfoChangeHandler);

					//close
					$template.find('strong.user_pop_tit').before(Regist.getCloseBtn('close'));

					//Sub Titile
					$template.find('p.tit_sub_txt').text(`(${info.gData.pclassNm} / ${reRegistConfig.REGISTER_GB_NM[info.gData.gubun]}등록 신청)`)

					drawList(data, $template);
					drawButton($template);
					info.popup.html($template);
					openPopup("userInfo");
				} else {
					throw new Error('Unable to retrieve the requested information.');
				}
			} else {
				//todo
			}
		}
		let drawButton = ($template) => {
			let btn1 = {
				text: (info.seq === 0) ? "취소" : "이전",
				class:"btn gray",
				data: { action: (info.seq === 0) ? "cancel" : "prev"},
				onClick: userInfo.userInfoChangeHandler
			};

			let btn2 = {
				text: (info.seq === info.maxCount) ? "완료" : "다음",
				class: "btn blue",
				data: { action: (info.seq === info.maxCount) ? "done" : "next" },
				onClick: userInfo.userInfoChangeHandler
			}
			$template.filter(".user_info_btn").first().append(Regist.btns.draw([btn1, btn2]));
		}
		
		let drawList = (data, $template) => {

			$template.find(".user_cont_tit > span").text(data.groupNm);

			data.items.forEach((detail, idx) => {
				let $newItem;
				if(typeof detail === "string"){
					$template.find('.user_info_bottom').hide();
					$template.find('.ly_pop_cont').addClass('txt');
					$newItem = detail;
				}else if(detail instanceof Array){
					$newItem = drawBox(detail, idx);
				}else{
					$newItem = drawItem(detail);
				}

				$template.find('.list_wrap').append($newItem);
			});
		}

		let drawItem = (item) => {
			const $item = $(`
					 <div class="info_list">
						<strong class="info_tit item_title"></strong>
						<div class="info_cont">
							<span class="item_value"></span>
						</div>
					</div>
				`);

			//title
			$item.find('.item_title').text(item.label);
			//content
			$item.find('.item_value').text(item.value);

			return $item;
		}

		let drawBox = (items, idx) => {
			const $box = $(`
				<div class="education_box">
					<div class="list_num"><span>${++idx}</span></div>
					<ul class="education_info"></ul>
				</div>			
			`);

			const $itemTemplate = $(`
				<li>
					<strong class="info_tit item_title"></strong>
					<span class="education_txt item_value"></span>
				</li>
			`);

			items.forEach(item => {
				let $item = $itemTemplate.clone();

				//title
				$item.find('.item_title').text(item.label);
				//content
				$item.find('.item_value').text(item.value);

				$box.find("ul.education_info").append($item);
			});

			return $box;
		}

		let userInfoChangeHandler = (event) => {
			let data = event.data;
			let action = data.action;

			//Step Setting
			if(action === 'prev'){
				info.seq--;
			}else if(action === 'next'){
				info.seq++;
			}

			//update step
			let param = {
				apiLogSeq : Regist.codes.getLogSeq(),
				apiStep : info.seq
			}
			Regist.updateStep(param);

			switch (action) {
				case 'cancel':
					Regist.changeHandler({data: {action: "cancel"}})
					break;
				case 'prev':
				case 'next':
					Regist.userInfo.process();
					break;
				case 'done':
					Regist.changeHandler({data: {action: "cancel"}})
					Regist.changeHandler({data: {action: "link", reRegGb: "R"}});
					break;
			}
	 	}
//		return {
//			process: process,
//			userInfoChangeHandler: userInfoChangeHandler,
//		}
//	})();
	})(Regist.userInfo = Regist.userInfo || {});

	/**
	 * sequence별 버튼 생성, event 수행
	 */
	((btns) => {
//	const btns = (function() {
		btns.draw = (optionsList) => {
			const $buttons = [];
			const defaults = {
		        text: "",        // 버튼 기본 텍스트
		        class: "btn blue",  // 기본 클래스
		        data: {},              // 데이터 속성, 객체 형태(상세정보보기용)
		        onClick: function() {} // 기본 onclick 이벤트 핸들러
		    };
			let arr = $.makeArray(optionsList);
			// 각 옵션 객체에 대해 반복
			arr.forEach(function(options) {
		        // 사용자 옵션과 기본 옵션 병합
		        const settings = $.extend(true, {}, defaults, options);

		        const $button = $("<button>", {
		        	type: "button",
		            html: settings.text,
		            class: settings.class,
		            id: settings.id || ""   // ID는 옵셔널, 제공되지 않을 경우 빈 문자열
		        });

		        $button.on('click.regist', settings.data, settings.onClick);
		        $buttons.push($button);
		    });

		    return $buttons;
		};
		
//		return {
//			draw: draw,
//		}
	})(Regist.btns = Regist.btns || {});
	
})(window.Regist = window.Regist || {});








