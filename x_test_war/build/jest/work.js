function work_getComplex_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" style="width:95%" id= "id"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	work_getComplex($('#id').val());
    });
}

function work_getComplex(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/work/' + id + '/complex',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_retract_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" style="width:95%" id= "id"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	work_retract($('#id').val());
    });
}

function work_retract(id) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/work/' + id + '/retract',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}