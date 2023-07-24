package com.x.processplatform.assemble.surface.jaxrs.job;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class V2Projection extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Projection.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		Process process = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);
			Work work = emc.firstEqual(Work.class, Work.job_FIELDNAME, job);

			if (null != work) {
				job = work.getJob();
				Control controlOfWork = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit()
						.build();
				if (BooleanUtils.isNotTrue(controlOfWork.getAllowVisit())) {
					throw new ExceptionAccessDenied(effectivePerson);
				}
				process = emc.find(work.getProcess(), Process.class);
				if (null == process) {
					throw new ExceptionEntityNotExist(job);
				}
			} else {
				WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, job);
				if (null != workCompleted) {
					job = workCompleted.getJob();
					Control controlOfWorkCompleted = new WorkCompletedControlBuilder(effectivePerson, business,
							workCompleted).enableAllowVisit().build();
					if (BooleanUtils.isNotTrue(controlOfWorkCompleted.getAllowVisit())) {
						throw new ExceptionAccessDenied(effectivePerson);
					}
					process = emc.find(workCompleted.getProcess(), Process.class);
					if (null == process) {
						throw new ExceptionEntityNotExist(job);
					}
				}
			}
		}

		Wo wo = ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("job", "v2", job, "projection"), job).getData(Wo.class);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.job.V2Projection$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -3206075665001702872L;

	}

}