person_parameter = {
    list_action : null,
    list_action_parameter : null,
    first : '(0)',
    last : '(0)',
    count : 20
};

function person_list_reload() {
    if (person_parameter.list_action) {
	person_parameter.list_action.call(window, person_parameter.list_action_parameter);
    } else {
	person_list_next('(0)');
    }
}

function person_list_next(id) {
    var id = (id ? id : person_parameter.last);
    person_parameter.list_action = person_list_next;
    person_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/list/' + id + '/next/' + person_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		person_parameter.first = data.data[0].id;
		person_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		person_parameter.first = '(0)';
	    }
	    $('#content').html(person_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    person_list_init();
	} else {
	    failure(data);
	}
    });
}

function person_list_prev(id) {
    var id = (id ? id : person_parameter.first);
    person_parameter.list_action = person_list_prev;
    person_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/list/' + id + '/prev/' + person_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		person_parameter.first = data.data[0].id;
		person_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		person_parameter.last = '(0)';
	    }
	    $('#content').html(person_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    person_list_init();
	} else {
	    failure(data);
	}
    });
}

function person_list_grid(items) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="7">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>name</th><th>display</th><th>employee</th><th>mobile</th><th>operate</th></tr>';
    $.each(items, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.rank + '</td>';
	str += '<td>' + item.id + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.display + '</td>';
	str += '<td>' + item.employee + '</td>';
	str += '<td>' + item.mobile + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="person_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="person_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</table>';
    return str;
}

function person_list_init() {
    $('#next', '#content').click(function() {
	person_list_next();
    });
    $('#prev', '#content').click(function() {
	person_list_prev();
    });
}

function person_create() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>display:</td><td><input type="text" id="display" style="width:95%"/></td></tr>';
    str += '<tr><td>genderType:</td><td><select id="genderType"><option value="f">female</option><option value="m">male</option><option value="o">unknown</option></select></td></tr>';
    str += '<tr><td>employee:</td><td><input type="text" id="employee" style="width:95%"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="text" id="password" style="width:95%"/></td></tr>';
    str += '<tr><td>qq:</td><td><input type="text" id="qq" style="width:95%"/></td></tr>';
    str += '<tr><td>mail:</td><td><input type="text" id="mail" style="width:95%"/></td></tr>';
    str += '<tr><td>weixin:</td><td><input type="text" id="weixin" style="width:95%"/></td></tr>';
    str += '<tr><td>weibo:</td><td><input type="text" id="weibo" style="width:95%"/></td></tr>';
    str += '<tr><td>mobile:</td><td><input type="text" id="mobile" style="width:95%"/></td></tr>';
    str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post', '#content').click(function() {
	person_post();
    });
}

function person_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : person_parameter.root,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name', '#content').val(),
	    genderType : $('#genderType', '#content').val(),
	    display : $('#display', '#content').val(),
	    employee : $('#employee', '#content').val(),
	    password : $('#password', '#content').val(),
	    qq : $('#qq', '#content').val(),
	    mail : $('#mail', '#content').val(),
	    weixin : $('#weixin', '#content').val(),
	    weibo : $('#weibo', '#content').val(),
	    mobile : $('#mobile', '#content').val(),
	    unique : $('#unique', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    person_list_reload();
	} else {
	    failure(data);
	}
    });
}

function person_edit(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id"></td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
    str += '<tr><td>name:</td><td><input type="text"  id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>genderType:</td><td><select id="genderType"><option value="f">female</option><option value="m">male</option><option value="o">unknown</option></select></td></tr>';
    str += '<tr><td>display:</td><td><input type="text" id="display" style="width:95%"/></td></tr>';
    str += '<tr><td>employee:</td><td><input type="text" id="employee" style="width:95%"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="text" id="password" style="width:95%"/></td></tr>';
    str += '<tr><td>qq:</td><td><input type="text" id="qq" style="width:95%"/></td></tr>';
    str += '<tr><td>mail:</td><td><input type="text" id="mail" style="width:95%"/></td></tr>';
    str += '<tr><td>weixin:</td><td><input type="text" id="weixin" style="width:95%"/></td></tr>';
    str += '<tr><td>weibo:</td><td><input type="text" id="weibo" style="width:95%"/></td></tr>';
    str += '<tr><td>mobile:</td><td><input type="text" id="mobile" style="width:95%"/></td></tr>';
    str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put', '#content').click(function() {
	person_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#id', '#content').html(data.data.id);
	    $('#sequence', '#content').html(data.data.sequence);
	    $('#name', '#content').val(data.data.name);
	    $('#genderType', '#content').val(data.data.genderType);
	    $('#display', '#content').val(data.data.display);
	    $('#employee', '#content').val(data.data.employee);
	    $('#password', '#content').val(data.data.password);
	    $('#qq', '#content').val(data.data.qq);
	    $('#mail', '#content').val(data.data.mail);
	    $('#weixin', '#content').val(data.data.weixin);
	    $('#weibo', '#content').val(data.data.weibo);
	    $('#mobile', '#content').val(data.data.mobile);
	    $('#unique', '#content').val(data.data.unique);
	} else {
	    failure(data);
	}
    });
}

function person_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : person_parameter.root + '/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name', '#content').val(),
	    genderType : $('#genderType', '#content').val(),
	    display : $('#display', '#content').val(),
	    employee : $('#employee', '#content').val(),
	    password : $('#password', '#content').val(),
	    qq : $('#qq', '#content').val(),
	    mail : $('#mail', '#content').val(),
	    weixin : $('#weixin', '#content').val(),
	    weibo : $('#weibo', '#content').val(),
	    mobile : $('#mobile', '#content').val(),
	    unique : $('#unique', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    person_list_reload();
	} else {
	    failure(data);
	}
    });
}

function person_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : person_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    person_list_next('(0)');
	} else {
	    failure(data);
	}
    });
}

function person_query_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="subDirectWithGroup">获取群组的直接群组成员.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="subNestedWithGroup">获取群组的所有嵌套群组成员.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="pinyinInitial">根据首字母进行查找.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="like">进行模糊查找.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="likePinyin">根据拼音进行查找.</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#subDirectWithGroup', '#content').click(function() {
	person_query_subDirectWithGroup($('#query', '#content').val());
    });
    $('#subNestedWithGroup', '#content').click(function() {
	person_query_subNestedWithGroup($('#query', '#content').val());
    });
    $('#like', '#content').click(function() {
	person_query_like($('#query', '#content').val());
    });
    $('#pinyinInitial', '#content').click(function() {
	person_query_pinyinInitial($('#query', '#content').val());
    });
    $('#likePinyin', '#content').click(function() {
	person_query_likePinyin($('#query', '#content').val());
    });
}

function person_query_grid(items) {
    var str = '<table border="1" width="100%">';
    str += '<tr><th>id</th><th>name</th><th>display</th><th>employee</th><th>mobile</th><th>operate</th></tr>';
    $.each(items, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.id + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.display + '</td>';
	str += '<td>' + item.employee + '</td>';
	str += '<td>' + item.mobile + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="person_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="person_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</table>';
    return str;
}

function person_query_subDirectWithGroup(id) {
    person_parameter.list_action = person_query_subDirectWithGroup;
    person_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/list/group/' + id + '/sub/direct',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(person_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function person_query_subNestedWithGroup(id) {
    person_parameter.list_action = person_query_subNestedWithGroup;
    person_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/list/group/' + id + '/sub/nested',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(person_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function person_query_like(key) {
    person_parameter.list_action = person_query_like;
    person_parameter.list_action_parameter = key;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/list/like/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(person_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function person_query_pinyinInitial(key) {
    person_parameter.list_action = person_query_pinyinInitial;
    person_parameter.list_action_parameter = key;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/list/pinyininitial/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(person_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function person_query_likePinyin(key) {
    person_parameter.list_action = person_query_likePinyin;
    person_parameter.list_action_parameter = key;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : person_parameter.root + '/list/like/pinyin/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(person_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function person_setPassword_select() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" id="password"  style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put', '#content').click(function() {
	person_setPassword($('#name').val(), $('#password').val());
    });
}

function person_setPassword(name, password) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/person/' + name + '/set/password',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    value : password
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function person_icon_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id"  style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><form><input type="file" id="file" name="file" style="width:95%"/></form></td></tr>';
    str += '<tr><td colspan="2" id="result">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put', '#content').click(function() {
	application_icon_put($('#id').val());
    });
}

function application_icon_put(id) {
    var formData = new FormData($('form')[0]);
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : common_parameter.host + '/x_organization_assemble_control/servlet/person/' + id + '/icon',
	data : formData,
	contentType : false,
	cache : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true,
	success : function(data, status, req) {
	    $('#result', '#content').val(data);
	}
    });
}