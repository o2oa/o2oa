function icon_get_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%" ></td></tr>';
    str += '<tr><td>icon:</td><td id="iconArea">&nbsp;</td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	var url = '../servlet/icon/' + $('#name').val();
	$('#iconArea').html('<img src="'+url+'"/>');
    });
}