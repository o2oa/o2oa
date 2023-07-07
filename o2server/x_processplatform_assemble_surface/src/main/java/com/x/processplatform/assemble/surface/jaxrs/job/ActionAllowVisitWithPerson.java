package com.x.processplatform.assemble.surface.jaxrs.job;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionAllowVisitWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAllowVisitWithPerson.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, String person) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String pro = null;
			String app = null;
			Work work = business.entityManagerContainer().firstEqual(Work.class, Work.job_FIELDNAME, job);
			if (null != work) {
				pro = work.getProcess();
				app = work.getApplication();
			} else {
				WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
						WorkCompleted.job_FIELDNAME, job);
				if (null != workCompleted) {
					pro = workCompleted.getProcess();
					app = workCompleted.getApplication();
				}
			}
			wo.setValue(business.ifPersonHasTaskReadTaskCompletedReadCompletedReviewWithJob(person, job)
					|| business.ifPersonCanManageApplicationOrProcess(person, app, pro)
					|| business.ifJobHasBeenCorrelation(person, job));
		}
		result.setData(wo);
		return result;
	}

	public class Wo extends WrapBoolean {

		private static final long serialVersionUID = 3303555046861835422L;

	}
}