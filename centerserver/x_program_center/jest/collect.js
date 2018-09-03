collect_parameter = {};

function collect_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="update">update</a>&nbsp;<a href="#" id="connect">connect</a>&nbsp;<a href="#" id="nameExist">nameExist</a>&nbsp;<a href="#" id="code">code</a>&nbsp;<a href="#" id="regist">regist</a>&nbsp;<a href="#" id="controllerMobile">controllerMobile</a>&nbsp;<a href="#" id="resetPassword">resetPassword</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="validate">validate</a>&nbsp;<a href="#" id="validateDirect">validateDirect</a>&nbsp;<a href="#" id="validateCodeAnswer">validateCodeAnswer</a>&nbsp;<a href="#" id="validatePassword">validatePassword</a></td></tr>';
    str += '<tr><td>enable:</td><td><select id="enable"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>name:</td><td><input id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>password:</td><td><input id="password" type="password" style="width:95%"/></td></tr>';
    str += '<tr><td>mobile:</td><td><input id="mobile" style="width:95%"/></td></tr>';
    str += '<tr><td>codeAnswer:</td><td><input id="codeAnswer" style="width:95%"/></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	collect_get();
    });
    $('#update').click(function() {
	collect_update();
    });
    $('#connect').click(function() {
	collect_connect();
    });
    $('#validate').click(function() {
	collect_validate();
    });
    $('#validateDirect').click(function() {
	collect_validateDirect();
    });
    $('#validateCodeAnswer').click(function() {
	collect_validateCodeAnswer();
    });
    $('#validatePassword').click(function() {
	collect_validatePassword();
    });
    $('#nameExist').click(function() {
	collect_name_exist($('#name').val());
    });
    $('#code').click(function() {
	collect_code($('#mobile').val());
    });
    $('#regist').click(function() {
	collect_regist();
    });
    $('#controllerMobile').click(function() {
	collect_controller_mobile($('#name').val(), $('#mobile').val());
    });
    $('#resetPassword').click(function() {
	collect_reset_password();
    });
}

function collect_get() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/collect',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    }).done(function(json) {
	$('#enable').val(json.data.enable + '');
	$('#name').val(json.data.name);
	$('#password').val(json.data.password);
    });
}

function collect_update() {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/collect',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    enable : $('#enable').val(),
	    name : $('#name').val(),
	    password : $('#password').val()
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_connect() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/collect/connect',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_validate() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/collect/validate',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_validateDirect() {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/collect/validate/direct',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    name : $('#name').val(),
	    password : $('#password').val()
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_validatePassword() {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/collect/validate/password',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    password : $('#password').val()
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_validateCodeAnswer() {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/collect/validate/codeanswer',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    mobile : $('#mobile').val(),
	    codeAnswer : $('#codeAnswer').val()
	}),
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_code(mobile) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/collect/code/mobile/' + mobile,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_name_exist(name) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/collect/name/' + name + '/exist',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_regist() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/collect',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    password : $('#password').val(),
	    mobile : $('#mobile').val(),
	    codeAnswer : $('#codeAnswer').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_reset_password() {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/collect/resetpassword',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    password : $('#password').val(),
	    mobile : $('#mobile').val(),
	    codeAnswer : $('#codeAnswer').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function collect_controller_mobile(name, mobile) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/collect/controllermobile/name/' + name + '/mobile/' + mobile,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}