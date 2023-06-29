package com.x.processplatform.assemble.surface.jaxrs.job;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;

class ActionAllowVisitWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAllowVisitWithPerson.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job)  {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Control control = new JobControlBuilder(effectivePerson, business, job).enableAllowVisit().build();
			wo.setValue(control.getAllowVisit());
		}
		result.setData(wo);
		return result;
	}

	public class Wo extends WrapBoolean {

		private static final long serialVersionUID = 3303555046861835422L;

	}
}