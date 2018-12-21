meeting_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function meeting_list_next(id) {
    $('#result').html('');
    var id = (id ? id : meeting_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/' + id + '/next/' + meeting_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		meeting_parameter.first = json.data[0].id;
		meeting_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		meeting_parameter.first = '(0)';
	    }
	    meeting_grid(json.data);
	} else {
	    failure(data);
	}
    });
}

function meeting_list_prev(id) {
    $('#result').html('');
    var id = (id ? id : meeting_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/' + id + '/prev/' + meeting_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		meeting_parameter.first = json.data[0].id;
		meeting_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		meeting_parameter.last = '(0)';
	    }
	    meeting_grid(json.data);
	} else {
	    failure(data);
	}
    });
}

function meeting_grid(items) {
    var str = '<table border="1" width="100%"><tobdy>';
    str += '<tr><th>rank</th><th>id</th><th>subject</th><th>confirmStatus</th><th>manualCompleted</th></tr>';
    if (items) {
	$.each(items, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.rank + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.subject + '</td>';
	    str += '<td>' + item.confirmStatus + '</td>';
	    str += '<td>' + item.manualCompleted + '</td>';
	    str += '</tr>';
	});
    }
    str += '</tobdy></table>';
    $('#content').html(str);
}

function meeting_list_waitConfirm() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/wait/confirm',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}

function meeting_listOnMonth_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>year:</td><td><input type="text" id="year" style="width:95%"/></td></tr>';
    str += '<tr><td>month:</td><td><input type="text" id="month" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	meeting_listOnMonth($('#year').val(), $('#month').val());
    });
}

function meeting_listOnMonth(year, month) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/year/' + year + '/month/' + month,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}

function meeting_listOnDay_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>year:</td><td><input type="text" id="year" style="width:95%"/></td></tr>';
    str += '<tr><td>month:</td><td><input type="text" id="month" style="width:95%"/></td></tr>';
    str += '<tr><td>day:</td><td><input type="text" id="day" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	meeting_listOnDay($('#year').val(), $('#month').val(), $('#day').val());
    });
}

function meeting_listOnDay(year, month, day) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/year/' + year + '/month/' + month + '/day/' + day,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}

function meeting_get_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	meeting_get($('#id').val());
    });
}

function meeting_get(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_post_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>room:</td><td><input type="text" id="room" style="width:95%"/></td></tr>';
    str += '<tr><td>subject:</td><td><input type="text" id="subject" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>startTime:</td><td><input type="text" id="startTime" style="width:95%"/></td></tr>';
    str += '<tr><td>completedTime:</td><td><input type="text" id="completedTime" style="width:95%"/></td></tr>';
    str += '<tr><td>invitePersonList:</td><td><textarea id="invitePersonList"  style="width:95%"/></td></tr>';
    str += '<tr><td>memo:</td><td><textarea id="memo"  style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	meeting_post();
    });
}

function meeting_post() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/meeting',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    room : $('#room').val(),
	    subject : $('#subject').val(),
	    description : $('#description').val(),
	    startTime : $('#startTime').val(),
	    completedTime : $('#completedTime').val(),
	    memo : $('#memo').val(),
	    invitePersonList : splitValue($('#invitePersonList').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_put_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	meeting_put_init($('#id').val());
    });
}

function meeting_put_init(id) {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id"></td></tr>';
    str += '<tr><td>applicant:</td><td id="applicant"></td></tr>';
    str += '<tr><td>room:</td><td><input type="text" id="room" style="width:95%"/></td></tr>';
    str += '<tr><td>subject:</td><td><input type="text" id="subject" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>startTime:</td><td><input type="text" id="startTime" style="width:95%"/></td></tr>';
    str += '<tr><td>completedTime:</td><td><input type="text" id="completedTime" style="width:95%"/></td></tr>';
    str += '<tr><td>invitePersonList:</td><td><textarea id="invitePersonList" style="width:95%"/></td></tr>';
    str += '<tr><td>acceptPersonList:</td><td><textarea id="acceptPersonList" style="width:95%"/></td></tr>';
    str += '<tr><td>rejectPersonList:</td><td><textarea id="rejectPersonList" style="width:95%"/></td></tr>';
    str += '<tr><td>memo:</td><td><textarea id="memo" style="width:95%"/></td></tr>';
    str += '<tr><td>confirmStatus:</td><td><select id="confirmStatus"><option value="wait">wait</option><option value="allow">allow</option><option value="deny">deny</option></select></td></tr>';
    str += '<tr><td>manualCompleted:</td><td><select id="manualCompleted"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	meeting_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#applicant').html(json.data.applicant);
	    $('#room').val(json.data.room);
	    $('#subject').val(json.data.subject);
	    $('#description').val(json.data.description);
	    $('#startTime').val(json.data.startTime);
	    $('#completedTime').val(json.data.completedTime);
	    $('#invitePersonList').val(joinValue(json.data.invitePersonList));
	    $('#acceptPersonList').val(joinValue(json.data.acceptPersonList));
	    $('#rejectPersonList').val(joinValue(json.data.rejectPersonList));
	    $('#memo').val(joinValue(json.data.memo));
	    $('#confirmStatus').val(json.data.confirmStatus);
	    $('#manualCompleted').val(json.data.manualCompleted + '');
	}
    });
}

function meeting_put(id) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/room/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    room : $('#room').val(),
	    subject : $('#subject').val(),
	    description : $('#description').val(),
	    startTime : $('#startTime').val(),
	    completedTime : $('#completedTime').val(),
	    invitePersonList : splitValue($('#invitePersonList').val()),
	    acceptPersonList : splitValue($('#acceptPersonList').val()),
	    rejectPersonList : splitValue($('#rejectPersonList').val()),
	    memo : $('#memo').val(),
	    confirmStatus : $('#confirmStatus').val(),
	    manualCompleted : $('#manualCompleted').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_delete_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>delete id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#delete').click(function() {
	meeting_delete($('#id').val());
    });
}

function meeting_delete(id) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_confirm_allow_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="allow">allow</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#allow').click(function() {
	meeting_confirm_allow($('#id').val());
    });
}

function meeting_confirm_allow(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id + '/confirm/allow',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_confirm_deny_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="deny">deny</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#deny').click(function() {
	meeting_confirm_deny($('#id').val());
    });
}

function meeting_confirm_deny(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id + '/confirm/deny',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_accept_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="accept">accept</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#accept').click(function() {
	meeting_accept($('#id').val());
    });
}

function meeting_accept(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id + '/accept',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_reject_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="reject">reject</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#reject').click(function() {
	meeting_reject($('#id').val());
    });
}

function meeting_reject(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id + '/reject',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_manualCompleted_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="completed">completed</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#completed').click(function() {
	meeting_manualCompleted($('#id').val());
    });
}

function meeting_manualCompleted(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id + '/manual/completed',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_addInvite_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="add">add</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>invitePersonList:</td><td><textarea type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#add').click(function() {
	meeting_addInvite($('#id').val());
    });
}

function meeting_addInvite(id) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/meeting/' + id + '/add/invite',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    invitePersonList : splitValue($('#invitePersonList').val())
	}),
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function meeting_listComingMonth_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>count:</td><td><input type="count" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	meeting_listComingMonth($('#count').val());
    });
}

function meeting_listComingMonth(count) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/coming/month/' + count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}

function meeting_listComingDay_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td>count:</td><td><input type="count" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	meeting_listComingDay($('#count').val());
    });
}

function meeting_listComingDay(count) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/coming/day/' + count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}

function meeting_search_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">';
    str += '<a href="#" id="listPinyinInitial">listPinyinInitial</a>';
    str += '&nbsp;';
    str += '<a href="#" id="listLike">listLike</a>';
    str += '&nbsp;';
    str += '<a href="#" id="listLikePinyin">listLikePinyin</a>';
    str += '</td></tr>';
    str += '<tr><td>key:</td><td><input type="text" id="key" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#listPinyinInitial').click(function() {
	meeting_listPinyinInitial($('#key').val());
    });
    $('#listLike').click(function() {
	meeting_listLike($('#key').val());
    });
    $('#listLikePinyin').click(function() {
	meeting_listLikePinyin($('#key').val());
    });
}

function meeting_listPinyinInitial(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/pinyininitial/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}

function meeting_listLike(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/like/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}

function meeting_listLikePinyin(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/meeting/list/like/pinyin/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	meeting_grid(json.data);
    });
}