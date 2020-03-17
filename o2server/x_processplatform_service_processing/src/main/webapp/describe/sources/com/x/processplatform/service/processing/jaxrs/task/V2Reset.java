package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.ListUtils;

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
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;
import com.x.processplatform.service.processing.Business;

class V2Reset extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Reset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		final String job;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			job = task.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
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
					/* 检查reset人员 */
					List<String> identites = ListTools
							.trim(business.organization().identity().list(wi.getIdentityList()), true, true);

					if (identites.isEmpty()) {
						throw new ExceptionResetEmpty();
					}

					emc.beginTransaction(Work.class);
					List<String> os = ListTools.trim(work.getManualTaskIdentityList(), true, true);

					os = ListUtils.sum(os, identites);
					/* 在新增待办人员中删除当前的处理人 */
					if (!wi.getKeep()) {
						os = ListUtils.subtract(os, ListTools.toList(task.getIdentity()));
					}

					if (ListTools.isEmpty(os)) {
						throw new ExceptionResetEmpty();
					}

					work.setManualTaskIdentityList(ListTools.trim(os, true, true));
					emc.check(work, CheckPersistType.all);
					emc.commit();

				}
				Wo wo = new Wo();
				wo.setValue(true);
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get();

	}

	public static class Wi extends V2ResetWi {

	}

	public static class Wo extends WrapBoolean {

	}

}