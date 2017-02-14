identity_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function identity_create() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	identity_post();
    });
}

function identity_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/identity',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    person : $('#person').val(),
	    department : $('#department').val(),
	    unique : $('#unique').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_edit(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id"></td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	identity_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#sequence').html(json.data.sequence);
	    $('#name').val(json.data.name);
	    $('#person').val(json.data.person);
	    $('#department').val(json.data.department);
	    $('#unique').val(json.data.unique);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/identity/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    person : $('#person').val(),
	    department : $('#department').val(),
	    unique : $('#unique').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/identity/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_list_next(id) {
    var id = (id ? id : identity_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/list/' + id + '/next/' + identity_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		identity_parameter.first = json.data[0].id;
		identity_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		identity_parameter.first = '(0)';
	    }
	    identity_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_list_prev(id) {
    var id = (id ? id : identity_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/list/' + id + '/prev/' + identity_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		identity_parameter.first = json.data[0].id;
		identity_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		identity_parameter.last = '(0)';
	    }
	    identity_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_list_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="6">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">' + json.count + '</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>name</th><th>person</th><th>department</th><th>operate</th></tr>';
    if (json.data) {
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.rank + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.person + '</td>';
	    str += '<td>' + item.department + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="identity_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="identity_delete(\'' + item.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	identity_list_next();
    });
    $('#prev').click(function() {
	identity_list_prev();
    });
}

function identity_query_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="withPerson">获取个人拥有的身份.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="withDepartment">获取部门的成员的身份.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="pinyinInitial">根据首字母进行查找.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="like">进行模糊查找.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="likePinyin">根据拼音进行查找.</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#withPerson').click(function() {
	identity_query_withPerson($('#query').val());
    });
    $('#withDepartment').click(function() {
	identity_query_withDepartment($('#query').val());
    });
    $('#like').click(function() {
	identity_query_like($('#query').val());
    });
    $('#pinyinInitial').click(function() {
	identity_query_pinyinInitial($('#query').val());
    });
    $('#likePinyin').click(function() {
	identity_query_likePinyin($('#query').val());
    });
}

function identity_query_likeWithSubCompanySubDepartment_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
    str += '<tr><td>companyList:</td><td><textarea id="companyList" style="width:95%"/></td></tr>';
    str += '<tr><td>departmentList:</td><td><textarea id="departmentList" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="likeWithSubCompanySubDepartment">查找.</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#likeWithSubCompanySubDepartment').click(function() {
	identity_query_likeWithSubCompanySubDepartment();
    });
}

function identity_query_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<tr><th>id</th><th>name</th><th>person</th><th>department</th><th>operate</th></tr>';
    if (json.data) {
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.person + '</td>';
	    str += '<td>' + item.department + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="identity_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="identity_delete(\'' + item.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    str += '</table>';
    $('#content').html(str);
}

function identity_query_withPerson(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/list/person/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_query_withDepartment(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/list/department/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_query_like(key) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/list/like/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_query_pinyinInitial(key) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/list/pinyininitial/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_query_likePinyin(key) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/identity/list/like/pinyin/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function identity_query_likeWithSubCompanySubDepartment() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/identity/list/company/sub/nest/department/sub/nest/like/' + encodeURIComponent($('#query').val()),
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    companyList : splitValue($('#companyList').val()),
	    departmentList : splitValue($('#departmentList').val())
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}
