companyAttribute_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/companyattribute',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20
};

function companyAttribute_list_reload() {
	if (companyAttribute_parameter.list_action) {
		companyAttribute_parameter.list_action.call(window, companyAttribute_parameter.list_action_parameter);
	} else {
		companyAttribute_list_next('(0)');
	}
}

function companyAttribute_list_next(id) {
	var id = ( id ? id : companyAttribute_parameter.last);
	companyAttribute_parameter.list_action = companyAttribute_list_next;
	companyAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyAttribute_parameter.root + '/list/' + id + '/next/' + companyAttribute_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				companyAttribute_parameter.first = data.data[0].id;
				companyAttribute_parameter.last = data.data[data.data.length - 1].id;
			} else {
				companyAttribute_parameter.first = '(0)';
			}
			$('#content').html(companyAttribute_list_grid(data.data));
			$('#total', '#content').html(data.count);
			companyAttribute_list_init();
		} else {
			failure(data);
		}
	});
}

function companyAttribute_list_prev(id) {
	var id = ( id ? id : companyAttribute_parameter.first);
	companyAttribute_parameter.list_action = companyAttribute_list_prev;
	companyAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyAttribute_parameter.root + '/list/' + id + '/prev/' + companyAttribute_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				companyAttribute_parameter.first = data.data[0].id;
				companyAttribute_parameter.last = data.data[data.data.length - 1].id;
			} else {
				companyAttribute_parameter.last = '(0)';
			}
			$('#content').html(companyAttribute_list_grid(data.data));
			$('#total', '#content').html(data.count);
			companyAttribute_list_init();
		} else {
			failure(data);
		}
	});
}

function companyAttribute_list_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="5>	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
	str += '<tr><th>rank</th><th>id</th><th>name</th><th>company</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.rank + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.company + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="companyAttribute_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="companyAttribute_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function companyAttribute_list_init() {
	$('#next', '#content').click(function() {
		companyAttribute_list_next();
	});
	$('#prev', '#content').click(function() {
		companyAttribute_list_prev();
	});
}

function companyAttribute_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>attributeList:</td><td><textarea id="attributeList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		companyAttribute_post();
	});
}

function companyAttribute_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : companyAttribute_parameter.root,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			company : $('#company', '#content').val(),
			unique : $('#unique', '#content').val(),
			attributeList : splitValue($('#attributeList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			companyAttribute_list_reload();
		} else {
			failure(data);
		}
	});
}

function companyAttribute_edit(id) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>id:</td><td id="id"></td></tr>';
	str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>attributeList:</td><td><textarea id="attributeList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		companyAttribute_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyAttribute_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#id', '#content').html(data.data.id);
			$('#sequence', '#content').html(data.data.sequence);
			$('#name', '#content').val(data.data.name);
			$('#company', '#content').val(data.data.company);
			$('#unique', '#content').val(data.data.unique);
			$('#attributeList', '#content').val(data.data.attributeList).join(',');
		} else {
			failure(data);
		}
	});
}

function companyAttribute_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : companyAttribute_parameter.root + '/' + id,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			company : $('#company', '#content').val(),
			unique : $('#unique', '#content').val(),
			attributeList : splitValue($('#attributeList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			companyAttribute_list_reload();
		} else {
			failure(data);
		}
	});

}

function companyAttribute_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : companyAttribute_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			companyAttribute_list_reload();
		} else {
			failure(data);
		}
	});
}

function companyAttribute_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>company</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.company + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="companyAttribute_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="companyAttribute_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function companyAttribute_query_init() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withCompany">获取指定公司属性.</a></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#withCompany', '#content').click(function() {
		companyAttribute_query_withCompany($('#query', '#content').val());
	});
}

function companyAttribute_query_withCompany(id) {
	companyAttribute_parameter.list_action = companyAttribute_query_withCompany;
	companyAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyAttribute_parameter.root + '/list/company/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(companyAttribute_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}