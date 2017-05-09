companyDuty_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/companyduty',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20
};

function companyDuty_list_reload() {
	if (companyDuty_parameter.list_action) {
		companyDuty_parameter.list_action.call(window, companyDuty_parameter.list_action_parameter);
	} else {
		companyDuty_list_next('(0)');
	}
}

function companyDuty_list_next(id) {
	var id = ( id ? id : companyDuty_parameter.last);
	companyDuty_parameter.list_action = companyDuty_list_next;
	companyDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyDuty_parameter.root + '/list/' + id + '/next/' + companyDuty_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				companyDuty_parameter.first = data.data[0].id;
				companyDuty_parameter.last = data.data[data.data.length - 1].id;
			} else {
				companyDuty_parameter.first = '(0)';
			}
			$('#content').html(companyDuty_list_grid(data.data));
			$('#total', '#content').html(data.count);
			companyDuty_list_init();
		} else {
			failure(data);
		}
	});
}

function companyDuty_list_prev(id) {
	var id = ( id ? id : companyDuty_parameter.first);
	companyDuty_parameter.list_action = companyDuty_list_prev;
	companyDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyDuty_parameter.root + '/list/' + id + '/prev/' + companyDuty_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				companyDuty_parameter.first = data.data[0].id;
				companyDuty_parameter.last = data.data[data.data.length - 1].id;
			} else {
				companyDuty_parameter.last = '(0)';
			}
			$('#content').html(companyDuty_list_grid(data.data));
			$('#total', '#content').html(data.count);
			companyDuty_list_init();
		} else {
			failure(data);
		}
	});
}

function companyDuty_list_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="5">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
	str += '<tr><th>rank</th><th>id</th><th>name</th><th>company</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.rank + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.company + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="companyDuty_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="companyDuty_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function companyDuty_list_init() {
	$('#next', '#content').click(function() {
		companyDuty_list_next();
	});
	$('#prev', '#content').click(function() {
		companyDuty_list_prev();
	});
}

function companyDuty_list_reload() {
	if (companyDuty_parameter.list_action) {
		companyDuty_parameter.list_action.call(window, companyDuty_parameter.list_action_parameter);
	} else {
		companyDuty_query_init();
	}
}

function companyDuty_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>identityList:</td><td><textarea id="identityList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		companyDuty_post();
	});
}

function companyDuty_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : companyDuty_parameter.root,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			company : $('#company', '#content').val(),
			unique : $('#unique', '#content').val(),
			identityList : splitValue($('#identityList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			companyDuty_list_reload();
		} else {
			failure(data);
		}
	});
}

function companyDuty_edit(id) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>id:</td><td id="id"></td></tr>';
	str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>identityList:</td><td><textarea id="identityList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		companyDuty_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyDuty_parameter.root + '/' + id,
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
			$('#identityList', '#content').val(data.data.identityList).join(',');
		} else {
			failure(data);
		}
	});
}

function companyDuty_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : companyDuty_parameter.root + '/' + id,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			company : $('#company', '#content').val(),
			unique : $('#unique', '#content').val(),
			identityList : splitValue($('#identityList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			companyDuty_list_reload();
		} else {
			failure(data);
		}
	});

}

function companyDuty_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : companyDuty_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			companyDuty_list_reload();
		} else {
			failure(data);
		}
	});
}

function companyDuty_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>company</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.company + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="companyDuty_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="companyDuty_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function companyDuty_query_init() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withCompany">获取指定公司的职务.</a></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withIdentity">获取指定身份的公司职务.</a></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#withCompany', '#content').click(function() {
		companyDuty_query_withCompany($('#query', '#content').val());
	});
	$('#withIdentity', '#content').click(function() {
		companyDuty_query_withIdentity($('#query', '#content').val());
	});
}

function companyDuty_query_withCompany(id) {
	companyDuty_parameter.list_action = companyDuty_query_withCompany;
	companyDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyDuty_parameter.root + '/list/company/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(companyDuty_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}

function companyDuty_query_withIdentity(id) {
	companyDuty_parameter.list_action = companyDuty_query_withIdentity;
	companyDuty_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : companyDuty_parameter.root + '/list/identity/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(companyDuty_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}