resource_parameter = {};

function resource_get_administratorDefinition() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td id="name">&nbsp;</td></tr>';
    str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
    str += '<tr><td>employee:</td><td><input type="text" style="width:95%" id="employee"/></td></tr>';
    str += '<tr><td>display:</td><td><input type="text" style="width:95%" id="display"/></td></tr>';
    str += '<tr><td>mobile:</td><td><input type="text" style="width:95%" id="mobile"/></td></tr>';
    str += '<tr><td>mail:</td><td><input type="text" style="width:95%" id="mail"/></td></tr>';
    str += '<tr><td>weixin:</td><td><input type="text" style="width:95%" id="weixin"/></td></tr>';
    str += '<tr><td>qq:</td><td><input type="text" style="width:95%" id="qq"/></td></tr>';
    str += '<tr><td>weibo:</td><td><input type="text" style="width:95%" id="weibo"/></td></tr>';
    str += '<tr><td>roleList:</td><td><textarea id= "roleList" style="width:95%"/></td></tr>';
    str += '<tr><td>icon:</td><td><textarea id= "icon" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/resource/administrator/definition',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').append(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#name').html(json.data.id);
	    $('#password').val(json.data.password);
	    $('#employee').val(json.data.employee);
	    $('#display').val(json.data.display);
	    $('#mail').val(json.data.mail);
	    $('#weixin').val(json.data.weixin);
	    $('#qq').val(json.data.qq);
	    $('#weibo').val(json.data.weibo);
	    $('#mobile').val(json.data.mobile);
	    $('#roleList').val(joinValue(json.data.roleList));
	    $('#icon').val(json.data.icon);
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	resource_put_administratorDefinition();
    });
}

function resource_put_administratorDefinition() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/resource/administrator/definition',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    password : $('#password').val(),
	    employee : $('#employee').val(),
	    display : $('#display').val(),
	    mail : $('#mail').val(),
	    weixin : $('#weixin').val(),
	    qq : $('#qq').val(),
	    weibo : $('#weibo').val(),
	    mobile : $('#mobile').val(),
	    roleList : splitValue($('#roleList').val()),
	    icon : $('#icon').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function resource_get_collectDefinition() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/resource/collect/definition',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').append(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#enable').val(json.data.enable + '');
	    $('#name').val(json.data.name);
	    $('#password').val(json.data.password);
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	resource_put_collectDefinition();
    });
}

function resource_put_collectDefinition() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/resource/collect/definition',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    enable : $('#enable').val(),
	    name : $('#name').val(),
	    password : $('#password').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function resource_get_openMeetingJunctionDefinition() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>server:</td><td><input type="text" style="width:95%" id="server"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>user:</td><td><input type="text" style="width:95%" id="user"/></td></tr>';
    str += '<tr><td>pass:</td><td><input type="text" style="width:95%" id="pass"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/resource/openmeetingjunction/definition',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').append(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#server').val(json.data.server);
	    $('#port').val(json.data.port);
	    $('#host').val(json.data.host);
	    $('#user').val(json.data.user);
	    $('#pass').val(json.data.pass);
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	resource_put_openMeetingJunctionDefinition();
    });
}

function resource_put_openMeetingJunctionDefinition() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/resource/openmeetingjunction/definition',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    server : $('#server').val(),
	    port : $('#port').val(),
	    host : $('#host').val(),
	    user : $('#user').val(),
	    pass : $('#pass').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function resource_get_passwordDefinition() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>key:</td><td><input type="text" style="width:95%" id="key"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/resource/password/definition',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').append(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#key').val(json.data.key);
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	resource_put_passwordDefinition();
    });
}

function resource_put_passwordDefinition() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/resource/person/definition',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    key : $('#key').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function resource_get_personDefinition() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>defaultPassword:</td><td><input type="text" style="width:95%" id="defaultPassword"/></td></tr>';
    str += '<tr><td>defaultIconMale:</td><td><textarea style="width:95%" id="defaultIconMale"/></td></tr>';
    str += '<tr><td>defaultIconFemale:</td><td><textarea style="width:95%" id="defaultIconFemale"/></td></tr>';
    str += '<tr><td>defaultIcon:</td><td><textarea style="width:95%" id="defaultIcon"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/resource/person/definition',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').append(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#defaultPassword').val(json.data.defaultPassword);
	    $('#defaultIconMale').val(json.data.defaultIconMale);
	    $('#defaultIconFemale').val(json.data.defaultIconFemale);
	    $('#defaultIcon').val(json.data.defaultIcon);
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	resource_put_personDefinition();
    });
}

function resource_put_personDefinition() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/resource/person/definition',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    defaultPassword : $('#defaultPassword').val(),
	    defaultIconMale : $('#defaultIconMale').val(),
	    defaultIconFemale : $('#defaultIconFemale').val(),
	    defaultIcon : $('#defaultIcon').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function resource_get_ssoDefinition() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>key:</td><td><input type="text" style="width:95%" id="key"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/resource/sso/definition',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').append(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#key').val(json.data.key);
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	resource_put_ssoDefinition();
    });
}

function resource_put_ssoDefinition() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/resource/sso/definition',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    key : $('#key').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function resource_get_workTimeDefinition() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>amStart:</td><td><input type="text" style="width:95%" id="amStart"/></td></tr>';
    str += '<tr><td>amEnd:</td><td><input type="text" style="width:95%" id="amEnd"/></td></tr>';
    str += '<tr><td>pmStart:</td><td><input type="text" style="width:95%" id="pmStart"/></td></tr>';
    str += '<tr><td>pmEnd:</td><td><input type="text" style="width:95%" id="pmEnd"/></td></tr>';
    str += '<tr><td>weekends:</td><td><textarea style="width:95%" id="weekends"/></td></tr>';
    str += '<tr><td>holidays:</td><td><textarea style="width:95%" id="holidays"/></td></tr>';
    str += '<tr><td>workdays:</td><td><textarea style="width:95%" id="workdays"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/resource/worktime/definition',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').append(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#amStart').val(json.data.amStart);
	    $('#amEnd').val(json.data.amEnd);
	    $('#pmStart').val(json.data.pmStart);
	    $('#pmEnd').val(json.data.pmEnd);
	    $('#weekends').val(joinValue(json.data.weekends));
	    $('#holidays').val(joinValue(json.data.holidays));
	    $('#workdays').val(joinValue(json.data.workdays));
	} else {
	    failure(json);
	}
    });
    $('#put').click(function() {
	resource_put_workTimeDefinition();
    });
}

function resource_put_workTimeDefinition() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/resource/worktime/definition',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    amStart : $('#amStart').val(),
	    amEnd : $('#amEnd').val(),
	    pmStart : $('#pmStart').val(),
	    pmStart : $('#pmStart').val(),
	    weekends : splitValue($('#weekends').val()),
	    holidays : splitValue($('#holidays').val()),
	    workdays : splitValue($('#workdays').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}