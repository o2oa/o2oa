attachment_parameter = {
    list_action : null,
    list_action_parameter : null,
    first : '(0)',
    last : '(0)',
    count : 20
};

function attachment_get_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="getBase64">getBase64</a>&nbsp;<a href="#" id="getImageScaleBase64">getImageScaleBase64</a>&nbsp;<a href="#" id="getImageWidthHeightBase64">getImageWidthHeightBase64</a></td></tr>';
    str += '<tr><td>id:</td><td><input type="text" style="width:95%" id="id"></td></tr>';
    str += '<tr><td>scale:</td><td><input type="text" style="width:95%" id="scale"></td></tr>';
    str += '<tr><td>width:</td><td><input type="text" style="width:95%" id="width"></td></tr>';
    str += '<tr><td>height:</td><td><input type="text" style="width:95%" id="height"></td></tr>';
    str += '<tr><td colspan="2" id="grid">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	attachment_get($('#id').val());
    });
    $('#getBase64').click(function() {
	attachment_getBase64($('#id').val());
    });
    $('#getImageScaleBase64').click(function() {
	attachment_getImageScaleBase64($('#id').val(), $('#scale').val());
    });
    $('#getImageWidthHeightBase64').click(function() {
	attachment_getImageWidthHeightBase64($('#id').val(), $('#width').val(), $('#height').val());
    });
}

function attachment_get(id) {
    $('#grid').html('');
    $('#result').html('');
    var str = '<iframe src="../servlet/download/' + id + '"/>';
    $('#grid').html(str);
}

function attachment_getBase64(id) {
    $('#grid').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id + '/binary/base64',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    var str = '<img src="data:image/png;base64,' + json.data.value + '"/>';
	    $('#grid').html(str);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_getImageScaleBase64(id, scale) {
    $('#grid').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id + '/image/scale/' + scale + '/binary/base64',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    var str = '<img src="data:image/png;base64,' + json.data.value + '"/>';
	    $('#grid').html(str);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function attachment_getImageWidthHeightBase64(id, width, height) {
    $('#grid').html('');
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/attachment/' + id + '/image/width/' + width + '/height/' + height + '/binary/base64',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    var str = '<img src="data:image/png;base64,' + json.data.value + '"/>';
	    $('#grid').html(str);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}
