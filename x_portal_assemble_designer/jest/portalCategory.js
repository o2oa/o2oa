portalCategory_parameter = {};

function portalCategory_list() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/portalcategory/list',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}