debug_parameter = {};

function debug_init() {
    $('#result').html('');
    $('#content').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp<a href="#" id="on">on</a>&nbsp;<a href="#" id="off">off</a></td></tr>';
    str += '<tr><td>debug:</td><td id="status">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	debug_get();
    });
    $('#on').click(function() {
	debug_on();
    });
    $('#off').click(function() {
	debug_off();
    });
}

function debug_get() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/debug',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#status').html(json.data.value + '');
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function debug_on() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/debug/true',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#status').html(json.data.value + '');
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function debug_off() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/debug/false',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    $('#status').html(json.data.value + '');
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}