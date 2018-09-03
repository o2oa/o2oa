function companyAttribute_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td>function:</td><td id="id"><select id="function">';
    str += '<option value="getWithNameWithCompany">getWithNameWithCompany</option>';
    str += '<option value="listWithCompany">listWithCompany</option>';
    str += '</select></td></tr>';
    str += '<tr><td>argument one:</td><td><input type="text"  style="width:95%" id="argumentOne"/></td></tr>';
    str += '<tr><td>argument two:</td><td><input type="text"  style="width:95%" id="argumentTwo"/></td></tr>';
    str += '<tr><td>url:</td><td id="url">&nbsp;</td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	companyAttribute_get();
    });
}

function companyAttribute_get() {
    $('#result').html('');
    var url = companyAttribute_getUrl();
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

function companyAttribute_getUrl() {
    var url = '../jaxrs/companyattribute';
    switch ($('#function').val()) {
    case 'getWithNameWithCompany':
	url += '/' + $('#argumentOne').val() + '/company/' + $('#argumentTwo').val();
	break;
    case 'listWithCompany':
	url += '/list/company/' + $('#argumentOne').val();
	break;
    }
    return url;
}