package com.x.processplatform.service.processing.jaxrs.data;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.XGsonBuilder;
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

class ActionUpdateWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithJob.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String executorSeed = job;

		Callable<ActionResult<Wo>> callable = new CallableImpl(job, wi);

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wi extends DataWi {

		private static final long serialVersionUID = 7115920805344418069L;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -7831217351198031373L;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String job;
		private DataWi wi;

		private CallableImpl(String job, DataWi wi) {
			this.job = job;
			this.wi = wi;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				JsonElement source = getData(business, job);
				JsonElement merge = XGsonBuilder.merge(wi.getJsonElement(), source);
				List<Work> works = emc.listEqual(Work.class, Work.job_FIELDNAME, job);
				if (!works.isEmpty()) {
					for (Work work : works) {
						/* 先更新title和serial,再更新DataItem,因为旧的DataItem中也有title和serial数据. */
						updateTitleSerialObjectSecurityClearance(business, work, merge);
					}
					/* updateTitleSerial 和 updateData 方法内进行了提交 */
					updateData(business, works.get(0), merge);

					wi.init(works.get(0), merge);
					createDataRecord(business, wi);
				} else {
					List<WorkCompleted> workCompletedList = emc.listEqual(WorkCompleted.class, Work.job_FIELDNAME, job);
					if (!workCompletedList.isEmpty()) {
						for (WorkCompleted work : workCompletedList) {
							/* 先更新title和serial,再更新DataItem,因为旧的DataItem中也有title和serial数据. */
							updateTitleSerialObjectSecurityClearance(business, work, merge);
						}
						/* updateTitleSerial 和 updateData 方法内进行了提交 */
						updateData(business, workCompletedList.get(0), merge);

						wi.init(workCompletedList.get(0), merge);
						createDataRecord(business, wi);
					}
				}
				wo.setId(job);
			}
			result.setData(wo);
			return result;
		}
	}
}
