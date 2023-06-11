function connections_view() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td><textarea style="width:95%; height:240px"  id="connections"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/connections',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data) {
		$('#connections', '#content').val(JSON.stringify(data.data, null, '\t'));
	    }
	} else {
	    failure(data);
	}
    });
}