package com.x.processplatform.service.processing.jaxrs.test;

import java.util.concurrent.Callable;

import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionTest extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job) throws Exception {
		Callable<ActionResult<Wo>> callable = () -> {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			result.setData(wo);
			System.err.println("!!!!!!!!!!!!!!!!!!!start sleep " + ProcessPlatformExecutorFactory.get(job));
			Thread.sleep(30000);
			System.err.println("!!!!!!!!!!!!!!!!!!!start completed " + ProcessPlatformExecutorFactory.get(job));
			return result;
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get();
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 1L;

	}

}