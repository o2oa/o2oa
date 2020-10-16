state_parameter = {};

function state_summary() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/state/summary',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function state_running() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/state/running',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function state_organization() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/state/organization',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function state_category() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/state/category',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}
