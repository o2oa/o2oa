var packagename = "okrconfigsecretary";
function listMyConfig() {
	var query_url = '../jaxrs/'+packagename+'/list/my';
	alert( query_url );
	send_get_request( query_url );
}