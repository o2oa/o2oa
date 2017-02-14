departmentDuty_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/departmentduty',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20
};

function departmentDuty_list_reload() {
	if (departmentDuty_parameter.list_action) {
		departmentDuty_parameter.list_action.call(window, departmentDuty_parameter.list_action_parameter);
	} else {
		departmentDuty_list_next('(0)');
	}
}

function departmentDuty_list_next(id) {
	var id = ( id ? id : departmentDuty_parameter.last);
	departmentDuty_parameter.list_action = departmentDuty_list_next;
	departmentDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentDuty_parameter.root + '/list/' + id + '/next/' + departmentDuty_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				departmentDuty_parameter.first = data.data[0].id;
				departmentDuty_parameter.last = data.data[data.data.length - 1].id;
			} else {
				departmentDuty_parameter.first = '(0)';
			}
			$('#content').html(departmentDuty_list_grid(data.data));
			$('#total', '#content').html(data.count);
			departmentDuty_list_init();
		} else {
			failure(data);
		}
	});
}

function departmentDuty_list_prev(id) {
	var id = ( id ? id : departmentDuty_parameter.first);
	departmentDuty_parameter.list_action = departmentDuty_list_prev;
	departmentDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentDuty_parameter.root + '/list/' + id + '/prev/' + departmentDuty_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				departmentDuty_parameter.first = data.data[0].id;
				departmentDuty_parameter.last = data.data[data.data.length - 1].id;
			} else {
				departmentDuty_parameter.last = '(0)';
			}
			$('#content').html(departmentDuty_list_grid(data.data));
			$('#total', '#content').html(data.count);
			departmentDuty_list_init();
		} else {
			failure(data);
		}
	});
}

function departmentDuty_list_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="5">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
	str += '<tr><th>rank</th><th>id</th><th>name</th><th>department</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.rank + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.department + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="departmentDuty_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="departmentDuty_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function departmentDuty_list_init() {
	$('#next', '#content').click(function() {
		departmentDuty_list_next();
	});
	$('#prev', '#content').click(function() {
		departmentDuty_list_prev();
	});
}

function departmentDuty_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>identityList:</td><td><textarea id="identityList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		departmentDuty_post();
	});
}

function departmentDuty_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : departmentDuty_parameter.root,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			department : $('#department', '#content').val(),
			unique : $('#unique', '#content').val(),
			identityList : splitValue($('#identityList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			departmentDuty_list_reload();
		} else {
			failure(data);
		}
	});
}

function departmentDuty_edit(id) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>id:</td><td id="id"></td></tr>';
	str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>identityList:</td><td><textarea id="identityList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		departmentDuty_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentDuty_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#id', '#content').html(data.data.id);
			$('#sequence', '#content').html(data.data.sequence);
			$('#name', '#content').val(data.data.name);
			$('#department', '#content').val(data.data.department);
			$('#unique', '#content').val(data.data.unique);
			$('#identityList', '#content').val(data.data.identityList).join(',');
		} else {
			failure(data);
		}
	});
}

function departmentDuty_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : departmentDuty_parameter.root + '/' + id,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			department : $('#department', '#content').val(),
			unique : $('#unique', '#content').val(),
			identityList : splitValue($('#identityList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			departmentDuty_list_reload();
		} else {
			failure(data);
		}
	});

}

function departmentDuty_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : departmentDuty_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			departmentDuty_list_reload();
		} else {
			failure(data);
		}
	});
}

function departmentDuty_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>department</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.department + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="departmentDuty_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="departmentDuty_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function departmentDuty_query_init() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withdepartment">获取指定部门的部门职务.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withIdentity">获取指定身份的部门职务.</a></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#withdepartment', '#content').click(function() {
		departmentDuty_query_withdepartment($('#query', '#content').val());
	});
	$('#withIdentity', '#content').click(function() {
		departmentDuty_query_withIdentity($('#query', '#content').val());
	});
}

function departmentDuty_query_withdepartment(id) {
	departmentDuty_parameter.list_action = departmentDuty_query_withdepartment;
	departmentDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentDuty_parameter.root + '/list/department/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(departmentDuty_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function departmentDuty_query_withIdentity(id) {
	departmentDuty_parameter.list_action = departmentDuty_query_withIdentity;
	departmentDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentDuty_parameter.root + '/list/identity/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(departmentDuty_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}