storageServer_parameter = {};

function storageServer_create() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>storageServiceType:</td><td><select id="storageServiceType"><option value="ftp">ftp</option></select></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>path:</td><td><input type="text" style="width:95%" id= "path"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	storageServer_post();
    });
}

function storageServer_edit(name) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>storageServiceType:</td><td><select id="storageServiceType"><option value="ftp">ftp</option></select></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>path:</td><td><input type="text" style="width:95%" id="path"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/storageserver/name/' + name,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		$('#order').val(json.data.order);
		$('#name').val(json.data.name);
		$('#storageServiceType').val(json.data.storageServiceType);
		$('#host').val(json.data.host);
		$('#port').val(json.data.port);
		$('#path').val(json.data.path);
		$('#username').val(json.data.username);
		$('#password').val(json.data.password);
	    }
	} else {
	    failure(json);
	}
    });
    $('#put', '#content').click(function() {
	storageServer_put(name);
    });
}

function storageServer_post() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/storageserver',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    order : $('#order').val(),
	    storageServiceType : $('#storageServiceType').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    path : $('#path').val(),
	    username : $('#username').val(),
	    password : $('#password').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function storageServer_put(name) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/storageserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    order : $('#order').val(),
	    storageServiceType : $('#storageServiceType').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    path : $('#path').val(),
	    username : $('#username').val(),
	    password : $('#password').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function storageServer_delete(name) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/storageserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}