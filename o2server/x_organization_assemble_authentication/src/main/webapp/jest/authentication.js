function authentication_login_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="checkCredential">checkCredential</a>&nbsp;<a href="#" id="login">login</a>&nbsp;<a href="#" id="createCode">createCode</a>&nbsp;<a href="#" id="codeLogin">codeLogin</a>&nbsp;<a href="#" id="createCaptcha">createCaptcha</a>&nbsp;<a href="#" id="captchaLogin">captchaLogin</a>&nbsp;<a href="#" id="createBind">createBind</a>&nbsp;<a href="#" id="bindLogin">bindLogin</a></td></tr>';
    str += '<tr><td>credential:</td><td><input type="text" id="credential" style="width:95%"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" id="password" style="width:95%"/></td></tr>';
    str += '<tr><td>width:</td><td><input type="text" value="120" id="width" style="width:95%"/></td></tr>';
    str += '<tr><td>height:</td><td><input type="text" value="50" id="height" style="width:95%"/></td></tr>';
    str += '<tr><td>codeAnswer:</td><td><input type="text" id="codeAnswer" style="width:95%"/></td></tr>';
    str += '<tr><td>captcha:</td><td><input type="text" id="captcha" style="width:95%"/></td></tr>';
    str += '<tr><td>captchaAnswer:</td><td><input type="text" id="captchaAnswer" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2" id="captchaImage">&nbsp;</td></tr>';
    str += '<tr><td>meta:</td><td><input type="text" id="meta" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2" id="bindImage">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#checkCredential').click(function() {
	authentication_check_credential($('#credential').val());
    });
    $('#login').click(function() {
	authentication_login($('#credential').val(), $('#password').val());
    });
    $('#createCode').click(function() {
	authentication_create_code($('#credential').val());
    });
    $('#codeLogin').click(function() {
	authentication_login_code($('#credential').val(), $('#codeAnswer').val());
    });
    $('#createCaptcha').click(function() {
	authentication_create_captcha($('#width').val(), $('#height').val());
    });
    $('#captchaLogin').click(function() {
	authentication_login_captcha($('#credential').val(), $('#password').val(), $('#captcha').val(), $('#captchaAnswer').val());
    });
    $('#createBind').click(function() {
	authentication_create_bind();
    });
    $('#bindLogin').click(function() {
	authentication_login_bind($('#meta').val());
    });
}

function authentication_check_credential(credential) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/authentication/check/credential/' + credential,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_login(credential, password) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/authentication',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    credential : credential,
	    password : password
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_create_code(credential) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/authentication/code/credential/' + credential,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_login_code(credential, codeAnswer) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/authentication/code',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    credential : credential,
	    codeAnswer : codeAnswer
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_create_captcha(width, height) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/authentication/captcha/width/' + width + '/height/' + height,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#captchaImage').html('<img src="data:image/png;base64,' + json.data.image + '"/>');
	$('#captcha').val(json.data.id);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_login_captcha(credential, password, captcha, captchaAnswer) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/authentication/captcha',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    credential : credential,
	    password : password,
	    captcha : captcha,
	    captchaAnswer : captchaAnswer
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_create_bind() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/authentication/bind',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#bindImage').html('<img src="data:image/png;base64,' + json.data.image + '"/>');
	$('#meta').val(json.data.meta);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function authentication_login_bind(meta) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/authentication/bind/meta/' + meta,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
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
    }).always(function(json) {
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
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}