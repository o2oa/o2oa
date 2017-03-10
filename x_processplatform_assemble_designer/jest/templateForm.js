templateForm_parameter = {};

function templateForm_list_category() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/templateform/list/category',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templateForm_list() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/templateform/list',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templateForm_init() {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="listWithCategory">listWithCategory</a>&nbsp;<a href="#" id="get">get</a>&nbsp;<a href="#" id="concreteFromForm">concreteFromForm</a>&nbsp;<a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id"  style="width:95%"/></td></tr>';
    str += '<tr><td>formId:</td><td><input type="text" id="formId"  style="width:95%"/></td></tr>';
    str += '<tr><td>category:</td><td><input type="text" id="category"  style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#listWithCategory').click(function() {
	templateForm_listWithCategory($('#category').val());
    });
    $('#get').click(function() {
	templateForm_get($('#id').val());
    });
    $('#concreteFromForm').click(function() {
	templateForm_concrete($('#formId').val(), $('#category').val(), $('#name').val());
    });
    $('#delete').click(function() {
	templateForm_delete($('#id').val());
    });
}

function templateForm_listWithCategory(category) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/templateform/list/category',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    category : category
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templateForm_get(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/templateform/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templateForm_create_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>category:</td><td><input type="text" id="category" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><textarea  id="icon" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td colspan="2">mobileData:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="mobileData" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	templateForm_post();
    });
}

function templateForm_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/templateform',
	data : JSON.stringify({
	    name : $('#name').val(),
	    category : $('#category').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    icon : $('#icon').val(),
	    data : $('#data').val(),
	    mobileData : $('#mobileData').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templateForm_delete(id) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/templateform/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}