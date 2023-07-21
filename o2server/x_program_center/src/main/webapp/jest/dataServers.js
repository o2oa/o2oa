dataServers_parameter = {};

function dataServers_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/dataservers',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json.data, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		var str = '<table border="1" width="100%">';
		str += '<tr><th>order</th><th>name</th><th>host</th><th>port</th><th>database</th><th>operate</th></tr>';
		$.each(json.data, function(index, o) {
		    str += '<tr>';
		    str += '<td>' + o.order + '</td>';
		    str += '<td>' + o.name + '</td>';
		    str += '<td>' + o.host + '</td>';
		    str += '<td>' + o.port + '</td>';
		    str += '<td>' + o.database + '</td>';
		    str += '<td>';
		    str += '<a href="#" onclick="dataServer_edit(\'' + o.name + '\')">edit</a>&nbsp;';
		    str += '<a href="#" onclick="dataServer_delete(\'' + o.name + '\')">delete</a>';
		    str += '</td>';
		    str += '</tr><tr>';
		    str += '<td>' + o.databaseType + '</td>';
		    str += '<td colspan="2">' + o.status + '</td>';
		    str += '<td colspan="3">' + o.message + '</td>';
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