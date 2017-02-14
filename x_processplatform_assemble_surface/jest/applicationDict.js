applicationDict_parameter = {};

function applicationDict_listWithApplication_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>flag:</td><td><input type="text" id="flag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#get').click(function() {
	applicationDict_listWithApplication($('#flag').val());
    });
}

function applicationDict_listWithApplication(flag) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationdict/list/application/' + flag,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationDict_get_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>flag:</td><td><input type="text" id="flag" style="width:95%"/></td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>updateTime:</td><td id="updateTime">&nbsp;</td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence">&nbsp;</td></tr>';
    str += '<tr><td>application</td><td id="application">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#get').click(function() {
	applicationDict_get($('#flag').val());
    });
    $('#put').click(function() {
	applicationDict_put($('#flag').val());
    });
}

function applicationDict_get(flag) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationdict/' + flag,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#createTime').html(json.data.createTime);
	    $('#updateTime').html(json.data.updateTime);
	    $('#id').html(json.data.id);
	    $('#sequence').html(json.data.sequence);
	    $('#application').html(json.data.application);
	    $('#name').val(json.data.name);
	    $('#alias').val(json.data.alias);
	    $('#description').val(json.data.description);
	    $('#data').val(JSON.stringify(json.data.data, null, 4));
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationDict_put(flag) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationdict/' + flag,
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    data : $.parseJSON($('#data').val())
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationDict_query() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="put">put</a>&nbsp;<a href="#" id="post">post</a>&nbsp;<a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>flag:</td><td><input type="text" id="flag" style="width:95%"/></td></tr>';
    str += '<tr><td>applicationFlag:</td><td><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '<tr><td>paths:</td><td><input type="text" id="paths" style="width:95%"></input></td></tr>';
    str += '<tr><td colspan="2"><textarea id="data" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#get').click(function() {
	applicationDict_getData($('#flag').val(), $('#applicationFlag').val(), $('#paths').val().replace(/\./g, '\/'));
    });
    $('#put').click(function() {
	applicationDict_updateData($('#flag').val(), $('#applicationFlag').val(), $('#paths').val().replace(/\./g, '\/'));
    });
    $('#post').click(function() {
	applicationDict_createData($('#flag').val(), $('#applicationFlag').val(), $('#paths').val().replace(/\./g, '\/'));
    });
    $('#delete').click(function() {
	applicationDict_deleteData($('#flag').val(), $('#applicationFlag').val(), $('#paths').val().replace(/\./g, '\/'));
    });
}

function applicationDict_getData(flag, applicationFlag, paths) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationdict/' + flag + '/application/' + applicationFlag + ((paths.length > 0) ? ('/' + paths) : '') + '/data',
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

function applicationDict_updateData(flag, applicationFlag, paths) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationdict/' + flag + '/application/' + applicationFlag + ((paths.length > 0) ? ('/' + paths) : '') + '/data',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify($.parseJSON($('#data').val())),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationDict_deleteData(flag, applicationFlag, paths) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationdict/' + flag + '/application/' + applicationFlag + ((paths.length > 0) ? ('/' + paths) : '') + '/data',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationDict_createData(flag, applicationFlag, paths) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationdict/' + flag + '/application/' + applicationFlag + ((paths.length > 0) ? ('/' + paths) : '') + '/data',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify($.parseJSON($('#data').val())),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}