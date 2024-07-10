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
	
	/** Error messages. */
	var AJAX_ERROR_TEXT = 'Ajax failed.',
		PARAMETAER_ERROR_TEXT = 'Parameter Type Error.',
		ELEMENT_ERROR_TEXT = 'The element is not valid.',
		API_TYPE_ERROR_TEXT = 'Unknown Api.';
	
	/** API Config. */
	var BASE_URL = './api/',
		BASE_AJAX = {
			dataType: 'json',
			method: 'POST',
			"error": function (xhr, error, thrown) {
				console.log(AJAX_ERROR_TEXT);
			}
		},
		BASE_TYPES = [
			'G1','G2','G3'
		];
        
	var root = (typeof self == 'object' && self && self.Object === Object && self) || Function('return this')();
	var apis = {};
  
	function isObjectLike(value) {
		return value != null && typeof value == 'object';
    }
    
    function isApiType(value) {
		return value != null && typeof value == 'string' && BASE_TYPES.includes(value);
    }
    
    function baseCallback(json){
		return json;
	}
	
	function setApiUrl(type){
		var url = BASE_URL;
		if(isApiType){
			url += type;
		}
		
		return url;
	}
	
	function setSelectOptions($el, json, defaultSelectedIndex){
		if(!$('#'+elementId).prop("tagName") === "SELECT"){
			console.log(ELEMENT_ERROR_TEXT);
			return false;
		}
		
		$el.empty();
		
		var selectedIndex = defaultSelectedIndex 
		var data = json || [];
		
		for(var i=0;i<data.length;i++) {
			var selected = "";
			var value = data.value;
			var text = data.text || value;
			if(i == defaultSelectedIndex){
				selected = "selected";	
			}
			$el.append(
				$("<OPTION></OPTION>")
  				.attr("selected", selected)
  				.attr("value", value)
  				.text(text)
  			);
		}
	}
	
    /**
     * Ajax Call sports api.
     *
     * @type function
     * @name _sportsApi.getData
     * @member
     * @param {string} type Api Type.
     * @param {object} params parameters.
     * @param {function} callback Callback Function.
     * @return {function} callback or {object} json.
     */
    function getData(type, params, callback) {
		if(isApiType(type)){
			console.log(API_TYPE_ERROR_TEXT);
			return false;
		}
		$.ajax($.extend( BASE_AJAX, {
				url: setApiUrl(type),
				data: params,
				success: function (json) {
					if (callback) {
						callback(json);
                    }else{
						baseCallback(json);
					}
				}
		}));
    }
    
    /**
     * Ajax Call sports api for Select.
     *
     * @type function
     * @name _sportsApi.getData
     * @member
     * @param {string} type Api Type.
     * @param {object} params parameters.
     * @param {function} callback Callback Function.
     */
    function getSelectData(type, params, elementId, defaultSelectedIndex, callback) {
		if(isApiType(type)){
			console.log(API_TYPE_ERROR_TEXT);
			return false;
		}
	
		$.ajax($.extend( BASE_AJAX, {
				url: setApiUrl(type),
				data: params,
				success: function (json) {
					if (callback) {
						callback(json);
                    }else if(isObjectLike($('#'+elementId))){
						setSelectOptions($('#'+elementId), json, defaultSelectedIndex);
					}else{
						baseCallback(json);
					}
				}
		}));
    }
	
	/** API function list */
	apis.getData = getData;
	apis.getSelectData = getSelectData;
	apis.isObjectLike = isObjectLike;
	apis.setSelectOptions = setSelectOptions;
	
	/** API Export -> _sportsApi */
	root._sportsApi = apis;
}));


