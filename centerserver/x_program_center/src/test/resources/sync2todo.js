load("nashorn:mozilla_compat.js");

var resp = resources.getContext().applications().getQuery(com.x.base.core.project.x_message_assemble_communicate.class, 'consume/list/sync2todo/count/10');

var text = resp.getData().toString();

var list = JSON.parse(text);

for (var i = 0; i < list.length; i++) {
	var item = list[i];
	switch (item.type) {
	case 'task_create':
		var task = JSON.parse(item.body);
		send_task_create(item.id, task);
		break;
	case 'taskCompleted_create':
		break;
	defalut: break;
	}
}

function date_to_string(date) {
date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + 'T' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
}

function send_task_create(id, task) {
var addr = 'http://172.16.92.50:9085/todo_hk/PushData/todoPush.do';
var xml = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>';
xml += '<apps id=\"strmgt\" name=\"战略管理\">';
xml += '<open>';
xml += '<work>';
xml += '<doc_id>' + task.id + '</doc_id>';
xml += '<url>url</url>';
xml += '<work_id>' + task.workId + '</work_id>';
xml += '<user_id>' + task.person + '</user_id>';
xml += '<doc_type>测试拆分</doc_type>';
xml += '<doc_type_zh_hk />';
xml += '<doc_type_en />';
xml += '<title>测试拆分lj444</title>';
xml += '<start_time>' + task.startTime + '</start_time>';
xml += '<from_man>appsadmin</from_man>';
xml += '<from_man_zh_hk />';
xml += '<from_man_en />';
xml += '<timeout_time />';
xml += '<form_id>' + task.form + '</form_id>';
xml += '<author_id>testCFnull</author_id>';
xml += '<pri>1</pri>';
xml += '<node_name>' + task.activityName + '</node_name>';
xml += '<node_name_zh_hk>处理2</node_name_zh_hk>';
xml += '<node_name_en />';
xml += '<type>0</type>';
xml += '</work>';
xml += '</open>';
xml += '</apps>';

var ArrayList = Java.type('java.util.ArrayList');
var heads = new ArrayList();
var NameValuePair = Java.type('com.x.base.core.project.bean.NameValuePair');
var p1 = new NameValuePair('Content-Type', 'application/x-www-form-urlencoded; charset=utf-8');
heads.add(p1);
var parameters = 'op=addForOpen&data=' + encodeURIComponent(xml);

var HttpConnectionClass = Java.type('com.x.base.core.project.connection.HttpConnection');

var resp = HttpConnectionClass.postAsString(addr, heads, parameters);
print(resp);
// resources.getContext().applications().getQuery("com.x.base.core.project.x_message_assemble_communicate",
// 'consume/' + id + '/type/sync2todo');
}