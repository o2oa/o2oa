function checkData( file_id ) {
	if( file_id == null || file_id == undefined || file_id == "" ){
		alert("请输入file_id");
		return false;
	}
	var query_url = '../jaxrs/fileimport/check/' + file_id;	
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

function importData( file_id ) {
	if( file_id == null || file_id == undefined || file_id == "" ){
		alert("请输入file_id");
		return false;
	}
	var query_url = '../jaxrs/fileimport/import/' + file_id;	
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

function testImport( ) {
	var query_url = '../jaxrs/fileimport/import/test';	
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