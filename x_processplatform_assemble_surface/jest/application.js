application_parameter = {};

function application_get_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="getIcon">getIcon</a></td></tr>';
    str += '<tr><td>flag:</td><td><input type="text" id="flag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#get').click(function() {
	application_get($('#flag').val());
    });
    $('#getIcon').click(function() {
	application_getIcon($('#flag').val());
    });
}

function application_get(flag) {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/application/' + flag,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function application_getIcon(flag) {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/application/' + flag,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function application_listWithPerson() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/application/list',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function application_listWithPersonComplex() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/application/list/complex',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}