function input_person_init() {
    $('#content').html('');
    $('#result').html('');
    var str = '<form enctype="multipart/form-data" action="../servlet/input/person" method="post" target="_blank" id="form"><table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>file:</td><td><input type="file" name="file" style="width:95%" id= "file"/></td></tr>';
    str += '</table></form>';
    $('#content').html(str);
    $('#post').click(function() {
	$('#form').submit();
    });
}

// function input_post() {
// var formData = new FormData($('form')[0]);
// $.ajax({
// type : 'post',
// //dataType : 'json',
// url : '../servlet/input/person',
// data : formData,
// contentType : false,
// cache : false,
// processData : false,
// xhrFields : {
// 'withCredentials' : true
// },
// crossDomain : true
// });
// }

// function input_person_init() {
// $('#result').html('');
// str = '<table border="1" width="100%">';
// str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
// str += '<tr><td>file:</td><td><input type="file" id="file"
// style="width:95%"/></td></tr>';
// str += '</table>';
// $('#content').html(str);
// $('#post').click(function() {
// input_person_post();
// });
// }
//
// function input_person_post() {
// var formData = new FormData();
// formData.append('file', $('#file')[0].files[0]);
// $.ajax({
// type : 'post',
// dataType : 'json',
// url : '../servlet/input/person',
// xhrFields : {
// 'withCredentials' : true
// },
// cache : false,
// processData : false,
// contentType : false,
// data : formData
// }).always(function(json) {
// $('#result').html(JSON.stringify(json, null, 4));
// });
// }
