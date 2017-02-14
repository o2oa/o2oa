departmentAttribute_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/departmentattribute',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20
};

function departmentAttribute_list_reload() {
	if (departmentAttribute_parameter.list_action) {
		departmentAttribute_parameter.list_action.call(window, departmentAttribute_parameter.list_action_parameter);
	} else {
		departmentAttribute_list_next('(0)');
	}
}

function departmentAttribute_list_next(id) {
	var id = ( id ? id : departmentAttribute_parameter.last);
	departmentAttribute_parameter.list_action = departmentAttribute_list_next;
	departmentAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentAttribute_parameter.root + '/list/' + id + '/next/' + departmentAttribute_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				departmentAttribute_parameter.first = data.data[0].id;
				departmentAttribute_parameter.last = data.data[data.data.length - 1].id;
			} else {
				departmentAttribute_parameter.first = '(0)';
			}
			$('#content').html(departmentAttribute_list_grid(data.data));
			$('#total', '#content').html(data.count);
			departmentAttribute_list_init();
		} else {
			failure(data);
		}
	});
}

function departmentAttribute_list_prev(id) {
	var id = ( id ? id : departmentAttribute_parameter.first);
	departmentAttribute_parameter.list_action = departmentAttribute_list_prev;
	departmentAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentAttribute_parameter.root + '/list/' + id + '/prev/' + departmentAttribute_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				departmentAttribute_parameter.first = data.data[0].id;
				departmentAttribute_parameter.last = data.data[data.data.length - 1].id;
			} else {
				departmentAttribute_parameter.last = '(0)';
			}
			$('#content').html(departmentAttribute_list_grid(data.data));
			$('#total', '#content').html(data.count);
			departmentAttribute_list_init();
		} else {
			failure(data);
		}
	});
}

function departmentAttribute_list_grid(items) {
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
		str += '<a href="#" onclick="departmentAttribute_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="departmentAttribute_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function departmentAttribute_list_init() {
	$('#next', '#content').click(function() {
		departmentAttribute_list_next();
	});
	$('#prev', '#content').click(function() {
		departmentAttribute_list_prev();
	});
}

function departmentAttribute_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>attributeList:</td><td><textarea id="attributeList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		departmentAttribute_post();
	});
}

function departmentAttribute_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : departmentAttribute_parameter.root,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			department : $('#department', '#content').val(),
			unique : $('#unique', '#content').val(),
			attributeList : splitValue($('#attributeList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			departmentAttribute_list_reload();
		} else {
			failure(data);
		}
	});
}

function departmentAttribute_edit(id) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>id:</td><td id="id"></td></tr>';
	str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>attributeList:</td><td><textarea id="attributeList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		departmentAttribute_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentAttribute_parameter.root + '/' + id,
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
			$('#attributeList', '#content').val(data.data.attributeList).join(',');
		} else {
			failure(data);
		}
	});
}

function departmentAttribute_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : departmentAttribute_parameter.root + '/' + id,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			department : $('#department', '#content').val(),
			unique : $('#unique', '#content').val(),
			attributeList : splitValue($('#attributeList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			departmentAttribute_list_reload();
		} else {
			failure(data);
		}
	});

}

function departmentAttribute_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : departmentAttribute_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			departmentAttribute_list_reload();
		} else {
			failure(data);
		}
	});
}

function departmentAttribute_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>department</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.department + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="departmentAttribute_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="departmentAttribute_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function departmentAttribute_query_init() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withDepartment">获取指定部门属性.</a></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#withDepartment', '#content').click(function() {
		departmentAttribute_query_withDepartment($('#query', '#content').val());
	});
}

function departmentAttribute_query_withDepartment(id) {
	departmentAttribute_parameter.list_action = departmentAttribute_query_withDepartment;
	departmentAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : departmentAttribute_parameter.root + '/list/department/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(departmentAttribute_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}