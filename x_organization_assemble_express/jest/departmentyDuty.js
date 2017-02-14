function departmentDuty_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td>function:</td><td id="id"><select id="function">';
    str += '<option value="getWithNameWithDepartment">getWithNameWithDepartment</option>';
    str += '<option value="listWithDepartment">listWithDepartment</option>';
    str += '<option value="listWithName">listWithName</option>';
    str += '<option value="listWithIdentity">listWithIdentity</option>';
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

function departmentDuty_get() {
    $('#result').html('');
    var url = departmentDuty_getUrl();
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

function departmentDuty_getUrl() {
    var url = '../jaxrs/departmentduty';
    switch ($('#function').val()) {
    case 'getWithNameWithDepartment':
	url += '/' + $('#argumentOne').val() + '/department/' + $('#argumentTwo').val();
	break;
    case 'listWithDepartment':
	url += '/list/department/' + $('#argumentOne').val();
	break;
    case 'listWithName':
	url += '/list/' + $('#argumentOne').val();
	break;
    case 'listWithIdentity':
	url += '/list/identity' + $('#argumentOne').val();
	break;
    }
    return url;
}