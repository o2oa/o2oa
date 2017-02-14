data_parameter = {};

function data_getWithWork_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="post">post</a>&nbsp;<a href="#" id="put">put</a>&nbsp;<a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"/></td></tr>';
    str += '<tr><td>paths:</td><td><input type="text" id="paths" style="width:95%"></input></td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#get').click(function() {
	data_getWithWork($('#workId').val(), $('#paths').val().replace(/\./g, '\/'));
    });
    $('#put').click(function() {
	data_updateWithWork($('#workId').val(), $('#paths').val().replace(/\./g, '\/'));
    });
    $('#delete').click(function() {
	data_deleteWithWork($('#workId').val(), $('#paths').val().replace(/\./g, '\/'));
    });
    $('#post').click(function() {
	data_createWithWork($('#workId').val(), $('#paths').val().replace(/\./g, '\/'));
    });
}

function data_getWithWork(workId, paths) {
    $('#data').val('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/data/work/' + workId + ((paths.length > 0) ? ('/' + paths) : ''),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#data').val(JSON.stringify(json.data, null, 4));
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function data_updateWithWork(workId, paths) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/data/work/' + workId + ((paths.length > 0) ? ('/' + paths) : ''),
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify($.parseJSON($('#data').val())),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function data_deleteWithWork(workId, paths) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/data/work/' + workId + ((paths.length > 0) ? ('/' + paths) : ''),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function data_createWithWork(workId, paths) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/data/work/' + workId + ((paths.length > 0) ? ('/' + paths) : ''),
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify($.parseJSON($('#data').val())),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function data_getWithWorkCompleted_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>workCompletedId:</td><td><input type="text" id="workCompletedId" style="width:95%"/></td></tr>';
    str += '<tr><td>paths:</td><td><input type="text" id="paths" style="width:95%"></input></td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#get').click(function() {
	data_getWithWorkCompleted($('#workCompletedId').val(), $('#paths').val().replace(/\./g, '\/'));
    });
}

function data_getWithWorkCompleted(workCompletedId, paths) {
    $('#data').val('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/data/workcompleted/' + workCompletedId + ((paths.length > 0) ? ('/' + paths) : ''),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#data').val(JSON.stringify(json.data, null, 4));
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}