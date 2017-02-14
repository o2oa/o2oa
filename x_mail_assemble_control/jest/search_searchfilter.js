var service_base_path = '../jaxrs/searchfilter';
function get_searchfilter( ) {
	var service_url = $("#service_url").val();
	var query_url = service_base_path + "/" +service_url;
	
	//获取用户所填写的参数
	
	//根据参数组织URL
	if( "list/filter" == service_url ){
		
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
