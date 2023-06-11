data_parameter = {};

function data_create(entity) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>entity:</td><td>' + entity + '</td></tr>';
    str += '<tr><td>dataServer:</td><td><input type="text" style="width:95%" id="dataServer"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>toolLevel:</td><td><select id="toolLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>runtimeLevel:</td><td><select id="runtimeLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>dataCacheLevel:</td><td><select id="dataCacheLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>metaDataLevel:</td><td><select id="metaDataLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>enhanceLevel:</td><td><select id="enhanceLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>queryLevel:</td><td><select id="queryLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>sqlLevel:</td><td><select id="sqlLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>jdbcLevel:</td><td><select id="jdbcLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	data_post(entity);
    });
}

function data_edit(entity, dataServer) {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>entity:</td><td>' + entity + '</td></tr>';
    str += '<tr><td>dataServer:</td><td><input type="text" style="width:95%" id="dataServer"/></td></tr>';
    str += '<tr><td>order:</td><td><input type="text" style="width:95%" id="order"/></td></tr>';
    str += '<tr><td>toolLevel:</td><td><select id="toolLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>runtimeLevel:</td><td><select id="runtimeLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>dataCacheLevel:</td><td><select id="dataCacheLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>metaDataLevel:</td><td><select id="metaDataLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>enhanceLevel:</td><td><select id="enhanceLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>queryLevel:</td><td><select id="queryLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>sqlLevel:</td><td><select id="sqlLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '<tr><td>jdbcLevel:</td><td><select id="jdbcLevel"><option value="FATAL">FATAL</option><option value="ERROR">ERROR</option><option value="WARN">WARN</option><option value="INFO">INFO</option><option value="TRACE">TRACE</option></select></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/data/entity/' + entity + '/dataserver/' + dataServer,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		$('#dataServer').val(json.data.dataServer);
		$('#order').val(json.data.order);
		$('#toolLevel').val(json.data.toolLevel);
		$('#runtimeLevel').val(json.data.runtimeLevel);
		$('#dataCacheLevel').val(json.data.dataCacheLevel);
		$('#metaDataLevel').val(json.data.metaDataLevel);
		$('#enhanceLevel').val(json.data.enhanceLevel);
		$('#queryLevel').val(json.data.queryLevel);
		$('#sqlLevel').val(json.data.sqlLevel);
		$('#jdbcLevel').val(json.data.jdbcLevel);
	    }
	} else {
	    failure(data);
	}
    });
    $('#put', '#content').click(function() {
	data_put(entity, dataServer);
    });
}

function data_post(entity) {
    $('#result').html('');
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/data/entity/' + entity,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    dataServer : $('#dataServer').val(),
	    order : $('#order').val(),
	    toolLevel : $('#toolLevel').val(),
	    runtimeLevel : $('#runtimeLevel').val(),
	    dataCacheLevel : $('#dataCacheLevel').val(),
	    metaDataLevel : $('#metaDataLevel').val(),
	    enhanceLevel : $('#enhanceLevel').val(),
	    queryLevel : $('#queryLevel').val(),
	    sqlLevel : $('#sqlLevel').val(),
	    jdbcLevel : $('#jdbcLevel').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function data_put(entity, dataServer) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/data/entity/' + entity + '/dataserver/' + dataServer,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    dataServer : $('#dataServer').val(),
	    order : $('#order').val(),
	    toolLevel : $('#toolLevel').val(),
	    runtimeLevel : $('#runtimeLevel').val(),
	    dataCacheLevel : $('#dataCacheLevel').val(),
	    metaDataLevel : $('#metaDataLevel').val(),
	    enhanceLevel : $('#enhanceLevel').val(),
	    queryLevel : $('#queryLevel').val(),
	    sqlLevel : $('#sqlLevel').val(),
	    jdbcLevel : $('#jdbcLevel').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function data_delete(entity, dataServer) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/data/entity/' + entity + '/dataserver/' + dataServer,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}