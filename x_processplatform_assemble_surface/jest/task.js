task_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function task_list_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="6"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	task_list_next();
    });
    $('#prev').click(function() {
	task_list_prev();
    });
    task_parameter.first = '(0)';
    task_parameter.last = '(0)';
    task_list_next();
}

function task_list_next(id) {
    var id = (id ? id : task_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/' + id + '/next/' + task_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		task_parameter.first = json.data[0].id;
		task_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		task_parameter.first = '(0)';
	    }
	    task_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_list_prev(id) {
    var id = (id ? id : task_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/' + id + '/prev/' + task_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		task_parameter.first = json.data[0].id;
		task_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		task_parameter.last = '(0)';
	    }
	    task_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_listWithApplication_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>applicationFlag</th><th colspan="3"><input type="text" id="applicationFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	task_listWithApplication_next();
    });
    $('#prev').click(function() {
	task_listWithApplication_prev();
    });
    task_parameter.first = '(0)';
    task_parameter.last = '(0)';
}

function task_listWithApplication_next(id) {
    var id = (id ? id : task_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/' + task_parameter.last + '/next/' + task_parameter.count + '/application/' + $('#applicationFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		task_parameter.first = json.data[0].id;
		task_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		task_parameter.first = '(0)';
	    }
	    task_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_listWithApplication_prev(id) {
    var id = (id ? id : task_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/' + id + '/prev/' + task_parameter.count + '/application/' + $('#applicationFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		task_parameter.first = json.data[0].id;
		task_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		task_parameter.last = '(0)';
	    }
	    task_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_listWithProcess_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>processFlag</th><th colspan="3"><input type="text" id="processFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	task_listWithProcess_next();
    });
    $('#prev').click(function() {
	task_listWithProcess_prev();
    });
    task_parameter.first = '(0)';
    task_parameter.last = '(0)';
}

function task_listWithProcess_next(id) {
    var id = (id ? id : task_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/' + id + '/next/' + task_parameter.count + '/process/' + $('#processFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		task_parameter.first = json.data[0].id;
		task_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		task_parameter.first = '(0)';
	    }
	    task_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_listWithProcess_prev(id) {
    var id = (id ? id : task_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/' + id + '/prev/' + task_parameter.count + '/process/' + $('#processFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		task_parameter.first = json.data[0].id;
		task_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		task_parameter.last = '(0)';
	    }
	    task_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_list_grid(json) {
    if (json.data && json.data.length > 0) {
	str = '';
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.rank + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.title + '</td>';
	    str += '<td>' + item.processName + '</td>';
	    str += '</tr>';
	});
	$('#total').html(json.count);
	$('#grid').html(str);
    } else {
	$('#total').html('0');
	$('#grid').html('');
    }
}

function task_listCountWithApplication() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/count/application',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_listCountWithProcess_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>applicationFlag:</td><td><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	task_listCountWithProcess($('#applicationFlag').val());
    });
}

function task_listCountWithProcess(applicationFlag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/list/count/application/' + applicationFlag + '/process',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_countWithPerson_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	task_countWithPerson($('#person').val());
    });
}

function task_countWithPerson(person) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/count/' + person,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_get_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="reference">reference</a>&nbsp;<a href="#" id="process">process</a>&nbsp;<a href="#" id="update">update</a>&nbsp;<a href="#" id="reset">reset</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>routeName:</td><td><input type="text" id="routeName" style="width:95%"/></td></tr>';
    str += '<tr><td>opinion:</td><td><input type="text" id="opinion" style="width:95%"/></td></tr>';
    str += '<tr><td>identityList:</td><td><textarea id="identityList" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	task_get($('#id').val());
    });
    $('#reference').click(function() {
	task_reference($('#id').val());
    });
    $('#process').click(function() {
	task_process($('#id').val());
    });
    $('#update').click(function() {
	task_update($('#id').val());
    });
    $('#reset').click(function() {
	task_reset($('#id').val());
    });
}

function task_get(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#routeName').val(json.data.routeName);
	    $('#opinion').val(json.data.opinion);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_update(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/task/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    routeName : $('#routeName').val(),
	    opinion : $('#opinion').val()
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_reference(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/task/' + id + '/reference',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_process(id) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/task/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    routeName : $('#routeName').val(),
	    opinion : $('#opinion').val()
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function task_reset(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/task/' + id + '/reset',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    routeName : $('#routeName').val(),
	    opinion : $('#opinion').val(),
	    identityList : splitValue($('#identityList').val())
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}