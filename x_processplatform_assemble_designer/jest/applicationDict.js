applicationDict_parameter = {};

function applicationDict_query_init(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>applicationDict:</td><td><input type="text" id="applicationDict" style="width:95%"/></td></tr>';
    str += '<tr><td>path0:</td><td><input type="text" id="path0" style="width:95%"/></td></tr>';
    str += '<tr><td>path1:</td><td><input type="text" id="path1" style="width:95%"/></td></tr>';
    str += '<tr><td>path2:</td><td><input type="text" id="path2" style="width:95%"/></td></tr>';
    str += '<tr><td>path3:</td><td><input type="text" id="path3" style="width:95%"/></td></tr>';
    str += '<tr><td>path4:</td><td><input type="text" id="path4" style="width:95%"/></td></tr>';
    str += '<tr><td>path5:</td><td><input type="text" id="path5" style="width:95%"/></td></tr>';
    str += '<tr><td>path6:</td><td><input type="text" id="path6" style="width:95%"/></td></tr>';
    str += '<tr><td>path7:</td><td><input type="text" id="path7" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td colspan="2"><textarea  id="data" style="width:95%; height:500px"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    if (id) {
	$('#applicationDict', '#content').val(id);
    }
    $('#get', '#content').click(function() {
	applicationDict_query();
    });
    $('#put', '#content').click(function() {
	applicationDict_put();
    });
}

function applicationDict_query() {
    var url = +'../jaxrs/applicationdict/' + $('#applicationDict', '#content').val() + '/application/' + $('#application', '#content').val();
    var path0 = $('#path0', '#content').val();
    var path1 = $('#path1', '#content').val();
    var path2 = $('#path2', '#content').val();
    var path3 = $('#path3', '#content').val();
    var path4 = $('#path4', '#content').val();
    var path5 = $('#path5', '#content').val();
    var path6 = $('#path6', '#content').val();
    var path7 = $('#path7', '#content').val();
    if (path0.length > 0) {
	url += '/' + path0;
	if (path1.length > 0) {
	    url += '/' + path1;
	    if (path2.length > 0) {
		url += '/' + path2;
		if (path3.length > 0) {
		    url += '/' + path3;
		    if (path4.length > 0) {
			url += '/' + path4;
			if (path5.length > 0) {
			    url += '/' + path5;
			    if (path6.length > 0) {
				url += '/' + path6;
				if (path7.length > 0) {
				    url += '/' + path7;
				}
			    }
			}
		    }
		}
	    }
	}
    }
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : url + '/data',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    if (data.data) {
		$('#data', '#content').val(JSON.stringify(data.data, null, '\t'));
	    } else {
		$('#result', '#content').val('');
	    }
	} else {
	    failure(data);
	}
    });
}

function applicationDict_create() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:480px"  id="data"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post', '#content').click(function() {
	applicationDict_post();
    });
}

function applicationDict_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/applicationdict/',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    application : $('#application', '#content').val(),
	    name : $('#name', '#content').val(),
	    alias : $('#alias', '#content').val(),
	    description : $('#description', '#content').val(),
	    data : JSON.parse($('#data', '#content').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	} else {
	    failure(data);
	}
    });
}

function applicationDict_list_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="list">list</a></td></tr>';
    str += '<tr><td colspan="2" id="result">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#list', '#content').click(function() {
	applicationDict_list();
    });
}

function applicationDict_list() {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/applicationdict/list/application/' + $('#application', '#content').val(),
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#result', '#content').html(JSON.stringify(data.data, null, '<br/>'));
	} else {
	    failure(data);
	}
    });
}

function applicationDict_view(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="put">put</a>&nbsp;<a href="#" id="delete">delete</a></td></tr>';
    str += '<tr><td>applicationDict:</td><td><input type="text" id="id" style="width:95%"/></td></tr>';
    str += '<tr><td>application:</td><td id="application">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><input type="text" id="description" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">data:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:480px"  id="data"/></td></tr>';
    str += '<tr><td colspan="2" id="result">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    if (id) {
	$('#id', '#content').val(id);
    }
    $('#get', '#content').click(function() {
	applicationDict_get($('#id', '#content').val());
    });
    $('#put', '#content').click(function() {
	applicationDict_put($('#id', '#content').val());
    });
    $('#delete', '#content').click(function() {
	applicationDict_delete($('#id', '#content').val());
    });
}

function applicationDict_get(application, id) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/applicationdict/' + id + '/application/' + application,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#id', '#content').val(data.data.id);
	    $('#name', '#content').val(data.data.name);
	    $('#application', '#content').html(data.data.application);
	    $('#alias', '#content').val(data.data.alias);
	    $('#description', '#content').val(data.data.description);
	    $('#data', '#content').val(JSON.stringify(data.data.data, null, '\t'));
	} else {
	    failure(data);
	}
    });
}

function applicationDict_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/applicationdict/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    'name' : $('#name', '#content').val(),
	    'alias' : $('#alias', '#content').val(),
	    'description' : $('#description', '#content').val(),
	    'data' : JSON.parse($('#data', '#content').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	} else {
	    failure(data);
	}
    });
}

function applicationDict_delete() {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/applicationdict/' + $('#applicationDict', '#content').val(),
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(data) {
	if (data.type == 'success') {
	    $('#result', '#content').html(JSON.stringify(data.data, null, '<br/>'));
	} else {
	    failure(data);
	}
    });
}
