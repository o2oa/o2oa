work_parameter = {
    first : '(0)',
    last : '(0)',
    count : 20
};

function work_count_init() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="2"><a href="#" id="countWithPerson">countWithPerson</a>&nbsp;<a href="#" id="countWithApplication">countWithApplication</a>&nbsp;<a href="#" id="countWithProcess">countWithProcess</a></td></tr>';
    str += '</thead>'
    str += '<tbody id="grid">';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"></td></tr>';
    str += '<tr><td>applicationFlag:</td><td><input type="text" id="applicationFlag" style="width:95%"></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#countWithPerson').click(function() {
	work_countWithPerson($('#person').val());
    });
    $('#countWithApplication').click(function() {
	work_countWithApplication();
    });
    $('#countWithProcess').click(function() {
	work_countWithProcess($('#applicationFlag').val());
    });
}

function work_countWithPerson(person) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/count/' + person,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_countWithApplication() {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/count/application',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_countWithProcess(applicationFlag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/count/application/' + applicationFlag + '/process',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_get_init() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="2"><a href="#" id="complex">complex</a>&nbsp;<a href="#" id="complexMobile">complexMobile</a>&nbsp;<a href="#" id="complexAppointForm">complexAppointForm</a>&nbsp;<a href="#" id="complexAppointFormMobile">complexAppointFormMobile</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="processing">processing</a>&nbsp;<a href="#" id="reroute">reroute</a>&nbsp;<a href="#" id="retract">retract</a>&nbsp;<a href="#" id="delete">delete</a></td></tr>';
    str += '</thead>';
    str += '<tbody id="grid">';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"></td></tr>';
    str += '<tr><td>formFlag:</td><td><input type="text" id="formFlag" style="width:95%"></td></tr>';
    str += '<tr><td>activityId:</td><td><input type="text" id="activityId" style="width:95%"></td></tr>';
    str += '<tr><td>activityType:</td><td><input type="text" id="activityType" style="width:95%"></td></tr>';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#complex').click(function() {
	work_complex($('#workId').val());
    });
    $('#complexMobile').click(function() {
	work_complexMobile($('#workId').val());
    });
    $('#complexAppointForm').click(function() {
	work_complexAppointForm($('#workId').val(), $('#formFlag').val());
    });
    $('#complexAppointFormMobile').click(function() {
	work_complexAppointFormMobile($('#workId').val(), $('#formFlag').val());
    });
    $('#processing').click(function() {
	work_processing($('#workId').val());
    });
    $('#reroute').click(function() {
	work_reroute($('#workId').val(), $('#activityId').val(), $('#activityType').val());
    });
    $('#retract').click(function() {
	work_retract($('#workId').val());
    });
    $('#delete').click(function() {
	work_retract($('#delete').val());
    });
}

function work_complex(workId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/complex',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_complexMobile(workId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/complex/mobile',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_complexAppointForm(workId, formFlag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/complex/appoint/form/' + formFlag,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_complexAppointFormMobile(workId, formFlag) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/complex/appoint/form/' + formFlag + '/mobile',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_processing(workId) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/processing',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_reroute(workId, activityId, activityType) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/reroute/activity/' + activityId + '/activitytype/' + activityType,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_retract(workId) {
    $('#result').html('');
    $.ajax({
	type : 'put',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/retract',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_delete(workId) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/work/' + workId,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_list_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr>';
    str += '</thead>';
    str += '<tbody id="gird">';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	work_list_next();
    });
    $('#prev').click(function() {
	work_list_prev();
    });
    work_parameter.first = '(0)';
    work_parameter.last = '(0)';
    work_list_next();
}

function work_list_next(id) {
    var id = (id ? id : work_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/next/' + work_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.first = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_list_prev(id) {
    var id = (id ? id : work_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/prev/' + work_parameter.count,
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.last = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_listWithApplication_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><th colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></th></tr>';
    str += '<tr><th>applicationFlag:</th><th colspan="3"><input type="text" id="applicationFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '</thead>';
    str += '<tbody id="gird">';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	work_listWithApplication_next(null, $('#applicationFlag').val());
    });
    $('#prev').click(function() {
	work_listWithApplication_prev(null, $('#applicationFlag').val());
    });
    work_parameter.first = '(0)';
    work_parameter.last = '(0)';
}

function work_listWithApplication_next(id, applicationFlag) {
    var id = (id ? id : work_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/next/' + work_parameter.count + '/application/{applicationFlag}',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.first = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_listWithApplication_prev(id, applicationFlag) {
    var id = (id ? id : work_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/prev/' + work_parameter.count + '/application/{applicationFlag}',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.last = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_listWithProcess_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><th colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></th></tr>';
    str += '<tr><th>processFlag:</th><th colspan="3"><input type="text" id="processFlag" style="width:95%"/></th></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr></thead>';
    str += '</thead>';
    str += '<tbody id="gird">';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	work_listWithProcess_next(null, $('#processFlag').val());
    });
    $('#prev').click(function() {
	work_listWithProcess_prev(null, $('#processFlag').val());
    });
    work_parameter.first = '(0)';
    work_parameter.last = '(0)';
}

function work_listWithProcess_next(id, processFlag) {
    var id = (id ? id : work_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/next/' + work_parameter.count + '/process/{processFlag}',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.first = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_listWithProcess_prev(id, processFlag) {
    var id = (id ? id : work_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/prev/' + work_parameter.count + '/process/{processFlag}',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.last = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_list_grid(json) {
    if (json.data && json.data.length > 0) {
	str = '';
	$.each(json.data, function(index, item) {
	    str += '<tr>';
	    str += '<td>' + item.rank + '</td>';
	    str += '<td>' + item.id + '</td>';
	    str += '<td>' + item.title + '</td>';
	    str += '<td>' + item.processName + '</td>';
	    str += '</tr>';
	});
	$('#total').html(json.count);
	$('#grid').html(str);
    } else {
	$('#total').html('0');
	$('#grid').html('');
    }
}

function work_manage_get_init() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a>&nbsp;<a href="#" id="assignment">assignment</a>&nbsp;<a href="#" id="listRelative">listRelative</a>&nbsp;<a href="#" id="deleteSingle">deleteSingle</a>&nbsp;<a href="#" id="deleteRelative">deleteRelative</a></td></tr>';
    str += '<tr><td>workId:</td><td><input type="text" id="workId" style="width:95%"/></td></tr>';
    str += '</thead>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	work_manage_get($('#workId').val());
    });
    $('#assignment').click(function() {
	work_manage_assignment($('#workId').val());
    });
    $('#listRelative').click(function() {
	work_manage_listRelative($('#workId').val());
    });
    $('#deleteSingle').click(function() {
	work_manage_deleteSingle($('#workId').val());
    });
    $('#deleteRelative').click(function() {
	work_manage_deleteRelative($('#workId').val());
    });
}

function work_manage_get(workId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_assignment(workId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/assignment/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_listRelative(workId) {
    $('#result').html('');
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/relative/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_deleteSingle(workId) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/single/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_deleteRelative(workId) {
    $('#result').html('');
    $.ajax({
	type : 'delete',
	dataType : 'json',
	url : '../jaxrs/work/' + workId + '/relative/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_countWithProcess_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="2"><a href="#" id="get">get</a></td></tr>';
    str += '<tr><td>applicationFlag:</td><td colspan="3"><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '</thead>';
    str += '</table>';
    $('#content').html(str);
    $('#get').click(function() {
	$.ajax({
	    type : 'get',
	    dataType : 'json',
	    url : '../jaxrs/work/list/count/application/' + $('#applicationFlag').val() + '/process/manage',
	    xhrFields : {
		'withCredentials' : true
	    },
	    crossDomain : true
	}).always(function(json) {
	    $('#result').html(JSON.stringify(json, null, 4));
	});
    });
}

function work_manage_list_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="4"><a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><td>applicationFlag:</td><td colspan="3"><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr>';
    str += '</thead>';
    str += '<tbody id="gird">';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	work_manage_list_next(null, $('#applicationFlag').val());
    });
    $('#prev').click(function() {
	work_manage_list_prev(null, $('#applicationFlag').val());
    });
    work_parameter.first = '(0)';
    work_parameter.last = '(0)';
}

function work_manage_list_next(id, applicationFlag) {
    var id = (id ? id : work_parameter.last);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/next/' + work_parameter.count + '/application/' + applicationFlag + '/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.first = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_list_prev(id, applicationFlag) {
    var id = (id ? id : work_parameter.first);
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/prev/' + work_parameter.count + '/application/' + applicationFlag + '/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.last = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_filter_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<thead>';
    str += '<tr><td colspan="4"><a href="#" id="filterAttribute">filterAttribute</a>&nbsp;<a href="#" id="clear">clear</a>&nbsp;<a href="#" id="prev">prev</a>&nbsp;<a href="#" id="next">next</a>&nbsp;<span id="total">0</span></td></tr>';
    str += '<tr><td>applicationFlag:</td><td colspan="3"><input type="text" id="applicationFlag" style="width:95%"/></td></tr>';
    str += '<tr><td>processFilter:</td><td colspan="3"><select id="processFilter"/></td></tr>';
    str += '<tr><td>creatorCompanyFilter:</td><td colspan="3"><select id="creatorCompanyFilter"/></td></tr>';
    str += '<tr><td>creatorDepartment:</td><td colspan="3"><select id="creatorDepartment"/></td></tr>';
    str += '<tr><td>activityName:</td><td colspan="3"><select id="activityName"/></td></tr>';
    str += '<tr><td>startTimeMonth:</td><td colspan="3"><select id="startTimeMonth"/></td></tr>';
    str += '<tr><td>workStatus:</td><td colspan="3"><select id="workStatus"/></td></tr>';
    str += '<tr><td>key:</td><td colspan="3"><input type="text" id = "keyFilter" style="width:95%"/></td></tr>';
    str += '<tr><th>rank</th><th>id</th><th>title</th><th>processName</th></tr>';
    str += '</thead>';
    str += '<tbody id="gird">';
    str += '</tbody>';
    str += '</table>';
    $('#content').html(str);
    $('#next').click(function() {
	work_manage_filter_list_next(null, $('#applicationFlag').val());
    });
    $('#prev').click(function() {
	work_manage_filter_list_prev(null, $('#applicationFlag').val());
    });
    $('#clear').click(function() {
	work_parameter.first = '(0)';
	work_parameter.last = '(0)';
    });
    $('#filterAttribute').click(function() {
	$.ajax({
	    type : 'get',
	    dataType : 'json',
	    url : '../jaxrs/work/filter/attribute/application/' + $('#applicationFlag').val() + '/manage',
	    xhrFields : {
		'withCredentials' : true
	    },
	    crossDomain : true
	}).done(function(json) {
	    if (json.type == 'success') {
		var txt = '<option value="">all</option>';
		if (json.data.processList) {
		    $.each(json.data.processList, function(index, item) {
			txt += '<option value="' + item.value + '">' + item.name + '</option>';
		    });
		}
		$('#processFilter').html(txt);
		txt = '<option value="">all</option>';
		if (json.data.creatorCompanyList) {
		    $.each(json.data.creatorCompanyList, function(index, item) {
			txt += '<option value="' + item.value + '">' + item.name + '</option>';
		    });
		}
		$('#creatorCompanyFilter').html(txt);
		txt = '<option value="">all</option>';
		if (json.data.creatorDepartmentList) {
		    $.each(json.data.creatorDepartmentList, function(index, item) {
			txt += '<option value="' + item.value + '">' + item.name + '</option>';
		    });
		}
		$('#creatorDepartmentFilter').html(txt);
		txt = '<option value="">all</option>';
		if (json.data.creatorDepartmentList) {
		    $.each(json.data.creatorDepartmentList, function(index, item) {
			txt += '<option value="' + item.value + '">' + item.name + '</option>';
		    });
		}
		$('#creatorDepartmentFilter').html(txt);
		txt = '<option value="">all</option>';
		if (json.data.activityNameList) {
		    $.each(json.data.activityNameList, function(index, item) {
			txt += '<option value="' + item.value + '">' + item.name + '</option>';
		    });
		}
		$('#activityNameFilter').html(txt);
		txt = '<option value="">all</option>';
		if (json.data.startTimeMonthList) {
		    $.each(json.data.startTimeMonthList, function(index, item) {
			txt += '<option value="' + item.value + '">' + item.name + '</option>';
		    });
		}
		$('#startTimeMonthFilter').html(txt);
		txt = '<option value="">all</option>';
		if (json.data.workStatusList) {
		    $.each(json.data.workStatusList, function(index, item) {
			txt += '<option value="' + item.value + '">' + item.name + '</option>';
		    });
		}
		$('#workStatusFilter').html(txt);
	    }
	}).always(function(json) {
	    $('#result').html(JSON.stringify(json, null, 4));
	});
    });
    work_parameter.first = '(0)';
    work_parameter.last = '(0)';
}

function work_manage_filter_list_next(id, applicationFlag) {
    var id = (id ? id : work_parameter.last);
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/next/' + work_parameter.count + '/application/{applicationFlag}/filter/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    process : $('#processFilter').val(),
	    creatorCompany : $('#creatorCompanyFilter').val(),
	    creatorDepartment : $('#creatorDepartmentFilter').val(),
	    activityName : $('#activityNameFilter').val(),
	    startTimeMonth : $('#startTimeMonthFilter').val(),
	    workStatus : $('#workStatusFilter').val(),
	    key : $('#keyFilter').val()
	}),
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.first = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function work_manage_filter_list_prev(id, applicationFlag) {
    var id = (id ? id : work_parameter.first);
    $.ajax({
	type : 'post',
	dataType : 'json',
	url : '../jaxrs/work/list/' + id + '/prev/' + work_parameter.count + '/application/{applicationFlag}/filter/manage',
	xhrFields : {
	    'withCredentials' : true
	},
	data : JSON.stringify({
	    process : $('#processFilter').val(),
	    creatorCompany : $('#creatorCompanyFilter').val(),
	    creatorDepartment : $('#creatorDepartmentFilter').val(),
	    activityName : $('#activityNameFilter').val(),
	    startTimeMonth : $('#startTimeMonthFilter').val(),
	    workStatus : $('#workStatusFilter').val(),
	    key : $('#keyFilter').val()
	}),
	crossDomain : true
    }).done(function(json) {
	if (json.type == 'success') {
	    if (json.data.length > 0) {
		work_parameter.first = json.data[0].id;
		work_parameter.last = json.data[json.data.length - 1].id;
	    } else {
		work_parameter.last = '(0)';
	    }
	    work_list_grid(json);
	}
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}
