applicationServer_parameter = {};

function applicationServer_create() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>containerType:</td><td><select id="containerType"><option value="tomcat8">tomcat8</option></select></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>proxyHost:</td><td><input type="text" style="width:95%" id="proxyHost"/></td></tr>';
    str += '<tr><td>proxyPort:</td><td><input type="text" style="width:95%" id="proxyPort"/></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="password" style="width:95%" id="password"/></td></tr>';
    str += '<tr><td>deployableList:</td><td id= "deployableList">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationserver/list/depolyable',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(
	    function(json) {
		$('#result').append(JSON.stringify(json, null, 4));
		if (json.type == 'success') {
		    if (json.data) {
			var str = '<table><tr><th>name</th><th>context</th><th>plan</th><th>weight</th></tr>';
			$.each(json.data, function(index, item) {
			    str += '<tr><td>' + item.name + '</td><td id="context_' + item.name + '">false</td><td><input type="checkbox" value="' + item.name + '" id="plan_' + item.name
				    + '"/></td><td><input type="text" style="width:95%" id="weight_' + item.name + '"/></td></tr>';
			});
			str += '</table>';
			$('#deployableList').html(str);
		    }
		} else {
		    failure(json);
		}
	    });
    $('#post').click(function() {
	applicationServer_post();
    });
}

function applicationServer_edit(name) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" style="width:95%" id="name"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>containerType:</td><td><select id="containerType"><option value="tomcat8">tomcat8</option></select></td></tr>';
    str += '<tr><td>host:</td><td><input type="text" style="width:95%" id="host"/></td></tr>';
    str += '<tr><td>port:</td><td><input type="text" style="width:95%" id="port"/></td></tr>';
    str += '<tr><td>proxyHost:</td><td><input type="text" style="width:95%" id="proxyHost"/></td></tr>';
    str += '<tr><td>proxyPort:</td><td><input type="text" style="width:95%" id="proxyPort"/></td></tr>';
    str += '<tr><td>username:</td><td><input type="text" style="width:95%" id="username"/></td></tr>';
    str += '<tr><td>password:</td><td><input type="text" style="width:95%" id="password"/></td></tr>';
    str += '<tr><td>deployableList:</td><td id= "deployableList">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/applicationserver/list/depolyable',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(
	    function(json) {
		$('#result').html(JSON.stringify(json, null, 4));
		if (json.type == 'success') {
		    if (json.data) {
			var str = '<table><tr><th>name</th><th>deployed</th><th>plan</th><th>weight</th></tr>';
			$.each(json.data, function(index, item) {
			    str += '<tr><td>' + item.name + '</td><td id="context_' + item.name + '">false</td><td><input type="checkbox" value="' + item.name + '" id="plan_' + item.name
				    + '"/></td><td><input type="text" style="width:95%" id="weight_' + item.name + '"/></td></tr>';
			});
			str += '</table>';
			$('#deployableList').html(str);
			$.ajax({
			    type : 'get',
			    dataType : 'json',
			    contentType : 'application/json; charset=utf-8',
			    url : '../jaxrs/applicationserver/name/' + name,
			    xhrFields : {
				'withCredentials' : true
			    },
			    crossDomain : true
			}).done(function(json) {
			    $('#result').html(JSON.stringify(json, null, 4));
			    if (json.type == 'success') {
				if (json.data) {
				    $('#order').val(json.data.order);
				    $('#name').val(json.data.name);
				    $('#containerType').val(json.data.containerType);
				    $('#host').val(json.data.host);
				    $('#port').val(json.data.port);
				    $('#proxyHost').val(json.data.proxyHost);
				    $('#proxyPort').val(json.data.proxyPort);
				    $('#username').val(json.data.username);
				    $('#password').val(json.data.password);
				    if (json.data.contextList) {
					$.each(json.data.contextList, function(idxm, m) {
					    if ($('#context_' + m)) {
						$('#context_' + m).html('true');
					    }
					});
				    }
				    if (json.data.planList) {
					$.each(json.data.planList, function(idxn, n) {
					    if ($('#plan_' + n.name)) {
						$('#plan_' + n.name).attr("checked", true);
						$('#weight_' + n.name).val(n.weight);
					    }
					});
				    }
				}
			    } else {
				failure(json);
			    }
			});
		    }
		} else {
		    failure(json);
		}
	    });
    $('#put', '#content').click(function() {
	applicationServer_put(name);
    });
}

function applicationServer_post() {
    alert($('input[type="checkbox"]:checked').val());
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/applicationserver',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    containerType : $('#containerType').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    proxyHost : $('#proxyHost').val(),
	    proxyPort : $('#proxyPort').val(),
	    username : $('#username').val(),
	    password : $('#password').val(),
	    planList : applicationServer_planListValue()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationServer_put(name) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/applicationserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    order : $('#order').val(),
	    containerType : $('#containerType').val(),
	    host : $('#host').val(),
	    port : $('#port').val(),
	    proxyHost : $('#proxyHost').val(),
	    proxyPort : $('#proxyPort').val(),
	    username : $('#username').val(),
	    password : $('#password').val(),
	    weight : $('#weight').val(),
	    planList : applicationServer_planListValue()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationServer_delete(name) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/applicationserver/name/' + name,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationServer_deploy(name, forceRedeploy) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/applicationserver/name/' + name + '/deploy/' + forceRedeploy,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function applicationServer_planListValue() {
    var arr = new Array();
    $('input[type="checkbox"]:checked').each(function() {
	var obj = {};
	obj.name = $(this).val();
	obj.weight = $('#weight_' + obj.name).val();
	arr.push(obj);
    });
    return arr;
}