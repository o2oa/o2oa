role_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/role',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20
};

function role_list_reload() {
	if (role_parameter.list_action) {
		role_parameter.list_action.call(window, role_parameter.list_action_parameter);
	} else {
		role_list_next('(0)');
	}
}

function role_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>personList:</td><td><textarea id="personList" style="width:95%;height:200px"/></td></tr>';
	str += '<tr><td>groupList:</td><td><textarea id="groupList" style="width:95%;height:200px"/></td></tr>';
	str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		role_post();
	});
}

function role_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : role_parameter.root,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			personList : splitValue($('#personList', '#content').val()),
			groupList : splitValue($('#groupList', '#content').val()),
			unique : $('#unique', '#content').val()
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			role_list_reload();
		} else {
			failure(data);
		}
	});
}

function role_edit(id) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>id:</td><td id="id"></td></tr>';
	str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>personList:</td><td><textarea id="personList" style="width:95%;height:200px"/></td></tr>';
	str += '<tr><td>groupList:</td><td><textarea id="groupList" style="width:95%;height:200px"/></td></tr>';
	str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		role_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#id', '#content').html(data.data.id);
			$('#sequence', '#content').html(data.data.sequence);
			$('#name', '#content').val(data.data.name);
			$('#personList', '#content').html(data.data.personList.join(','));
			$('#groupList', '#content').html(data.data.groupList.join(','));
			$('#unique', '#content').val(data.data.unique);
		} else {
			failure(data);
		}
	});
}

function role_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : role_parameter.root + '/' + id,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			personList : splitValue($('#personList', '#content').val()),
			groupList : splitValue($('#groupList', '#content').val()),
			unique : $('#unique', '#content').val()
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			role_list_reload();
		} else {
			failure(data);
		}
	});
}

function role_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : role_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			role_list_next('(0)');
		} else {
			failure(data);
		}
	});
}

function role_list_next(id) {
	var id = ( id ? id : role_parameter.last);
	role_parameter.list_action = role_list_next;
	role_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/list/' + id + '/next/' + role_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				role_parameter.first = data.data[0].id;
				role_parameter.last = data.data[data.data.length - 1].id;
			} else {
				role_parameter.first = '(0)';
			}
			$('#content').html(role_list_grid(data.data));
			$('#total', '#content').html(data.count);
			role_list_init();
		} else {
			failure(data);
		}
	});
}

function role_list_prev(id) {
	var id = ( id ? id : role_parameter.first);
	role_parameter.list_action = role_list_prev;
	role_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/list/' + id + '/prev/' + role_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				role_parameter.first = data.data[0].id;
				role_parameter.last = data.data[data.data.length - 1].id;
			} else {
				role_parameter.last = '(0)';
			}
			$('#content').html(role_list_grid(data.data));
			$('#total', '#content').html(data.count);
			role_list_init();
		} else {
			failure(data);
		}
	});
}

function role_list_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="4">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
	str += '<tr><th>rank</th><th>id</th><th>name</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.rank + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="role_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="role_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function role_list_init() {
	$('#next', '#content').click(function() {
		role_list_next();
	});
	$('#prev', '#content').click(function() {
		role_list_prev();
	});
}

function role_query_init() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withPerson">获取人员所拥有的角色.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withGroup">获取群组所拥有的角色.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="pinyinInitial">根据首字母进行查找.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="like">进行模糊查找.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="likePinyin">根据拼音进行查找.</a></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#withPerson', '#content').click(function() {
		role_query_withPerson($('#query', '#content').val());
	});
	$('#withGroup', '#content').click(function() {
		role_query_withGroup($('#query', '#content').val());
	});
	$('#like', '#content').click(function() {
		role_query_like($('#query', '#content').val());
	});
	$('#pinyinInitial', '#content').click(function() {
		role_query_pinyinInitial($('#query', '#content').val());
	});
	$('#likePinyin', '#content').click(function() {
		role_query_likePinyin($('#query', '#content').val());
	});
}

function role_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="role_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="role_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function role_query_withPerson(id) {
	role_parameter.list_action = role_query_withPerson;
	role_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/list/person/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(role_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function role_query_withGroup(id) {
	role_parameter.list_action = role_query_withGroup;
	role_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/list/group/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(role_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function role_query_like(key) {
	role_parameter.list_action = role_query_like;
	role_parameter.list_action_parameter = key;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/list/like/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(role_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function role_query_pinyinInitial(key) {
	role_parameter.list_action = role_query_pinyinInitial;
	role_parameter.list_action_parameter = key;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/list/pinyininitial/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(role_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function role_query_likePinyin(key) {
	role_parameter.list_action = role_query_likePinyin;
	role_parameter.list_action_parameter = key;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : role_parameter.root + '/list/like/pinyin/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(role_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}