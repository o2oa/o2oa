script_parameter = {
    root : '../jaxrs/script',
    list_action : null,
    list_action_parameter : null,
    first : '(0)',
    last : '(0)',
    count : 20,
    application : null
};

function script_list_reload() {
    if (script_parameter.list_action) {
	script_parameter.list_action.call(window, script_parameter.list_action_parameter);
    } else {
	script_list_next('(0)');
    }
}

function script_list_next(id) {
    var id = (id ? id : script_parameter.last);
    script_parameter.list_action = script_list_next;
    script_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : script_parameter.root + '/list/' + id + '/next/' + script_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		script_parameter.first = data.data[0].id;
		script_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		script_parameter.first = '(0)';
	    }
	    $('#content').html(script_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    script_list_init();
	} else {
	    failure(data);
	}
    });
}

function script_list_prev(id) {
    var id = (id ? id : script_parameter.first);
    script_parameter.list_action = script_list_prev;
    script_parameter.list_action_parameter = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : script_parameter.root + '/list/' + id + '/prev/' + script_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data.length > 0) {
		script_parameter.first = data.data[0].id;
		script_parameter.last = data.data[data.data.length - 1].id;
	    } else {
		script_parameter.last = '(0)';
	    }
	    $('#content').html(script_list_grid(data.data));
	    $('#total', '#content').html(data.count);
	    script_list_init();
	} else {
	    failure(data);
	}
    });
}

function script_list_grid(items) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="4">	<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>id</th><th>name</th><th>application</th><th>operate</th></tr>';
    $.each(items, function(index, item) {
	str += '<tr>';
	str += '<td>' + item.id + '</td>';
	str += '<td>' + item.name + '</td>';
	str += '<td>' + item.application + '</td>';
	str += '<td>';
	str += '<a href="#" onclick="script_edit(\'' + item.id + '\')">edit</a>&nbsp;';
	str += '<a href="#" onclick="script_delete(\'' + item.id + '\')">delete</a>';
	str += '</td>';
	str += '</tr>';
    });
    str += '</table>';
    return str;
}

function script_list_init() {
    $('#next', '#content').click(function() {
	script_list_next();
    });
    $('#prev', '#content').click(function() {
	script_list_prev();
    });
}

function script_query() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>application id:</td><td><input type="text" id="application" style="width:95%"></td></tr>';
    str += '<tr><td>script name:</td><td><input type="text" id="name" style="width:95%"></td></tr>';
    str += '<tr><td colspan="2"><button id="listWithApplication">application</button>&nbsp;<button id="getWithName">name</button></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#listWithApplication', '#content').click(function() {
	script_parameter.application = $('#application', '#content').val();
	script_listWithApplication($('#application', '#content').val());
    });
    $('#getWithName', '#content').click(function() {
	script_parameter.application = $('#application', '#content').val();
	script_getWithName($('#application', '#content').val(), $('#name', '#content').val());
    });
}

function script_create() {

    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application"  style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>languageType:</td><td><select id="languageType"><option value="JavaScript">JavaScript</option></select></td></tr>';
    str += '<tr><td>validated:</td><td><select id="validated"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>dependScriptList:</td><td><textarea id="dependScriptList" style="width:95%;height:100px"/></td></tr>';
    str += '<tr><td colspan="2">text:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="text" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post', '#content').click(function() {
	script_post();
    });
}

function script_post(application) {
    script_parameter.application = $('#application', '#content').val();
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : script_parameter.root,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    application : $('#application', '#content').val(),
	    name : $('#name', '#content').val(),
	    alias : $('#alias', '#content').val(),
	    description : $('#description', '#content').val(),
	    validated : $('#validated', '#content').val(),
	    languageType : $('#languageType', '#content').val(),
	    dependScriptList : splitValue($('#dependScriptList', '#content').val()),
	    text : $('#text', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    script_listWithApplication(script_parameter.application);
	} else {
	    failure(data);
	}
    });
}

function script_edit(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>creatorPerson:</td><td id="creatorPerson">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdateTime:</td><td id="lastUpdateTime">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdatePerson:</td><td id="lastUpdatePerson">&nbsp;</td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application"  style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name"  style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias"  style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>languageType:</td><td><select id="languageType"><option value="JavaScript">JavaScript</option></select></td></tr>';
    str += '<tr><td>validated:</td><td><select id="validated"><option value="true">true</option><option value="false">false</option></select></td></tr>';
    str += '<tr><td>dependScriptList:</td><td><textarea id="dependScriptList" style="width:95%;height:100px"/></td></tr>';
    str += '<tr><td colspan="2">text:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="text" style="width:95%;height:500px"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put', '#content').click(function() {
	script_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : script_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#id', '#content').html(data.data.id);
	    $('#createTime', '#content').html(data.data.createTime);
	    $('#creatorPerson', '#content').html(data.data.creatorPerson);
	    $('#lastUpdateTime', '#content').html(data.data.lastUpdateTime);
	    $('#lastUpdatePerson', '#content').html(data.data.lastUpdatePerson);
	    $('#application', '#content').val(data.data.application);
	    $('#name', '#content').val(data.data.name);
	    $('#alias', '#content').val(data.data.alias);
	    $('#description', '#content').val(data.data.description);
	    $('#languageType', '#content').val(data.data.languageType);
	    $('#validated', '#content').val(data.data.validated);
	    $('#text', '#content').val(data.data.text);
	    $('#dependScriptList', '#content').val(joinValue(data.data.dependScriptList));
	} else {
	    failure(data);
	}
    });
}

function script_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : script_parameter.root + '/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    application : $('#application', '#content').val(),
	    name : $('#name', '#content').val(),
	    alias : $('#alias', '#content').val(),
	    description : $('#description', '#content').val(),
	    validated : $('#validated', '#content').val(),
	    languageType : $('#languageType', '#content').val(),
	    dependScriptList : splitValue($('#dependScriptList', '#content').val()),
	    text : $('#text', '#content').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    script_listWithApplication(script_parameter.application);
	} else {
	    failure(data);
	}
    });
}

function script_delete(id) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : script_parameter.root + '/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    script_listWithApplication(script_parameter.application);
	} else {
	    failure(data);
	}
    });
}

function script_listWithApplication(id) {
    script_parameter.application = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : script_parameter.root + '/application/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    var str = '<table border="1" width="100%"><tbody>';
	    str += '<tr><th>id</th><th>name</th><th>alias</th><th>operate</th></tr>';
	    $.each(data.data, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.alias + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="script_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="script_delete(\'' + item.id + '\')">delete</a>';
		str += '</td>';
		str += '</tr>';
	    });
	    str += '</tbody></table>';
	    $('#content').html(str);
	} else {
	    failure(data);
	}
    });
}

function script_getWithName(application, name) {
    script_parameter.application = application;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : script_parameter.root + '/' + name,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><th>id</th><th>name</th><th>operate</th></tr>';
	    str += '<tr>';
	    str += '<td>' + data.data.id + '</td>';
	    str += '<td>' + data.data.name + '</td>';
	    str += '<td>';
	    str += '<a href="#" onclick="script_edit(\'' + data.data.id + '\')">edit</a>&nbsp;';
	    str += '<a href="#" onclick="script_delete(\'' + data.data.id + '\')">delete</a>';
	    str += '</td>';
	    str += '</tr>';
	    str += '</table>';
	    $('#content').html(str);
	} else {
	    failure(data);
	}
    });
}