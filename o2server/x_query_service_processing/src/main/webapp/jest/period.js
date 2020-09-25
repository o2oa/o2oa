period_parameter = {};

function period_symbol(str) {
    if (str.length == 0) {
	return '(0)';
    }
    return str;
}

function period_getQuery(url) {
    $.ajax({
	type : 'get',
	dataType : 'json',
	url : url,
	contentType : 'application/json; charset=utf-8',
	xhrFields : {
	    'withCredentials' : true
	},
	crossDomain : true
    }).always(function(json) {
	$('#result').html(JSON.stringify(json, null, 4));
    });
}

function period_listCountStartTask_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="applicationStubs">applicationStubs</a>&nbsp;<a href="#" id="companyStubs">companyStubs</a>&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="byCompany">byCompany</a>&nbsp;<a href="#" id="byDepartment">byDepartment</a>&nbsp;<a href="#" id="byApplication">byApplication</a>&nbsp;<a href="#" id="byProcess">byProcess</a>&nbsp;<a href="#" id="byActivity">byActivity</a></td></tr>';
    str += '<tr><td>applicationId:</td><td><input type="text" id="applicationId" style="width:95%"/></td></tr>';
    str += '<tr><td>processId:</td><td><input type="text" id="processId" style="width:95%"/></td></tr>';
    str += '<tr><td>activityId:</td><td><input type="text" id="activityId" style="width:95%"/></td></tr>';
    str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#applicationStubs').click(function() {
	period_listStartTaskApplicationStubs();
    });
    $('#companyStubs').click(function() {
	period_listStartTaskCompanyStubs();
    });
    $('#list').click(
	    function() {
		period_listCountStartTask(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()),
			period_symbol($('#person').val()));
	    });
    $('#byCompany').click(function() {
	period_listCountStartTaskByCompany(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()));
    });
    $('#byDepartment').click(function() {
	period_listCountStartTaskByDepartment(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()), period_symbol($('#company').val()));
    });
    $('#byApplication').click(function() {
	period_listCountStartTaskByApplication(period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byProcess').click(function() {
	period_listCountStartTaskByProcess(period_symbol($('#applicationId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byActivity').click(function() {
	period_listCountStartTaskByActivity(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
}

function period_listStartTaskApplicationStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/start/task/applicationstubs';
    period_getQuery(url);
}

function period_listStartTaskCompanyStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/start/task/companystubs';
    period_getQuery(url);
}

function period_listCountStartTask(applicationId, processId, activityId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/company/' + company + '/department/' + department + '/person/' + person;
    period_getQuery(url);
}

function period_listCountStartTaskByCompany(applicationId, processId, activityId) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/by/company/';
    period_getQuery(url);
}

function period_listCountStartTaskByDepartment(applicationId, processId, activityId, company) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/company/' + company + '/by/department';
    period_getQuery(url);
}

function period_listCountStartTaskByApplication(company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/task/company/' + company + '/department/' + department + '/person/' + person + '/by/application';
    period_getQuery(url);
}

function period_listCountStartTaskByProcess(applicationId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/task/application/' + applicationId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/process';
    period_getQuery(url);
}

function period_listCountStartTaskByActivity(applicationId, processId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/task/application/' + applicationId + '/process/' + processId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/activity';
    period_getQuery(url);
}

function period_listCountCompletedTask_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="applicationStubs">applicationStubs</a>&nbsp;<a href="#" id="companyStubs">companyStubs</a>&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="byCompany">byCompany</a>&nbsp;<a href="#" id="byDepartment">byDepartment</a>&nbsp;<a href="#" id="byApplication">byApplication</a>&nbsp;<a href="#" id="byProcess">byProcess</a>&nbsp;<a href="#" id="byActivity">byActivity</a></td></tr>';
    str += '<tr><td>applicationId:</td><td><input type="text" id="applicationId" style="width:95%"/></td></tr>';
    str += '<tr><td>processId:</td><td><input type="text" id="processId" style="width:95%"/></td></tr>';
    str += '<tr><td>activityId:</td><td><input type="text" id="activityId" style="width:95%"/></td></tr>';
    str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#applicationStubs').click(function() {
	period_listCompletedTaskApplicationStubs();
    });
    $('#companyStubs').click(function() {
	period_listCompletedTaskCompanyStubs();
    });
    $('#list').click(
	    function() {
		period_listCountCompletedTask(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()),
			period_symbol($('#person').val()));
	    });
    $('#byCompany').click(function() {
	period_listCountCompletedTaskByCompany(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()));
    });
    $('#byDepartment').click(function() {
	period_listCountCompletedTaskByDepartment(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()), period_symbol($('#company').val()));
    });
    $('#byApplication').click(function() {
	period_listCountCompletedTaskByApplication(period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byProcess').click(function() {
	period_listCountCompletedTaskByProcess(period_symbol($('#applicationId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byActivity').click(function() {
	period_listCountCompletedTaskByActivity(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
}

function period_listCompletedTaskApplicationStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/completed/task/applicationstubs';
    period_getQuery(url);
}

function period_listCompletedTaskCompanyStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/completed/task/companystubs';
    period_getQuery(url);
}

function period_listCountCompletedTask(applicationId, processId, activityId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/company/' + company + '/department/' + department + '/person/' + person;
    period_getQuery(url);
}

function period_listCountCompletedTaskByCompany(applicationId, processId, activityId) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/by/company/';
    period_getQuery(url);
}

function period_listCountCompletedTaskByDepartment(applicationId, processId, activityId, company) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/company/' + company + '/by/department';
    period_getQuery(url);
}

function period_listCountCompletedTaskByApplication(company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/task/company/' + company + '/department/' + department + '/person/' + person + '/by/application';
    period_getQuery(url);
}

function period_listCountCompletedTaskByProcess(applicationId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/task/application/' + applicationId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/process';
    period_getQuery(url);
}

function period_listCountCompletedTaskByActivity(applicationId, processId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/task/application/' + applicationId + '/process/' + processId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/activity';
    period_getQuery(url);
}

function period_listCountExpiredTask_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="applicationStubs">applicationStubs</a>&nbsp;<a href="#" id="companyStubs">companyStubs</a>&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="byCompany">byCompany</a>&nbsp;<a href="#" id="byDepartment">byDepartment</a>&nbsp;<a href="#" id="byApplication">byApplication</a>&nbsp;<a href="#" id="byProcess">byProcess</a>&nbsp;<a href="#" id="byActivity">byActivity</a></td></tr>';
    str += '<tr><td>applicationId:</td><td><input type="text" id="applicationId" style="width:95%"/></td></tr>';
    str += '<tr><td>processId:</td><td><input type="text" id="processId" style="width:95%"/></td></tr>';
    str += '<tr><td>activityId:</td><td><input type="text" id="activityId" style="width:95%"/></td></tr>';
    str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#applicationStubs').click(function() {
	period_listExpiredTaskApplicationStubs();
    });
    $('#companyStubs').click(function() {
	period_listExpiredTaskCompanyStubs();
    });
    $('#list').click(
	    function() {
		period_listCountExpiredTask(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()),
			period_symbol($('#person').val()));
	    });
    $('#byCompany').click(function() {
	period_listCountExpiredTaskByCompany(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()));
    });
    $('#byDepartment').click(function() {
	period_listCountExpiredTaskByDepartment(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#activityId').val()), period_symbol($('#company').val()));
    });
    $('#byApplication').click(function() {
	period_listCountExpiredTaskByApplication(period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byProcess').click(function() {
	period_listCountExpiredTaskByProcess(period_symbol($('#applicationId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byActivity').click(function() {
	period_listCountExpiredTaskByActivity(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
}

function period_listExpiredTaskApplicationStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/expired/task/applicationstubs';
    period_getQuery(url);
}

function period_listExpiredTaskCompanyStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/expired/task/companystubs';
    period_getQuery(url);
}

function period_listCountExpiredTask(applicationId, processId, activityId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/company/' + company + '/department/' + department + '/person/' + person;
    period_getQuery(url);
}

function period_listCountExpiredTaskByCompany(applicationId, processId, activityId) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/by/company/';
    period_getQuery(url);
}

function period_listCountExpiredTaskByDepartment(applicationId, processId, activityId, company) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/task/application/' + applicationId + '/process/' + processId + '/activity/' + activityId + '/company/' + company + '/by/department';
    period_getQuery(url);
}

function period_listCountExpiredTaskByApplication(company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/task/company/' + company + '/department/' + department + '/person/' + person + '/by/application';
    period_getQuery(url);
}

function period_listCountExpiredTaskByProcess(applicationId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/task/application/' + applicationId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/process';
    period_getQuery(url);
}

function period_listCountExpiredTaskByActivity(applicationId, processId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/task/application/' + applicationId + '/process/' + processId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/activity';
    period_getQuery(url);
}

function period_listCountStartWork_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="applicationStubs">applicationStubs</a>&nbsp;<a href="#" id="companyStubs">companyStubs</a>&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="byCompany">byCompany</a>&nbsp;<a href="#" id="byDepartment">byDepartment</a>&nbsp;<a href="#" id="byApplication">byApplication</a>&nbsp;<a href="#" id="byProcess">byProcess</a></td></tr>';
    str += '<tr><td>applicationId:</td><td><input type="text" id="applicationId" style="width:95%"/></td></tr>';
    str += '<tr><td>processId:</td><td><input type="text" id="processId" style="width:95%"/></td></tr>';
    str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);

    $('#applicationStubs').click(function() {
	period_listStartWorkApplicationStubs();
    });
    $('#companyStubs').click(function() {
	period_listStartWorkCompanyStubs();
    });
    $('#list').click(function() {
	period_listCountStartWork(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byCompany').click(function() {
	period_listCountStartWorkByCompany(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()));
    });
    $('#byDepartment').click(function() {
	period_listCountStartWorkByDepartment(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()));
    });
    $('#byApplication').click(function() {
	period_listCountStartWorkByApplication(period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byProcess').click(function() {
	period_listCountStartWorkByProcess(period_symbol($('#applicationId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
}

function period_listStartWorkApplicationStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/start/work/applicationstubs';
    period_getQuery(url);
}

function period_listStartWorkCompanyStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/start/work/companystubs';
    period_getQuery(url);
}

function period_listCountStartWork(applicationId, processId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/work/application/' + applicationId + '/process/' + processId + '/company/' + company + '/department/' + department + '/person/' + person;
    period_getQuery(url);
}

function period_listCountStartWorkByCompany(applicationId, processId) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/work/application/' + applicationId + '/process/' + processId + '/by/company/';
    period_getQuery(url);
}

function period_listCountStartWorkByDepartment(applicationId, processId, company) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/work/application/' + applicationId + '/process/' + processId + '/company/' + company + '/by/department';
    period_getQuery(url);
}

function period_listCountStartWorkByApplication(company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/work/company/' + company + '/department/' + department + '/person/' + person + '/by/application';
    period_getQuery(url);
}

function period_listCountStartWorkByProcess(applicationId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/start/work/application/' + applicationId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/process';
    period_getQuery(url);
}

function period_listCountCompletedWork_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="applicationStubs">applicationStubs</a>&nbsp;<a href="#" id="companyStubs">companyStubs</a>&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="byCompany">byCompany</a>&nbsp;<a href="#" id="byDepartment">byDepartment</a>&nbsp;<a href="#" id="byApplication">byApplication</a>&nbsp;<a href="#" id="byProcess">byProcess</a></td></tr>';
    str += '<tr><td>applicationId:</td><td><input type="text" id="applicationId" style="width:95%"/></td></tr>';
    str += '<tr><td>processId:</td><td><input type="text" id="processId" style="width:95%"/></td></tr>';
    str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#applicationStubs').click(function() {
	period_listCompletedWorkApplicationStubs();
    });
    $('#companyStubs').click(function() {
	period_listCompletedWorkCompanyStubs();
    });
    $('#list').click(function() {
	period_listCountCompletedWork(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byCompany').click(function() {
	period_listCountCompletedWorkByCompany(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()));
    });
    $('#byDepartment').click(function() {
	period_listCountCompletedWorkByDepartment(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()));
    });
    $('#byApplication').click(function() {
	period_listCountCompletedWorkByApplication(period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byProcess').click(function() {
	period_listCountCompletedWorkByProcess(period_symbol($('#applicationId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
}

function period_listCompletedWorkApplicationStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/completed/work/applicationstubs';
    period_getQuery(url);
}

function period_listCompletedWorkCompanyStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/completed/work/companystubs';
    period_getQuery(url);
}

function period_listCountCompletedWork(applicationId, processId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/work/application/' + applicationId + '/process/' + processId + '/company/' + company + '/department/' + department + '/person/' + person;
    period_getQuery(url);
}

function period_listCountCompletedWorkByCompany(applicationId, processId) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/work/application/' + applicationId + '/process/' + processId + '/by/company/';
    period_getQuery(url);
}

function period_listCountCompletedWorkByDepartment(applicationId, processId, company) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/work/application/' + applicationId + '/process/' + processId + '/company/' + company + '/by/department';
    period_getQuery(url);
}

function period_listCountCompletedWorkByApplication(company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/work/company/' + company + '/department/' + department + '/person/' + person + '/by/application';
    period_getQuery(url);
}

function period_listCountCompletedWorkByProcess(applicationId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/completed/work/application/' + applicationId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/process';
    period_getQuery(url);
}

function period_listCountExpiredWork_init() {
    $('#result').html('');
    str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="applicationStubs">applicationStubs</a>&nbsp;<a href="#" id="companyStubs">companyStubs</a>&nbsp;<a href="#" id="list">list</a></td></tr>';
    str += '<tr><td colspan="2"><a href="#" id="byCompany">byCompany</a>&nbsp;<a href="#" id="byDepartment">byDepartment</a>&nbsp;<a href="#" id="byApplication">byApplication</a>&nbsp;<a href="#" id="byProcess">byProcess</a></td></tr>';
    str += '<tr><td>applicationId:</td><td><input type="text" id="applicationId" style="width:95%"/></td></tr>';
    str += '<tr><td>processId:</td><td><input type="text" id="processId" style="width:95%"/></td></tr>';
    str += '<tr><td>company:</td><td><input type="text" id="company" style="width:95%"/></td></tr>';
    str += '<tr><td>department:</td><td><input type="text" id="department" style="width:95%"/></td></tr>';
    str += '<tr><td>person:</td><td><input type="text" id="person" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);

    $('#applicationStubs').click(function() {
	period_listExpiredWorkApplicationStubs();
    });
    $('#companyStubs').click(function() {
	period_listExpiredWorkCompanyStubs();
    });
    $('#list').click(function() {
	period_listCountExpiredWork(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byCompany').click(function() {
	period_listCountExpiredWorkByCompany(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()));
    });
    $('#byDepartment').click(function() {
	period_listCountExpiredWorkByDepartment(period_symbol($('#applicationId').val()), period_symbol($('#processId').val()), period_symbol($('#company').val()));
    });
    $('#byApplication').click(function() {
	period_listCountExpiredWorkByApplication(period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
    $('#byProcess').click(function() {
	period_listCountExpiredWorkByProcess(period_symbol($('#applicationId').val()), period_symbol($('#company').val()), period_symbol($('#department').val()), period_symbol($('#person').val()));
    });
}

function period_listExpiredWorkApplicationStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/expired/work/applicationstubs';
    period_getQuery(url);
}

function period_listExpiredWorkCompanyStubs() {
    $('#result').html('');
    var url = '../jaxrs/period/list/expired/work/companystubs';
    period_getQuery(url);
}

function period_listCountExpiredWork(applicationId, processId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/work/application/' + applicationId + '/process/' + processId + '/company/' + company + '/department/' + department + '/person/' + person;
    period_getQuery(url);
}

function period_listCountExpiredWorkByCompany(applicationId, processId) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/work/application/' + applicationId + '/process/' + processId + '/by/company/';
    period_getQuery(url);
}

function period_listCountExpiredWorkByDepartment(applicationId, processId, company) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/work/application/' + applicationId + '/process/' + processId + '/company/' + company + '/by/department';
    period_getQuery(url);
}

function period_listCountExpiredWorkByApplication(company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/work/company/' + company + '/department/' + department + '/person/' + person + '/by/application';
    period_getQuery(url);
}

function period_listCountExpiredWorkByProcess(applicationId, company, department, person) {
    $('#result').html('');
    var url = '../jaxrs/period/list/count/expired/work/application/' + applicationId + '/company/' + company + '/department/' + department + '/person/' + person + '/by/process';
    period_getQuery(url);
}