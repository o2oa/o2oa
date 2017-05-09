function loginRecord_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2">	<a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>stream:</td><td><select id="stream"><option value="true">true<option/><option value="false">false</option></select></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	loginRecord_get($('#stream').val());
    });
}

function loginRecord_get(stream) {
    window.open('../jaxrs/loginrecord/' + stream, '_blank');
}