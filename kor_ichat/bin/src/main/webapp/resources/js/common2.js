function selectTask(step1, step2, step3) {
    var param = {"step1":step1, "step2":step2, "step3":step3};
    var task ="";
    $.ajax({
		url: "./selectTask",
		type:'GET',
		cache: false,
		dataType: "html",
		data: param,
		async: false,
        success: function(data) {
        	task = data;
		},
		error:function(request,status,error){
	        alert("code = "+ request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
	    }
 	});
    return task;
}

function step2Call(step1){
	var arrayStep2 ="";
	
	var param = {"step1":step1};
		    $.ajax({
				url: "./step2Call",
				type:'GET',
				cache: false,
				data: param,
				async: false,
		        success: function(data) {
		        	arrayStep2 =data;
		        	var array = arrayStep2.split(',');
		        	var inputhtml="";
		        	for( var i in array){
		        		var step3IsNull =isNullStep3(step1, array[i]);
		        		if(step3IsNull !='' && step3IsNull !='null'){
		        			inputhtml += "<li><a id=\"addValue\" href=\"#none\" onClick=\"step3Call('"+step1+"','"+array[i]+"')\"> "+ array[i] +"<i class=\"\"></i></a>";
		        			inputhtml += "<ul class=\"depth3\" id=\"step3Ul"+step1+array[i]+"\" style=\"display:none;\">";
		        			inputhtml += "</ul>";
		        		}else{
		        			inputhtml += "<li class=\"noDepth\"><a href=\"#none\""+" onClick=\"fnDoList('"+step1+"','"+array[i]+"');\"> "+ array[i] +"</a>";
		        		}
		        		inputhtml += "</li>";
		        	}
		        	var id = "step2Ul"+step1;
		        	document.getElementById(id).innerHTML = inputhtml;
				},
				error:function(request,status,error){
			        alert("code = "+ request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
			    }
		 	});
		    return arrayStep2;
}

function step3Call(step1, step2){
	var arrayStep3 ="";
	var param = {"step1":step1, "step2":step2};
		    $.ajax({
				url: "./step3Call",
				type:'GET',
				cache: false,
				data: param,
				async: false,
		        success: function(data) {
		        	arrayStep3 =data;
		        	var inputhtml="";
		        	var array = arrayStep3.split(',');
		        	for( var i in array){
			        	if(array[i] !='' && array[i] !='null'){
			        		inputhtml += "<li class=\"on\">"
			        		inputhtml += "<a href=\"#none\" onClick=\"fnDoList('"+step1+"','"+step2+"','"+array[i]+"');\">"
			        		inputhtml += array[i]
			        		inputhtml +="</a>"
			        		inputhtml += "</li>"
			        	}else{
			        		inputhtml ="";
			        		break;
			        	}
		        	}
		        	var id = "step3Ul"+step1+step2;
		        	document.getElementById(id).innerHTML = inputhtml;
		        	//$("#step2Ul").append("<li><a href='#' class='no' id='"+code_id+"'>"+code_name+"</a></li>");
				},
				error:function(request,status,error){
			        alert("code = "+ request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
			    }
		 	});
		    return arrayStep3;
}

function isNullStep3(step1, step2){
	var arrayStep3 ="";
	var param = {"step1":step1, "step2":step2};
		    $.ajax({
				url: "./step3Call",
				type:'GET',
				cache: false,
				data: param,
				async: false,
		        success: function(data) {
		        	arrayStep3 =data;
				},
				error:function(request,status,error){
			        alert("code = "+ request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
			    }
		 	});
		    return arrayStep3;
}

function fnDoList(step1, step2, step3) {
    var param = {"step1":step1, "step2":step2, "step3":step3};
    $.ajax({
		url: "./phoneDetailInfo",
		type:'GET',
		cache: false,
		dataType: "html",
		data: param,
		async: false,
        success: function(data) {
        	document.getElementById("listArea").innerHTML = data;
        	$('#selectTask').html(selectTask(step1, step2, step3));
        	detailCall(2);
		},
		error:function(request,status,error){
	        alert("code = "+ request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
	    }
 	});
}


function detailCall(num){
	$('#main_pop').hide();
	$('#detail_pop').show();
}
		
function showMain(type){
	if( type=='chat'){
		window.location.href='./phone.do';
	}else{
		$('.develop_sch').hide();
		$('#detail_pop').hide();
		$('#main_pop').show();
	}
}