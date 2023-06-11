clock_parameter = {};

function clock_listTimer() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/clock/list/timer',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function clock_listTimerLog_init() {
    $('#context').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="get">get</a></td></tr></thead>';
    str += '<tbody>';
    str += '<tr><td>application</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	clock_listTimerLog($('#application').val());
    });
}

function clock_listTimerLog(application) {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/clock/list/timerlog/application/' + application,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function clock_listSchedule() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/clock/list/schedule',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function clock_listScheduleLog_init() {
    $('#context').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="get">get</a></td></tr></thead>';
    str += '<tbody>';
    str += '<tr><td>application</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	clock_listScheduleLog($('#application').val());
    });
}

function clock_listScheduleLog(application) {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/clock/list/schedulelog/application/' + application,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}