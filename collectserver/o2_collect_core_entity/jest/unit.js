unit_parameter = {
    list_action : null,
    list_action_parameter : null,
    first : '(0)',
    last : '(0)',
    count : 50
};

function unit_list_reload() {
    if (unit_parameter.list_action) {
	unit_parameter.list_action.call(window, unit_parameter.list_action_parameter);
    } else {
	unit_list_next('(0)');
    }
}

function unit_list_next(id) {
    var id = (id ? id : unit_parameter.first);
    unit_parameter.list_action = unit_list_next;
    unit_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : +'../jaxrs/unit/list/' + id + '/next/' + unit_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		unit_parameter.first = data.data[0].id;
		unit_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		unit_parameter.first = '(0)';
	    }
	    $('#content').html(unit_list_grid(data.data));
	    $('#total').html(data.count);
	    unit_list_init();
	} else {
	    failure(data);
	}
    });
}

function unit_list_prev(id) {
    var id = (id ? id : unit_parameter.last);
    unit_parameter.list_action = unit_list_prev;
    unit_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/list/' + id + '/prev/' + unit_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data && data.data.length > 0) {
		unit_parameter.first = data.data[0].id;
		unit_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		unit_parameter.last = '(0)';
	    }
	    unit_list_grid(data);
	    $('#next').click(function() {
		unit_list_next(unit_parameter.last);
	    });
	    $('#prev').click(function() {
		unit_list_prev(unit_parameter.first);
	    });
	} else {
	    failure(data);
	}
    });
}

function unit_list_grid(data) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="6">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span>' + data.count + '</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>name</th><th>level</th><th>superior</th><th>operate</th></tr>';
    if (data.data && data.data.length > 0) {
	$.each(data.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.rank + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.level + '</td>';
	    str += '<td>' + item.superior + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="unit_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="unit_delete(\'' + item.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    str += '</table>';
    $('#content').html(str);
    return str;
}

function unit_create() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" id="password" style="width:95%"/></td></tr>';
    str += '<tr><td>confirmPassword:</td><td><input type="password" id="confirmPassword" style="width:95%"/></td></tr>';
    str += '<tr><td>phone:</td><td><input type="text" id="phone" style="width:95%"/></td></tr>';
    str += '<tr><td>send code:</td><td><button id="sendCode">send code</button></td></tr>';
    str += '<tr><td>code:</td><td><input type="text" id="code" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#sendCode').click(function() {
	smsCode_send($('#phone').val(), $('#name').val());
    });
    $('#post').click(function() {
	unit_post();
    });
}

function unit_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : unit_parameter.root,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    superior : $('#superior').val(),
	    unique : $('#unique').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    unit_list_reload();
	} else {
	    failure(data);
	}
    });
}

function unit_edit(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id"></td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence"></td></tr>';
    str += '<tr><td>level:</td><td id="level"></td></tr>';
    str += '<tr><td>name:</td><td><input type="text"  id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>superior:</td><td><input type="text" id="superior" style="width:95%"/></td></tr>';
    str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	unit_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#id').html(data.data.id);
	    $('#sequence').html(data.data.sequence);
	    $('#level').html(data.data.level);
	    $('#name').val(data.data.name);
	    $('#superior').val(data.data.superior);
	    $('#unique').val(data.data.unique);
	} else {
	    failure(data);
	}
    });
}

function unit_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : unit_parameter.root + '/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    superior : $('#superior').val(),
	    unique : $('#unique').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    unit_list_reload();
	} else {
	    failure(data);
	}
    });
}

function unit_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : unit_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    unit_list_reload();
	} else {
	    failure(data);
	}
    });
}

function unit_query_grid(items) {
    var str = '<table border="1" width="100%">';
    str += '<tr><th>id</th><th>name</th><th>level</th><th>superior</th><th>operate</th></tr>';
    $.each(items, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.id + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.level + '</td>';
	str += '<td>' + item.superior + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="unit_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="unit_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</table>';
    return str;
}

function unit_query_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="supDirect">查找公司的直接上级公司.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="supNested">查找所有嵌套的上级公司.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="subDirect">查找公司的直接下属公司.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="subNested">查找所有嵌套的下属公司.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="pinyinInitial">进行首字母查找.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="like">进行模糊查找.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="likePinyin">进行拼音查找.</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#supDirect').click(function() {
	unit_query_supDirect($('#query').val());
    });
    $('#supNested').click(function() {
	unit_query_supNested($('#query').val());
    });
    $('#subDirect').click(function() {
	unit_query_subDirect($('#query').val());
    });
    $('#subNested').click(function() {
	unit_query_subNested($('#query').val());
    });
    $('#pinyinInitial').click(function() {
	unit_query_pinyinInitial($('#query').val());
    });
    $('#like').click(function() {
	unit_query_like($('#query').val());
    });
    $('#likePinyin').click(function() {
	unit_query_likePinyin($('#query').val());
    });

}

function unit_query_supDirect(id) {
    unit_parameter.list_action = unit_query_supDirect;
    unit_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/' + id + '/sup/direct',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data) {
		$('#content').html(unit_query_grid(new Array(data.data)));
	    } else {
		$('#content').html(unit_query_grid(new Array()));
	    }
	} else {
	    failure(data);
	}
    });
}

function unit_query_supNested(id) {
    unit_parameter.list_action = unit_query_supNested;
    unit_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/list/' + id + '/sup/nested',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(unit_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function unit_query_subDirect(id) {
    unit_parameter.list_action = unit_query_subDirect;
    unit_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/list/' + id + '/sub/direct',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(unit_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function unit_query_subNested(id) {
    unit_parameter.list_action = unit_query_subNested;
    unit_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/list/' + id + '/sub/nested',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(unit_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function unit_query_pinyinInitial(key) {
    unit_parameter.list_action = unit_query_pinyinInitial;
    unit_parameter.list_action_parameter = key;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/list/pinyininitial/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(unit_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function unit_query_like(key) {
    unit_parameter.list_action = unit_query_like;
    unit_parameter.list_action_parameter = key;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/list/like/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(unit_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}

function unit_query_likePinyin(key) {
    unit_parameter.list_action = unit_query_likePinyin;
    unit_parameter.list_action_parameter = key;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : unit_parameter.root + '/list/like/pinyin/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#content').html(unit_query_grid(data.data));
	} else {
	    failure(data);
	}
    });
}
