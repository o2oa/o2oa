distribute_parameter = {};

function distribute_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="assemble">assemble</a>&nbsp;<a href="#" id="webServerAssemble">webServerAssemble</a></td></tr>';
    str += '<tr><td>source</td><td><input type="text" style="width:95%" id="source"></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#assemble').click(function() {
	distribute_assemble($('#source').val());
    });
    $('#webServerAssemble').click(function() {
	distribute_webServerAssemble($('#source').val());
    });
}

function distribute_assemble(source) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/distribute/assemble/source/' + source,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function distribute_webServerAssemble(source) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/distribute/webserver/assemble/source/' + source,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}