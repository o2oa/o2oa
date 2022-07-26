package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;

class ActionReroute extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReroute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String activityId, JsonElement jsonElement)
			throws Exception {
		
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			job = work.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Work work = emc.find(id, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(id, Work.class);
					}
					Activity activity = business.element().getActivity(activityId);
					if (!StringUtils.equals(work.getProcess(), activity.getProcess())) {
						throw new ExceptionProcessNotMatch();
					}
					emc.beginTransaction(Work.class);
					emc.beginTransaction(Task.class);
					work.setSplitting(false);
					work.setSplitToken("");
					work.getSplitTokenList().clear();
					work.setSplitValue("");
					work.setDestinationActivity(activity.getId());
					work.setDestinationActivityType(activity.getActivityType());
					work.setDestinationRoute("");
					work.setDestinationRouteName("");
					work.getProperties().getManualForceTaskIdentityList().clear();
					work.getProperties().getManualForceTaskIdentityList().addAll(wi.getManualForceTaskIdentityList());
					emc.check(work, CheckPersistType.all);
					removeTask(business, work);
					removeOtherWork(business, work);
					removeOtherWorkLog(business, work);
					emc.commit();
					return "";
				}
			}
		};

		ProcessPlatformExecutorFactory.get(job).submit(callable).get(300, TimeUnit.SECONDS);

		wo = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", id, "processing"), new ProcessingAttributes(), job)
				.getData(Wo.class);

		result.setData(wo);
		return result;
	}

	public static class Wi extends ProcessingAttributes {

		private static final long serialVersionUID = 4974042099520226536L;

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -179926711404067030L;
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
				LOGGER.error(e);
			}
		});
	}

	private void removeOtherWork(Business business, Work work) throws Exception {
		List<Work> os = business.entityManagerContainer().listEqualAndNotEqual(Work.class, Work.job_FIELDNAME,
				work.getJob(), JpaObject.id_FIELDNAME, work.getId());
		os.stream().forEach(o -> {
			try {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.work_delete(o);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void removeOtherWorkLog(Business business, Work work) throws Exception {
		List<WorkLog> os = business.entityManagerContainer().listEqualAndEqualAndNotEqual(WorkLog.class,
				WorkLog.JOB_FIELDNAME, work.getJob(), WorkLog.CONNECTED_FIELDNAME, false,
				WorkLog.FROMACTIVITY_FIELDNAME, work.getActivity());
		os.stream().forEach(o -> {
			try {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

}