/**
 * 
 */
var spinner = null; //
var list = [];      // ajax response 
var strAppend = ''; // display 

function js_util_fnAjax(url, data, callback){
	if($("#projectId").length > 0){
        data.projectId = $("#projectId").val(); 
    }
    $.ajax({
        type :"post",
        url  : url,
        data : data,
        dataType: "json",
        timeout : 100000,
        beforeSend:function(){
        },
        success: function (result) {	
        	
        	$("#loading").remove();
        	var bSuccess = (typeof result.success) === undefined ? false : data.success;
        	if(result.success){ //true
        		if(callback)
        			callback(result.outdata);        		
        	}else{
        		$("#loading").remove();
        		var ERR = result.error;
        	}	
        },
    	error:function(result){
    		chatState = true;
    		$("#loading").remove();
//    		location.reload();
 	    }
    });
}

function js_util_fnSubmit(opt_formId){
	appendSubmitForm();
	
	this.formId = isNull(opt_formId) == true ? "submitForm" : opt_formId;
	this.url = "";
	
	if(this.formId == "submitForm") {
		$("#submitForm")[0].reset();
	}
	
	this.setUrl = function setUrl(url){
		this.url = url;
	}
	
	this.addParam = function addParam(key, value){
		if(hasObject(key) == true){				
			$("#"+this.formId).find("input[name='"+key+"']").remove(); 
		}
		$("#"+this.formId).append($("<input type='hidden' name='"+key+"' id='"+key+"' value='"+value+"'>"));		
	}
	
	this.submit = function submit(){
		var frm = $("#"+this.formId)[0];
		frm.action = this.url;
		frm.method = "post";
		frm.submit();
	}
}

function appendSubmitForm(){
	if(hasObject('submitForm')){
		return false; 
	}else{
		var strHtml = "";		
		strHtml += "<form id='submitForm$' name='submitForm' >";		
		strHtml += "</form>";
		$("body").append(strHtml);
	}
}

function hasObject(divId){
	var len = $('#'+divId).length;
	if(len > 0){
		return true;
	}else{
		return false;
	}
}

function isNull(str){
	if (str == null) return true;
	if (str == "NaN") return true;
	if (new String(str).valueOf() == "undefined") return true;
	var chkStr = new String(str);
	if (chkStr.valueOf() == "undefined") return true;
	if (chkStr == null) return true;
	if (chkStr.toString().length == 0) return true;
	return false;
}

function appendSpinerDiv(){
	if(hasObject('spinLoading')){
		return false; 
	}else{
		var strHtml = "";		
		strHtml += "<div id='spinLoading'>";
		strHtml += "  <div id='loadingcontent'>";
		strHtml += "    <p id='loadingspinner'>";
		strHtml += "       Please Waiting ...";
		strHtml += "    </p>";
		strHtml += "  </div>";
		strHtml += "</div>";		
		$("body").append(strHtml);
	}
}

function startSpinner() {	
	$("#spinLoading").fadeIn();
    var opts = {
        lines: 12,    // The number of lines to draw
        length: 10,   // The length of each line
        width: 4,     // The line thickness
        radius: 10,   // The radius of the inner circle
        color: '#000',// #rgb or #rrggbb
        speed: 1,     // Rounds per second
        trail: 60,    // Afterglow percentage
        shadow: false // Whether to render a shadow
    };

    var target = document.getElementById('spinLoading');
    if (spinner == null) {
        spinner = new Spinner(opts).spin(target);
    }
}
	
function stopSpinner(){
	if(spinner != null){
		spinner.stop();
		spinner = null;		
	}
	$("#spinLoading").fadeOut();
}

function yyyymmdd(dateIn) {
   var yyyy = dateIn.getFullYear();
   var mm = dateIn.getMonth()+1; // getMonth() is zero-based
   var dd  = dateIn.getDate();
   return String(10000*yyyy + 100*mm + dd); // Leading zeros for mm and dd
}

