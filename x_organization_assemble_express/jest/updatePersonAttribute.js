function updatePersonAttribute_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td>name:</td><td><input type="text"  style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>attributeList:</td><td><textarea  style="width:95%" id="attributeList"/></td></tr>';
    str += '<tr><td>url:</td><td id="url">&nbsp;</td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	updatePersonAttribute_put();
    });
}

function updatePersonAttribute_put() {
    $('#result').html('');
    var url = updatePersonAttribute_getUrl();
    $('#url').html(url);
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : url,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    attributeList : splitValue($('#attributeList').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function updatePersonAttribute_getUrl() {
    var url = '../jaxrs/updatepersonattribute';
    url += '/' + $('#name').val();
    return url;
}