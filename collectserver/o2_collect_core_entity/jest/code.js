function smsCode_send(phone, name) {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/smscode',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    phone : phone,
	    name : name
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}