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
import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUpdateWithJobPath extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithJobPath.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, String path, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, job:{}, path:{}.", effectivePerson::getDistinguishedName, () -> job, () -> path);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String executorSeed = job;

		Callable<Wo> callable = callable(job, path, wi);

		Wo wo = ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	private Callable<Wo> callable(String job, String path, Wi wi) {
		return () -> {
			Wo wo = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<Work> works = emc.listEqual(Work.class, Work.job_FIELDNAME, job);
				String[] paths = path.split(PATH_SPLIT);
				if (!works.isEmpty()) {
					updateData(business, works.get(0), wi.getJsonElement(), paths);

					wi.init(works.get(0));
					wi.setJsonElement(getDataWithPath(business, wi.getJob(), paths[0]));
					createDataRecord(business, wi);
				}else{
					List<WorkCompleted> workCompletedList = emc.listEqual(WorkCompleted.class, Work.job_FIELDNAME, job);
					if(!workCompletedList.isEmpty()) {
						updateData(business, workCompletedList.get(0), wi.getJsonElement(), paths);

						wi.init(workCompletedList.get(0));
						wi.setJsonElement(getDataWithPath(business, wi.getJob(), paths[0]));
						createDataRecord(business, wi);
					}
				}
				wo.setId(job);
			}
			return wo;
		};
	}

	public static class Wi extends DataWi {

		private static final long serialVersionUID = 4990146701198052422L;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -7831217351198031373L;

	}

}
