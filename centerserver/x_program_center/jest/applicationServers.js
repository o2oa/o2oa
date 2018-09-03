applicationServers_parameter = {};

function applicationServers_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationservers',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		var str = '<table border="1" width="100%">';
		str += '<tr><th>order</th><th>name</th><th>host</th><th>port</th><th>username</th><th>operate</th></tr>';
		$.each(json.data, function(index, o) {
		    str += '<tr>';
		    str += '<td>' + o.order + '</td>';
		    str += '<td>' + o.name + '</td>';
		    str += '<td>' + o.host + '</td>';
		    str += '<td>' + o.port + '</td>';
		    str += '<td>' + o.username + '</td>';
		    str += '<td><a href="#" onclick="applicationServer_edit(\'' + o.name + '\')">edit</a>&nbsp;';
		    str += '<a href="#" onclick="applicationServer_delete(\'' + o.name + '\')">delete</a>';
		    str += '</td>';
		    str += '</tr><tr>'
		    str += '<td>' + o.status + '</td>';
		    str += '<td colspan="5">';
		    if (o.message) {
			str += o.message;
		    } else {
			str += '&nbsp';
		    }
		    str += '</td>';
		    str += '</tr><tr>'
		    str += '<td>planList</td>';
		    str += '<td colspan="4"><table border="0"><tr><th>name</th><th>weight</th></tr>';
		    if (o.planList) {
			$.each(o.planList, function(idx, p) {
			    str += '<tr><td>' + p.name + '</td><td>' + p.weight + '</td></tr>';
			})
		    }
		    str += '</table></td>';
		    str += '<td>';
		    str += '<a href="#" onclick="applicationServer_deploy(\'' + o.name + '\', false)">deploy</a>&nbsp;';
		    str += '<a href="#" onclick="applicationServer_deploy(\'' + o.name + '\', true)">redeploy</a>';
		    str += '</td>';
		    str += '</tr>';
		});
		str += '</table>';
		$('#content').html(str);
	    }
	} else {
	    failure(json);
	}
    });
}