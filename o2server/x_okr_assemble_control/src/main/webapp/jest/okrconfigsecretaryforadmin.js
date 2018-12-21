var packagename = "admin/okrconfigsecretary";
function query_by_id() {
	var id = $("#id").val()
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
	var query_url = '../jaxrs/'+packagename+'/' + id;
	alert( query_url );
	send_get_request( query_url );
}

function saveEntity() {
	var url = '../jaxrs/' + packagename;
	alert( url );
	send_post_request( url );
}

function query_put() {
	var url = '../jaxrs/' + packagename;
	var count = $("#count").val();
	var id = $("#id").val();
	var put_url = $("#put_url").val();
	put_url = put_url.replace("{id}", id );
	put_url = put_url.replace("{count}", count );
	alert( url );
	send_put_request( url );
}

function deleteEntity( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
	var url = '../jaxrs/' + packagename ;
	alert( url );
	send_delete_request( url, id );
}
