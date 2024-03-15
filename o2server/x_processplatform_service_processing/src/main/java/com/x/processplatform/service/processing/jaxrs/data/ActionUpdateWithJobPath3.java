package com.x.processplatform.service.processing.jaxrs.data;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

@Deprecated
class ActionUpdateWithJobPath3 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithJobPath3.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, String path0, String path1, String path2,
			String path3, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, job:{}, path0:{}, path1:{}, path2:{}, path3:{}.",
				effectivePerson::getDistinguishedName, () -> job, () -> path0, () -> path1, () -> path2, () -> path3);
		ActionResult<Wo> result = new ActionResult<>();

		String executorSeed = job;

		Callable<Wo> callable = callable(job, path0, path1, path2, path3, jsonElement);

		Wo wo = ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	private Callable<Wo> callable(String job, String path0, String path1, String path2, String path3,
			JsonElement jsonElement) {
		return () -> {
			Wo wo = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<Work> works = emc.listEqual(Work.class, Work.job_FIELDNAME, job);
				if (!works.isEmpty()) {
					updateData(business, works.get(0), jsonElement, path0, path1, path2, path3);
				}else{
					List<WorkCompleted> workCompletedList = emc.listEqual(WorkCompleted.class, Work.job_FIELDNAME, job);
					if(!workCompletedList.isEmpty()) {
						updateData(business, workCompletedList.get(0), jsonElement, path0, path1, path2, path3);
					}
				}
				wo.setId(job);
			}
			return wo;
		};
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -7831217351198031373L;

	}
}
