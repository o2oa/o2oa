function storage_create(storageType) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>storageType:</td><td id="storageType">' + storageType + '</td></tr>';
    str += '<tr><td>storageServer:</td><td><input type="text" style="width:95%" id="storageServer"/></td></tr>';
    str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>weight:</td><td><input type="text" style="width:95%" id="weight"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post', '#content').click(function() {
	storage_post(storageType);
    });
}

function storage_post(storageType) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/storage/storagetype/' + storageType,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    enable : $('#enable').val(),
	    weight : $('#weight').val(),
	    order : $('#order').val(),
	    storageServer : $('#storageServer').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function storage_edit(storageType, storageServer) {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>storageType:</td><td id="storageType">' + storageType + '</td></tr>';
    str += '<tr><td>storageServer:</td><td><input type="text" style="width:95%" id="storageServer"/></td></tr>';
    str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>weight:</td><td><input type="text" style="width:95%" id="weight"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/storage/storagetype/' + storageType + '/storageserver/' + storageServer,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		$('#storageServer').val(json.data.storageServer);
		$('#enable').val(json.data.enable);
		$('#weight').val(json.data.weight);
		$('#order').val(json.data.order);
	    }
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	storage_put(storageType, storageServer);
    });
}

function storage_put(storageType, storageServer) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/storage/storagetype/' + storageType + '/storageserver/' + storageServer,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    enable : $('#enable').val(),
	    weight : $('#weight').val(),
	    order : $('#order').val(),
	    storageServer : $('#storageServer').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function storage_delete(storageType, storageServer) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/storage/storagetype/' + storageType + '/storageserver/' + storageServer,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}