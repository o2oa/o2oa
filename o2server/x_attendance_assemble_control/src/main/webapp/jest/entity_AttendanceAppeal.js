var service_base_path = '../jaxrs/attendanceappealInfo';
function data_get_attendanceappeal( ) {
	var service_url = $("#service_url").val();
	var query_url =  service_base_path + "/" + service_url;
	
	//获取用户所填写的参数
	var id = $("#id").val();
	
	//根据参数组织URL
	query_url = query_url.replace( "{id}",id );

    $.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : query_url,
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

function data_put_attendanceappeal( ) {
	var service_url = $("#put_url").val();
	var query_url =  service_base_path + "/" + service_url;
	
	//获取用户所填写的参数
	var id = $("#id").val();
	var last_id = $("#last_id").val();
	var count = $("#count").val();
	
	//根据参数组织URL	
	if( "filter/list/{id}/next/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
	}else if( "filter/list/{id}/prev/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
	}else{
		query_url = query_url.replace( "{id}",id );
		query_url = query_url.replace( "{count}",count );
	}
	
	alert("PUT:"+query_url);
	$.ajax({
		type : 'put',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : query_url,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_delete_attendanceappeal( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'delete',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/attendancedetail/' + id ,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}