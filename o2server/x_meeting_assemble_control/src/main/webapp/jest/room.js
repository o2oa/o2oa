room_parameter = {};

function room_grid(items) {
    var str = '<table border="1" width="100%"><tbody>';
    str += '<tr><th>id</th><th>name</th><th>address</th><th>operate</th></tr>';
    if (items) {
	$.each(items, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.name + '</td><td colspan="2">';
	    if (item.photo && item.photo != '') {
		str += imageBase64(item.photo);
	    } else {
		str += '&nbsp;';
	    }
	    str += '</td><td>';
	    str += '<a href="#" onclick="room_put_init(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="room_delete(\'' + item.id + '\')">delete</a>&nbsp;';
	    str += '<a href="#" onclick="room_update_photo_init(\'' + item.id + '\')">photo</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    str += '</tobdy></table>';
    $('#content').html(str);
}

function room_list() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/room/list',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    room_grid(json.data, true);
	}
    });
}

function room_get_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	room_delete(id);
    });
}

function room_get(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/room/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function room_post_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>building:</td><td><input type="text" id="building" style="width:95%"/></td></tr>';
    str += '<tr><td>floor:</td><td><input type="text" id="floor" style="width:95%"/></td></tr>';
    str += '<tr><td>roomNumber:</td><td><input type="text" id="roomNumber" style="width:95%"/></td></tr>';
    str += '<tr><td>capacity:</td><td><input type="text" id="capacity" style="width:95%"/></td></tr>';
    str += '<tr><td>auditor:</td><td><input type="text" id="auditor" style="width:95%"/></td></tr>';
    str += '<tr><td>available:</td><td><select id="available"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>device:</td><td><textarea id="device" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	room_post();
    });
}

function room_post() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/room',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    building : $('#building').val(),
	    floor : $('#floor').val(),
	    roomNumber : $('#roomNumber').val(),
	    capacity : $('#capacity').val(),
	    auditor : $('#auditor').val(),
	    available : $('#available').val(),
	    device : $('#device').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function room_put_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="select">select</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#select').click(function() {
	room_put_init(id);
    });
}

function room_put_init(id) {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id"></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>building:</td><td><input type="text" id="building" style="width:95%"/></td></tr>';
    str += '<tr><td>floor:</td><td><input type="text" id="floor" style="width:95%"/></td></tr>';
    str += '<tr><td>roomNumber:</td><td><input type="text" id="roomNumber" style="width:95%"/></td></tr>';
    str += '<tr><td>capacity:</td><td><input type="text" id="capacity" style="width:95%"/></td></tr>';
    str += '<tr><td>auditor:</td><td><input type="text" id="auditor" style="width:95%"/></td></tr>';
    str += '<tr><td>available:</td><td><select id="available"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>device:</td><td><textarea id="device" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td>photo:</td><td id="photo">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	room_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/room/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#name').val(json.data.name);
	    $('#building').val(json.data.building);
	    $('#floor').val(json.data.floor);
	    $('#roomNumber').val(json.data.roomNumber);
	    $('#capacity').val(json.data.capacity);
	    $('#auditor').val(json.data.auditor);
	    $('#available').val(json.data.available + '');
	    $('#device').val(json.data.device);
	}
    });
}

function room_put(id) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/room/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    building : $('#building').val(),
	    floor : $('#floor').val(),
	    roomNumber : $('#roomNumber').val(),
	    capacity : $('#capacity').val(),
	    auditor : $('#auditor').val(),
	    available : $('#available').val(),
	    device : $('#device').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function room_delete_select() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="delete">delete</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#delete').click(function() {
	room_delete(id);
    });
}

function room_delete(id) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/room/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function room_put_photo_select(id) {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>photo:</td><td><input type="file" id="file" style="width:95%" ></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	room_put_photo(id);
    });
}

function room_put_photo(id) {
    $('#result').html('');
    var formData = new FormData();
    $.each($('input[type=file]'), function(index, item) {
	formData.append('file', item.files[0]);
    });
    $.ajax({
	type : 'POST',
	url : '../servlet/room/' + id + '/photo',
	data : formData,
	contentType : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    });
}

function room_search_select() {
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
	room_listPinyinInitial($('#key').val());
    });
    $('#listLike').click(function() {
	room_listLike($('#key').val());
    });
    $('#listLikePinyin').click(function() {
	room_listLikePinyin($('#key').val());
    });
}

function room_listPinyinInitial(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/room/list/pinyininitial/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	room_grid(json.data);
    });
}

function room_listLike(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/room/list/like/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	room_grid(json.data);
    });
}

function room_listLikePinyin(key) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/room/list/like/pinyin/' + key,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	room_grid(json.data);
    });
}