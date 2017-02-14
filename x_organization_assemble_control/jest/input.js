function input_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<form enctype="multipart/form-data" action="../servlet/input" method="post" id="form"><table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" name="file" style="width:95%" id= "file"/></td></tr>';
    str += '</table></form>';
    $('#content').html(str);
    $('#post').click(function() {
	input_post();
    });
}

function input_post() {
    $('#form').submit();
}