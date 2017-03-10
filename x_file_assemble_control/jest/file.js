file_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function file_get_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" style="width:95%" id="id"></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	file_get($('#id').val());
    });
}

function file_get(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_referenceType() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/referencetype',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_next(id) {
    var id = (id ? id : file_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/' + id + '/next/' + file_parameter.count,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		file_parameter.first = json.data[0].id;
		file_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		file_parameter.first = '(0)';
	    }
	    file_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_prev(id) {
    var id = (id ? id : file_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/' + id + '/prev/' + file_parameter.count,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		file_parameter.first = json.data[0].id;
		file_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		file_parameter.last = '(0)';
	    }
	    file_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="5">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">' + json.count + '</span></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>person</th><th>referenceType</th><th>reference</th></tr>';
    if (json.data) {
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.person + '</td>';
	    str += '<td>' + item.referenceType + '</td>';
	    str += '<td>' + item.reference + '</td>';
	    str += '</tr>';
	});
    }
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	file_list_next();
    });
    $('#prev').click(function() {
	file_list_prev();
    });
}

function file_list_next_all(id) {
    var id = (id ? id : file_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/' + id + '/next/' + file_parameter.count + '/all',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		file_parameter.first = json.data[0].id;
		file_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		file_parameter.first = '(0)';
	    }
	    file_list_all_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_prev_all(id) {
    var id = (id ? id : file_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/' + id + '/prev/' + file_parameter.count + '/all',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		file_parameter.first = json.data[0].id;
		file_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		file_parameter.last = '(0)';
	    }
	    file_list_all_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_all_grid(json) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="5">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">' + json.count + '</span></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>person</th><th>referenceType</th><th>reference</th></tr>';
    if (json.data) {
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.person + '</td>';
	    str += '<td>' + item.referenceType + '</td>';
	    str += '<td>' + item.reference + '</td>';
	    str += '</tr>';
	});
    }
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	file_list_next_all();
    });
    $('#prev').click(function() {
	file_list_prev_all();
    });
}

function file_list_withReferenceTypeWithReference_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="5"><a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>referenceType:</td><td colspan="4"><input type="text" style="width:95%" id="referenceType"></td></tr>';
    str += '<tr><td>reference:</td><td colspan="4"><input type="text" style="width:95%" id="referenceType"></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>person</th><th>referenceType</th><th>reference</th></tr>';
    str += '</thead><tbody id="grid"></tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	file_list_withReferenceTypeWithReference();
    });
}

function file_list_withReferenceTypeWithReference(referenceType, reference) {
    $('#grid').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/referencetype/' + referenceType + '/reference/' + reference,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data) {
		var str = '';
		$.each(json.data, function(index, item) {
		    str += '<tr>';
		    str += '<td>' + item.person + '</td>';
		    str += '<td>' + item.id + '</td>';
		    str += '<td>' + item.name + '</td>';
		    str += '<td>' + item.referenceType + '</td>';
		    str += '<td>' + item.reference + '</td>';
		    str += '</tr>';
		});
		$('#total').html(json.count);
		$('#grid').html(str);
	    }
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_withReferenceType_init() {
    $('#content').html('');
    $('#result').html('');
    file_parameter.first = '(0)';
    file_parameter.last = '(0)';
    var str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="5"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><td>referenceType:</td><td colspan="4"><input type="text" style="width:95%" id="referenceType"></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>person</th><th>referenceType</th><th>reference</th></tr>';
    str += '</thead><tbody id="grid"></tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	file_list_next_withReferenceType();
    });
    $('#prev').click(function() {
	file_list_prev_withReferenceType();
    });
}

function file_list_next_withReferenceType(id) {
    var id = (id ? id : file_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/' + id + '/next/' + file_parameter.count + '/referencetype/' + $('#referenceType').val(),
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		file_parameter.first = json.data[0].id;
		file_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		file_parameter.first = '(0)';
	    }
	    file_list_withReference_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_prev_withReferenceType(id) {
    var id = (id ? id : file_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/list/' + id + '/prev/' + file_parameter.count + '/referencetype/' + $('#referenceType').val(),
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		file_parameter.first = json.data[0].id;
		file_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		file_parameter.last = '(0)';
	    }
	    file_list_withReference_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_list_withReference_grid(json) {
    if (json.data) {
	var str = '';
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.person + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.referenceType + '</td>';
	    str += '<td>' + item.reference + '</td>';
	    str += '</tr>';
	});
	$('#total').html(json.count);
	$('#grid').html(str);
    }
}

function file_upload_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>referenceType:</td><td><input type="text" style="width:95%" id= "referenceType"/></td></tr>';
    str += '<tr><td>reference:</td><td><input type="text" style="width:95%" id= "reference"/></td></tr>';
    str += '<tr><td>scale:</td><td><input type="text" value="200" style="width:95%" id= "scale"/></td></tr>';
    str += '<tr><td>file:</td><td><form><input type="file" name="file" style="width:95%" id= "file"/></form></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	file_upload($('#referenceType').val(), $('#reference').val(), $('#scale').val());
    });
}

function file_upload(referenceType, reference, scale) {
    var formData = new FormData($('form')[0]);
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../servlet/file/upload/referencetype/' + referenceType + '/reference/' + reference + '/scale/' + scale,
	data : formData,
	contentType : false,
	cache : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).success(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_download_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" style="width:95%" id= "id"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	file_download($('#id').val());
    });
}

function file_download(id) {
    window.open('../servlet/file/download/' + id, '_blank');
}

function file_copy_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>attachmentId:</td><td><input type="text" style="width:95%" id= "attachmentId"/</td></tr>';
    str += '<tr><td>referenceType:</td><td><input type="text" style="width:95%" id= "referenceType"/></td></tr>';
    str += '<tr><td>reference:</td><td><input type="text" style="width:95%" id= "reference"/></td></tr>';
    str += '<tr><td>scale:</td><td><input type="text" value="200" style="width:95%" id= "scale"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	file_copy($('#attachmentId').val(), $('#referenceType').val(), $('#reference').val(), $('#scale').val());
    });
}

function file_copy(attachmentId, referenceType, reference, scale) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/file/copy/attachment/' + attachmentId + '/referencetype/' + referenceType + '/reference/' + reference + '/scale/' + scale,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function file_delete_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>referenceType:</td><td><input type="text" style="width:95%" id= "referenceType"/></td></tr>';
    str += '<tr><td>reference:</td><td><input type="text" style="width:95%" id= "reference"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#delete').click(function() {
	file_delete($('#referenceType').val(), $('#reference').val());
    });
}

function file_delete(referenceType, reference, scale) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/file/referencetype/' + referenceType + '/reference/' + reference,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}