function regist_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="mode">mode</a>&nbsp;<a href="#" id="createCaptcha">createCaptcha</a>&nbsp;<a href="#" id="createCode">createCode</a>&nbsp;<a href="#" id="checkName">checkName</a>&nbsp;<a href="#" id="checkMobile">checkMobile</a>&nbsp;<a href="#" id="checkPassword">checkPassword</a>&nbsp;<a href="#" id="create">create</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id= "name"/></td></tr>';
    str += '<tr><td>mobile:</td><td><input type="text" style="width:95%" id= "mobile"/></td></tr>';
    str += '<tr><td>genderType:</td><td><select id= "genderType"><option value="f">female</option><option value="m">male</option><option value="d">secret</option></select></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id= "password"/></td></tr>';
    str += '<tr><td>width:</td><td><input type="text" style="width:95%" value="120" id= "width"/></td></tr>';
    str += '<tr><td>height:</td><td><input type="text" style="width:95%" value="50" id= "height"/></td></tr>';
    str += '<tr><td>captcha:</td><td><input type="text" style="width:95%" id= "captcha"/></td></tr>';
    str += '<tr><td>captchaAnswer:</td><td><input type="text" style="width:95%" id= "captchaAnswer"/></td></tr>';
    str += '<tr><td>codeAnswer:</td><td><input type="text" style="width:95%" id= "codeAnswer"/></td></tr>';
    str += '<tr><td colspan="2" id="image">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#mode').click(function() {
	regist_mode();
    });
    $('#createCaptcha').click(function() {
	regist_captcha($('#width').val(), $('#height').val());
    });
    $('#createCode').click(function() {
	regist_code($('#mobile').val());
    });
    $('#checkName').click(function() {
	regist_check_name($('#name').val());
    });
    $('#checkMobile').click(function() {
	regist_check_mobile($('#mobile').val());
    });
    $('#checkPassword').click(function() {
	regist_check_password($('#password').val());
    });
    $('#create').click(function() {
	regist_create();
    });
}

function regist_mode() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/regist/mode',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function regist_captcha(width, height) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/regist/captcha/width/' + width + '/height/' + height,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#image').html('<img src="data:image/png;base64,' + json.data.image + '"/>');
	$('#captcha').val(json.data.id);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function regist_code(mobile) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/regist/code/mobile/' + mobile,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function regist_check_mobile(mobile) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/regist/check/mobile/' + mobile,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function regist_check_password(password) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/regist/check/password/' + password,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function regist_check_name(name) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/regist/check/name/' + name,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function regist_create() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/regist',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    genderType : $('#genderType').val(),
	    password : $('#password').val(),
	    mobile : $('#mobile').val(),
	    captcha : $('#captcha').val(),
	    captchaAnswer : $('#captchaAnswer').val(),
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