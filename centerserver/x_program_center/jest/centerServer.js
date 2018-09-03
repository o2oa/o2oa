centerServer_parameter = {};

function centerServer_get() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>cipher:</td><td><input type="text" style="width:95%" id="cipher"/></td></tr>';
    str += '<tr><td>proxyHost:</td><td><input type="text" style="width:95%" id="proxyHost"/></td></tr>';
    str += '<tr><td>proxyPort:</td><td><input type="text" style="width:95%" id="proxyPort"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/centerserver',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		$('#host').val(json.data.host);
		$('#port').val(json.data.port);
		$('#cipher').val(json.data.cipher);
		$('#proxyHost').val(json.data.proxyHost);
		$('#proxyPort').val(json.data.proxyPort);
	    }
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	centerServer_put();
    });
}

function centerServer_put() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/centerserver',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    host : $('#host').val(),
	    port : $('#port').val(),
	    cipher : $('#cipher').val(),
	    proxyHost : $('#proxyHost').val(),
	    proxyPort : $('#proxyPort').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}