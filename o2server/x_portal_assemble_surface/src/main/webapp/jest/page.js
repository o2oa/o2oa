page_parameter = {};

function page_list_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>portal:</td><td><input type="text" id="portal" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	page_list($('#portal').val());
    });
}

function page_list(portal) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/page/list/portal/' + portal,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function page_get_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	page_get($('#id').val());
    });
}

function page_get(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/page/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function page_getWithPortal_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>portalFlag:</td><td><input type="text" id="portalFlag" style="width:95%"/></td></tr>';
    str += '<tr><td>pageFlag:</td><td><input type="text" id="pageFlag" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	page_getWithPortal($('#portalFlag').val(), $('#pageFlag').val());
    });
}

function page_getWithPortal(portalFlag, pageFlag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/page/' + pageFlag + '/portal/' + portalFlag,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}