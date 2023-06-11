webServer_parameter = {};

function webServer_create() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	webServer_post();
    });
}

function webServer_edit(name) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/webserver/name/' + name,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	$('#result').html(JSON.stringify(data.data, null, 4));
	if (data.type == 'success') {
	    if (data.data) {
		$('#order').val(data.data.order);
		$('#name').val(data.data.name);
		$('#host').val(data.data.host);
		$('#port').val(data.data.port);
		$('#username').val(data.data.username);
		$('#password').val(data.data.password);
	    }
	} else {
	    failure(data);
	}
    });
    $('#put', '#content').click(function() {
	webServer_put(name);
    });
}

function webServer_post() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/webserver',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    username : $('#username').val(),
	    password : $('#password').val(),
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	$('#result').html(JSON.stringify(data.data, null, 4));
    });
}

function webServer_put(name) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/webserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    order : $('#order').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    username : $('#username').val(),
	    password : $('#password').val(),
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	$('#result').html(JSON.stringify(data.data, null, 4));
    });
}

function webServer_delete(name) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/webserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	$('#result').html(JSON.stringify(data.data, null, 4));
    });
}