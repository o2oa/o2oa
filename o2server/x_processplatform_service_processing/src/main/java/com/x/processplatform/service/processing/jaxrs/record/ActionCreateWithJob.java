package com.x.processplatform.service.processing.jaxrs.record;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionCreateWithJob extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreateWithJob.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		Callable<ActionResult<Wo>> callable = () -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				if ((emc.countEqual(Work.class, Work.job_FIELDNAME, job) == 0)
						&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, job) == 0)) {
					throw new ExceptionEntityNotExist(job, "job");
				}
				emc.beginTransaction(Record.class);
				Record record = Wi.copier.copy(wi);
				record.setJob(job);
				emc.persist(record);
				emc.commit();
				Wo wo = new Wo();
				wo.setId(record.getId());
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wi extends Record {

		private static final long serialVersionUID = 4179509440650818001L;

		static WrapCopier<Wi, Record> copier = WrapCopierFactory.wi(Wi.class, Record.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {

	}

}