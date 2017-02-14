function load_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" style="width:95%" id= "file"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	load_post();
    });
}

function load_post() {
    var formData = new FormData();
    $.each($('input[type=file]'), function(index, item) {
	formData.append('file', item.files[0]);
    });
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../servlet/load',
	data : formData,
	contentType : false,
	cache : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true,
	success : function(json, status, req) {
	    $('#result').html(JSON.stringify(json, null, 4));
	}
    });
}