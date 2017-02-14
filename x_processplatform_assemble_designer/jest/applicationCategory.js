applicationCategory_parameter = {};

function applicatiionCategory_list() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/applicationcategory/list',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><th>applicationCategory</th><th>count</th></tr>';
	    $.each(json.data, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.applicationCategory + '</td>';
		str += '<td>' + item.count + '</td>';
		str += '</tr>';
	    });
	    str += '</table>';
	    $('#content').html(str);
	}
    });
}