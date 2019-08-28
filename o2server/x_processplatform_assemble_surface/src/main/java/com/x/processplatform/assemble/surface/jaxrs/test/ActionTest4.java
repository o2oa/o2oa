package com.x.processplatform.assemble.surface.jaxrs.test;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest4 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest4.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson, String op, String data) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			return result;
		}
	}
}
