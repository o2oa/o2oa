company_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function company_list_next(id) {
    $('#result').html('');
    var id = (id ? id : company_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/list/' + id + '/next/' + company_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		company_parameter.first = json.data[0].id;
		company_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		company_parameter.first = '(0)';
	    }
	    company_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_list_prev(id) {
    $('#result').html('');
    var id = (id ? id : company_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/list/' + id + '/prev/' + company_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		company_parameter.first = json.data[0].id;
		company_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		company_parameter.last = '(0)';
	    }
	    company_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_list_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="6">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">' + json.count + '</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>name</th><th>level</th><th>superior</th><th>operate</th></tr>';
    if (json.data) {
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.rank + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.level + '</td>';
	    str += '<td>' + item.superior + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="company_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="company_delete(\'' + item.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	company_list_next();
    });
    $('#prev').click(function() {
	company_list_prev();
    });
}

function company_list_top() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/list/top',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    company_query_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_create() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>superior:</td><td><input type="text" id="superior" style="width:95%"/></td></tr>';
    str += '<tr><td>unique:</td><td><input type="text" id="unique" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	company_post();
    });
}

function company_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/company',
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
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_edit(id) {
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
	company_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#sequence').html(json.data.sequence);
	    $('#level').html(json.data.level);
	    $('#name').val(json.data.name);
	    $('#superior').val(json.data.superior);
	    $('#unique').val(json.data.unique);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : +'../jaxrs/company/' + id,
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
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/company/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_query_init() {
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
	company_query_supDirect($('#query').val());
    });
    $('#supNested').click(function() {
	company_query_supNested($('#query').val());
    });
    $('#subDirect').click(function() {
	company_query_subDirect($('#query').val());
    });
    $('#subNested').click(function() {
	company_query_subNested($('#query').val());
    });
    $('#pinyinInitial').click(function() {
	company_query_pinyinInitial($('#query').val());
    });
    $('#like').click(function() {
	company_query_like($('#query').val());
    });
    $('#likePinyin').click(function() {
	company_query_likePinyin($('#query').val());
    });

}

function company_query_supDirect(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/' + id + '/sup/direct',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_query_supNested(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/list/' + id + '/sup/nested',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_query_subDirect(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url :   '../jaxrs/company/list/' + id + '/sub/direct',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_query_subNested(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/list/' + id + '/sub/nested',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_query_pinyinInitial(key) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url :  '../jaxrs/company/list/pinyininitial/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_query_like(key) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/list/like/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function company_query_likePinyin(key) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/company/list/like/pinyin/' + encodeURIComponent(key),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}
