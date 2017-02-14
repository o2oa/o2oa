adminLogin_parameter = {};

function adminLogin_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>credential:</td><td><input type="text" style="width:95%" id="credential"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	adminLogin_login();
    });
}

function adminLogin_login() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/adminlogin',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    credential : $('#credential').val(),
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