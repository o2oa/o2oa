function icon_update() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>icon:</td><td><input type="file" id="file" style="width:95%" ></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	icon_post();
    });
}

function icon_post() {
    $('#result').html('');
    var formData = new FormData();
    $.each($('input[type=file]'), function(index, item) {
	formData.append('file', item.files[0]);
    });
    $.ajax({
	type : 'POST',
	url : '../servlet/icon',
	data : formData,
	contentType : false,
	processData : false,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true,
	dataType : 'json',
	success : function(json) {
	    $('#result').html(JSON.stringify(JSON.parse(json), null, 4));
	}
    });
}