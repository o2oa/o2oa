function authentication_login() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="login">login</a></tr>';
    str += '<tr><td>credential:</td><td><input type="text" id="credential" style="width:95%"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" id="password" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#login').click(function() {
	authentication_post();
    });
}

function authentication_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/authentication',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    credential : $('#credential', '#content').val(),
	    password : $('#password', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_logout() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/authentication',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_who() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/authentication',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    authentication_view(json);
	} else {
	    failure(json);
	}
    });
}

function authentication_view(data) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>name:</td><td id="name">&nbsp;</td></tr>';
    str += '<tr><td>display:</td><td id="display">&nbsp;</td></tr>';
    str += '<tr><td>employee:</td><td id="employee">&nbsp;</td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>qq:</td><td id="qq">&nbsp;</td></tr>';
    str += '<tr><td>weixin:</td><td id="weixin">&nbsp;</td></tr>';
    str += '<tr><td>weibo:</td><td id="weibo">&nbsp;</td></tr>';
    str += '<tr><td>mail:</td><td id="mail">&nbsp;</td></tr>';
    str += '<tr><td>mobile:</td><td id="mobile">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    if (data.data) {
	$('#id').html(data.data.id);
	$('#name').html(data.data.name);
	$('#display').html(data.data.display);
	$('#employee').html(data.data.employee);
	$('#qq').html(data.data.qq);
	$('#weibo').html(data.data.weibo);
	$('#weixin').html(data.data.weixin);
	$('#mail').html(data.data.mail);
	$('#mobile').html(data.data.mobile);
    }
}