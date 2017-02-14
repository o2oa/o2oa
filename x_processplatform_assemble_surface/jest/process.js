process_parameter = {};

function process_getComplex_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>flag:</td><td><input type="text" id="flag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	process_getComplex($('#flag').val());
    });
}

function process_getComplex(flag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/process/' + flag + '/complex',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function process_getAllowRerouteTo_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>flag:</td><td><input type="text" id="flag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	process_getAllowRerouteTo($('#flag').val());
    });
}

function process_getAllowRerouteTo(flag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/process/' + flag + '/allowrerouteto',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function process_listWithApplication_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>applicationFlag:</td><td><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	process_listWithApplication($('#applicationFlag').val());
    });
}

function process_listWithApplication(applicationFlag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/process/list/application/' + applicationFlag,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}