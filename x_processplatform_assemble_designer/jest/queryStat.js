queryStat_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function queryStat_list_next(id) {
    $('#result').html('');
    $('#content').html('');
    var id = (id ? id : queryStat_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/querystat/list/' + id + '/next/' + queryStat_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		queryStat_parameter.first = json.data[0].id;
		queryStat_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		queryStat_parameter.first = '(0)';
	    }
	    $('#content').html(queryStat_list_grid(json));
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_list_prev(id) {
    $('#result').html('');
    $('#content').html('');
    var id = (id ? id : queryStat_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/querystat/list/' + id + '/prev/' + queryStat_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		queryStat_parameter.first = json.data[0].id;
		queryStat_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		queryStat_parameter.last = '(0)';
	    }
	    queryStat_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_list_grid(json) {
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
	str += '<a href="#" onclick="queryStat_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="queryStat_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	queryStat_list_next();
    });
    $('#prev').click(function() {
	queryStat_list_prev();
    });

}

function queryStat_simulate_init() {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="simulate">simulate</a>&nbsp;<a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>date:</td><td><textarea id="date" style="width:95%;height:300px"/></td></tr>';
    str += '<tr><td colspan="2">sample</td></tr>';
    str += '<tr><td colspan="2">';
    str += '{<br/>';
    str += '"dateRangeType":"",<br/>';
    str += '//year, season, month, week, date, range, none<br/>';
    str += '"dateEffectType":"",<br/>';
    str += '//start, completed<br/>';
    str += '"start":"",<br/>';
    str += '"completed":"",<br/>';
    str += '"year":"",<br/>';
    str += '"month":"",<br/>';
    str += '"date":"",<br/>';
    str += '"season":0,<br/>';
    str += '"week":0,<br/>';
    str += '"adjust":0<br/>';
    str += '}';
    str += '</td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>process:</td><td><input type="text" id="process" style="width:95%"/></td></tr>';
    str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>identity:</td><td><input type="text" id="identity" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '<tr><td>filter:</td><td><textarea  id="filter" style="width:95%;height:300px"/></td></tr>';
    str += '<tr><td colspan="2">sample</td></tr>';
    str += '<tr><td colspan="2">';
    str += '{<br/>';
    str += '"path":"",<br/>';
    str += '"value":"",<br/>';
    str += '"formatType":"",<br/>';
    str += '//textValue, numberValue, booleanValue, dateTimeValue<br/>';
    str += '"logic":"",<br/>';
    str += '//and,or<br/>';
    str += '"comparison":"",<br/>';
    str += '//equals,notEquals,greaterThan,greaterThanOrEqualTo,lessThan,lessThanOrEqualTo,like,notLike<br/>';
    str += '}';
    str += '</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#simulate').click(function() {
	queryStat_simulate($('#id').val());
    });
    $('#get').click(function() {
	queryStat_get($('#id').val());
    });
}

function queryStat_simulate(id) {
    data = {};
    if ($('#date').val() != '') {
	data.date = JSON.parse($('#date').val());
    }
    data.application = splitValue($('#application').val());
    data.process = splitValue($('#process').val());
    data.company = splitValue($('#company').val());
    data.department = splitValue($('#department').val());
    data.identity = splitValue($('#identity').val());
    data.person = splitValue($('#person').val());
    if ($('#filter').val() != '') {
	data.filter = JSON.parse($('#filter').val());
    }
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/querystat/' + id + '/simulate?' + Math.random(),
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify(data),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_create() {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description"  style="width:95%"/></td></tr>';
    str += '<tr><td>availablePersonList:</td><td><textarea  id="availablePersonList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableIdentityList:</td><td><textarea  id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableDepartmentList:</td><td><textarea  id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableCompanyList:</td><td><textarea  id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>queryView:</td><td><input  id="queryView" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td colspan="2">sample</td></tr>';
    str += '<tr><td colspan="2">';
    str += '{<br/>';
    str += '"calculate":[<br/>';
    str += '{<br/>';
    str += '"column":"",<br/>';
    str += '"displayName":"",<br/>';
    str += '"calculateType":"",//sum, average, count, groupSum, groupAverage, groupCount<br/>';
    str += '"orderType":"",//desc, asc, original<br/>';
    str += '"orderEffectType":"",//key, value<br/>';
    str += '"id":""<br/>';
    str += '}<br>';
    str += ']';
    str += '}';
    str += '</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	queryStat_post();
    });
}

function queryStat_post() {
    var data = {};
    data.application = $('#application').val();
    data.name = $('#name').val();
    data.alias = $('#alias').val();
    data.description = $('#description').val();
    data.availablePersonList = splitValue($('#availablePersonList').val());
    data.availableIdentityList = splitValue($('#availableIdentityList').val());
    data.availableDepartmentList = splitValue($('#availableDepartmentList').val());
    data.availableCompanyList = splitValue($('#availableCompanyList').val());
    data.queryView = $('#queryView').val();
    if ($('#data').val() != '') {
	data.data = $('#data').val();
    }
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/querystat',
	data : JSON.stringify(data),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_edit(id) {
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
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description"  style="width:95%"/></td></tr>';
    str += '<tr><td>availablePersonList:</td><td><textarea  id="availablePersonList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableIdentityList:</td><td><textarea  id="availableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableDepartmentList:</td><td><textarea  id="availableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>availableCompanyList:</td><td><textarea  id="availableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>queryView:</td><td><input  id="queryView" style="width:95%"/></td></tr>';
    str += '<tr><td>queryViewName:</td><td id="queryViewName">&nbsp;</td></tr>';
    str += '<tr><td>queryViewAlias:</td><td id="queryViewAlias">&nbsp;</td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td colspan="2">sample</td></tr>';
    str += '<tr><td colspan="2">';
    str += '{<br/>';
    str += '"calculate":[<br/>';
    str += '{<br/>';
    str += '"column":"",<br/>';
    str += '"displayName":"",<br/>';
    str += '"calculateType":"",//sum, average, count, groupSum, groupAverage, groupCount<br/>';
    str += '"orderType":"",//desc, asc, original<br/>';
    str += '"orderEffectType":"",//key, value<br/>';
    str += '"id":""<br/>';
    str += '}<br>';
    str += ']';
    str += '}';
    str += '</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	queryStat_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/querystat/' + id,
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
	    $('#availablePersonList').val(joinValue(json.data.availablePersonList));
	    $('#availableIdentityList').val(joinValue(json.data.availableIdentityList));
	    $('#availableDepartmentList').val(joinValue(json.data.availableDepartmentList));
	    $('#availableCompanyList').val(joinValue(json.data.availableCompanyList));
	    $('#data').val(json.data.data);
	    $('#queryView').val(json.data.queryView);
	    $('#queryViewName').html(json.data.queryViewName);
	    $('#queryViewAlias').html(json.data.queryViewAlias);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_put(id) {
    var data = {};
    data.application = $('#application').val();
    data.name = $('#name').val();
    data.alias = $('#alias').val();
    data.description = $('#description').val();
    data.availablePersonList = splitValue($('#availablePersonList').val());
    data.availableIdentityList = splitValue($('#availableIdentityList').val());
    data.availableDepartmentList = splitValue($('#availableDepartmentList').val());
    data.availableCompanyList = splitValue($('#availableCompanyList').val());
    data.queryView = $('#queryView').val();
    if ($('#data').val() != '') {
	data.data = $('#data').val();
    }
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/querystat/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify(data),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/querystat/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_get(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/querystat/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function queryStat_listWithApplication_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>application flag:</td><td><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2" id="gird">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	queryStat_listWithApplication($('#applicationFlag').val());
    });
}

function queryStat_listWithApplication(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/querystat/list/application/' + id,
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
		str += '<a href="#" onclick="queryStat_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="queryStat_delete(\'' + item.id + '\')">delete</a>';
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