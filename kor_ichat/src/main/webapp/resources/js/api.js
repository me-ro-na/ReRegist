/**
 * 대한체육회 연계 API
 */
;(function(factory) {
	'use strict';
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else if (typeof exports !== 'undefined') {
        module.exports = factory(require('jquery'));
    } else {
        factory(jQuery);
    }
}(function factory($){
	'use strict';
	
	/** COMMON */
	var root = (typeof self == 'object' && self &	``& self.Object === Object && self) || Function('return this')();
	var apis = {};
	var getUserId = ()=>{return $("#userId").val()};
	
	/** Error messages. */
	var AJAX_ERROR_TEXT = 'Ajax failed.',
		API_TYPE_ERROR_TEXT = 'Unknown Api.',
		UNVALID_USER = 'Unvalid User.';
	
	/** API Config. */
	var BASE_URL = './api',
		BASE_AJAX = {
			dataType: 'json',
			contentType: 'application/json;charset=UTF-8',
			method: 'POST',
			beforeSend: () => {
				$('.popup.loading, .load_dimed').fadeIn(430);
			},
			complete: () => {
				$('.popup.loading, .load_dimed').stop().fadeOut(100);
			},
			error: function (xhr, error) {
				if (xhr.status === 403) {
		            goLoginPage("Y");
		        }else if (xhr.status === 500) {
					openAlertPopup('오류', "일시적인 오류가 발생하였습니다.<br>챗봇을 종료 후 다시 실행해주세요.", false);
				}
			}
		},
		API_TYPES = {
			sample	 : ['SAMPLE'],
			retrieve : ['G01', 'G02', 'G03', 'G04', 'G05', 'G06', 'G07', 'G08', 'G09', 'G10', 'G11'],
			reRegist : ['R01', 'R02', 'R03', 'R04', 'R07', 'R08'],
			agree 	 : ['R05', 'R06']
		};
		      
	function isObjectLike(value) {
		return value != null && typeof value == 'object';
    }
    
	function setApiUrl(data){
		return `${BASE_URL}/${getDtoByApiType(data.apiType)}`;
	}
	
	function setParam(param){
		var id = getUserId();
		if(id == ""){
			goLoginPage("Y");
			throw new Error(UNVALID_USER);
		}else{
			return $.extend({}, param, {userId:id});
		}
	}
	
	function getDtoByApiType(apiType){
		return Object.keys(API_TYPES).find(key => API_TYPES[key].indexOf(apiType) > -1);
	}
	
	/**
     * Check the Api Type is valid.
     *
     * @type function
     * @name _sportsApi.isApiType
     * @member
     * @param {string} API Type.
     * @return {boolean}
     */
	function isApiType(value) {
		return value != null && typeof value == 'string' && typeof getDtoByApiType(value) != 'undefined';
    }
    
    /**
     * Check the Intent needs Api Call.
     *
     * @type function
     * @name _sportsApi.isApiIntent
     * @member
     * @param {string} Intent Name.
     * @return {boolean}
     */
    function isApiIntent(intentNm){
		return intentNm != null && typeof intentNm == 'string' && new RegExp(/^API\_[A-Z]?[0-9]{2}_/i).test(intentNm);
	}
	
	/**
     * Get API Type From Intent Name
     *
     * @type function
     * @name _sportsApi.getIntentApiType
     * @member
     * @param {string} Intent Name.
     * @return {string} Api Type
     */
	function getIntentApiType(intentNm){
		var tp = "";
		if(isApiIntent(intentNm)){
			tp = intentNm.substring(4, 7).toUpperCase();
		}
		return tp;
	}

	/**
	 * Ajax Call sports api.
	 *
	 * @type function
	 * @name _sportsApi.fnAjax
	 * @member
	 * @param {object} param parameter.
	 * @param {function} callback Callback Function.
	 * @return {object} json(or call callback).
	 */
	function fnAjax(url, param, callback) {
		var result = {};
		$.ajax($.extend(
			BASE_AJAX,
			{
				url: url,
				data: JSON.stringify(setParam(param)),
				success: function (data) {
					if (callback) {
						callback(data);
					}else{
						result =  data;
					}
				}
			}
		));

		return result;
	}
	
    /**
     * Ajax Call sports api.
     *
     * @type function
     * @name _sportsApi.getData
     * @member
     * @param {object} param parameter.
     * @param {function} callback Callback Function.
     * @return {object} json(or call callback).
     */
    function getData(param, callback) {
		
		if(!isApiType(param.apiType)){
			throw new Error(API_TYPE_ERROR_TEXT);
		}
		var params = setParam(param);
		var result = {};
		$.ajax($.extend( 
				BASE_AJAX, 
				{
					url: setApiUrl(params),
					data: JSON.stringify(params),
					success: function (data) {
						if (callback) {
							callback(data);
	                    }else{
							result =  data;
						}
					}
				}
		));
		
		return result;
    }

	function moveSite(flag, gubun, classCd) {
		$.ajax($.extend(
			BASE_AJAX,
			{
				url: "auth/makeSso.do",
				data: JSON.stringify({
					"sysGb": flag,
					"gubun": gubun,
					"classCd": classCd
				}),
				success: function (res) {
					if (res.resultCode == "3000200") {
						goLoginPage();
					} else {
						if (res.resultCode != "0000000") {
							alert(res.resultMsg);
							return;
						} else {
							window.open('https://g1.sports.or.kr' + res.targetUrl + '?key=' + res.ssid, '_blank')
						}
					}
				},
				fail: function (result) {
					alert("사이트를 이동하는 중 오류가 발생하였습니다.");
					return;
				}
			}
		));
	}
    
	/** API function List */
	apis.getData = getData;
	apis.isApiType = isApiType;
	apis.isApiIntent = isApiIntent;
	apis.getIntentApiType = getIntentApiType;
	apis.moveSite = moveSite;
	apis.fnAjax = fnAjax;
	
	/** API Export -> _sportsApi */
	root._sportsApi = apis;
}));
