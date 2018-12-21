function data_query() {	
	var html = template( 'data_query_template', request_config );
    $('#content').html(html);
    $('#result').html('');
    
    $('#get', '#content').click(function() {
    	data_get($('#id').val(), $('#type').val());
    });
    $('#put', '#content').click(function() {
    	data_put($('#id').val(), $('#type').val());
    });
    $('#post', '#content').click(function() {
    	data_post($('#id').val(), $('#type').val());
    });
    $('#uuid', '#content').click(function() {
    	get_uuid();
    });
}

function get_uuid( ) {
	$('#data', '#content').val('');
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : '../jaxrs/uuid/random',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(json) {
		$('#result').html(JSON.stringify( json, null, 4));
	});
}

function data_get(id, type) {
	var query_url = '../jaxrs/' + type + '/' + id;
	
	if( id == null || id == undefined || id == "" ){
		query_url = '../jaxrs/' + type + '/list/all';
	}
	alert(query_url);
    $('#data', '#content').val('');
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
    	$('#result').html(JSON.stringify( json, null, 4));
    }).fail(function(json) {
    	failure(json);
    });
}

function data_put( id, type ) {
    $.ajax({
		type : 'put',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/' + type + '/' + id,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#data', '#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}

function data_post( id, type ) {
    $.ajax({
		type : 'post',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/' + type ,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#data', '#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}