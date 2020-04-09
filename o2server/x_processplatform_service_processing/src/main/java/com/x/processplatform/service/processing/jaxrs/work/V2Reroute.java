package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
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
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RerouteWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.jaxrs.work.V2Retract.Wo;

class V2Reroute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Reroute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		final String job;
		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			job = work.getJob();
		}
		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				ActionResult<Wo> result = new ActionResult<>();
				Work work;
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					work = emc.find(id, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(id, Work.class);
					}
					Activity activity = business.element().getActivity(wi.getActivity());
					if (!StringUtils.equals(work.getProcess(), activity.getProcess())) {
						throw new ExceptionProcessNotMatch();
					}
					emc.beginTransaction(Work.class);
					emc.beginTransaction(Task.class);
					emc.beginTransaction(WorkLog.class);
					// work.setForceRoute(true);
//					work.setSplitting(false);
//					work.setSplitToken("");
//					work.getSplitTokenList().clear();
//					work.setSplitValue("");
					work.setDestinationActivity(activity.getId());
					work.setDestinationActivityType(activity.getActivityType());
					work.setDestinationRoute("");
					work.setDestinationRouteName("");
					work.getProperties().setManualForceTaskIdentityList(new ArrayList<String>());
					if (ListTools.isNotEmpty(wi.getManualForceTaskIdentityList())) {
						work.getProperties().setManualForceTaskIdentityList(wi.getManualForceTaskIdentityList());
					}
					removeTask(business, work);
					if (wi.getMergeWork()) {
						/* 合并工作 */
						work.setSplitting(false);
						work.setSplitToken("");
						work.getSplitTokenList().clear();
						work.setSplitValue("");
						removeOtherWork(business, work);
						removeOtherWorkLog(business, work);
					}
					emc.check(work, CheckPersistType.all);
					emc.commit();
				}

				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
				return result;
			}
		};
		return ProcessPlatformExecutorFactory.get(job).submit(callable).get();

	}

	public static class Wi extends V2RerouteWi {

	}

	public static class Wo extends WrapBoolean {
	}

	private void removeTask(Business business, Work work) throws Exception {
		/* 删除可能的待办 */
		List<Task> os = business.entityManagerContainer().listEqual(Task.class, Task.activityToken_FIELDNAME,
				work.getActivityToken());
		os.stream().forEach(o -> {
			try {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.task_delete(o);
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private void removeOtherWork(Business business, Work work) throws Exception {
		List<Work> os = business.entityManagerContainer().listEqualAndNotEqual(Work.class, Work.job_FIELDNAME,
				work.getJob(), Work.id_FIELDNAME, work.getId());
		os.stream().forEach(o -> {
			try {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.work_delete(o);
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private void removeOtherWorkLog(Business business, Work work) throws Exception {
		List<WorkLog> os = business.entityManagerContainer().listEqualAndEqualAndNotEqual(WorkLog.class,
				WorkLog.job_FIELDNAME, work.getJob(), WorkLog.connected_FIELDNAME, false,
				WorkLog.fromActivity_FIELDNAME, work.getActivity());
		os.stream().forEach(o -> {
			try {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

}