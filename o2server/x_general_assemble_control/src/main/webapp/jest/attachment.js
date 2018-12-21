attachment_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function attachment_list_next(id) {
    var id = (id ? id : attachment_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/list/' + id + '/next/' + attachment_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		attachment_parameter.first = json.data[0].id;
		attachment_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		attachment_parameter.first = '(0)';
	    }
	    attachment_grid(json.data);
	} else {
	    failure(data);
	}
    });
}

function attachment_list_prev(id) {
    var id = (id ? id : attachment_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/list/' + id + '/prev/' + attachment_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		attachment_parameter.first = json.data[0].id;
		attachment_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		attachment_parameter.last = '(0)';
	    }
	    attachment_grid(json.data);
	} else {
	    failure(data);
	}
    });
}

function attachment_grid(items) {
    var str = '<table border="1" width="100%"><tobdy>';
    str += '<tr><th>rank</th><th>id</th><th>name</th><th>fileName</th><th>summary</th><th>length</th><th>operate</th></tr>';
    $.each(items, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.rank + '</td>';
	str += '<td>' + item.id + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.fileName + '</td>';
	str += '<td>' + item.summary + '</td>';
	str += '<td>' + item.length + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="attachment_download_select(\'' + item.id + '\')">download</a>&nbsp;';
	str += '<a href="#" onclick="attachment_update_select(\'' + item.id + '\')">update</a>&nbsp;';
	str += '<a href="#" onclick="attachment_delete_select(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</tobdy></table>';
    $('#content').html(str);
}

function attachment_get_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%" /></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_get($('#id').val());
    });
}

function attachment_get(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_listWithMeeting_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>meeting:</td><td><input type="text" id="meeting" style="width:95%" ></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_listWithMeeting($('#meeting').val());
    });
}

function attachment_listWithMeeting(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/list/meeting/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    attachment_grid(json.data);
	}
    });
}

function attachment_post_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>meeting:</td><td><input type="text" id="meeting" style="width:95%" /></td></tr>';
    str += '<tr><td>type:</td><td><select id="type"><option value="attachment">attachment</option><option value="summary">summary</option></select></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" id="file" style="width:95%" ></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	attachment_post();
    });
}

function attachment_post() {
    var formData = new FormData();
    $.each($('input[type=file]'), function(index, item) {
	formData.append('file', item.files[0]);
    });
    var url = '../servlet/attachment/upload/meeting/' + $('#meeting').val();
    url += $('#type').val() == 'summary' ? '/summary' : '';
    $.ajax({
	type : 'POST',
	url : url,
	data : formData,
	contentType : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    });
}

function attachment_put_select(id) {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%" ></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" id="file" style="width:95%" ></td></tr>';
    str += '</table>';
    $('#content').html(str);
    if (id) {
	$('#id').val(id);
    }
    $('#put').click(function() {
	attachment_put();
    });
}

function attachment_put() {
    $('#result').html('');
    var formData = new FormData();
    $.each($('input[type=file]'), function(index, item) {
	formData.append('file', item.files[0]);
    });
    $.ajax({
	type : 'POST',
	url : '../servlet/attachment/update/' + $('#id').val(),
	data : formData,
	contentType : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    });
}

function attachment_download_select(id) {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%" ></td></tr>';
    str += '<tr><td>type:</td><td><select id="type"><option value="stream">stream</option><option value="contentType">contentType</option></select></td></tr>';
    str += '</table>';
    $('#content').html(str);
    if (id) {
	$('#id').val(id);
    }
    $('#get').click(function() {
	attachment_download();
    });
}

function attachment_download() {
    var url = '../servlet/attachment/download/' + $('#id').val();
    if ($('#type').val() == 'stream') {
	url += '/stream';
    }
    window.open(url, '_blank');
}

function attachment_delete_select(id) {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%" ></td></tr>';
    str += '</table>';
    $('#content').html(str);
    if (id) {
	$('#id').val(id);
    }
    $('#delete').click(function() {
	attachment_delete($('#id').val());
    });
}

function attachment_delete(id) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}