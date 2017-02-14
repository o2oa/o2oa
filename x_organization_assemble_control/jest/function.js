group_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/group',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20

};

function group_list_reload() {
	if (group_parameter.list_action) {
		group_parameter.list_action.call(window, group_parameter.list_action_parameter);
	} else {
		group_list_next('(0)');
	}
}

function group_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>personList:</td><td><textarea id="personList" style="width:95%;height:200px"/></td></tr>';
	str += '<tr><td>groupList:</td><td><textarea id="groupList" style="width:95%;height:200px"/></td></tr>';
	str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		group_post();
	});
}

function group_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : group_parameter.root,
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
			group_list_reload();
		} else {
			failure(data);
		}
	});
}

function group_edit(id) {
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
		group_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#id', '#content').html(data.data.id);
			$('#sequence', '#content').html(data.data.sequence);
			$('#name', '#content').val(data.data.name);
			$('#personList', '#content').val(data.data.personList.join(','));
			$('#groupList', '#content').val(data.data.groupList.join(','));
			$('#unique', '#content').val(data.data.unique);
		} else {
			failure(data);
		}
	});
}

function group_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : group_parameter.root + '/' + id,
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
			group_list_reload();
		} else {
			failure(data);
		}
	});
}

function group_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : group_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			group_list_next('(0)');
		} else {
			failure(data);
		}
	});
}

function group_list_next(id) {
	var id = ( id ? id : group_parameter.last);
	group_parameter.list_action = group_list_next;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/' + id + '/next/' + group_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				group_parameter.first = data.data[0].id;
				group_parameter.last = data.data[data.data.length - 1].id;
			} else {
				group_parameter.first = '(0)';
			}
			$('#content').html(group_list_grid(data.data));
			$('#total', '#content').html(data.count);
			group_list_init();
		} else {
			failure(data);
		}
	});
}

function group_list_prev(id) {
	var id = ( id ? id : group_parameter.first);
	group_parameter.list_action = group_list_prev;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/' + id + '/prev/' + group_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				group_parameter.first = data.data[0].id;
				group_parameter.last = data.data[data.data.length - 1].id;
			} else {
				group_parameter.last = '(0)';
			}
			$('#content').html(group_list_grid(data.data));
			$('#total', '#content').html(data.count);
			group_list_init();
		} else {
			failure(data);
		}
	});
}

function group_list_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="4">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
	str += '<tr><th>rank</th><th>id</th><th>name</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.rank + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="group_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="group_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function group_list_init() {
	$('#next', '#content').click(function() {
		group_list_next();
	});
	$('#prev', '#content').click(function() {
		group_list_prev();
	});
}

function group_query_init() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="supDirect">获取群组的直接上级群组.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="supNested">获取群组的嵌套上级群组.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="supDirectWithPerson">获取人员所在的直接群组.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="supNestedWithPerson">获取人员所在的群组,包括嵌套的群组.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="subDirect">获取群组的直接下级群组.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="subNested">获取群组的下级群组,包括嵌套的群组.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="pinyinInitial">根据首字母进行查找.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="like">进行模糊查找.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="likePinyin">根据拼音进行查找.</a></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#supDirect', '#content').click(function() {
		group_query_supDirect($('#query', '#content').val());
	});
	$('#supNested', '#content').click(function() {
		group_query_supNested($('#query', '#content').val());
	});
	$('#supDirectWithPerson', '#content').click(function() {
		group_query_supDirectWithPerson($('#query', '#content').val());
	});
	$('#supNestedWithPerson', '#content').click(function() {
		group_query_supNestedWithPerson($('#query', '#content').val());
	});
	$('#subDirect', '#content').click(function() {
		group_query_subDirect($('#query', '#content').val());
	});
	$('#subNested', '#content').click(function() {
		group_query_subNested($('#query', '#content').val());
	});
	$('#pinyinInitial', '#content').click(function() {
		group_query_pinyinInitial($('#query', '#content').val());
	});
	$('#like', '#content').click(function() {
		group_query_like($('#query', '#content').val());
	});
	$('#likePinyin', '#content').click(function() {
		group_query_likePinyin($('#query', '#content').val());
	});
}

function group_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="group_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="group_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function group_query_supDirect(id) {
	group_parameter.list_action = group_query_supDirect;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/' + id + '/sup/direct',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_supNested(id) {
	group_parameter.list_action = group_query_supNested;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/' + id + '/sup/nested',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_supDirectWithPerson(id) {
	group_parameter.list_action = group_query_supDirectWithPerson;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/person/' + id + '/sup/direct',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_supNestedWithPerson(id) {
	group_parameter.list_action = group_query_supNestedWithPerson;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/person/' + id + '/sup/nested',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_subDirect(id) {
	group_parameter.list_action = group_query_subDirect;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/' + id + '/sub/direct',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_subNested(id) {
	group_parameter.list_action = group_query_subNested;
	group_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/' + id + '/sub/nested',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_like(key) {
	group_parameter.list_action = group_query_like;
	group_parameter.list_action_parameter = key;

	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/like/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_pinyinInitial(key) {
	group_parameter.list_action = group_query_pinyinInitial;
	group_parameter.list_action_parameter = key;

	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/pinyininitial/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function group_query_likePinyin(key) {
	group_parameter.list_action = group_query_likePinyin;
	group_parameter.list_action_parameter = key;

	$.ajax({
		type : 'get',
		dataType : 'json',
		url : group_parameter.root + '/list/like/pinyin/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(group_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}