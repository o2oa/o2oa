function status_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/status/list',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		var str = '<table border="1" width="100%">';
		str += '<tr><th>name</th><th>title</th><th>path</th><th>widgetName</th><th>widgetTitle</th></tr>';
		str += '<tr><td colspan="5">allow:</td></tr>';
		if (json.data.allowList) {
		    $.each(json.data.allowList, function(index, o) {
			str += '<tr>';
			str += '<td>' + o.name + '</td>';
			str += '<td>' + o.title + '</td>';
			str += '<td>' + o.path + '</td>';
			str += '<td>' + o.widgetName + '</td>';
			str += '<td>' + o.widgetTitle + '</td>';
			str += '</tr>';
		    });
		}
		str += '<tr><td colspan="5">deny:</td></tr>';
		if (json.data.denyList) {
		    $.each(json.data.denyList, function(index, o) {
			str += '<tr>';
			str += '<td>' + o.name + '</td>';
			str += '<td>' + o.title + '</td>';
			str += '<td>' + o.path + '</td>';
			str += '<td>' + o.widgetName + '</td>';
			str += '<td>' + o.widgetTitle + '</td>';
			str += '</tr>';
		    });
		}
		str += '</table>';
		$('#content').html(str);
	    }
	} else {
	    failure(json);
	}
    });
}