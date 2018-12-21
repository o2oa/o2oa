templatePage_parameter = {};

function templatePage_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/templatepage/list',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	templatePage_list_grid(json);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templatePage_listCategory() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/templatepage/list/category',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templatePage_listWithCategory_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>category:</td><td><input type="text" id="category" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	templatePage_listWithCategory($('#category').val());
    });
}

function templatePage_listWithCategory(category) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/templatepage/list/category',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    category : category
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templatePage_list_grid(json) {
    if (json.data) {
	var str = '<table border="1" width="100%">';
	str += '<thead><tr><th>name</th><th>id</th><th>operate</th></tr></thead>';
	str += '<tbody>';
	$.each(json.data, function(k, v) {
	    str += '<tr>';
	    str += '<td colspan="3">' + k + '</td>';
	    str += '</tr>';
	    $.each(v, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.id + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="templatePage_get(\'' + item.id + '\')">get</a>&nbsp;';
		str += '<a href="#" onclick="templatePage_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	    });
	});
	str += '</tbody></table>';
    }
    $('#content').html(str);
}

function templatePage_create_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="post">post</a></td></tr></thead>';
    str += '<tbody>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>category:</td><td><input type="text" id="category" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>availableIdentityList:</td><td><textarea id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableDepartmentList:</td><td><textarea id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableCompanyList:</td><td><textarea id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>controllerList:</td><td><textarea id="controllerList" style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><textarea id="icon" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td>preview:</td><td><textarea id="preview" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td>data:</td><td><input type="text" id="data" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td>mobileData:</td><td><textarea id="mobileData" style="width:95%;height:500px"/></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	templatePage_post();
    });
}

function templatePage_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/templatepage',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    category : $('#category').val(),
	    description : $('#description').val(),
	    availableIdentityList : splitValue($('#availableIdentityList').val()),
	    availableDepartmentList : splitValue($('#availableDepartmentList').val()),
	    availableCompanyList : splitValue($('#availableCompanyList').val()),
	    controllerList : splitValue($('#controllerList').val()),
	    icon : $('#icon').val(),
	    preview : $('#preview').val(),
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

function templatePage_get(id) {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/templatepage/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function templatePage_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/templatepage/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    category : $('#category').val(),
	    description : $('#description').val(),
	    availableIdentityList : splitValue($('#availableIdentityList').val()),
	    availableDepartmentList : splitValue($('#availableDepartmentList').val()),
	    availableCompanyList : splitValue($('#availableCompanyList').val()),
	    controllerList : splitValue($('#controllerList').val()),
	    icon : $('#icon').val(),
	    preview : $('#preview').val(),
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

function templatePage_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/templatepage/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}