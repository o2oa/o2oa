function password_change() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>oldPassword:</td><td><input type="password"  style="width:95%" id="oldPassword"/></td></tr>';
    str += '<tr><td>newPassword:</td><td><input type="password"  style="width:95%" id="newPassword"/></td></tr>';
    str += '<tr><td>confirmPassword:</td><td><input type="password"  style="width:95%" id="confirmPassword"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	password_put();
    });
}

function password_put() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/password',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    oldPassword : $('#oldPassword').val(),
	    newPassword : $('#newPassword').val(),
	    confirmPassword : $('#confirmPassword').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}