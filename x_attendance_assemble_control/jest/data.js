function data_query() {
    str = '<table border="1" width="100%">';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>type:</td><td>';
    str += '<select id="type">';
    str += '<option value="appInfo">appInfo</option>';
    str += '<option value="catagoryInfo">catagoryInfo</option>';
    str += '<option value="fileInfo">fileInfo</option>';
    str += '<option value="appCatagoryPermission">appCatagoryPermission</option>';
    str += '<option value="appCatagoryAdmin">appCatagoryAdmin</option>';
    str += '<option value="log">log</option>';
    str += '</select>';
    str += '</td></tr>';
    str += '<tr><td colspan="2"><button id="get">get</button>&nbsp;<button id="put">put</button>&nbsp;<button id="post">post</button>&nbsp;<button id="uuid">UUID</button></td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
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
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(JSON.stringify(json.data));
		} else {
			failure(data);
		}
	});
}

function data_get(id, type) {
    $('#data', '#content').val('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/' + type + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
		$('#result').html(JSON.stringify(data, null, 4));
		if (json.type == 'success') {
		    $('#data', '#content').val(JSON.stringify(json.data, null, 4));
		} else {
		    failure(data);
		}
    }).fail(function(data) {
    	failure(data);
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
    }).done(function(data) {
    	$('#result').html(JSON.stringify(data.data, null, 4));
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
    }).done(function(data) {
    	$('#result').html(JSON.stringify(data.data, null, 4));
    });
}