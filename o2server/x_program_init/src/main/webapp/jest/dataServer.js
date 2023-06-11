dataServer_parameter = {};

function dataServer_create() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" style="width:95%" id="description"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>databaseType:</td><td><select id="databaseType"><option value="postgreSQL">postgreSQL</option><option value="mysql">mysql</option><option value="db2">db2</option><option value="oracle">oracle</option></select></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>database:</td><td><input type="text" style="width:95%" id="database"/></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	dataServer_post();
    });
}

function dataServer_edit(name) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" style="width:95%" id="description"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>databaseType:</td><td><select id="databaseType"><option value="postgreSQL">postgreSQL</option><option value="mysql">mysql</option><option value="db2">db2</option><option value="oracle">oracle</option></select></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>database:</td><td><input type="text" style="width:95%" id="database"/></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/dataserver/name/' + name,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		$('#name').val(json.data.name);
		$('#description').val(json.data.description);
		$('#order').val(json.data.order);
		$('#databaseType').val(json.data.databaseType);
		$('#host').val(json.data.host);
		$('#port').val(json.data.port);
		$('#database').val(json.data.database);
		$('#username').val(json.data.username);
		$('#password').val(json.data.password);
	    }
	} else {
	    failure(data);
	}
    });
    $('#put', '#content').click(function() {
	dataServer_put(name);
    });
}

function dataServer_post() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/dataserver',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    description : $('#description').val(),
	    order : $('#order').val(),
	    databaseType : $('#databaseType').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    database : $('#database').val(),
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

function dataServer_put(name) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/dataserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    description : $('#description').val(),
	    order : $('#order').val(),
	    databaseType : $('#databaseType').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    database : $('#database').val(),
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

function dataServer_delete(name) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/dataserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}