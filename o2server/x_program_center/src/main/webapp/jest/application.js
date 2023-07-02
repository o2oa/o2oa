application_parameter = {
	applications : null
};

function application_list() {
	$('#content').html('');
	$.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/applications',
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			if (data.data && data.data.applicationTypes) {
				application_parameter.applications = data.data;
				var str = '<table border="1" width="100%">';
				str += '<tr><th>host</th><th>port</th><th>contextPath</th><th>available</th><th>weight</th><th>operate</th></tr>';
				$.each(data.data.applicationTypes, function(index, item) {
					str += '<tr><td colspan="6">' + item.type + '</td></tr>';
					$.each(item.applications, function(index, o) {
						str += '<tr>';
						str += '<td>' + o.host + '</td>';
						str += '<td>' + o.port + '</td>';
						str += '<td>' + o.contextPath + '</td>';
						str += '<td>' + o.available + '</td>';
						str += '<td>' + o.weight + '</td>';
						str += '<td>';
						str += '<a href="#" onclick="application_edit(\'' + o.token + '\')">edit</a>&nbsp;';
						str += '</td>';
						str += '</tr>';
					});
				});
				str += '</table>';
				$('#content').html(str);
			}
		} else {
			failure(data);
		}
		$('#result').html(JSON.stringify(data.data, null, 4));
	});
}

function application_edit(token) {
	$('#result').html('');
	var str = '<table border="1" width="100%">';
	str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
	str += '<tr><td>token:</td><td id="token">&nbsp;</td></tr>';
	str += '<tr><td>available:</td><td id="available"/>&nbsp;</td></tr>';
	str += '<tr><td>reportDate:</td><td id="reportDate"/>&nbsp;</td></tr>';
	str += '<tr><td>contextPath:</td><td id="contextPath">&nbsp;</td></tr>';
	str += '<tr><td>enable:</td><td><select id="enable"><option value ="true">true</option><option value ="false">false</option></select></td></tr>';
	str += '<tr><td>host:</td><td><input type="text" style="width:95%" id= "host"/></td></tr>';
	str += '<tr><td>port:</td><td><input type="text" style="width:95%" id= "port"/></td></tr>';
	str += '<tr><td>weight:</td><td><input type="text" style="width:95%" id= "weight"/></td></tr>';
	str += '</table>';
	$('#content').html(str);
	$('#put', '#content').click(function() {
		application_put(token);
	});
	$.each(application_parameter.applications.applicationTypes, function(index, item) {
		$.each(item.applications, function(index, o) {
			if (o.token == token) {
				$('#token', '#content').html(o.token);
				$('#available', '#content').html(o.available + '');
				$('#reportDate', '#content').html(o.reportDate);
				$('#contextPath', '#content').html(o.contextPath);
				$('#enable', '#content').val(o.enable + '');
				$('#host', '#content').val(o.host);
				$('#port', '#content').val(o.port);
				$('#weight', '#content').val(o.weight);
			}
		});
	});
}

function application_put(token) {
	$('#result').html('');
	$.ajax({
		type : 'put',
		dataType : 'json',
		url : '../jaxrs/application/' + token,
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			enable : $('#enable', '#content').val(),
			host : $('#host', '#content').val(),
			port : $('#port', '#content').val(),
			weight : $('#weight', '#content').val()
		}),
		xhrFields : {
			'withCredentials' : true
		},
		crossDomain : true
	}).done(function(data) {
		if (data.type == 'success') {
			application_list();
		} else {
			failure(data);
		}
	});

}