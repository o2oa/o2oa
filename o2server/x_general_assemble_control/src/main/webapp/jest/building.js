building_parameter = {

};

function building_grid(items, gridRoom) {
    var str = '<table border="1" width="100%"><tbody>';
    str += '<tr><th>id</th><th>name</th><th>address</th><th>operate</th></tr>';
    if (items) {
	$.each(items, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.address + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="building_put_init(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="building_delete(\'' + item.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	    if (gridRoom) {
		str += building_grid_room(item.roomList);
	    }
	});
    }
    str += '</tobdy></table>';
    $('#content').html(str);
}

function building_grid_room(items) {
    var str = "";
    if (items) {
	$.each(items, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.id + '</td><td>';
	    if (item.photo && item.photo != '') {
		str += '<img src="data:image/png;base64,' + item.photo + '"/>';
	    } else {
		str += '&nbsp;';
	    }
	    str += '</td><td>';
	    str += '<a href="#" onclick="room_put_select(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="room_delete_select(\'' + item.id + '\')">delete</a>&nbsp;';
	    str += '<a href="#" onclick="room_update_photo_init(\'' + item.id + '\')">photo</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    return str;
}

function building_list() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/building/list',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    building_grid(json.data, true);
	}
    });
}

function building_listCheckRoomIdle_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="list">list</a>';
    str += '</td></tr>';
    str += '<tr><td>start(yyyy-MM-dd HH:mm):</td><td><input type="text" id="start" style="width:95%"/></td></tr>';
    str += '<tr><td>completed(yyyy-MM-dd HH:mm):</td><td><input type="text" id="completed" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list').click(function() {
	building_listCheckRoomIdle($('#start').val(), $('#completed').val());
    });
}

function building_listCheckRoomIdle(start, completed) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/building/list//start/' + start + '/completed/' + completed,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    building_grid(json.data, true);
	}
    });
}

function building_post_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>address:</td><td><input type="text" id="address" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	building_post();
    });
}

function building_post() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/building',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    address : $('#address').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function building_put_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">';
    str += '<a href="#" id="select">select</a>';
    str += '</td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#select').click(function() {
	building_put_init($('#id').val());
    });
}

function building_put_init(id) {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id"></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>address:</td><td><input type="text" id="address" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	building_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/building/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#name').val(json.data.name);
	    $('#address').val(json.data.address);
	}
    });
}

function building_put(id) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/building/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    address : $('#address').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function building_delete_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">';
    str += '<a href="#" id="delete">delete</a>';
    str += '</td></tr>';
    str += '<tr><td>id:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#select').click(function() {
	building_delete($('#id').val());
    });
}

function building_delete(id) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/building/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function building_search_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">';
    str += '<a href="#" id="listPinyinInitial">listPinyinInitial</a>';
    str += '&nbsp;';
    str += '<a href="#" id="listLike">listLike</a>';
    str += '&nbsp;';
    str += '<a href="#" id="listLikePinyin">listLikePinyin</a>';
    str += '</td></tr>';
    str += '<tr><td>key:</td><td><input type="text" id="key" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#listPinyinInitial').click(function() {
	building_listPinyinInitial($('#key').val());
    });
    $('#listLike').click(function() {
	building_listLike($('#key').val());
    });
    $('#listLikePinyin').click(function() {
	building_listLikePinyin($('#key').val());
    });
}

function building_listPinyinInitial(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/building/list/pinyininitial/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	building_grid(json.data, false);
    });
}

function building_listLike(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/building/list/like/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	building_grid(json.data, false);
    });
}

function building_listLikePinyin(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/building/list/like/pinyin/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	building_grid(json.data, false);
    });
}