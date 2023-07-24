portal_parameter = {};

function portal_list_portalSummary() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/portal/list/summary',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_list_portalWithCategory_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="listSummary">listSummary</a>&nbsp;&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>portalCategory:</td><td><input type="text" id="portalCategory" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#listSummary').click(function() {
	portal_list_portalSummaryWithCategory($('#portalCategory').val());
    });
    $('#list').click(function() {
	portal_list_portalWithCategory($('#portalCategory').val());
    });
}

function portal_list_portalSummaryWithCategory(portalCategory) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/portal/list/summary/portalcategory/' + portalCategory,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_list_portalWithCategory(portalCategory) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/portal/list/portalcategory/' + portalCategory,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/portal/list',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	portal_list_grid(json);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_list_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><th>name</th><th>id</th><th>operate</th></tr></thead>';
    str += '<tbody>';
    if (json.data) {
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="portal_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="portal_delete(\'' + item.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	    str += '<tr>';
	    str += '<td colspan="3">';
	    str += '<a href="#" onclick="menu_list_init(\'' + item.id + '\')">menu</a>&nbsp;&nbsp;';
	    str += '<a href="#" onclick="page_list_init(\'' + item.id + '\')">page</a>&nbsp;&nbsp;';
	    str += '<a href="#" onclick="source_list_init(\'' + item.id + '\')">source</a>&nbsp;&nbsp;';
	    str += '<a href="#" onclick="script_list_init(\'' + item.id + '\')">script</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
}

function portal_create_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="post">post</a></td></tr></thead>';
    str += '<tbody>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>portalCategory:</td><td><input type="text" id="portalCategory" style="width:95%"/></td></tr>';
    str += '<tr><td>controllerList:</td><td><textarea id="controllerList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableIdentityList:</td><td><textarea id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableDepartmentList:</td><td><textarea id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableCompanyList:</td><td><textarea id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>firstPage:</td><td><input type="text" id="firstPage" style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><textarea id="icon" style="width:95%"/></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#post').click(function() {
	portal_post();
    });
}

function portal_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/portal',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    portalCategory : $('#portalCategory').val(),
	    controllerList : splitValue($('#controllerList').val()),
	    availableIdentityList : splitValue($('#availableIdentityList').val()),
	    availableDepartmentList : splitValue($('#availableDepartmentList').val()),
	    availableCompanyList : splitValue($('#availableCompanyList').val()),
	    firstPage : $('#firstPage').val(),
	    icon : $('#icon').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_edit(id) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="put">put</a></td></tr></thead>';
    str += '<tbody>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>updateTime:</td><td id="updateTime">&nbsp;</td></tr>';
    str += '<tr><td>creatorPerson:</td><td id="creatorPerson">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdatePerson:</td><td id="lastUpdatePerson">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdateTime:</td><td id="lastUpdateTime">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>portalCategory:</td><td><input type="text" id="portalCategory" style="width:95%"/></td></tr>';
    str += '<tr><td>controllerList:</td><td><textarea id="controllerList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableIdentityList:</td><td><textarea id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableDepartmentList:</td><td><textarea id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableCompanyList:</td><td><textarea id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>firstPage:</td><td><input type="text" id="firstPage" style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><textarea id="icon" style="width:95%"/></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	portal_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/portal/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#id').html(json.data.id);
	$('#createTime').html(json.data.createTime);
	$('#updateTime').html(json.data.updateTime);
	$('#creatorPerson').html(json.data.creatorPerson);
	$('#lastUpdatePerson').html(json.data.lastUpdatePerson);
	$('#lastUpdateTime').html(json.data.lastUpdateTime);
	$('#name').val(json.data.name);
	$('#alias').val(json.data.alias);
	$('#description').val(json.data.description);
	$('#portalCategory').val(json.data.portalCategory);
	$('#controllerList').val(joinValue(json.data.controllerList));
	$('#availableIdentityList').val(joinValue(json.data.availableIdentityList));
	$('#availableDepartmentList').val(joinValue(json.data.availableDepartmentList));
	$('#availableCompanyList').val(joinValue(json.data.availableCompanyList));
	$('#firstPage').val(json.data.firstPage);
	$('#icon').val(json.data.icon);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/portal/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    portalCategory : $('#portalCategory').val(),
	    controllerList : splitValue($('#controllerList').val()),
	    availableIdentityList : splitValue($('#availableIdentityList').val()),
	    availableDepartmentList : splitValue($('#availableDepartmentList').val()),
	    availableCompanyList : splitValue($('#availableCompanyList').val()),
	    firstPage : $('#firstPage').val(),
	    icon : $('#icon').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/portal/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function portal_icon_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>portal:</td><td><input type="text" id="id"  style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><form><input type="file" id="file" name="file" style="width:95%"/></form></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	portal_icon($('#id').val());
    });
}

function portal_icon(id) {
    var formData = new FormData($('form')[0]);
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../servlet/portal/' + id + '/icon',
	data : formData,
	contentType : false,
	cache : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true,
	success : function(json) {
	    $('#result').html(JSON.stringify(json, null, 4));
	}
    });
}