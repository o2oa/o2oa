process_parameter = {
    application : ''
};

function process_query() {
    var str = '<table border="1" width="100%">';
    str += '<tr><td>application id</td><td><input type="text" id="id" style="width:95%"></td><tr>';
    str += '<tr><td  colspan="2"><button id="query">query</button></td></tr>';
    str += '</tr></table>';
    $('#content').html(str);
    $('#query').click(function() {
	process_listWithApplication($('#id').val());
    });
}

function process_create() {
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td colspan="2">json:</td></tr>';
    str += '<tr><td colspan="2"><textarea id="json" style="width:95%;height:500px" /></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#post').click(function() {
	process_post();
    });
}

function process_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/process',
	contentType : 'application/json; charset=utf-8',
	data : $('#json').val(),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function process_edit(id) {
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="put">put</a></td></tr>';
    str += '<tr><td>id:</td><td id="id">&nbsp;</td></tr>';
    str += '<tr><td>createTime:</td><td id="createTime">&nbsp;</td></tr>';
    str += '<tr><td>updateTime:</td><td id="updateTime">&nbsp;</td></tr>';
    str += '<tr><td>creatorPerson:</td><td id="creatorPerson">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdateTime:</td><td id="lastUpdateTime">&nbsp;</td></tr>';
    str += '<tr><td>lastUpdatePerson:</td><td id="lastUpdatePerson">&nbsp;</td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '<tr><td>alias:</td><td><input type="text" id="alias" style="width:95%"/></td></tr>';
    str += '<tr><td>description:</td><td><textarea  id="description" style="width:95%"/></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>managerIdentityList:</td><td><textarea style="width:95%"  id="managerIdentityList"/></td></tr>';
    str += '<tr><td>reviewIdentityList:</td><td><textarea style="width:95%"  id="reviewIdentityList"/></td></tr>';
    str += '<tr><td>beforeBeginScript:</td><td><input type="text" id="beforeBeginScript" style="width:95%"/></td></tr>';
    str += '<tr><td>beforeBeginScriptText:</td><td><textarea id="beforeBeginScriptText" style="width:95%"/></td></tr>';
    str += '<tr><td>afterBeginScript:</td><td><input type="text" id="afterBeginScript" style="width:95%"/></td></tr>';
    str += '<tr><td>afterBeginScriptText:</td><td><textarea id="afterBeginScriptText" style="width:95%"/></td></tr>';
    str += '<tr><td>beforeEndScript:</td><td><input type="text" id="beforeEndScript" style="width:95%"/></td></tr>';
    str += '<tr><td>beforeEndScriptText:</td><td><textarea id="beforeEndScriptText" style="width:95%"/></td></tr>';
    str += '<tr><td>afterEndScript:</td><td><input type="text" id="afterEndScript" style="width:95%"/></td></tr>';
    str += '<tr><td>afterEndScriptText:</td><td><textarea id="afterEndScriptText" style="width:95%"/></td></tr>';
    str += '<tr><td>startableIdentityList:</td><td><textarea id="startableIdentityList" style="width:95%"/></td></tr>';
    str += '<tr><td>startableDepartmentList:</td><td><textarea id="startableDepartmentList" style="width:95%"/></td></tr>';
    str += '<tr><td>startableCompanyList:</td><td><textarea id="startableCompanyList" style="width:95%"/></td></tr>';
    str += '<tr><td>serialTexture:</td><td><textarea id="serialTexture" style="width:95%"/></td></tr>';
    str += '<tr><td>serialActivity:</td><td><input type="text" id="serialActivity" style="width:95%"/></td></tr>';
    str += '<tr><td colspan="2">agentList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="agentList"/></td></tr>';
    str += '<tr><td colspan="2">begin</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="begin"/></td></tr>';
    str += '<tr><td colspan="2">cancelList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="cancelList"/></td></tr>';
    str += '<tr><td colspan="2">choiceList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="choiceList"/></td></tr>';
    str += '<tr><td colspan="2">delayList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="delayList"/></td></tr>';
    str += '<tr><td colspan="2">embedList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="embedList"/></td></tr>';
    str += '<tr><td colspan="2">endList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="endList"/></td></tr>';
    str += '<tr><td colspan="2">invokeList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="invokeList"/></td></tr>';
    str += '<tr><td colspan="2">manualList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="manualList"/></td></tr>';
    str += '<tr><td colspan="2">mergeList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="mergeList"/></td></tr>';
    str += '<tr><td colspan="2">messageList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="messageList"/></td></tr>';
    str += '<tr><td colspan="2">parallelList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="parallelList"/></td></tr>';
    str += '<tr><td colspan="2">serviceList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="serviceList"/></td></tr>';
    str += '<tr><td colspan="2">splitList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="splitList"/></td></tr>';
    str += '<tr><td colspan="2">routeList:</td></tr>';
    str += '<tr><td colspan="2"><textarea style="width:95%; height:240px"  id="routeList"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#put').click(function() {
	process_put(id);
    });
    // 装载流程数据
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/process/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    $('#id').html(json.data.id);
	    $('#createTime').html(json.data.createTime);
	    $('#updateTime').html(json.data.updateTime);
	    $('#creatorPerson').html(json.data.creatorPerson);
	    $('#lastUpdatePerson').html(json.data.lastUpdatePerson);
	    $('#lastUpdateTime').html(json.data.lastUpdateTime);

	    $('#name').val(json.data.name);
	    $('#alias').val(json.data.alias);
	    $('#description').val(json.data.description);
	    $('#application').val(json.data.application);
	    $('#managerIdentityList').val(joinValue(joinValue(json.data.managerIdentityList)));
	    $('#reviewIdentityList').val(joinValue(joinValue(json.data.reviewIdentityList)));
	    $('#beforeBeginScript').val(json.data.beforeBeginScript);
	    $('#beforeBeginScriptText').val(json.data.beforeBeginScriptText);
	    $('#afterBeginScript').val(json.data.afterBeginScript);
	    $('#afterBeginScriptText').val(json.data.afterBeginScriptText);
	    $('#beginEndScript').val(json.data.beginEndScript);
	    $('#beginEndScriptText').val(json.data.beginEndScriptText);
	    $('#afterEndScript').val(json.data.afterEndScript);
	    $('#afterEndScriptText').val(json.data.afterEndScriptText);
	    $('#startableIdentityList').val(joinValue(joinValue(json.data.startableIdentityList)));
	    $('#startableDepartmentList').val(joinValue(joinValue(json.data.startableDepartmentList)));
	    $('#startableCompanyList').val(joinValue(joinValue(json.data.startableCompanyList)));
	    $('#serialTexture').val(json.data.serialTexture);
	    $('#serialActivity').val(json.data.serialActivity);
	    $('#agentList').val(JSON.stringify(json.data.agentList, null, '\t'));
	    $('#begin').val(JSON.stringify(json.data.begin, null, '\t'));
	    $('#cancelList').val(JSON.stringify(json.data.cancelList, null, '\t'));
	    $('#choiceList').val(JSON.stringify(json.data.cancelList, null, '\t'));
	    $('#conditionList').val(JSON.stringify(json.data.choiceList, null, '\t'));
	    $('#delayList').val(JSON.stringify(json.data.delayList, null, '\t'));
	    $('#embedList').val(JSON.stringify(json.data.embedList, null, '\t'));
	    $('#endList').val(JSON.stringify(json.data.endList, null, '\t'));
	    $('#invokeList').val(JSON.stringify(json.data.invokeList, null, '\t'));
	    $('#manualList').val(JSON.stringify(json.data.manualList, null, '\t'));
	    $('#mergeList').val(JSON.stringify(json.data.mergeList, null, '\t'));
	    $('#messageList').val(JSON.stringify(json.data.messageList, null, '\t'));
	    $('#parallelList').val(JSON.stringify(json.data.parallelList, null, '\t'));
	    $('#serviceList').val(JSON.stringify(json.data.serviceList, null, '\t'));
	    $('#splitList').val(JSON.stringify(json.data.splitList, null, '\t'));
	    $('#routeList').val(JSON.stringify(json.data.routeList, null, '\t'));
	} else {
	    failure(data);
	}
    });
}

function process_put(id) {
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/process/' + id,
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    // name : String
	    // alias : String
	    // description : String
	    // creatorPerson : String
	    // lastUpdatePerson : String
	    // lastUpdateTime : Date
	    // application : String
	    // managerIdentityList : List<String>
	    // reviewIdentityList : List<String>
	    // beforeBeginScript : String
	    // beforeBeginScriptText : String
	    // afterBeginScript : String
	    // afterBeginScriptText : String
	    // beforeEndScript : String
	    // beforeEndScriptText : String
	    // afterEndScript : String
	    // afterEndScriptText : String
	    // startableIdentityList : List<String>
	    // startableDepartmentList : List<String>
	    // startableCompanyList : List<String>
	    // serialTexture : String
	    // serialActivity : String
	    name : $('#name').val(),
	    alias : $('#alias').val(),
	    description : $('#description').val(),
	    application : $('#application').val(),
	    managerIdentityList : $('#managerIdentityList').val().split(','),
	    reviewIdentityList : $('#reviewIdentityList').val().split(','),
	    beforeBeginScript : $('#beforeBeginScript').val(),
	    beforeBeginScriptText : $('#beforeBeginScriptText').val(),
	    afterBeginScript : $('#afterBeginScript').val(),
	    afterBeginScriptText : $('#afterBeginScriptText').val(),
	    beforeEndScript : $('#beforeEndScript').val(),
	    beforeEndScriptText : $('#beforeEndScriptText').val(),
	    afterEndScript : $('#afterEndScript').val(),
	    afterEndScriptText : $('#afterEndScriptText').val(),
	    startableIdentityList : $('#startableIdentityList').val().split(','),
	    startableDepartmentList : $('#startableDepartmentList').val().split(','),
	    startableCompanyList : $('#startableCompanyList').val().split(','),
	    serialTexture : $('#serialTexture').val(),
	    serialActivity : $('#serialActivity').val(),
	    agentList : jQuery.parseJSON($('#agentList').val()),
	    begin : jQuery.parseJSON($('#begin').val()),
	    cancelList : jQuery.parseJSON($('#cancelList').val()),
	    choiceList : jQuery.parseJSON($('#choiceList').val()),
	    delayList : jQuery.parseJSON($('#delayList').val()),
	    embedList : jQuery.parseJSON($('#embedList').val()),
	    endList : jQuery.parseJSON($('#endList').val()),
	    invokeList : jQuery.parseJSON($('#invokeList').val()),
	    manualList : jQuery.parseJSON($('#manualList').val()),
	    mergeList : jQuery.parseJSON($('#mergeList').val()),
	    messageList : jQuery.parseJSON($('#messageList').val()),
	    parallelList : jQuery.parseJSON($('#parallelList').val()),
	    serviceList : jQuery.parseJSON($('#serviceList').val()),
	    splitList : jQuery.parseJSON($('#splitList').val()),
	    routeList : jQuery.parseJSON($('#routeList').val())
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function process_delete(id, onlyRemoveNotCompleted) {
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/process/' + id + '/' + onlyRemoveNotCompleted,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function process_listWithApplication(id) {
    process_parameter.application = id;
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/process/application/' + id,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
	if (json.type == 'success') {
	    var str = '<table border="1" width="100%">';
	    str += '<tr><th>id</th><th>name</th><th>alias</th><th>operate</th></tr>';
	    $.each(json.data, function(index, item) {
		str += '<tr>';
		str += '<td>' + item.id + '</td>';
		str += '<td>' + item.name + '</td>';
		str += '<td>' + item.alias + '</td>';
		str += '<td>';
		str += '<a href="#" onclick="process_edit(\'' + item.id + '\')">edit</a>&nbsp;';
		str += '<a href="#" onclick="process_delete(\'' + item.id + '\', true)">delete</a>&nbsp;';
		str += '<a href="#" onclick="process_delete(\'' + item.id + '\', false)">delete all</a>';
		str += '</td>';
		str += '</tr>';
	    });
	    str += '</table>';
	    $('#content').html(str);
	}
    });
}

function process_demo_simple_create() {
    str = '<div><table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '</table></div>';
    $('#content').html(str);
    $('#post').click(function() {
	process_demo_simple_post();
    });
}

function process_demo_simple_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/process/demo/simple',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    application : $('#application').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function process_demo_split_create() {
    str = '<div><table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="post">post</a></td></tr>';
    str += '<tr><td>application:</td><td><input type="text" id="application" style="width:95%"/></td></tr>';
    str += '<tr><td>name:</td><td><input type="text" id="name" style="width:95%"/></td></tr>';
    str += '</table></div>';
    $('#content').html(str);
    $('#post').click(function() {
	process_demo_split_post();
    });
}

function process_demo_split_post() {
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/process/demo/split',
	contentType : 'application/json; charset=utf-8',
	data : JSON.stringify({
	    name : $('#name').val(),
	    application : $('#application').val()
	}),
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}