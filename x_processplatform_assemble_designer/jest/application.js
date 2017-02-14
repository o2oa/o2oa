application_parameter = {};

function application_create() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>applicationCategory:</td><td><input type="text"  id="applicationCategory" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description"  style="width:95%"/></td></tr>';
    str += '<tr><td>availableIdentityList:</td><td><textarea  id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableDepartmentList:</td><td><textarea  id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableCompanyList:</td><td><textarea  id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>controllerList:</td><td><textarea  id="controllerList" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	application_post();
    });
}

function application_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/application',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    availableIdentityList : $('#availableIdentityList').val().split(','),
	    availableDepartmentList : $('#availableDepartmentList').val().split(','),
	    availableCompanyList : $('#availableCompanyList').val().split(','),
	    applicationCategory : $('#applicationCategory').val(),
	    controllerList : $('#controllerList').val().split(',')
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function application_edit(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>creatorPerson:</td><td id="creatorPerson">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdateTime:</td><td id="lastUpdateTime">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdatePerson:</td><td id="lastUpdatePerson">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>applicationCategory:</td><td><input type="text"  id="applicationCategory" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description"  style="width:95%"/></td></tr>';
    str += '<tr><td>availableIdentityList:</td><td><textarea  id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableDepartmentList:</td><td><textarea  id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableCompanyList:</td><td><textarea  id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>controllerList:</td><td><textarea  id="controllerList" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	application_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/application/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#createTime').html(json.data.createTime);
	    $('#creatorPerson').html(json.data.creatorPerson);
	    $('#lastUpdateTime').html(json.data.lastUpdateTime);
	    $('#lastUpdatePerson').html(json.data.lastUpdatePerson);
	    $('#name').val(json.data.name);
	    $('#alias').val(json.data.alias);
	    $('#applicationCategory').val(json.data.applicationCategory);
	    $('#description').val(json.data.description);
	    $('#availableIdentityList').val(joinValue(json.data.availableIdentityList, ','));
	    $('#availableDepartmentList').val(joinValue(json.data.availableDepartmentList, ','));
	    $('#availableCompanyList').val(joinValue(json.data.availableCompanyList, ','));
	    $('#controllerList').val(joinValue(json.data.controllerList, ','));
	}
    });
}

function application_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/application/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    availableIdentityList : splitValue($('#availableIdentityList').val()),
	    availableDepartmentList : splitValue($('#availableDepartmentList').val()),
	    availableCompanyList : splitValue($('#availableCompanyList').val()),
	    applicationCategory : $('#applicationCategory').val(),
	    controllerList : $('#controllerList').val().split(',')
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function application_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/application/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function application_list_summary() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/application/list/summary',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    var str = '<table border="1" width="100%"><tbody>';
	    str += '<tr><th>id</th><th>process</th><th>form</th><th>icon</th><th>name</th><th>operate</th><th>process</th><th>form</th><th>script</th><th>dict</th></tr>';
	    $.each(json.data, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.processList.length + '</td>';
		str += '<td>' + item.formList.length + '</td>';
		if (item.icon && item.icon != '') {
		    str += '<td><img src="data:image/png;base64,' + item.icon + '"/></td>';
		} else {
		    str += '<td>&nbsp;</td>';
		}
		str += '<td>' + item.name + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="application_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="application_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '<td>';
		str += '<a href="#" onclick="process_listWithApplication(\'' + item.id + '\')">list</a>&nbsp;';
		str += '</td>';
		str += '<td>';
		str += '<a href="#" onclick="form_listWithApplication(\'' + item.id + '\')">list</a>&nbsp;';
		str += '</td>';
		str += '<td>';
		str += '<a href="#" onclick="script_listWithApplication(\'' + item.id + '\')">list</a>&nbsp;';
		str += '</td>';
		str += '<td>';
		str += '<a href="#" onclick="applicationDict_listWithApplication(\'' + item.id + '\')">list</a>&nbsp;';
		str += '</td>';
		str += '</tr>';
	    });
	    str += '</tbody></table>';
	    $('#content').html(str);
	}
    });
}

function application_icon_update() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>application id:</td><td><input type="text" id="id"  style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><form><input type="file" id="file" name="file" style="width:95%"/></form></td></tr>';
    str += '<tr><td colspan="2" id="result">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	application_icon_post($('#id').val());
    });
}

function application_icon_post(id) {
    var formData = new FormData($('form')[0]);
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../servlet/application/' + id + '/icon',
	data : formData,
	contentType : false,
	cache : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true,
	success : function(result) {
	    $('#result').html(JSON.stringify(json, null, 4));
	}
    });
}
