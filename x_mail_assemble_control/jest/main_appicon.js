function image_file_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td>file:</td><td><input type="file" id="file" style="width:95%"/></td></tr>';
    str += '<tr><td>size:</td><td><input type="text" id="size" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><button id="convert">convert</button></td></tr>';
    str += '<tr><td colspan="2" id="image">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#convert').click(function() {
    	image_file();
    });
}

function image_url_init() {
    str = '<table border="1" width="100%">';
    str += '<tr><td>url:</td><td><input type="text" id="url" style="width:95%"/></td></tr>';
    str += '<tr><td>size:</td><td><input type="text" id="size" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><button id="convert">convert</button></td></tr>';
    str += '<tr><td colspan="2" id="image">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#result').html('');
    $('#convert').click(function() {
    	image_url();
    });
}

function image_file() {
    var formData = new FormData();
    formData.append('file', $('#file')[0].files[0]);
    var url = '../servlet/image' + ($('#size').val() == '' ? '' : ('/size/' + $('#size').val()));
    jQuery.ajax({
		type : 'post',
		url : url,
		data : formData,
		dataType : 'json',
		cache : false,
		contentType : false,
		processData : false,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true,
		success : function(json) {
			$('#result').html(JSON.stringify(json, null, 4));
		    $('#image').html('<img src="data:image/png;base64,' + json.data + '" />');
		},
		error : function(data) {
		    failure(data);
		}
    });
}

function image_url() {
    var data = {
    	url : $('#url').val()
    };
    if ($('#size', '#content').val()) {
    	data.size = $('#size', '#content').val();
    }
    $.ajax({
		type : 'post',
		dataType : 'json',
		url : '../jaxrs/image',
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify(data),
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
		if (json.type == 'success') {
			$('#result').html(JSON.stringify(json, null, 4));
		    $('#image', '#content').html('<img src="data:image/png;base64,' + json.data + '" />');
		} else {
		    failure(data);
		}
    }).fail(function(data) {
    	failure(data);
    });
}