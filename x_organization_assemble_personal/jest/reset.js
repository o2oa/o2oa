function reset_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="checkCredential">checkCredential</a>&nbsp;<a href="#" id="checkPassword">checkPassword</a>&nbsp;<a href="#" id="createCode">createCode</a>&nbsp;<a href="#" id="update">update</a></td></tr>';
    str += '<tr><td>credential:</td><td><input type="text" style="width:95%" id= "credential"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id= "password"/></td></tr>';
    str += '<tr><td>codeAnswer:</td><td><input type="text" style="width:95%" id= "codeAnswer"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#checkCredential').click(function() {
	reset_check_credential($('#credential').val());
    });
    $('#checkPassword').click(function() {
	reset_check_password($('#password').val());
    });
    $('#createCode').click(function() {
	reset_create_code($('#credential').val());
    });

    $('#update').click(function() {
	reset_update($('#credential').val(), $('#password').val(), $('#codeAnswer').val());
    });
}

function reset_check_credential(credential) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/reset/check/credential/' + credential,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function reset_check_password(password) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/reset/check/password/' + password,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function reset_create_code(credential) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/reset/code/credential/' + credential,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function reset_update(credential, password, codeAnswer) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/reset',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    credential : credential,
	    password : password,
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