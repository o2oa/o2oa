load("nashorn:mozilla_compat.js");
print(requestText)
var impower = JSON.parse(requestText);
var from = impower.from;
var fromPerson = resources.getOrganization().person().get(from);
if (fromPerson == ''){
	print('授权个人: ' + from + ' 不存在.');
} else {
	var to =impower.to;
	var identities = resources.getOrganization().identity().listWithPerson(to);
	print(identities.length)
	if (identities.length < 1) {
		print('被授权个人: ' + to + ' 不存在可用的身份.');
	}  else {
		var toIdentity = identities[0];
		var resp = resources.getContext().applications().postQuery(com.x.base.core.project.x_processplatform_service_processing.class, 'task/list/filter/count/10','{"personList":["'+fromPerson+'"]}');
		var text = resp.getData().toString();
		var list = JSON.parse(text);
		for (var i = 0; i < list.length; i++) {
			var item = list[i];
			print('正在授权:' + item.title + ', 由:' +fromPerson+', 授权至:' + toIdentity+ '.');
			resources.getContext().applications().putQuery(com.x.base.core.project.x_processplatform_service_processing.class, 'task/'+item.id+'/reset','{"identityList":["'+toIdentity+'"]}');
		}
	}
}