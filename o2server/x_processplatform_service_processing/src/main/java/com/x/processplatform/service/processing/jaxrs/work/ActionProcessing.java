package com.x.processplatform.service.processing.jaxrs.work;

import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Processing;

class ActionProcessing extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		String job;

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			wi.setDebugger(effectivePerson.getDebugger());

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			job = work.getJob();

		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				Processing processing = new Processing(wi);
				processing.processing(id);
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setId(id);
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get();

	}

	public static class Wi extends ProcessingAttributes {

	}

	public static class Wo extends WoId {
	}

}