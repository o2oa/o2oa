package com.x.processplatform.service.processing.jaxrs.data;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

@Deprecated
class ActionCreateWithWorkPath0 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWorkPath0.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, id:{}, path0:{}.", effectivePerson::getDistinguishedName, () -> id, () -> path0);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}

		CallableImpl impl = new CallableImpl(id, path0, jsonElement);
		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(impl).get(300, TimeUnit.SECONDS);
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private JsonElement jsonElement;
		private String id;
		private String path0;

		private CallableImpl(String id, String path0, JsonElement jsonElement) {
			this.id = id;
			this.path0 = path0;
			this.jsonElement = jsonElement;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}
				createData(business, work, jsonElement, path0);
				Wo wo = new Wo();
				wo.setId(work.getId());
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 5105871346329462375L;

	}

}
