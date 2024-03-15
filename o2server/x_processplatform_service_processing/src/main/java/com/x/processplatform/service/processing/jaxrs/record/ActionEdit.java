package com.x.processplatform.service.processing.jaxrs.record;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id, jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			param.wi = this.convertToWrapIn(jsonElement, Wi.class);
			param.rec = emc.find(id, Record.class);
			if (null == param.rec) {
				throw new ExceptionEntityNotExist(id, Record.class);
			}
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, param.rec.getJob()) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, param.rec.getJob()) == 0)) {
				throw new ExceptionWorkOrWorkCompletedNotExist(param.rec.getJob());
			}
			param.job = param.rec.getJob();
		}
		return param;
	}

	public static class Param {
		String job;
		Wi wi;
		Record rec;
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Record rec = emc.find(param.rec.getId(), Record.class);
				Wi.copier.copy(param.wi, rec);
				emc.beginTransaction(Record.class);
				emc.check(rec, CheckPersistType.all);
				emc.commit();
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setId(rec.getId());
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wi extends Record {

		private static final long serialVersionUID = 4179509440650818001L;

		static WrapCopier<Wi, Record> copier = WrapCopierFactory.wi(Wi.class, Record.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -6313207070375921023L;

	}

}