var service_base_path = '../jaxrs/fileinfo';
function data_get_document( ) {
	var service_url = $("#get_service_url").val();
	var query_url = service_base_path + "/" + service_url;
	
	//获取用户所填写的参数
	var id = $("#id").val();
	var documentId = $("#documentId").val();
	
	//根据参数组织URL
	if( "{id}" == service_url ){
		query_url = query_url.replace( "{id}",id );
		if( id == null || id == "" || id == undefined){
			alert("请填写ID");return false;
		}
	}else if( "list/document/{documentId}" == service_url ){
		query_url = query_url.replace( "{documentId}",documentId );
		if( documentId == null || documentId == "" || documentId == undefined){
			alert("请填写documentId");return false;
		}
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

function data_delete_document( id ) {
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