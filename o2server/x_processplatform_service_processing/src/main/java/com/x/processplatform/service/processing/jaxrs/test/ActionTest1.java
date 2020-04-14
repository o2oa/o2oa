package com.x.processplatform.service.processing.jaxrs.test;

import java.util.concurrent.Callable;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.service.processing.ThisApplication;

class ActionTest1 extends BaseAction {

	private String unique = "111";

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CallableAction action = new CallableAction();
		ProcessPlatformExecutorFactory.get(unique).submit(action).get();
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

	public class CallableAction implements Callable<String> {
		public String call() throws Exception {
			ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("test", "test1"), unique);
			return "";
		}
	};

}