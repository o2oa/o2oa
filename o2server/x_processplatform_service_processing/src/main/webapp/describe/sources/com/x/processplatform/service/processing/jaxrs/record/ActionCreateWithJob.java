package com.x.processplatform.service.processing.jaxrs.record;

import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Record;

class ActionCreateWithJob extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreateWithJob.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
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
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get();

	}

	public static class Wi extends Record {

		private static final long serialVersionUID = 4179509440650818001L;

		static WrapCopier<Wi, Record> copier = WrapCopierFactory.wi(Wi.class, Record.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {

	}

}