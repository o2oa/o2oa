package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutCount;
import com.x.organization.core.express.wrap.WrapPerson;
import com.x.processplatform.assemble.surface.Business;

class ActionCountWithPerson extends ActionBase {

	ActionResult<WrapOutCount> execute(String credential) throws Exception {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapOutCount wrap = new WrapOutCount();
			Business business = new Business(emc);
			WrapPerson wrapPerson = business.organization().person().getWithCredential(credential);
			if (null != wrapPerson) {
				Long count = business.read().countWithPerson(wrapPerson.getName());
				wrap.setCount(count);
			}
			result.setData(wrap);
			return result;
		}
	}
}