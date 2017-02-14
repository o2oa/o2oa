function storages_list() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/storages',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><th>order</th><th>storageServer</th><th>enable</th><th>weight</th><th>operate</th></tr>';
	    if (json.data) {
		$.each(json.data, function(type, item) {
		    str += '<tr><td colspan="6">' + type + '&nbsp;<a href="#" onclick="storage_create(\'' + type + '\')">add</a></td></tr>';
		    $.each(item, function(i, o) {
			str += '<tr>';
			str += '<td>' + o.order + '</td>';
			str += '<td>' + o.storageServer + '</td>';
			str += '<td>' + o.enable + '</td>';
			str += '<td>' + o.weight + '</td>';
			str += '<td>';
			str += '<a href="#" onclick="storage_edit(\'' + type + '\',\'' + o.storageServer + '\')">edit</a>&nbsp;';
			str += '<a href="#" onclick="storage_delete(\'' + type + '\',\'' + o.storageServer + '\')">delete</a>';
			str += '</td>';
			str += '</tr>';
		    });
		});
	    }
	    str += '</table>';
	    $('#content').html(str);
	} else {
	    failure(json);
	}
    });
}