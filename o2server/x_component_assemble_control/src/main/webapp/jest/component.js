component_parameter = {};

function component_listAll() {
    $('#content').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/component/list/all',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		var str = '<table border="1" width="100%">';
		str += '<tr><th>name</th><th>title</th><th>widgetName</th><th>widgetTitle</th><th>path</th><th>operate</th></tr>';
		$.each(json.data, function(index, o) {
		    str += '<tr>';
		    str += '<td>' + o.name + '</td>';
		    str += '<td>' + o.title + '</td>';
		    str += '<td>' + o.widgetName + '</td>';
		    str += '<td>' + o.widgetTitle + '</td>';
		    str += '<td>' + o.path + '</td>';
		    str += '<td>';
		    str += '<a href="#" onclick="component_edit(\'' + o.id + '\')">edit</a>&nbsp;';
		    str += '<a href="#" onclick="component_delete(\'' + o.id + '\')">delete</a>';
		    str += '</td>';
		    str += '</tr>';
		});
		str += '</table>';
		$('#content').html(str);
	    }
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function component_create(id) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id= "name"/></td></tr>';
    str += '<tr><td>title:</td><td><input type="text" style="width:95%" id= "title"/></td></tr>';
    str += '<tr><td>path:</td><td><input type="text" style="width:95%" id= "path"/></td></tr>';
    str += '<tr><td>iconPath:</td><td><input type="text" style="width:95%" id= "iconPath"/></td></tr>';
    str += '<tr><td>widgetName:</td><td><input type="text" style="width:95%" id= "widgetName"/></td></tr>';
    str += '<tr><td>widgetTitle:</td><td><input type="text" style="width:95%" id= "widgetTitle"/></td></tr>';
    str += '<tr><td>widgetIconPath:</td><td><input type="text" style="width:95%" id= "widgetIconPath"/></td></tr>';
    str += '<tr><td>widgetStart:</td><td><select id="widgetStart"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>widgetVisible:</td><td><select id="widgetVisible"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>visible:</td><td><select id="visible"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id= "order"/></td></tr>';
    str += '<tr><td>allowList:</td><td><textarea style="width:95%"  id="allowList"/></td></tr>';
    str += '<tr><td>denyList:</td><td><textarea style="width:95%"  id="denyList"/></td></tr>';
    str += '<tr><td>controllerList:</td><td><textarea style="width:95%"  id="controllerList"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	component_post();
    });
}

function component_post() {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/component',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    title : $('#title').val(),
	    path : $('#path').val(),
	    iconPath : $('#iconPath').val(),
	    widgetName : $('#widgetName').val(),
	    widgetTitle : $('#widgetTitle').val(),
	    widgetIconPath : $('#widgetIconPath').val(),
	    widgetStart : $('#widgetStart').val(),
	    widgetVisible : $('#widgetVisible').val(),
	    visible : $('#visible').val(),
	    order : $('#order').val(),
	    allowList : splitValue($('#allowList').val()),
	    denyList : splitValue($('#denyList').val()),
	    controllerList : splitValue($('#controllerList').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function component_edit(id) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id= "name"/></td></tr>';
    str += '<tr><td>title:</td><td><input type="text" style="width:95%" id= "title"/></td></tr>';
    str += '<tr><td>path:</td><td><input type="text" style="width:95%" id= "path"/></td></tr>';
    str += '<tr><td>iconPath:</td><td><input type="text" style="width:95%" id= "iconPath"/></td></tr>';
    str += '<tr><td>widgetName:</td><td><input type="text" style="width:95%" id= "widgetName"/></td></tr>';
    str += '<tr><td>widgetTitle:</td><td><input type="text" style="width:95%" id= "widgetTitle"/></td></tr>';
    str += '<tr><td>widgetIconPath:</td><td><input type="text" style="width:95%" id= "widgetIconPath"/></td></tr>';
    str += '<tr><td>widgetStart:</td><td><select id="widgetStart"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>widgetVisible:</td><td><select id="widgetVisible"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>visible:</td><td><select id="visible"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id= "order"/></td></tr>';
    str += '<tr><td>allowList:</td><td><textarea style="width:95%"  id="allowList"/></td></tr>';
    str += '<tr><td>denyList:</td><td><textarea style="width:95%"  id="denyList"/></td></tr>';
    str += '<tr><td>controllerList:</td><td><textarea style="width:95%"  id="controllerList"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	component_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/component/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		$('#id').html(json.data.id);
		$('#name').val(json.data.name);
		$('#title').val(json.data.title);
		$('#path').val(json.data.path);
		$('#iconPath').val(json.data.iconPath);
		$('#widgetName').val(json.data.widgetName);
		$('#widgetTitle').val(json.data.widgetTitle);
		$('#widgetIconPath').val(json.data.widgetIconPath);
		$('#widgetStart').val(json.data.widgetStart + '');
		$('#widgetVisible').val(json.data.widgetVisible + '');
		$('#visible').val(json.data.visible + '');
		$('#order').val(json.data.order);
		$('#allowList').val(joinValue(json.data.allowList));
		$('#denyList').val(joinValue(json.data.denyList));
		$('#controllerList').val(joinValue(json.data.controllerList));
	    }
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function component_put(id) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/component/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    title : $('#title').val(),
	    path : $('#path').val(),
	    iconPath : $('#iconPath').val(),
	    widgetName : $('#widgetName').val(),
	    widgetTitle : $('#widgetTitle').val(),
	    widgetIconPath : $('#widgetIconPath').val(),
	    widgetStart : $('#widgetStart').val(),
	    widgetVisible : $('#widgetVisible').val(),
	    visible : $('#visible').val(),
	    order : $('#order').val(),
	    allowList : splitValue($('#allowList').val()),
	    denyList : splitValue($('#denyList').val()),
	    controllerList : splitValue($('#controllerList').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function component_delete(id) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/component/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}