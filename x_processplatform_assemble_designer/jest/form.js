form_parameter = {
    list_action : null,
    list_action_parameter : null,
    first : '(0)',
    last : '(0)',
    count : 20
};

function form_list_reload() {
    if (form_parameter.list_action) {
	form_parameter.list_action.call(window, form_parameter.list_action_parameter);
    } else {
	form_list_next('(0)');
    }
}

function form_list_next(id) {
    var id = (id ? id : form_parameter.last);
    form_parameter.list_action = form_list_next;
    form_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/list/' + id + '/next/' + form_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		form_parameter.first = data.data[0].id;
		form_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		form_parameter.first = '(0)';
	    }
	    $('#content').html(form_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    form_list_init();
	} else {
	    failure(data);
	}
    });
}

function form_list_prev(id) {
    var id = (id ? id : form_parameter.first);
    form_parameter.list_action = form_list_prev;
    form_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/list/' + id + '/prev/' + form_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		form_parameter.first = data.data[0].id;
		form_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		form_parameter.last = '(0)';
	    }
	    $('#content').html(form_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    form_list_init();
	} else {
	    failure(data);
	}
    });
}

function form_list_grid(items) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="4">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>application</th><th>operate</th></tr>';
    $.each(items, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.id + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.application + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="form_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="form_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</table>';
    return str;
}

function form_list_init() {
    $('#next', '#content').click(function() {
	form_list_next();
    });
    $('#prev', '#content').click(function() {
	form_list_prev();
    });
}

function form_query() {
    str = '<table border="1" width="100%"><tr>';
    str += '<td  rowspan="2">application id</td>';
    str += '<td><input type="text" id="id" style="width:95%"></td>';
    str += '</tr><tr>';
    str += '<td><button id="query">query</button></td>';
    str += '</tr></table>';
    $('#content').html(str);
    $('#query', '#content').click(function() {
	form_listWithApplication($('#id').val());
    });
}

function form_create() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post', '#content').click(function() {
	form_post();
    });
}

function form_post() {
    form_parameter.application = $('#application', '#content').val();
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/form',
	data : JSON.stringify({
	    name : $('#name', '#content').val(),
	    application : $('#application', '#content').val(),
	    description : $('#description', '#content').val(),
	    data : $('#data', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    form_list(form_parameter.application);
	} else {
	    failure(data);
	}
    });
}

function form_edit(id) {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>form category:</td><td><select id="formCategory"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence">&nbsp;</td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put', '#content').click(function() {
	form_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : +'../jaxrs/form/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#name', '#content').val(data.data.name);
	    $('#application', '#content').val(data.data.application);
	    $('#description', '#content').val(data.data.description);
	    $('#data', '#content').val(data.data.data);
	    $('#id', '#content').html(data.data.id);
	} else {
	    failure(data);
	}
    });
}

function form_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/form/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name', '#content').val(),
	    application : $('#application', '#content').val(),
	    description : $('#description', '#content').val(),
	    data : $('#data', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    form_list(form_parameter.application);
	} else {
	    failure(data);
	}
    });
}

function form_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/form/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    form_list(form_parameter.application);
	} else {
	    failure(data);
	}
    });
}

function form_listWithApplication(id) {
    form_parameter.application = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/application/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><th>id</th><th>name</th><th>application</th><th>operate</th></tr>';
	    $.each(data.data, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.application + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="form_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="form_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	    });
	    str += '</table>';
	    $('#content').html(str);
	} else {
	    failure(data);
	}
    });
}