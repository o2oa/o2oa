webServers_parameter = {};

function webServers_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/webservers',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	$('#result').html(JSON.stringify(data.data, null, 4));
	if (data.type == 'success') {
	    if (data.data) {
		var str = '<table border="1" width="100%">';
		str += '<tr><th>order</th><th>name</th><th>host</th><th>port</th><th>username</th><th>operate</th></tr>';
		$.each(json.data, function(index, o) {
		    str += '<tr>';
		    str += '<td>' + o.order + '</td>';
		    str += '<td>' + o.name + '</td>';
		    str += '<td>' + o.host + '</td>';
		    str += '<td>' + o.port + '</td>';
		    str += '<td>' + o.username + '</td>';
		    str += '<td><a href="#" onclick="webServer_edit(\'' + o.name + '\')">edit</a>&nbsp;';
		    str += '<a href="#" onclick="webServer_delete(\'' + o.name + '\')">delete</a>&nbsp;';
		    str += '</td>';
		    str += '</tr><tr>'
		    str += '<td>' + o.status + '</td>';
		    str += '<td colspan="3">' + o.message + '</td>';
		    str += '<td colspan="2">' + joinValue(o.contextList, '<br/>') + '</td>';
		    str += '</tr>';
		});
		str += '</table>';
		$('#content').html(str);
	    }
	} else {
	    failure(data);
	}
    });
}