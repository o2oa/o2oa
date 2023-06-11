storageMappings_parameter = {};

function storageMappings_get() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/storagemappings',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}