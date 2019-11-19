package com.x.processplatform.assemble.surface.jaxrs.test;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;

class ActionTest2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest2.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Object> result = new ActionResult<>();
			System.out.println("!!!!!!!!!!!!!!!!!!");
			System.out.println(business.organization().unit().getWithIdentityWithLevel("p1", 1));
			System.out.println("!!!!!!!!!!!!!!!!!!");
			result.setData(business.organization().unit().getWithIdentityWithLevel("p1", 100));
			return result;
		}
	}
}
