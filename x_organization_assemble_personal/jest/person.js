function person_getComplex() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/person/complex',
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function person_edit() {
    $('#content').html('');
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>updateTime:</td><td id="updateTime">&nbsp;</td></tr>';
    str += '<tr><td>sequence:</td><td id="sequence">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td id="name">&nbsp;</td></tr>';
    str += '<tr><td>pinyin:</td><td id="pinyin">&nbsp;</td></tr>';
    str += '<tr><td>pinyinInitial:</td><td id="pinyinInitial">&nbsp;</td></tr>';
    str += '<tr><td>employee:</td><td id="employee">&nbsp;</td></tr>';
    str += '<tr><td>unique:</td><td id="unique">&nbsp;</td></tr>';
    str += '<tr><td>display:</td><td id="display">&nbsp;</td></tr>';
    str += '<tr><td>controllerList:</td><td id="controllerList">&nbsp;</td></tr>';
    str += '<tr><td>deviceList:</td><td id="deviceList">&nbsp;</td></tr>';
    str += '<tr><td>boardDate:</td><td id="boardDate">&nbsp;</td></tr>';
    str += '<tr><td>icon:</td><td id="icon">&nbsp;</td></tr>';
    str += '<tr><td>genderType:</td><td><select id="genderType"><option value="m">male</option><option value="f">female</option><option value="d">private</option></select></td></tr>';
    str += '<tr><td>mail:</td><td><input type="text" style="width:95%" id= "mail"/></td></tr>';
    str += '<tr><td>weixin:</td><td><input type="text" style="width:95%" id= "weixin"/></td></tr>';
    str += '<tr><td>qq:</td><td><input type="text" style="width:95%" id= "qq"/></td></tr>';
    str += '<tr><td>mobile:</td><td><input type="text" style="width:95%" id= "mobile"/></td></tr>';
    str += '<tr><td>officePhone:</td><td><input type="text" style="width:95%" id= "officePhone"/></td></tr>';
    str += '<tr><td>birthday:</td><td><input type="text" style="width:95%" id= "birthday"/></td></tr>';
    str += '<tr><td>age:</td><td><input type="text" style="width:95%" id= "age"/></td></tr>';
    str += '<tr><td>signature:</td><td><input type="text" style="width:95%" id= "signature"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	component_put(id);
    });
    $.ajax({
	type : 'get',
	dataType : 'json',
	contentType : 'application/json; charset=utf-8',
	url : '../jaxrs/person',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    if (json.data) {
		$('#id').html(json.data.id);
		$('#createTime').html(json.data.createTime);
		$('#updateTime').html(json.data.updateTime);
		$('#sequence').html(json.data.sequence);
		$('#name').html(json.data.name);
		$('#pinyin').html(json.data.pinyin);
		$('#pinyinInitial').html(json.data.pinyinInitial);
		$('#employee').html(json.data.employee);
		$('#unique').html(json.data.unique);
		$('#display').html(json.data.display);
		$('#controllerList').html(joinValue(json.data.controllerList));
		$('#deviceList').html(joinValue(json.data.deviceList));
		$('#boardDate').html(json.data.boardDate);
		$('#icon').html('<img src="data:image/png;base64,' + json.data.icon + '" />');
		$('#genderType').val(json.data.genderType);
		$('#mail').val(json.data.mail);
		$('#weixin').val(json.data.weixin);
		$('#qq').val(json.data.qq);
		$('#mobile').val(json.data.mobile);
		$('#officePhone').val(json.data.officePhone);
		$('#birthday').val(json.data.birthday);
		$('#age').val(json.data.age);
		$('#signature').val(json.data.signature);
	    }
	} else {
	    failure(json);
	}
    });
}

function person_put() {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/person',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    genderType : $('#genderType').val(),
	    mail : $('#mail').val(),
	    weixin : $('#weixin').val(),
	    qq : $('#qq').val(),
	    mobile : $('#mobile').val(),
	    officePhone : $('#officePhone').val(),
	    birthday : $('#birthday').val(),
	    age : $('#age').val(),
	    signature : $('#signature').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}