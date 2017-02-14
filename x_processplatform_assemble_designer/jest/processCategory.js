processCategory_parameter = {
    root : '../jaxrs/processCategory',
    list_action : null,
    list_action_parameter : null,
    first : '(0)',
    last : '(0)',
    count : 20
};

function processCategory_list_reload() {
    if (processCategory_parameter.list_action) {
	processCategory_parameter.list_action.call(window, processCategory_parameter.list_action_parameter);
    } else {
	processCategory_list_next('(0)');
    }
}

function processCategory_create() {
    str = '<div><table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>available mode:</td><td><select id="availableMode"><option value ="all">all</option><option value ="authenticated">authenticated</option><option value ="assign">assign</option></select></td></tr>';
    str += '<tr><td>available identities:</td><td><textarea  id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>available departments:</td><td><textarea  id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>available companies:</td><td><textarea  id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '</table></div>';
    $('#content').html(str);
    $('#availableMode', '#content').val('authenticated');
    $('#post', '#content').click(function() {
	processCategory_post();
    });
}

function processCategory_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : processCategory_parameter.root,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name', '#content').val(),
	    availableMode : $('#availableMode', '#content').val(),
	    availableIdentityList : $('#availableIdentityList', '#content').val().split(','),
	    availableDepartmentList : $('#availableDepartmentList', '#content').val().split(','),
	    availableCompanyList : $('#availableCompanyList', '#content').val().split(','),
	    description : $('#description', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    processCategory_list_reload();
	} else {
	    failure(data);
	}
    });
}

function processCategory_edit(id) {
    str = '<div><table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>available mode:</td><td><select id="availableMode"><option value ="all">all</option><option value ="authenticated">authenticated</option><option value ="assign">assign</option></select></td></tr>';
    str += '<tr><td>available identities:</td><td><textarea  id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>available departments:</td><td><textarea  id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>available companies:</td><td><textarea  id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence">&nbsp;</td></tr>';
    str += '</table></div>';
    $('#content').html(str);
    $('#put', '#content').click(function() {
	processCategory_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : processCategory_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#name', '#content').val(data.data.name);
	    $('#availableMode', '#content').val(data.data.availableMode);
	    $('#availableIdentityList', '#content').val(data.data.availableIdentityList.join(','));
	    $('#availableDepartmentList', '#content').val(data.data.availableDepartmentList.join(','));
	    $('#availableCompanyList', '#content').val(data.data.availableCompanyList.join(','));
	    $('#description', '#content').val(data.data.description);
	    $('#id', '#content').html(data.data.id);
	    $('#sequence', '#content').html(data.data.sequence);
	} else {
	    failure(data);
	}
    });
}

function processCategory_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : processCategory_parameter.root + '/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name', '#content').val(),
	    availableMode : $('#availableMode', '#content').val(),
	    availableIdentityList : $('#availableIdentityList', '#content').val().split(','),
	    availableDepartmentList : $('#availableDepartmentList', '#content').val().split(','),
	    availableCompanyList : $('#availableCompanyList', '#content').val().split(','),
	    description : $('#description', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    processCategory_list_reload();
	} else {
	    failure(data);
	}
    });
}

function processCategory_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : processCategory_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    processCategory_list_next('(0)');
	} else {
	    failure(data);
	}
    });
}

function processCategory_list_next(id) {
    var id = (id ? id : processCategory_parameter.last);
    processCategory_parameter.list_action = processCategory_list_next;
    processCategory_parameter.list_action_parameter = id;
    processCategory_list_init();
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : processCategory_parameter.root + '/list/' + id + '/next/' + processCategory_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		processCategory_parameter.first = data.data[0].id;
		processCategory_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		processCategory_parameter.first = '(0)';
	    }
	    $('#content').html(processCategory_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    processCategory_list_init();
	} else {
	    failure(data);
	}
    });
}

function processCategory_list_prev(id) {
    var id = (id ? id : processCategory_parameter.first);
    processCategory_parameter.list_action = processCategory_list_prev;
    processCategory_parameter.list_action_parameter = id;
    processCategory_list_init();
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : processCategory_parameter.root + '/list/' + (id ? id : processCategory_parameter.first) + '/prev/' + processCategory_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		processCategory_parameter.first = data.data[0].id;
		processCategory_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		processCategory_parameter.last = '(0)';
	    }
	    $('#content').html(processCategory_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    processCategory_list_init();
	} else {
	    failure(data);
	}
    });

}

function processCategory_list_grid(items) {
    var str = '<div><table border="1" width="100%"><tbody>';
    str += '<tr><td colspan="6">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><td>rank</td><td>name</td><td>availableMode</td><td>processCount</td><td>operate</td></tr>';
    $.each(items, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.rank + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.availableMode + '</td>';
	str += '<td>' + item.processCount + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="processCategory_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="processCategory_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</tbody></table></div>';
    return str;
}

function processCategory_list_init() {
    $('#next', '#content').click(function() {
	processCategory_list_next();
    });
    $('#prev', '#content').click(function() {
	processCategory_list_prev();
    });
}
