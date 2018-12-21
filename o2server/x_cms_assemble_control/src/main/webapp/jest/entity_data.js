var service_base_path = '../jaxrs/data';
var docId,path0,path1,path2,path3,path4,path5,path6,path7;

function get_request_url(){
	var url = service_base_path;
	docId = $("#documentId").val();
	//documentId一定要填写
	if( docId == null || docId == "" || docId == undefined ){
		alert("请输入documentId");
		$("#documentId").focus();
		return null;
	}else{
		url = service_base_path + "/document/" + docId;
		path0 = $("#path0").val();
		path1 = $("#path1").val();
		path2 = $("#path2").val();
		path3 = $("#path3").val();
		path4 = $("#path4").val();
		path5 = $("#path5").val();
		path6 = $("#path6").val();
		path7 = $("#path7").val();
		if( path0 == null || path0 == "" || path0 == undefined ){
			return url;
		}else{
			url = service_base_path + "/" + path0;
		}
		
		if( path1 == null || path1 == "" || path1 == undefined ){
			return url;
		}else{
			url = url + "/" + path1;
		}
		
		if( path2 == null || path2 == "" || path2 == undefined ){
			return url;
		}else{
			url = url + "/" + path2;
		}
		
		if( path3 == null || path3 == "" || path3 == undefined ){
			return url;
		}else{
			url = url + "/" + path3;
		}
		
		if( path4 == null || path4 == "" || path4 == undefined ){
			return url;
		}else{
			url = url + "/" + path4;
		}
		
		if( path5 == null || path5 == "" || path5 == undefined ){
			return url;
		}else{
			url = url + "/" + path5;
		}
		
		if( path6 == null || path6 == "" || path6 == undefined ){
			return url;
		}else{
			url = url + "/" + path6;
		}
		
		if( path7 == null || path7 == "" || path7 == undefined ){
			return url;
		}else{
			url = url + "/" + path7;
		}
	}	
	return url;
}
function data_get_data( ) {
	var request_url = get_request_url();
	alert(request_url);
	if( request_url == null ){
		return false;
	}
    $.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : request_url,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html( JSON.stringify( json, null, 4) );
    }).fail(function(json) {
    	failure(json);
    });
}

function data_put_data() {
	var request_url = get_request_url();
	alert(request_url);
	if( request_url == null ){
		return false;
	}
    $.ajax({
		type : 'put',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : request_url,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_post_data() {
	var request_url = get_request_url();
	alert(request_url);
	if( request_url == null ){
		return false;
	}
    $.ajax({
		type : 'post',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : request_url ,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_delete_data() {
	var request_url = get_request_url();
	alert(request_url);
	if( request_url == null ){
		return false;
	}
    $.ajax({
		type : 'delete',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : request_url ,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}