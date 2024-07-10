/* 마스킹 처리 */
var maskingFunc = {
	checkNull : function (str){
		if(typeof str == "undefined" || str == null || str == ""){
			return true;
		}
		else{
			return false;
		}
	},
	masking : function(str){
		str = this.email(str);
		str = this.phone(str);
		str = this.rrn(str);
		str = this.driver(str);
		str = this.passport(str);
		
		return str;
	},
	/*
	※ 이메일 마스킹
	ex1) 원본 데이터 : abcdefg12345@naver.com
		 변경 데이터 : ab**********@naver.com
	ex2) 원본 데이터 : abcdefg12345@naver.com
	     변경 데이터 : ab**********@nav******
	*/
	email : function(str){
		var emailMatchValue = str.match(/([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9._-]+)/gi);

	    if(this.checkNull(emailMatchValue) == true)
	    {
	     	return str;
	    }
	    else
	    {
	        var len = emailMatchValue.toString().split('@').length;
	        return str.toString().replace(new RegExp('.(?=.{0,' + len + '}@)', 'gi'), '*');
	    }
	},
	/* 
	※ 휴대폰 번호 마스킹
	ex1) 원본 데이터 : 01012345678, 변경 데이터 : 010****5678
	ex2) 원본 데이터 : 010-1234-5678, 변경 데이터 : 010-****-5678
	ex3) 원본 데이터 : 0111234567, 변경 데이터 : 011***4567
	ex4) 원본 데이터 : 011-123-4567, 변경 데이터 : 011-***-4567
	*/
	phone : function(str){
		if (str)
	    {
	        var regex = /(\d{3})([\s|-]?)(\d{2})(\d{2})([\s|-]?)(\d{2})(\d{2})/gi;
	        var result = str.replace(regex, '$1$2$3**$5$6**');
	        return result;
	    }
	},
	/*
	※ 주민등록 번호 마스킹 (Resident Registration Number, RRN Masking)
	ex1) 원본 데이터 : 990101-1234567, 변경 데이터 : 990101-1******
	*외국인 번호도 13자리 동일(XXXXXX-XXXXXXX)
      생년월일           6자리
      성별              1자리
      등록기관 일련번호     5자리 
      등록자구분          1자리
	*/
	rrn : function(str){
	    // - 있을 경우
	    var rrnMatchValue = str.match(/(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1]))-[1-4]{1}[0-9]{6}\b/gi);
	    if(this.checkNull(rrnMatchValue) == true)
	    {
	        str = str;
	    }
	    else
	    {
	        var len = rrnMatchValue.toString().split('-').length;
	        str = str.toString().replace(rrnMatchValue,rrnMatchValue.toString()
	                      .replace(/(-?)([1-4]{1})([0-9]{6})\b/gi,"$1$2******"));
	    }
	
	    // - 없을 경우
	    rrnMatchValue = str.match(/\d{13}/gi);
	    if(this.checkNull(rrnMatchValue) == true)
	    {
	        str = str;
	    }
	    else
	    {
	        str = str.toString().replace(rrnMatchValue,rrnMatchValue.toString()
	                      .replace(/([0-9]{6})$/gi,"******"));
	    }
	    
	    return str;
	},
	/*
	※ 운전면허증 마스킹 (Driver License)
	*/
	driver : function(str){
		if (str)
	    {
	        var regex = /(\d{2}|\D{2})([\s|-]?)(\d{2})([\s|-]?)(\d{6})([\s|-]?)(\d{2})/gi;
	        var result = str.replace(regex, '$1$2$3$4******$6$7');
	        return result;
	    }
	},
	/*
	※ 여권번호 마스킹 (Passport)
	*/
    passport: function(str){
        if (str)
        {
            var regex = /(\D{1})(\d{8})/gi;
            var result = str.replace(regex, '$1********');
            return result;
        }
    }
}

/* 쿠키 처리 */
var cookieFunc = {
	set : function(name, value, exp) {
    	var date = new Date();
    	date.setTime(date.getTime() + exp*24*60*60*1000);
    	document.cookie = name + '=' + encodeURIComponent(value) + ';expires=' + date.toUTCString() + ';path=/';
	},
	get : function(name) {
    	var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    	
    	return value? decodeURIComponent(value[2]) : null;
  	},
  	delete : function(name) {
    	document.cookie = name + '=; expires=Thu, 01 Jan 1999 00:00:10 GMT;';
	}
}