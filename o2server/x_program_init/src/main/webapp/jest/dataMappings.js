dataMappings_parameter = {};

function dataMappings_get() {
    $('#result').html('');
    $('#content').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/datamappings',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}