read_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function read_list_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="6"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	read_list_next();
    });
    $('#prev').click(function() {
	read_list_prev();
    });
    read_parameter.first = '(0)';
    read_parameter.last = '(0)';
    read_list_next();
}

function read_list_next(id) {
    var id = (id ? id : read_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/' + id + '/next/' + read_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		read_parameter.first = json.data[0].id;
		read_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		read_parameter.first = '(0)';
	    }
	    read_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_list_prev(id) {
    var id = (id ? id : read_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/' + id + '/prev/' + read_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		read_parameter.first = json.data[0].id;
		read_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		read_parameter.last = '(0)';
	    }
	    read_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_listWithApplication_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>applicationFlag</th><th colspan="3"><input type="text" id="applicationFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	read_listWithApplication_next();
    });
    $('#prev').click(function() {
	read_listWithApplication_prev();
    });
    read_parameter.first = '(0)';
    read_parameter.last = '(0)';
}

function read_listWithApplication_next(id) {
    var id = (id ? id : read_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/' + read_parameter.last + '/next/' + read_parameter.count + '/application/' + $('#applicationFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		read_parameter.first = json.data[0].id;
		read_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		read_parameter.first = '(0)';
	    }
	    read_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_listWithApplication_prev(id) {
    var id = (id ? id : read_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/' + id + '/prev/' + read_parameter.count + '/application/' + $('#applicationFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		read_parameter.first = json.data[0].id;
		read_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		read_parameter.last = '(0)';
	    }
	    read_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_listWithProcess_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>processFlag</th><th colspan="3"><input type="text" id="processFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '<tbody id="grid"></tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	read_listWithProcess_next();
    });
    $('#prev').click(function() {
	read_listWithProcess_prev();
    });
    read_parameter.first = '(0)';
    read_parameter.last = '(0)';
}

function read_listWithProcess_next(id) {
    var id = (id ? id : read_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/' + id + '/next/' + read_parameter.count + '/process/' + $('#processFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		read_parameter.first = json.data[0].id;
		read_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		read_parameter.first = '(0)';
	    }
	    read_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_listWithProcess_prev(id) {
    var id = (id ? id : read_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/' + id + '/prev/' + read_parameter.count + '/process/' + $('#processFlag').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		read_parameter.first = json.data[0].id;
		read_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		read_parameter.last = '(0)';
	    }
	    read_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_list_grid(json) {
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

function read_listCountWithApplication() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/count/application',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_listCountWithProcess_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>applicationFlag:</td><td><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	read_listCountWithProcess();
    });
}

function read_listCountWithProcess() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/list/count/application/' + $('#applicationFlag').val() + '/process',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_countWithPerson_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	read_countWithPerson();
    });
}

function read_countWithPerson() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/read/count/' + $('#person').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function read_get_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="reference">reference</a>&nbsp;<a href="#" id="process">process</a>&nbsp;<a href="#" id="update">update</a></td></tr>';
    str += '<tr><td>flag:</td><td><input type="text" id="flag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	read_get();
    });
    $('#reference').click(function() {
	read_reference();
    });
    $('#process').click(function() {
	read_process();
    });
    $('#update').click(function() {
	read_update();
    });
}

function read_get() {

}

function read_update() {

}

function read_reference() {

}

function read_process() {

}

function read_filter_init() {

}