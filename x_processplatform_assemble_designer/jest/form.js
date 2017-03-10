form_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function form_list_next(id) {
    var id = (id ? id : form_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/list/' + id + '/next/' + form_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		form_parameter.first = json.data[0].id;
		form_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		form_parameter.first = '(0)';
	    }
	    form_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function form_list_prev(id) {
    var id = (id ? id : form_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/list/' + id + '/prev/' + form_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (data.data.length > 0) {
		form_parameter.first = json.data[0].id;
		form_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		form_parameter.last = '(0)';
	    }
	    form_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function form_list_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="6">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">' + json.count + '</span></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>application</th><th>operate</th></tr>';
    if (json.data) {
	$.each(json.data, function(index, item) {
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
    }
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	form_list_next();
    });
    $('#prev').click(function() {
	form_list_prev();
    });
}

function form_query() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="listWithApplication">listWithApplication</a>&nbsp;<a href="#" id="listFormFieldWithApplication">listFormFieldWithApplication</a>&nbsp;<a href="#" id="listFormFieldWithForm">listFormFieldWithForm</a></td></tr>';
    str += '<tr><td>application id</td><td><input type="text" id="applicationId" style="width:95%"></td>';
    str += '<tr><td>form id</td><td><input type="text" id="formId" style="width:95%"></td>';
    str += '</tr></table>';
    $('#content').html(str);
    $('#listWithApplication').click(function() {
	form_listWithApplication($('#applicationId').val());
    });
    $('#listFormFieldWithApplication').click(function() {
	form_listFormFieldWithApplication($('#applicationId').val());
    });
    $('#listFormFieldWithForm').click(function() {
	form_listFormFieldWithForm($('#formId').val());
    });
}

function form_create_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td colspan="2"><textarea id="formFieldList" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	form_post();
    });
}

function form_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/form',
	data : JSON.stringify({
	    name : $('#name').val(),
	    application : $('#application').val(),
	    description : $('#description').val(),
	    data : $('#data').val(),
	    formFieldList : $('#formFieldList').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
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
    str += '<tr><td colspan="2"><textarea id="formFieldList" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put', '#content').click(function() {
	form_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#name').val(json.data.name);
	    $('#application').val(json.data.application);
	    $('#description').val(json.data.description);
	    $('#data').val(json.data.data);
	    $('#formFieldList').val(joinValue(json.data.formFieldList));
	    $('#id').html(json.data.id);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function form_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/form/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    application : $('#application').val(),
	    description : $('#description').val(),
	    data : $('#data').val(),
	    formFieldList : JSON.parse($('#formFieldList').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
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
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function form_listWithApplication(id) {
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
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function form_listFormFieldWithApplication(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/list/formfield/application/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function form_listFormFieldWithForm(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/form/list/' + id + '/formfield',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}