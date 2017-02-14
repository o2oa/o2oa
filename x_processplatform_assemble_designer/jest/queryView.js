queryView_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function queryView_list_next(id) {
    $('#result').html('');
    $('#content').html('');
    var id = (id ? id : queryView_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/queryview/list/' + id + '/next/' + queryView_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		queryView_parameter.first = json.data[0].id;
		queryView_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		queryView_parameter.first = '(0)';
	    }
	    $('#content').html(queryView_list_grid(json));
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_list_prev(id) {
    $('#result').html('');
    $('#content').html('');
    var id = (id ? id : queryView_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/queryview/list/' + id + '/prev/' + queryView_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		queryView_parameter.first = json.data[0].id;
		queryView_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		queryView_parameter.last = '(0)';
	    }
	    queryView_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_list_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="5">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">' + json.count + '</span></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>alias</th><th>application</th><th>operate</th></tr>';
    $.each(json.data, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.id + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.alias + '</td>';
	str += '<td>' + item.application + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="queryView_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="queryView_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	queryView_list_next();
    });
    $('#prev').click(function() {
	queryView_list_prev();
    });

}

function queryView_simulate_init() {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="simulate">simulate</a>&nbsp;<a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>filter:</td><td><input type="text" id="filter" style="width:95%"/></td></tr>';
    str += '<tr><td>where:</td><td><input type="text" id="where" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#simulate').click(function() {
	queryView_simulate($('#id').val());
    });
    $('#flag').click(function() {
	queryView_get($('#id').val());
    });
}

function queryView_simulate(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/queryview/' + id + '/simulate?' + Math.random(),
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    filter : $('#filter').val(),
	    where : $('#where').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_create() {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description"  style="width:95%"/></td></tr>';
    str += '<tr><td>allowPersonList:</td><td><textarea  id="allowPersonList" style="width:95%"/></td></tr>';
    str += '<tr><td>allowIdentityList:</td><td><textarea  id="allowIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>allowDepartmentList:</td><td><textarea  id="allowDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>allowCompanyList:</td><td><textarea  id="allowCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	queryView_post();
    });
}

function queryView_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/queryview',
	data : JSON.stringify({
	    application : $('#application').val(),
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    allowPersonList : splitValue($('#allowPersonList').val()),
	    allowIdentityList : splitValue($('#allowIdentityList').val()),
	    allowDepartmentList : splitValue($('#allowDepartmentList').val()),
	    allowCompanyList : splitValue($('#allowCompanyList').val()),
	    data : $('#data').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_edit(id) {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>updateTime:</td><td id="updateTime">&nbsp;</td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence">&nbsp;</td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdatePerson:</td><td id="lastUpdatePerson">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdateTime:</td><td id="lastUpdateTime">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description"  style="width:95%"/></td></tr>';
    str += '<tr><td>allowPersonList:</td><td><textarea  id="allowPersonList" style="width:95%"/></td></tr>';
    str += '<tr><td>allowIdentityList:</td><td><textarea  id="allowIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>allowDepartmentList:</td><td><textarea  id="allowDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>allowCompanyList:</td><td><textarea  id="allowCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	queryView_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/queryview/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#createTime').html(json.data.createTime);
	    $('#updateTime').html(json.data.updateTime);
	    $('#sequence').html(json.data.sequence);
	    $('#id').html(json.data.id);
	    $('#lastUpdatePerson').html(json.data.lastUpdatePerson);
	    $('#lastUpdateTime').html(json.data.lastUpdateTime);
	    $('#name').val(json.data.name);
	    $('#application').val(json.data.application);
	    $('#description').val(json.data.description);
	    $('#allowPersonList').val(joinValue(json.data.allowPersonList));
	    $('#allowIdentityList').val(joinValue(json.data.allowIdentityList));
	    $('#allowDepartmentList').val(joinValue(json.data.allowDepartmentList));
	    $('#allowCompanyList').val(joinValue(json.data.allowCompanyList));
	    $('#data').val(json.data.data);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/queryview/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    application : $('#application').val(),
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    allowPersonList : splitValue($('#allowPersonList').val()),
	    allowIdentityList : splitValue($('#allowIdentityList').val()),
	    allowDepartmentList : splitValue($('#allowDepartmentList').val()),
	    allowCompanyList : splitValue($('#allowCompanyList').val()),
	    data : $('#data').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/queryview/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_get(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/queryview/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryView_listWithApplication_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>application flag:</td><td><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2" id="gird">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	queryView_listWithApplication($('#applicationFlag').val());
    });
}

function queryView_listWithApplication(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/queryview/list/application/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><th>id</th><th>name</th><th>alias</th><th>application</th><th>operate</th></tr>';
	    $.each(json.data, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.alias + '</td>';
		str += '<td>' + item.application + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="queryView_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="queryView_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	    });
	    str += '</table>';
	    $('#gird').html(str);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}