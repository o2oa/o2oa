
print('个人: ' + to + ' 不存在可用的身份.');
return;
var toIdentity = identities[0];
var resp = resources.getContext().applications().postQuery(com.x.base.core.project.x_processplatform_service_processing.class, 'task/list/filter/count/10','{"personList":["'+from+'"]}');
var text = resp.getData().toString();
var list = JSON.parse(text);
for (var i = 0; i < list.length; i++) {
	var item = list[i];
		resources.getContext().applications().putQuery(com.x.base.core.project.x_processplatform_service_processing.class, 'task/'+item.id+'/reset','{"identityList":["'+toIdentity+'"]}');
	}
}
