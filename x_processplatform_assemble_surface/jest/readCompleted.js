readCompleted_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function readCompleted_list_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="6"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	readCompleted_list_next();
    });
    $('#prev').click(function() {
	readCompleted_list_prev();
    });
    readCompleted_parameter.first = '(0)';
    readCompleted_parameter.last = '(0)';
    readCompleted_list_next();
}

function readCompleted_list_next(id) {
    var id = (id ? id : readCompleted_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + id + '/next/' + readCompleted_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.first = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_list_prev(id) {
    var id = (id ? id : readCompleted_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + id + '/prev/' + readCompleted_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.last = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listWithApplication_init() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>applicationFlag</th><th colspan="3"><input type="text" id="applicationFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	readCompleted_listWithApplication_next();
    });
    $('#prev').click(function() {
	readCompleted_listWithApplication_prev();
    });
    readCompleted_parameter.first = '(0)';
    readCompleted_parameter.last = '(0)';
}

function readCompleted_listWithApplication_next(id) {
    var id = (id ? id : readCompleted_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + readCompleted_parameter.last + '/next/' + readCompleted_parameter.count + '/application/' + $('#applicationFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.first = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listWithApplication_prev(id) {
    var id = (id ? id : readCompleted_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + id + '/prev/' + readCompleted_parameter.count + '/application/' + $('#applicationFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.last = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listWithProcess_init() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>processFlag</th><th colspan="3"><input type="text" id="processFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	readCompleted_listWithProcess_next();
    });
    $('#prev').click(function() {
	readCompleted_listWithProcess_prev();
    });
    readCompleted_parameter.first = '(0)';
    readCompleted_parameter.last = '(0)';
}

function readCompleted_listWithProcess_next(id) {
    var id = (id ? id : readCompleted_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + id + '/next/' + readCompleted_parameter.count + '/process/' + $('#processFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.first = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listWithProcess_prev(id) {
    var id = (id ? id : readCompleted_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + id + '/prev/' + readCompleted_parameter.count + '/process/' + $('#processFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.last = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_list_grid(json) {
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

function readCompleted_listCountWithApplication() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/count/application',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listCountWithProcess_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>applicationFlag:</td><td><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	readCompleted_listCountWithProcess($('#applicationFlag').val());
    });
}

function readCompleted_listCountWithProcess(applicationFlag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/count/application/' + applicationFlag + '/process',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_countWithPerson_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	readCompleted_countWithPerson($('#person').val());
    });
}

function readCompleted_countWithPerson(person) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/count/' + person,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_reference_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	readCompleted_reference($('#id').val());
    });
}

function readCompleted_reference(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/' + id + '/reference',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listFilter_init() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<a href="#" id="clear">clear</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><td>application</td><td colspan="3"><select id="applicationFilter"></select></td></tr>';
    str += '<tr><td>process</td><td colspan="3"><select id="processFilter"></select></td></tr>';
    str += '<tr><td>creatorCompany</td><td colspan="3"><select id="creatorCompanyFilter"></select></td></tr>';
    str += '<tr><td>creatorDepartment</td><td colspan="3"><select id="creatorDepartmentFilter"></select></td></tr>';
    str += '<tr><td>completedTimeMonth</td><td colspan="3"><select id="completedTimeMonthFilter"></select></td></tr>';
    str += '<tr><td>activityName</td><td colspan="3"><select id="activityNameFilter"></select></td></tr>';
    str += '<tr><td>key</td><td colspan="3"><input type="text" id="keyFilter" style="width:95%"/></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	readCompleted_listFilter_next();
    });
    $('#prev').click(function() {
	readCompleted_listFilter_prev();
    });
    $('#clear').click(function() {
	readCompleted_parameter.first = '(0)';
	readCompleted_parameter.last = '(0)';
    });
    readCompleted_parameter.first = '(0)';
    readCompleted_parameter.last = '(0)';
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/filter/attribute',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    txt = '<option value="">all</option>';
	    if (json.data.applicationList) {
		$.each(json.data.applicationList, function(index, item) {
		    txt += '<option value="' + item.value + '">' + item.name + '</option>';
		});
	    }
	    $('#applicationFilter').html(txt);
	    txt = '<option value="">all</option>';
	    if (json.data.processList) {
		$.each(json.data.processList, function(index, item) {
		    txt += '<option value="' + item.value + '">' + item.name + '</option>';
		});
	    }
	    $('#processFilter').html(txt);
	    txt = '<option value="">all</option>';
	    if (json.data.creatorCompanyList) {
		$.each(json.data.creatorCompanyList, function(index, item) {
		    txt += '<option value="' + item.value + '">' + item.name + '</option>';
		});
	    }
	    $('#creatorCompanyFilter').html(txt);
	    txt = '<option value="">all</option>';
	    if (json.data.creatorDepartmentList) {
		$.each(json.data.creatorDepartmentList, function(index, item) {
		    txt += '<option value="' + item.value + '">' + item.name + '</option>';
		});
	    }
	    $('#creatorDepartmentFilter').html(txt);
	    txt = '<option value="">all</option>';
	    if (json.data.completedTimeMonthList) {
		$.each(json.data.completedTimeMonthList, function(index, item) {
		    txt += '<option value="' + item.value + '">' + item.name + '</option>';
		});
	    }
	    $('#completedTimeMonthFilter').html(txt);
	    txt = '<option value="">all</option>';
	    if (json.data.activityNameList) {
		$.each(json.data.activityNameList, function(index, item) {
		    txt += '<option value="' + item.value + '">' + item.name + '</option>';
		});
	    }
	    $('#activityNameFilter').html(txt);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listFilter_next() {
    var id = (id ? id : readCompleted_parameter.last);
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + id + '/next/' + readCompleted_parameter.count + '/filter',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    application : $('#applicationFilter').val(),
	    process : $('#processFilter').val(),
	    creatorCompany : $('#creatorCompanyFilter').val(),
	    creatorDepartment : $('#creatorDepartmentFilter').val(),
	    completedTimeMonth : $('#completedTimeMonthFilter').val(),
	    creatorDepartment : $('#creatorDepartmentFilter').val(),
	    activityName : $('#activityNameFilter').val(),
	    key : $('#keyFilter').val()
	}),
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.first = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function readCompleted_listFilter_prev() {
    var id = (id ? id : readCompleted_parameter.first);
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/readcompleted/list/' + id + '/prev/' + readCompleted_parameter.count + '/filter',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    application : $('#applicationFilter').val(),
	    process : $('#processFilter').val(),
	    creatorCompany : $('#creatorCompanyFilter').val(),
	    creatorDepartment : $('#creatorDepartmentFilter').val(),
	    completedTimeMonth : $('#completedTimeMonthFilter').val(),
	    creatorDepartment : $('#creatorDepartmentFilter').val(),
	    activityName : $('#activityNameFilter').val(),
	    key : $('#keyFilter').val()
	}),
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		readCompleted_parameter.first = json.data[0].id;
		readCompleted_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		readCompleted_parameter.last = '(0)';
	    }
	    readCompleted_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}