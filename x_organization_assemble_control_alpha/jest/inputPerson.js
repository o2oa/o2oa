function inputPerson_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<form enctype="multipart/form-data" action="../jaxrs/inputperson" method="post" target="_blank" id="form"><table border="1" style="border-collapse: collapse" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" name="file" style="width:95%" id= "file"/></td></tr>';
    str += '</table></form>';
    $('#content').html(str);
    $('#post').click(function() {
	$('#form').submit();
    });
}

function inputPerson_template() {
    window.open('../jaxrs/inputperson/template', '_blank');
}