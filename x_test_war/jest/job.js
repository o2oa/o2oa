function job_get_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>job:</td><td><input type="text" style="width:95%" id= "job"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	job_get($('#job').val());
    });
}

function job_get(job) {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/job/' + job,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><td>WorkList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.workList, null, 4);
	    str += '</div></td></tr>';
	    str += '<tr><td>TaskList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.taskList, null, 4);
	    str += '</div></td></tr>';
	    str += '<tr><td>TaskCompletedList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.taskCompletedList, null, 4);
	    str += '</div></td></tr>';
	    str += '<tr><td>ReadList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.readList, null, 4);
	    str += '</div></td></tr>';
	    str += '<tr><td>ReadCompletedList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.readCompletedList, null, 4);
	    str += '</div></td></tr>';
	    str += '<tr><td>ReviewList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.reviewList, null, 4);
	    str += '</div></td></tr>';
	    str += '<tr><td>WorkLogList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.workLogList, null, 4);
	    str += '</div></td></tr>';
	    str += '<tr><td>AttachmentList:</td><td><div style="width: 100%; white-space: pre; font-size: 10px; word-break: break-all; word-wrap: break-word;">';
	    str += JSON.stringify(json.data.attachmentList, null, 4);
	    str += '</div></td></tr>';
	    str += '</table>';
	    $('#content').html(str);
	} else {
	    failure(json);
	}
    });
}