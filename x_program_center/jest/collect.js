collect_parameter = {};

function collect_check_connect_select() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="check">check</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#check').click(function() {
	collect_check_connect();
    });
}
function collect_check_connect() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/collect/check/connect',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}