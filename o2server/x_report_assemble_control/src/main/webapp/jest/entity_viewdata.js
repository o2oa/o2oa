var service_base_path = '../jaxrs/view';
function dataList() {

	var query_url = service_base_path + "/viewdata/list/{id}/next/{count}";
	var id = $("#id").val();
	var count = $("#count").val();
	
	query_url = query_url.replace( "{id}",id );
	query_url = query_url.replace( "{count}",count );
	
    $.ajax({
		type : 'post',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : query_url ,
		xhrFields : {
		    'withCredentials' : true
		},
		data : JSON.stringify($.parseJSON($('#content').val())),
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}