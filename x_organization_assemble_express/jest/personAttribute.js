function personAttribute_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td>function:</td><td id="id"><select id="function">';
    str += '<option value="getWithPerson">getWithPerson</option>';
    str += '<option value="listWithPerson">listWithPerson</option>';
    str += '</select></td></tr>';
    str += '<tr><td>name:</td><td><input type="text"  style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text"  style="width:95%" id="person"/></td></tr>';
    str += '<tr><td>url:</td><td id="url">&nbsp;</td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	personAttribute_get();
    });
}

function personAttribute_get() {
    $('#result').html('');
    var url = personAttribute_getUrl();
    $('#url').html(url);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : url,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function personAttribute_getUrl() {
    var url = '../jaxrs/personattribute';
    switch ($('#function').val()) {
    case 'getWithPerson':
	url += '/' + $('#name').val() + '/person/' + $('#person').val();
	break;
    case 'listWithPerson':
	url += '/list/person/' + $('#person').val();
	break;
    }
    return url;
}