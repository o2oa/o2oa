department_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/department',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20
};

function department_list_reload() {
	if (department_parameter.list_action) {
		department_parameter.list_action.call(window, department_parameter.list_action_parameter);
	} else {
		department_list_next('(0)');
	}
}

function department_list_next(id) {
	var id = ( id ? id : department_parameter.last);
	department_parameter.list_action = department_list_next;
	department_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/' + id + '/next/' + department_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				department_parameter.first = data.data[0].id;
				department_parameter.last = data.data[data.data.length - 1].id;
			} else {
				department_parameter.first = '(0)';
			}
			$('#content').html(department_list_grid(data.data));
			$('#total', '#content').html(data.count);
			department_list_init();
		} else {
			failure(data);
		}
	});
}

function department_list_prev(id) {
	var id = ( id ? id : department_parameter.first);
	department_parameter.list_action = department_list_prev;
	department_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/' + id + '/prev/' + department_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				department_parameter.first = data.data[0].id;
				department_parameter.last = data.data[data.data.length - 1].id;
			} else {
				department_parameter.last = '(0)';
			}
			$('#content').html(department_list_grid(data.data));
			$('#total', '#content').html(data.count);
			department_list_init();
		} else {
			failure(data);
		}
	});
}

function department_list_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="6">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
	str += '<tr><th>rank</th><th>id</th><th>name</th><th>level</th><th>superior</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.rank + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.level + '</td>';
		str += '<td>' + item.superior + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="department_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="department_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function department_list_init() {
	$('#next', '#content').click(function() {
		department_list_next();
	});
	$('#prev', '#content').click(function() {
		department_list_prev();
	});
}

function department_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
	str += '<tr><td>superior:</td><td><input type="text" id="superior" style="width:95%"/></td></tr>';
	str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		department_post();
	});
}

function department_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : department_parameter.root,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			company : $('#company', '#content').val(),
			superior : $('#superior', '#content').val(),
			unique : $('#unique', '#content').val()
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			department_list_reload();
		} else {
			failure(data);
		}
	});
}

function department_edit(id) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>id:</td><td id="id"></td></tr>';
	str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
	str += '<tr><td>level:</td><td id="level"></td></tr>';
	str += '<tr><td>name:</td><td><input type="text"  id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
	str += '<tr><td>superior:</td><td><input type="text" id="superior" style="width:95%"/></td></tr>';
	str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		department_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#name', '#content').val(data.data.name);
			$('#company', '#content').val(data.data.company);
			$('#superior', '#content').val(data.data.superior);
			$('#unique', '#content').val(data.data.unique);
			$('#id', '#content').html(data.data.id);
			$('#sequence', '#content').html(data.data.sequence);
			$('#level', '#content').html(data.data.level);
		} else {
			failure(data);
		}
	});
}

function department_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : department_parameter.root + '/' + id,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			company : $('#company', '#content').val(),
			superior : $('#superior', '#content').val(),
			unique : $('#unique', '#content').val()
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			department_list_reload();
		} else {
			failure(data);
		}
	});
}

function department_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : department_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			department_list_reload();
		} else {
			failure(data);
		}
	});
}

function department_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>company</th><th>superior</th><th>level</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.company + '</td>';
		str += '<td>' + item.superior + '</td>';
		str += '<td>' + item.level + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="department_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="department_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function department_query_init() {
	str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="subDirectWithCompany">获取指定公司的直接下级部门.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="subNestedWithCompany">获取指定公司的嵌套下级部门.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="subDirect">获取指定部门的直接下级部门.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="subNested">获取指定部门的嵌套下级部门.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="pinyinInitial">根据首字母查找.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="like">进行模糊查找.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="likePinyin">根据拼音查找.</a></td></tr>';

	str += '</table>';
	$('#content').html(str);
	$('#subDirectWithCompany', '#content').click(function() {
		department_query_subDirectWithCompany($('#query', '#content').val());
	});
	$('#subNestedWithCompany', '#content').click(function() {
		department_query_subNestedWithCompany($('#query', '#content').val());
	});
	$('#subDirect', '#content').click(function() {
		department_query_subDirect($('#query', '#content').val());
	});
	$('#subNested', '#content').click(function() {
		department_query_subNested($('#query', '#content').val());
	});
	$('#pinyinInitial', '#content').click(function() {
		department_query_pinyinInitial($('#query', '#content').val());
	});
	$('#like', '#content').click(function() {
		department_query_like($('#query', '#content').val());
	});
	$('#likePinyin', '#content').click(function() {
		department_query_likePinyin($('#query', '#content').val());
	});
}

function department_query_subDirectWithCompany(id) {
	department_parameter.list_action = department_query_subDirectWithCompany;
	department_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/company/' + id + '/sub/direct',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(department_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function department_query_subNestedWithCompany(id) {
	department_parameter.list_action = department_query_subNestedWithCompany;
	department_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/company/' + id + '/sub/nested',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(department_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function department_query_subDirect(id) {
	department_parameter.list_action = department_query_subDirect;
	department_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/' + id + '/sub/direct',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(department_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function department_query_subNested(id) {
	department_parameter.list_action = department_query_subNested;
	department_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/' + id + '/sub/nested',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(department_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function department_query_like(key) {
	department_parameter.list_action = department_query_like;
	department_parameter.list_action_parameter = key;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/like/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(department_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function department_query_pinyinInitial(key) {
	department_parameter.list_action = department_query_pinyinInitial;
	department_parameter.list_action_parameter = key;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/pinyininitial/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(department_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function department_query_likePinyin(key) {
	department_parameter.list_action = department_query_likePinyin;
	department_parameter.list_action_parameter = key;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : department_parameter.root + '/list/like/pinyin/' + encodeURIComponent(key),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(department_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}