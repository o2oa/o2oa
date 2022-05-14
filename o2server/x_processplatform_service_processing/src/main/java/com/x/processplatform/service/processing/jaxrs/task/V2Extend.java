package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ExtendWi;

class V2Extend extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Extend.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		}

		Task task = getTask(wi.getTask());

		if (null == task) {
			throw new ExceptionEntityNotExist(wi.getTask(), Task.class);
		}

		if (null == this.getWork(task.getWork())) {
			throw new ExceptionEntityNotExist(task.getWork(), Work.class);
		}

		return ProcessPlatformExecutorFactory.get(task.getJob())
				.submit(new CallableImpl(task.getWork(), task.getIdentity(), wi.getReplace(), wi.getIdentityList()))
				.get(300, TimeUnit.SECONDS);

	}

	private Task getTask(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.fetch(id, Task.class,
					ListTools.toList(Task.job_FIELDNAME, Task.identity_FIELDNAME, Task.work_FIELDNAME));
		}
	}

	private Work getWork(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.fetch(id, Work.class, ListTools.toList());
		}
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		private String identity;

		private Boolean replace;

		private List<String> identities;

		CallableImpl(String id, String identity, Boolean replace, List<String> identities) {
			this.id = id;
			this.identity = identity;
			this.replace = replace;
			this.identities = identities;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.beginTransaction(Work.class);
				Work work = emc.find(this.id, Work.class);
				ManualTaskIdentityMatrix matrix = work.getManualTaskIdentityMatrix();
				matrix.extend(identity, replace, identities);
				work.setManualTaskIdentityMatrix(matrix);
				emc.check(work, CheckPersistType.all);
				emc.commit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}

	}

	public static class Wi extends V2ExtendWi {

		private static final long serialVersionUID = 8207729800479548859L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 6457473592503074552L;

	}

}
