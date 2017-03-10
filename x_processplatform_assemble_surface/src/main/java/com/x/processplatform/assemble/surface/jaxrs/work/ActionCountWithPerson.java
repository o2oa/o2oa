package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutMap;
import com.x.organization.core.express.wrap.WrapPerson;
import com.x.processplatform.assemble.surface.Business;

class ActionCountWithPerson extends ActionBase {

	ActionResult<WrapOutMap> execute(String credential) throws Exception {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapOutMap wrap = new WrapOutMap();
			Business business = new Business(emc);
			WrapPerson wrapPerson = business.organization().person().getWithCredential(credential);
			if (null != wrapPerson) {
				Long count = 0L;
				count = business.task().countWithPerson(wrapPerson.getName());
				wrap.put("task", count);
				count = business.taskCompleted().countWithPerson(wrapPerson.getName());
				wrap.put("taskCompleted", count);
				count = business.read().countWithPerson(wrapPerson.getName());
				wrap.put("read", count);
				count = business.readCompleted().countWithPerson(wrapPerson.getName());
				wrap.put("readCompleted", count);
				count = business.review().countWithPerson(wrapPerson.getName());
				wrap.put("review", count);
			} else {
				wrap.put("task", 0L);
				wrap.put("taskCompleted", 0L);
				wrap.put("read", 0L);
				wrap.put("readCompleted", 0L);
				wrap.put("revivew", 0L);
			}
			result.setData(wrap);
			return result;
		}
	}

}