script_parameter = {};

function script_list_init(portal) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="3">portal:&nbsp;<input type="text" id="portal" style="width:50%">&nbsp;&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><th>name</th><th>id</th><th>operate</th></tr>';
    str += '</thead>';
    str += '<tbody id="grid">';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#list').click(function() {
	script_list($("#portal").val());
    });
    if (portal) {
	$('#portal').val(portal);
	script_list(portal);
    }
}

function script_list(portal) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/script/list/portal/' + portal,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	script_list_grid(json);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function script_list_grid(json) {
    if (json.data) {
	var str = '';
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.name + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="script_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="script_delete(\'' + item.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	});
    }
    $('#grid').html(str);
}

function script_create_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="post">post</a></td></tr></thead>';
    str += '<tbody>';
    str += '<tr><td>portal:</td><td><input type="text" id="portal" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>dependScriptList:</td><td><textarea id="dependScriptList" style="width:95%"/></td></tr>';
    str += '<tr><td>text:</td><td><input type="text" id="text" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td>validated:</td><td><select id="validated"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	script_post();
    });
}

function script_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/script',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    portal : $('#portal').val(),
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    dependScriptList : splitValue($('#dependScriptList').val()),
	    text : $('#text').val(),
	    validated : $('#validated').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function script_edit(id) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead><tr><td colspan="2"><a href="#" id="put">put</a></td></tr></thead>';
    str += '<tbody>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>updateTime:</td><td id="updateTime">&nbsp;</td></tr>';
    str += '<tr><td>portal:</td><td id="portal">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>dependScriptList:</td><td><textarea id="dependScriptList" style="width:95%"/></td></tr>';
    str += '<tr><td>text:</td><td><input type="text" id="text" style="width:95%;height:500px"/></td></tr>';
    str += '<tr><td>validated:</td><td><select id="validated"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	script_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/script/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#id').html(json.data.id);
	$('#createTime').html(json.data.createTime);
	$('#updateTime').html(json.data.updateTime);
	$('#creatorPerson').html(json.data.creatorPerson);
	$('#lastUpdateTime').html(json.data.lastUpdateTime);
	$('#lastUpdatePerson').html(json.data.lastUpdatePerson);
	$('#portal').html(json.data.portal);
	$('#name').val(json.data.name);
	$('#alias').val(json.data.alias);
	$('#description').val(json.data.description);
	$('#dependScriptList').val(joinValue(json.data.dependScriptList));
	$('#text').val(json.data.text);
	$('#validated').val(json.data.validated);
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function script_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/script/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    dependScriptList : splitValue($('#dependScriptList').val()),
	    text : $('#text').val(),
	    validated : $('#validated').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function script_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/script/' + id,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}