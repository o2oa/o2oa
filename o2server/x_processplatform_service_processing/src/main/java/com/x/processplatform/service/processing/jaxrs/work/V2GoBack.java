package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkProperties.GoBackStore;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualProperties;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2GoBackWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class V2GoBack extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2GoBack.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		final String job;
		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			job = work.getJob();
		}

		Callable<ActionResult<Wo>> callable = new CallableImpl(id, wi);

		return ProcessPlatformKeyClassifyExecutorFactory.get(job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wi extends V2GoBackWi {

		private static final long serialVersionUID = 4131889338839380226L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 6797942626499506636L;
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;
		private Wi wi;

		private CallableImpl(String id, Wi wi) {
			this.id = id;
			this.wi = wi;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Work work;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				work = emc.find(id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}
				Manual manual = (Manual) business.element().get(wi.getActivity(), ActivityType.manual);

				if (!StringUtils.equals(work.getProcess(), manual.getProcess())) {
					throw new ExceptionProcessNotMatch();
				}
				emc.beginTransaction(Work.class);
				emc.beginTransaction(Task.class);
				emc.beginTransaction(Read.class);
				emc.beginTransaction(WorkLog.class);
				// 重新设置表单
				String formId = business.element().lookupSuitableForm(work.getProcess(), manual.getId());
				if (StringUtils.isNotBlank(formId)) {
					work.setForm(formId);
				}
				// 调度强制把这个标志设置为true,这样可以避免在拟稿状态就调度,系统认为是拟稿状态,默认不创建待办.
				work.setWorkThroughManual(true);
				work.setDestinationActivity(manual.getId());
				work.setDestinationActivityType(manual.getActivityType());
				work.setDestinationRoute("");
				work.setDestinationRouteName("");
				work.setGoBackActivityToken(wi.getActivityToken());
				if (StringUtils.equalsIgnoreCase(wi.getWay(), ManualProperties.GoBackConfig.WAY_JUMP)) {
					// way = jump
					GoBackStore goBackStore = new GoBackStore();
					goBackStore.setTickets(work.getTickets());
					goBackStore.setActivity(work.getActivity());
					goBackStore.setActivityType(work.getActivityType());
					goBackStore.setActivityToken(work.getActivityToken());
					work.setGoBackStore(goBackStore);
				}
				if (ListTools.isNotEmpty(wi.getIdentityList())) {
					work.setTickets(manual.identitiesToTickets(wi.getIdentityList()));
				}
				removeTask(business, work);
				emc.check(work, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}

		private void removeTask(Business business, Work work) throws Exception {
			// 删除可能的待办
			List<Task> os = business.entityManagerContainer().listEqual(Task.class, Task.work_FIELDNAME, work.getId());
			os.stream().forEach(o -> {
				try {
					business.entityManagerContainer().remove(o, CheckRemoveType.all);
					MessageFactory.task_delete(o);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

}