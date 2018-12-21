var service_base_path = '../jaxrs/appcatagoryadmin';
function data_get_catagoryinfo( ) {
	var service_url = $("#service_url").val();
	var query_url = service_base_path + "/" +service_url;
	
	//获取用户所填写的参数
	var id = $("#id").val();
	var person = $("#person").val();
	var objectType = $("#objectType").val();
	var objectId = $("#objectId").val();

	//根据参数组织URL
	if( "{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "list/catagory/{catagoryId}" == service_url ){
		query_url = query_url.replace( "{catagoryId}",objectId );
		if( objectId == null || objectId == "" || objectId == undefined){
			alert("请填写objectId");return false;
		}
	}else if( "list/app/{appId}" == service_url ){
		query_url = query_url.replace( "{appId}",objectId );
		if( objectId == null || objectId == "" || objectId == undefined){
			alert("请填写objectId");return false;
		}
	}else if( "list/person/{person}" == service_url ){
		query_url = query_url.replace( "{person}",person );
		query_url = query_url.replace( "{objectType}",objectType );
		if( person == null || person == "" || person == undefined){
			alert("请填写person");return false;
		}
	}else if( "list/{person}/type/{objectType}" == service_url ){
		query_url = query_url.replace( "{person}",person );
		query_url = query_url.replace( "{objectType}",objectType );
		if( person == null || person == "" || person == undefined){
			alert("请填写person");return false;
		}
		if( objectType == null || objectType == "" || objectType == undefined){
			alert("请填写objectType!");return false;
		}
	}else if( "list/all" == service_url ){
		
	}
	alert(query_url);	
	
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

function data_put_catagoryinfo( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'put',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : service_base_path + "/" +id,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_post_catagoryinfo( id ) {
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

function data_delete_catagoryinfo( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'delete',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : service_base_path + "/" +id ,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}