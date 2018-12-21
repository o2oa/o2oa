var service_base_path = '../jaxrs/attendancedetail';
function data_get_attendancedetail( ) {
	var service_url = $("#service_url").val();
	var query_url =  service_base_path + "/" + service_url;
	
	//获取用户所填写的参数
	var id = $("#id").val();
	var file_id = $("#file_id").val();
	var last_id = $("#last_id").val();
	var count = $("#count").val();
	
	//根据参数组织URL
	if( "{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "list/{file_id}" == service_url ){
		if( file_id == null || file_id == "" || file_id == undefined){
			alert("请填写file_id");return false;
		}
		query_url = query_url.replace( "{file_id}",file_id );
	}

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

function data_put_attendancedetail( ) {
	var service_url = $("#put_url").val();
	var query_url =  service_base_path + "/" + service_url;
	
	//获取用户所填写的参数
	var last_id = $("#last_id").val();
	var count = $("#count").val();
	
	//根据参数组织URL
	if( "filter/list/{id}/next/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
		if( last_id == null || last_id == "" || last_id == undefined){
			alert("请填写last_id");return false;
		}
		if( count == null || count == "" || count == undefined){
			alert("请填写count");return false;
		}
	}else if( "filter/list/{id}/prev/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
		if( last_id == null || last_id == "" || last_id == undefined){
			alert("请填写last_id");return false;
		}
		if( count == null || count == "" || count == undefined){
			alert("请填写count");return false;
		}
	}
	alert("PUT:"+query_url);
	alert(JSON.stringify($.parseJSON($('#content').val())));
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

function data_Analyse( anlyseStartDate, anlyseEndDate ) {
	var query_url = '../jaxrs/attendancedetail/analyse/{startDate}/{endDate}';
	//如果未输入ID，那么就查询所有的应用信息
	query_url = query_url.replace( "{startDate}",anlyseStartDate );
	query_url = query_url.replace( "{endDate}",anlyseEndDate );
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

function data_delete_attendancedetail( id ) {
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