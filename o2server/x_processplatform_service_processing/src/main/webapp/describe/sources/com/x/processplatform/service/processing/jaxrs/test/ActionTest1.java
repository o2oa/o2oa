package com.x.processplatform.service.processing.jaxrs.test;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.core.entity.content.Work;

class ActionTest1 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			String id = "0cf6f6fd-072a-4b63-957c-ee85d6a95b9f";
			Work work = emc.find(id, Work.class);
			emc.beginTransaction(Work.class);
			try (EntityManagerContainer emc2 = EntityManagerContainerFactory.instance().create()) {
				emc2.beginTransaction(Work.class);
				Work work2 = emc2.find(id, Work.class);
				emc2.commit();
			}
			emc.commit();
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}