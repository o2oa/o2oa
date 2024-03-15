package com.x.processplatform.service.processing.jaxrs.readcompleted;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ReadCompleted readCompleted = emc.fetch(id, ReadCompleted.class,
					ListTools.toList(ReadCompleted.job_FIELDNAME));
			if (null == readCompleted) {
				throw new ExceptionEntityNotExist(id, ReadCompleted.class);
			}
			executorSeed = readCompleted.getJob();
		}

		CallableImpl impl = new CallableImpl(id);

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(impl).get(300, TimeUnit.SECONDS);
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		private CallableImpl(String id) {
			this.id = id;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				ReadCompleted readCompleted = emc.find(id, ReadCompleted.class);
				if (null == readCompleted) {
					throw new ExceptionEntityNotExist(id, ReadCompleted.class);
				}
				emc.beginTransaction(ReadCompleted.class);
				emc.remove(readCompleted, CheckRemoveType.all);
				emc.commit();
				MessageFactory.readCompleted_delete(readCompleted);
				Wo wo = new Wo();
				wo.setId(readCompleted.getId());
				result.setData(wo);
			}
			return result;
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 978361088939191102L;
	}

}