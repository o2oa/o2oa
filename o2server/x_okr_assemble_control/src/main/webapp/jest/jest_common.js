function failure(data) {
	console.log(data);
	alert(data.message);
}

function splitValue(str) {
	if (str) {
		if (str.length > 0) {
			return str.split(',');
		}
	}
	return [];
}

function joinValue(o, split) {
	var s = ',';
	if (split) {
		s = '' + split;
	}
	if (o) {
		if (toString.apply(o) === '[object Array]') {
			return o.join(s);
		}
	}
	return o;
}

function send_get_request( url ){
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : url,
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

function send_post_request( url ){
	var content = $('#content').val();
	if( content == null || content == undefined || content == "" ){
		alert("请输入content");
		return false;
	}
	$.ajax({
		type : 'post',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : url ,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON( content )),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function send_put_request( url ){
	var content = $('#content').val();
	if( content == null || content == undefined || content == "" ){
		alert("请输入content");
		return false;
	}
	$.ajax({
		type : 'put',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : url ,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON( content )),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function send_delete_request( url, id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'delete',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : url + '/' + id ,
		xhrFields : { 'withCredentials' : true },
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify( json.data, null, 4 ));
    });
}