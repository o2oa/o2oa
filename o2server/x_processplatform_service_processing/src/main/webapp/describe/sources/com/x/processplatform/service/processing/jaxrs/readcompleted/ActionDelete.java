package com.x.processplatform.service.processing.jaxrs.readcompleted;

import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.MessageFactory;

class ActionDelete extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ReadCompleted readCompleted = emc.fetch(id, ReadCompleted.class,
					ListTools.toList(ReadCompleted.job_FIELDNAME));
			if (null == readCompleted) {
				throw new ExceptionEntityNotExist(id, ReadCompleted.class);
			}
			executorSeed = readCompleted.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					ReadCompleted readCompleted = emc.find(id, ReadCompleted.class);
					if (null == readCompleted) {
						throw new ExceptionEntityNotExist(id, ReadCompleted.class);
					}
					emc.beginTransaction(ReadCompleted.class);
					emc.remove(readCompleted, CheckRemoveType.all);
					emc.commit();
					MessageFactory.readCompleted_delete(readCompleted);
					wo.setId(readCompleted.getId());
				}
				return "";
			}
		};

		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {
	}

}