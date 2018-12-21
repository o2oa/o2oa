function data_get_appinfo( id ) {
	var query_url = '../jaxrs/appinfo/' + id;
	//如果未输入ID，那么就查询所有的应用信息
	if( id == null || id == undefined || id == "" ){
		query_url = '../jaxrs/appinfo/list/all';
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

function data_getforadmin_appinfo( ) {
	var query_url = '../jaxrs/appinfo/list/admin';
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

function data_getforuser_appinfo( ) {
	var query_url = '../jaxrs/appinfo/list/user';
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

function data_geticon_appinfo( id ) {
	var query_url = '../jaxrs/appinfo/' + id + "/icon";
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
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

function data_put_appinfo( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'put',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/appinfo/' + id,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_post_appinfo( id ) {
    $.ajax({
		type : 'post',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/appinfo' ,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_delete_appinfo( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'delete',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/appinfo/' + id ,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}