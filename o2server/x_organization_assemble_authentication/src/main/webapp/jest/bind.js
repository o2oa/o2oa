bind_parameter = {}

function bind_list() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/bind/list',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><td colspan="3"><span id="total">' + json.data.length + '</span></td></tr>';
	    str += '<tr><th>id</th><th>meta</th><th>name</th></tr>';
	    $.each(json.data, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.meta + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '</tr>';
	    });
	    str += '</table>';
	    $('#content').html(str);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}