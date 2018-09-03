datas_parameter = {};

function datas_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/datas',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json.data, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		var str = '<table border="1" width="100%">';
		str += '<tr><th>order</th><th>dataServer</th><th>operate</th></tr>';
		$.each(json.data, function(k, v) {
		    str += '<tr><td colspan="3">' + k + '&nbsp;' + '<a href="#" onclick="data_create(\'' + k + '\')">add</a>';
		    +'</td></tr>';
		    $.each(v, function(i, o) {
			str += '<tr>';
			str += '<td>' + o.order + '</td>';
			str += '<td>' + o.dataServer + '</td>';
			str += '<td>';
			str += '<a href="#" onclick="data_edit(\'' + k + '\',\'' + o.dataServer + '\')">edit</a>&nbsp;';
			str += '<a href="#" onclick="data_delete(\'' + k + '\',\'' + o.dataServer + '\')">delete</a>';
			str += '</td>';
			str += '</tr>';
		    });
		});
		str += '</table>';
		$('#content').html(str);
	    }
	} else {
	    failure(data);
	}
    });
}