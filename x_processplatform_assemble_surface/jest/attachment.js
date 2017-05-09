attachment_parameter = {};

function attachment_listWithWork_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_listWithWork($('#workId').val());
    });
}

function attachment_listWithWork(workId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/list/work/' + workId,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_listWithWorkCompleted_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>workCompletedId:</td><td><input type="text" id="workCompletedId" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_listWithWorkCompleted($('#workCompletedId').val());
    });
}

function attachment_listWithWorkCompleted(workCompletedId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/list/workcompleted/' + workCompletedId,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_getWithWork_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_getWithWork($('#id').val(), $('#workId').val());
    });
    $('#delete').click(function() {
	attachment_deleteWithWork($('#id').val(), $('#workId').val());
    });
}

function attachment_getWithWorkCompleted_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>workCompletedId:</td><td><input type="text" id="workCompletedId" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_getWithWorkCompleted($('#id').val(), $('#workCompletedId').val());
    });
}

function attachment_getWithWork(id, workId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id + '/work/' + workId,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_deleteWithWork(id, workId) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id + '/work/' + workId,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_getWithWorkCompleted(id, workCompletedId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id + '/workcompleted/' + workCompletedId,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_download_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="downloadWithWork">downloadWithWork</a>&nbsp;<a href="#" id="downloadWithWorkCompleted">downloadWithWorkCompleted</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"/></td></tr>';
    str += '<tr><td>workCompletedId:</td><td><input type="text" id="workCompletedId" style="width:95%"/></td></tr>';
    str += '<tr><td>stream:</td><td><select id="stream"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#downloadWithWork').click(function() {
	attachment_downloadWithWork($('#id').val(), $('#workId').val(), $('#stream').val());
    });
    $('#downloadWithWorkCompleted').click(function() {
	attachment_downloadWithWorkCompleted($('#id').val(), $('#workCompletedId').val(), $('#stream').val());
    });
}

function attachment_downloadWithWork(id, workId, stream) {
    url = '../jaxrs/attachment/download/' + id + '/work/' + workId + '/stream/' + stream;
    window.open(url, '_blank');
}

function attachment_downloadWithWorkCompleted(id, workCompletedId, stream) {
    url = '../jaxrs/attachment/download/' + id + '/workcompleted/' + workCompletedId + '/stream/' + stream;
    window.open(url, '_blank');
}

function attachment_upload_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="upload">upload</a></td></tr>';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"/></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" id="file" style="width:95%"/></td></tr>';
    str += '<tr><td>site:</td><td><input id="site" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#upload').click(function() {
	attachment_upload($('#workId').val());
    });
}

function attachment_upload(workId) {
    var formData = new FormData();
    formData.append('file', $('#file')[0].files[0]);
    formData.append('site', $('#site').val());
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/attachment/upload/work/' + workId,
	xhrFields : {
	    'withCredentials' : true
	},
	cache : false,
	processData : false,
	contentType : false,
	data : formData
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_update_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="update">update</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"/></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" id="file" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#update').click(function() {
	attachment_update($('#id').val(), $('#workId').val());
    });
}

function attachment_update(id, workId) {
    var formData = new FormData();
    formData.append('file', $('#file')[0].files[0]);
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/attachment/update/' + id + '/work/' + workId,
	xhrFields : {
	    'withCredentials' : true
	},
	cache : false,
	processData : false,
	contentType : false,
	data : formData
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_available_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_available($('#id').val());
    });
}

function attachment_available(id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id + '/available',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}