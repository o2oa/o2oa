var service_base_path = '../jaxrs/viewfieldconfig';
function data_get_viewfieldconfig( ) {
	var service_url = $("#get_service_url").val();
	var query_url = service_base_path + "/" + service_url;
	
	//获取用户所填写的参数
	var id = $("#id").val();
	var viewId = $("#viewId").val();
	var last_id = $("#last_id").val();
	var count = $("#count").val();
	
	//根据参数组织URL
	if( "{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "list/viewId/{viewId}" == service_url ){
		query_url = query_url.replace( "{viewId}",viewId );
		if( appId == null || appId == "" || appId == undefined){
			alert("请填写appId");return false;
		}
	}else if( "list/all" == service_url ){
		
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

function data_put_viewfieldconfig( id ) {
	var service_url = $("#put_service_url").val();
	var query_url = service_base_path + "/" + service_url;
	
	//获取用户所填写的参数
	var id = $("#id").val();
	var appId = $("#appId").val();
	var catagoryId = $("#catagoryId").val();
	var last_id = $("#last_id").val();
	var count = $("#count").val();
	var status = $("#status").val();
	//根据参数组织URL
	if( "{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "publish/{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "publish/{id}/cancel" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "achive/{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "draft/{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "draft/list/{id}/next/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
		if( last_id == null || last_id == "" || last_id == undefined){
			alert("请填写last_id");return false;
		}
		if( count == null || count == "" || count == undefined){
			alert("请填写count");return false;
		}
	}else if( "draft/list/{id}/prev/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
		if( last_id == null || last_id == "" || last_id == undefined){
			alert("请填写last_id");return false;
		}
		if( count == null || count == "" || count == undefined){
			alert("请填写count");return false;
		}
	}else if( "filter/list/{id}/next/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
		query_url = query_url.replace( "{status}",status );
		if( last_id == null || last_id == "" || last_id == undefined){
			alert("请填写last_id");return false;
		}
		if( count == null || count == "" || count == undefined){
			alert("请填写count");return false;
		}
	}else if( "filter/list/{id}/prev/{count}" == service_url ){
		query_url = query_url.replace( "{id}",last_id );
		query_url = query_url.replace( "{count}",count );
		query_url = query_url.replace( "{status}",status );
		if( last_id == null || last_id == "" || last_id == undefined){
			alert("请填写last_id");return false;
		}
		if( count == null || count == "" || count == undefined){
			alert("请填写count");return false;
		}
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

function data_post_viewfieldconfig( id ) {
    $.ajax({
		type : 'post',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : service_base_path ,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_delete_viewfieldconfig( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'delete',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : service_base_path + "/" + id ,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}