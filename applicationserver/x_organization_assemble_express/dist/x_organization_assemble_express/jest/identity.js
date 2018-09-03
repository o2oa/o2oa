function identity_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td>function:</td><td id="id"><select id="function">';
    str += '<option value="getWithName">getWithName</option>';
    str += '<option value="listWithPerson">listWithPerson</option>';
    str += '<option value="listWithDepartmentSubDirect">listWithDepartmentSubDirect</option>';
    str += '<option value="listWithDepartmentSubNested">listWithDepartmentSubNested</option>';
    str += '<option value="listWithCompanySubDirect">listWithCompanySubDirect</option>';
    str += '<option value="listWithCompanySubNested">listWithCompanySubNested</option>';
    str += '<option value="listLikeWithCompanySubNestedWithDepartmentSubNested">listLikeWithCompanySubNestedWithDepartmentSubNested</option>';
    str += '<option value="listPinyinInitial">listPinyinInitial</option>';
    str += '<option value="listLikePinyin">listLikePinyin</option>';
    str += '<option value="listLike">listLike</option>';
    str += '</select></td></tr>';
    str += '<tr><td>argument one:</td><td><input type="text"  style="width:95%" id="argumentOne"/></td></tr>';
    str += '<tr><td>url:</td><td id="url">&nbsp;</td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	identity_get();
    });
}

function identity_get() {
    $('#result').html('');
    var url = identity_getUrl();
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

function identity_getUrl() {
    var url = '../jaxrs/identity/';
    switch ($('#function').val()) {
    case 'getWithName':
	url += $('#argumentOne').val();
	break;
    case 'listWithPerson':
	url += 'list/person/' + $('#argumentOne').val();
	break;
    case 'listWithDepartmentSubDirect':
	url += 'list/department/' + $('#argumentOne').val() + '/sub/direct';
	break;
    case 'listWithDepartmentSubNested':
	url += 'list/department/' + $('#argumentOne').val() + '/sub/nested';
	break;
    case 'listWithCompanySubDirect':
	url += 'list/company/' + $('#argumentOne').val() + '/sub/direct';
	break;
    case 'listWithCompanySubNested':
	url += 'list/company/' + $('#argumentOne').val() + '/sub/nested';
	break;
    case 'listLikeWithCompanySubNestedWithDepartmentSubNested':
	url += 'list/company/sub/nested/department/sub/nested/like/' + $('#argumentOne').val();
	break;
    case 'listPinyinInitial':
	url += 'list/pinyininitial/' + $('#argumentOne').val();
	break;
    case 'listLikePinyin':
	url += 'list/like/pinyin/' + $('#argumentOne').val();
	break;
    case 'listLike':
	url += 'list/like/' + $('#argumentOne').val();
	break;
    }
    return url;
}