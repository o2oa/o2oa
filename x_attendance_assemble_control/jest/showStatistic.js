var service_base_path = '../jaxrs/statisticshow/';
function getStatistic( name, year, month, date ) {
	var query_url = service_base_path + $("#queryUrl").val();
	if( name != null && name != undefined && name != "(0)"){
		query_url = query_url.replace( "{name}", encodeURIComponent(name) );
	}
	if( date != null && date != undefined && date != "(0)"){
		query_url = query_url.replace( "{date}", encodeURIComponent(date) );
	}
	if( year != null && year != undefined && year != "(0)"){
		query_url = query_url.replace( "{year}", year );
	}
	if( month != null && month != undefined && month != "(0)"){
		query_url = query_url.replace( "{month}", month );
	}
	alert(query_url);
    $.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : query_url,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html( JSON.stringify( json, null, 4) );
    }).fail(function(json) {
    	failure(json);
    });
}

function getFilterListForStatistic( last_id, _count ){
	var service_url = $("#putUrl").val();
	var query_url =  service_base_path + "/" + service_url;
	
	if( last_id == null || last_id == undefined ){
		last_id="(0)";
	}
	if( _count == null || _count == undefined ){
		_count=20;
	}
	
	//根据参数组织URL
	query_url = query_url.replace( "{id}",last_id );
	query_url = query_url.replace( "{count}",_count );
	
	alert( "PUT:"+query_url );
	$.ajax({
		type : 'put',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : query_url,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}