sso_parameter = {};

function sso_login_init() {
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="post">post</a></td></tr></thead>';
    str += '<tbody id="grid">';
    str += '<tr><td>client:</td><td><input id ="client" type="text" style="width:95%"/></td></tr>';
    str += '<tr><td>token:</td><td><input id="token" type="text" style="width:95%"/></td></tr>';
    str += '</tbody>'
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	sso_login_get($('#client').val(), $('#token').val());
    });
    $('#post').click(function() {
	sso_login_post($('#client').val(), $('#token').val());
    });
}

function sso_login_get(client, token) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/sso/client/' + client + '/token/' + token,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function sso_login_post(client, token) {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/sso',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    client : client,
	    token : token
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}