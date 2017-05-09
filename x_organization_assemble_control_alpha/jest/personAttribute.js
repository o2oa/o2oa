personAttribute_parameter = {
	root : common_parameter.host + '/x_organization_assemble_control/jaxrs/personattribute',
	list_action : null,
	list_action_parameter : null,
	first : '(0)',
	last : '(0)',
	count : 20
};

function personAttribute_list_reload() {
	if (personAttribute_parameter.list_action) {
		personAttribute_parameter.list_action.call(window, personAttribute_parameter.list_action_parameter);
	} else {
		personAttribute_list_next('(0)');
	}
}

function personAttribute_list_next(id) {
	var id = ( id ? id : personAttribute_parameter.last);
	personAttribute_parameter.list_action = personAttribute_list_next;
	personAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : personAttribute_parameter.root + '/list/' + id + '/next/' + personAttribute_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				personAttribute_parameter.first = data.data[0].id;
				personAttribute_parameter.last = data.data[data.data.length - 1].id;
			} else {
				personAttribute_parameter.first = '(0)';
			}
			$('#content').html(personAttribute_list_grid(data.data));
			$('#total', '#content').html(data.count);
			personAttribute_list_init();
		} else {
			failure(data);
		}
	});
}

function personAttribute_list_prev(id) {
	var id = ( id ? id : personAttribute_parameter.first);
	personAttribute_parameter.list_action = personAttribute_list_prev;
	personAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : personAttribute_parameter.root + '/list/' + id + '/prev/' + personAttribute_parameter.count,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data.length > 0) {
				personAttribute_parameter.first = data.data[0].id;
				personAttribute_parameter.last = data.data[data.data.length - 1].id;
			} else {
				personAttribute_parameter.last = '(0)';
			}
			$('#content').html(personAttribute_list_grid(data.data));
			$('#total', '#content').html(data.count);
			personAttribute_list_init();
		} else {
			failure(data);
		}
	});
}

function personAttribute_list_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="5">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
	str += '<tr><th>rank</th><th>id</th><th>name</th><th>person</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.rank + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.person + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="personAttribute_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="personAttribute_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function personAttribute_list_init() {
	$('#next', '#content').click(function() {
		personAttribute_list_next();
	});
	$('#prev', '#content').click(function() {
		personAttribute_list_prev();
	});
}

function personAttribute_create() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>attributeList:</td><td><textarea id="attributeList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#post', '#content').click(function() {
		personAttribute_post();
	});
}

function personAttribute_post() {
	$.ajax({
		type : 'post',
		dataType : 'json',
		url : personAttribute_parameter.root,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			person : $('#person', '#content').val(),
			unique : $('#unique', '#content').val(),
			attributeList : splitValue($('#attributeList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			personAttribute_list_reload();
		} else {
			failure(data);
		}
	});
}

function personAttribute_edit(id) {
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>id:</td><td id="id"></td></tr>';
	str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
	str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
	str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
	str += '<tr><td>unqiue:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
	str += '<tr><td>attributeList:</td><td><textarea id="attributeList" style="width:95%;height:200px"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		personAttribute_put(id);
	});
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : personAttribute_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#id', '#content').html(data.data.id);
			$('#sequence', '#content').html(data.data.sequence);
			$('#name', '#content').val(data.data.name);
			$('#person', '#content').val(data.data.person);
			$('#unique', '#content').val(data.data.unique);
			$('#attributeList', '#content').val(data.data.attributeList).join(',');
		} else {
			failure(data);
		}
	});
}

function personAttribute_put(id) {
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : personAttribute_parameter.root + '/' + id,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : $('#name', '#content').val(),
			person : $('#person', '#content').val(),
			unique : $('#unique', '#content').val(),
			attributeList : splitValue($('#attributeList', '#content').val())
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			personAttribute_list_reload();
		} else {
			failure(data);
		}
	});

}

function personAttribute_delete(id) {
	$.ajax({
		type : 'delete',
		dataType : 'json',
		url : personAttribute_parameter.root + '/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			personAttribute_list_reload();
		} else {
			failure(data);
		}
	});
}

function personAttribute_query_grid(items) {
	var str = '<table border="1" width="100%">';
	str += '<tr><th>id</th><th>name</th><th>person</th><th>operate</th></tr>';
	$.each(items, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.person + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="personAttribute_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="personAttribute_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	});
	str += '</table>';
	return str;
}

function personAttribute_query_init() {
	var str = '<table border="1" width="100%">';
	str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
	str += '<tr><td colspan="2"><a href="#" id="withPerson">获取指定人员属性.</a></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#withPerson', '#content').click(function() {
		personAttribute_query_withPerson($('#query', '#content').val());
	});
}

function personAttribute_query_withPerson(id) {
	personAttribute_parameter.list_action = personAttribute_query_withPerson;
	personAttribute_parameter.list_action_parameter = id;
	$.ajax({
		type : 'get',
		dataType : 'json',
		url : personAttribute_parameter.root + '/list/person/' + id,
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			$('#content').html(personAttribute_query_grid(data.data));
		} else {
			failure(data);
		}
	});
}