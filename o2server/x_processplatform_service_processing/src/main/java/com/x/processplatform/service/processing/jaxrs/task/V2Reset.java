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
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;
import com.x.processplatform.service.processing.Business;

class V2Reset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		Task task = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
		}

		return ProcessPlatformExecutorFactory.get(task.getJob()).submit(new CallableImpl(task.getId(),
				wi.getAddBeforeList(), wi.getExtendList(), wi.getAddAfterList(), wi.getRemove()))
				.get(300, TimeUnit.SECONDS);

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;
		private List<String> addBeforeIdentities;
		private List<String> extendIdentities;
		private List<String> addAfterIdentities;
		private boolean remove;

		private CallableImpl(String id, List<String> addBeforeIdentities, List<String> extendIdentities,
				List<String> addAfterIdentities, boolean remove) {
			this.id = id;
			this.addBeforeIdentities = addBeforeIdentities;
			this.extendIdentities = extendIdentities;
			this.addAfterIdentities = addAfterIdentities;
			this.remove = remove;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Task task = emc.find(id, Task.class);
				if (null == task) {
					throw new ExceptionEntityNotExist(id, Task.class);
				}
				Work work = emc.find(task.getWork(), Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(task.getWork(), Work.class);
				}

				this.addBeforeIdentities = ListTools.trim(business.organization().identity().list(addBeforeIdentities),
						true, true);
				this.extendIdentities = ListTools.trim(business.organization().identity().list(extendIdentities), true,
						true);
				this.addAfterIdentities = ListTools.trim(business.organization().identity().list(addAfterIdentities),
						true, true);

				emc.beginTransaction(Work.class);
				ManualTaskIdentityMatrix matrix = work.getManualTaskIdentityMatrix();
				matrix.reset(task.getIdentity(), addBeforeIdentities, extendIdentities, addAfterIdentities, remove);
				work.setManualTaskIdentityMatrix(matrix);
				emc.check(work, CheckPersistType.all);
				emc.commit();

			}
			Wo wo = new Wo();
			wo.setValue(true);
			ActionResult<Wo> result = new ActionResult<>();
			result.setData(wo);
			return result;
		}

	}

	public static class Wi extends V2ResetWi {

		private static final long serialVersionUID = -36317314462442492L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -1577970926042381340L;

	}

}