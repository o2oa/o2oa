complex_parameter = {};

function complex_query_init() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>query:</td><td><input type="text" id="query" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="company">查找公司的直接下属公司,直接下属部门.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="department">查找部门的直接下属部门,直接下属身份.</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="person">查找个人的公司属性,部门属性,身份.</a></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#company').click(function() {
	complex_query_company($('#query').val());
    });
    $('#department').click(function() {
	complex_query_department($('#query').val());
    });
    $('#person').click(function() {
	complex_query_person($('#query').val());
    });
}

function complex_query_company(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/complex/company/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function complex_query_department(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/complex/department/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function complex_query_person(id) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/complex/person/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}