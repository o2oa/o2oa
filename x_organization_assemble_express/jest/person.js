function person_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td>function:</td><td id="id"><select id="function">';
    str += '<option value="getWithName">getWithName</option>';
    str += '<option value="getWithIdentity">getWithIdentity</option>';
    str += '<option value="getWithCredential">getWithCredential</option>';
    str += '<option value="listWithDepartment">listWithDepartment</option>';
    str += '<option value="listWithGroupSubDirect">listWithGroupSubDirect</option>';
    str += '<option value="listWithGroupSubNested">listWithGroupSubNested</option>';
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
	person_get();
    });
}

function person_get() {
    $('#result').html('');
    var url = person_getUrl();
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

function person_getUrl() {
    var url = '../jaxrs/person/';
    switch ($('#function').val()) {
    case 'getWithName':
	url += $('#argumentOne').val();
	break;
    case 'getWithIdentity':
	url += 'identity/' + $('#argumentOne').val();
	break;
    case 'getWithCredential':
	url += 'credential/' + $('#argumentOne').val();
	break;
    case 'listWithDepartment':
	url += 'department/' + $('#argumentOne').val();
	break;
    case 'listWithGroupSubDirect':
	url += 'list/group/' + $('#argumentOne').val() + '/sub/direct';
	break;
    case 'listWithGroupSubNested':
	url += 'list/group/' + $('#argumentOne').val() + '/sub/nested';
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