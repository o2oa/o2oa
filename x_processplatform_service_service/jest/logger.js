logger_parameter = {};

function logger_init() {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp<a href="#" id="trace">trace</a>&nbsp;<a href="#" id="debug">debug</a>&nbsp;<a href="#" id="info">info</a>&nbsp;<a href="#" id="warn">warn</a></td></tr>';
    str += '<tr><td>debug:</td><td id="level">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	logger_get();
    });
    $('#trace').click(function() {
	logger_trace();
    });
    $('#debug').click(function() {
	logger_debug();
    });
    $('#info').click(function() {
	logger_info();
    });
    $('#warn').click(function() {
	logger_warn();
    });
}

function logger_get() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/logger',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#level').html(json.data.value);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function logger_trace() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/logger/trace',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function logger_debug() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/logger/debug',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function logger_info() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/logger/info',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function logger_warn() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/logger/warn',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}