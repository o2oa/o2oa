package com.x.processplatform.service.processing.jaxrs.job;

import java.util.concurrent.ThreadPoolExecutor;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.express.service.processing.jaxrs.job.ActionExecutorStatusWo;

public class ActionExecutorStatus extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job) throws Exception {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) ProcessPlatformExecutorFactory.get(job);
		Integer size = executor.getQueue().size();
		Wo wo = new Wo();
		wo.setBusy(size >= Config.processPlatform().getExecutorQueueBusyThreshold());
		wo.setSize(size);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	public static class Wo extends ActionExecutorStatusWo {

		private static final long serialVersionUID = 1L;

	}
}
