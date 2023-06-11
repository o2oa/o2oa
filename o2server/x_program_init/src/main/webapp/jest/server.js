function server_list() {
	$('#content').html('');
	$('#result').html('');
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/server/list',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			var str = '<table border="1" width="100%">';
			str += '<tr><th>host</th><th>port</th><th>username</th><th>operate</th></tr>';
			if (data.data) {
				$.each(data.data, function(typeIndex, item) {
					str += '<tr>';
					str += '<td>' + item.host + '</td>';
					str += '<td>' + item.port + '</td>';
					str += '<td>' + item.username + '</td>';
					str += '<td>';
					str += '<a href="#" onclick="server_edit(' + item.order + ')">edit</a>&nbsp;';
					str += '<a href="#" onclick="server_delete(' + item.order + ')">delete</a>';
					str += '</td>';
					str += '</tr>';
				});
			}
			str += '</table>';
			$('#content').html(str);
			$('#result').html(JSON.stringify(data.data, null, 4));
		} else {
			failure(data);
		}
	});
}

function server_edit(order) {
	$('#result').html('');
	$('#content').html('');
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
	str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
	str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
	str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
	str += '<tr><td>containerType:</td><td><select id="containerType"><option value="tomcat8">tomcat8</option></select></td></tr>';
	str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/server/order/' + order,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#host', '#content').val(data.data.host);
			$('#port', '#content').val(data.data.port);
			$('#username', '#content').val(data.data.username);
			$('#password', '#content').val(data.data.password);
			$('#containerType', '#content').val(data.data.containerType);
			$('#order', '#content').val(data.data.order);
		} else {
			failure(data);
		}
		$('#result').html(JSON.stringify(data.data, null, 4));
	});
	$('#put', '#content').click(function() {
		server_put(order);
	});
}

function server_put(order) {
	$('#result').html('');
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : '../jaxrs/server/order/' + order,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			host : $('#host', '#content').val(),
			port : $('#port', '#content').val(),
			username : $('#username', '#content').val(),
			password : $('#password', '#content').val(),
			containerType : $('#containerType', '#content').val(),
			order : $('#order', '#content').val()
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
		} else {
			failure(data);
		}
		$('#result').html(JSON.stringify(data.data, null, 4));
	});
}

function server_create_init() {
	$('#result').html('');
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
	str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
	str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
	str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
	str += '<tr><td>containerType:</td><td><select id="containerType"><option value="tomcat8">tomcat8</option></select></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		server_post();
	});
}

function server_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : '../jaxrs/server',
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			host : $('#host', '#content').val(),
			port : $('#port', '#content').val(),
			username : $('#username', '#content').val(),
			password : $('#password', '#content').val(),
			containerType : $('#containerType', '#content').val()
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
		} else {
			failure(data);
		}
		$('#result').html(JSON.stringify(data.data, null, 4));
	});
}

function server_delete(order) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : '../jaxrs/server/order/' + order,
		contentType : 'application/json; charset=utf-8',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
		} else {
			failure(data);
		}
		$('#result').html(JSON.stringify(data.data, null, 4));
	});
}