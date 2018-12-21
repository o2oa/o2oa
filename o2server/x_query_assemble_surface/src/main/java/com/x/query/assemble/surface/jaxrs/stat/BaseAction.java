package com.x.query.assemble.surface.jaxrs.stat;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.surface.Business;
import com.x.query.core.express.plan.Runtime;

abstract class BaseAction extends StandardJaxrsAction {

	protected <T extends Runtime> void append(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		t.person = effectivePerson.getDistinguishedName();
		t.identityList = business.organization().identity().listWithPerson(effectivePerson);
		t.unitList = business.organization().unit().listWithPerson(effectivePerson);
		t.unitAllList = business.organization().unit().listWithPersonSupNested(effectivePerson);
		t.groupList = business.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
		t.roleList = business.organization().role().listWithPerson(effectivePerson);
	}
}
