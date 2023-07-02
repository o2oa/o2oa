function connection_list_init() {
	$('#result').html('');
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/connection/list/connectiontype',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			var str = '<table border="1" width="100%">';
			str += '<tr><td>type</td><td><select id="type">';
			if (data.data) {
				$.each(data.data, function(typeIndex, type) {
					str += '<option value="' + type + '">' + typeIndex + '.' + type + '</option>';
				});
			}
			str += '</select>&nbsp;<a href="#" id="list">list</a></td></tr><tr><td colspan="2" id="grid">&nbsp;</td></tr></table>';
			$('#content').html(str);
			$('#list', '#content').click(function() {
				connection_list($('#type', '#content').val());
			});
			$('#result').html(JSON.stringify(data.data, null, 4));
		} else {
			failure(data);
		}
	});
}

function connection_list(connectionType) {
	$('#grid').html('');
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/connection/list/connectiontype/' + connectionType,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			var str = '<table border="1" width="100%">';
			str += '<tr><th>url</th><th>username</th><th>operate</th></tr>';
			if (data.data) {
				$.each(data.data, function(typeIndex, item) {
					str += '<tr>';
					str += '<td>' + item.url + '</td>';
					str += '<td>' + item.username + '</td>';
					str += '<td>';
					str += '<a href="#" onclick="connection_edit(\'' + connectionType + '\',\'' + item.order + '\')">edit</a>&nbsp;';
					str += '<a href="#" onclick="connection_delete(\'' + connectionType + '\',\'' + item.order + '\')">delete</a>&nbsp;';
					str += '</td>';
					str += '</tr>';
				});
			}
			str += '</table>';
			$('#grid').html(str);
			$('#result').html(JSON.stringify(data.data, null, 4));
		} else {
			failure(data);
		}
	});
}

function connection_edit(connectionType, order) {
	$('#result').html('');
	$('#content').html('');
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>type</td><td id="type">' + connectionType + '</td></tr>';
	str += '<tr><td>url:</td><td><input type="text" style="width:95%" id="url"/></td></tr>';
	str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
	str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
	str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
	str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/connection/connectiontype/' + connectionType + '/order/' + order,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#url', '#content').val(data.data.url);
			$('#username', '#content').val(data.data.username);
			$('#password', '#content').val(data.data.password);
			$('#enable', '#content').val(data.data.enable + '');
			$('#order', '#content').val(data.data.order);
		} else {
			failure(data);
		}
		$('#result').html(JSON.stringify(data.data, null, 4));
	});
	$('#put', '#content').click(function() {
		connection_put(connectionType, order);
	});
}

function connection_put(connectionType, order) {
	$('#result').html('');
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : '../jaxrs/connection/connectiontype/' + connectionType + '/order/' + order,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			url : $('#url', '#content').val(),
			username : $('#username', '#content').val(),
			password : $('#password', '#content').val(),
			order : $('#order', '#content').val(),
			enable : $('#enable', '#content').val(),
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

function connection_create_init() {
	$('#result').html('');
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>type:</td><td><select id="type"/></td></tr>';
	str += '<tr><td>url:</td><td><input type="text" style="width:95%" id="url"/></td></tr>';
	str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
	str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
	str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/connection/list/connectiontype',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data) {
				var str = '';
				$.each(data.data, function(typeIndex, type) {
					str += '<option value="' + type + '">' + type + '</option>';
				});
			}
			$('#type').html(str);
			$('#result').html(JSON.stringify(data.data, null, 4));
		} else {
			failure(data);
		}
	});
	$('#post', '#content').click(function() {
		connection_post($('#type', '#content').val());
	});
}

function connection_post(connectionType) {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : '../jaxrs/connection/connectiontype/' + connectionType,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			url : $('#url', '#content').val(),
			username : $('#username', '#content').val(),
			password : $('#password', '#content').val(),
			enable : $('#enable', '#content').val()
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

function connection_update() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>type:</td><td><input type="text" style="width:95%" id="type"/></td></tr>';
	str += '<tr><td>url:</td><td><input type="text" style="width:95%" id="url"/></td></tr>';
	str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
	str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
	str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
	str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
	str += '<tr><td>toolLevel:</td><td><select id="toolLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '<tr><td>runtimeLevel:</td><td><select id="runtimeLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '<tr><td>dataCacheLevel:</td><td><select id="dataCacheLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '<tr><td>metaDataLevel:</td><td><select id="metaDataLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '<tr><td>enhanceLevel:</td><td><select id="enhanceLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '<tr><td>queryLevel:</td><td><select id="queryLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '<tr><td>sqlLevel:</td><td><select id="sqlLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '<tr><td>jdbcLevel:</td><td><select id="jdbcLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		connection_put();
	});
}

function connection_delete(connectionType, order) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : '../jaxrs/connection/connectiontype/' + connectionType + '/order/' + order,
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

function connection_createForEach_init() {
	$('#result').html('');
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>url:</td><td><input type="text" style="width:95%" id="url"/></td></tr>';
	str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
	str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
	str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		connection_postForEach();
	});
}

function connection_postForEach() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : '../jaxrs/connection/connectiontype/all/type',
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			url : $('#url', '#content').val(),
			username : $('#username', '#content').val(),
			password : $('#password', '#content').val(),
			enable : $('#enable', '#content').val()
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