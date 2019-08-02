package com.x.query.assemble.designer.jaxrs.statement;

import java.util.LinkedHashMap;
import java.util.Map;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.express.statement.Runtime;

abstract class BaseAction extends StandardJaxrsAction {

	protected Runtime runtime(EffectivePerson effectivePerson, Business business, Map<String, Object> parameter,
			Integer page, Integer size) throws Exception {
		Runtime runtime = new Runtime();
		runtime.person = effectivePerson.getDistinguishedName();
		runtime.identityList = business.organization().identity().listWithPerson(effectivePerson);
		runtime.unitList = business.organization().unit().listWithPerson(effectivePerson);
		runtime.unitAllList = business.organization().unit().listWithPersonSupNested(effectivePerson);
		runtime.groupList = business.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
		runtime.roleList = business.organization().role().listWithPerson(effectivePerson);
		runtime.parameter = (null == parameter) ? new LinkedHashMap<String, Object>() : parameter;
		runtime.page = page;
		runtime.size = size;
		return runtime;
	}

}
