package com.x.processplatform.assemble.surface.jaxrs.test;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest6 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest6.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			System.out.println("!");
			System.out.println(Config.workTime().minutesOfWorkDay());
			System.out.println("!");
			return null;
		}
	}
}
