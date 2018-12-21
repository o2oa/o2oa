var packagename = "okrworkproblemprocesslog";
function query_by_id( id ) {
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

function deleteEntity( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
	var url = '../jaxrs/' + packagename ;
	alert( url );
	send_delete_request( url, id );
}
