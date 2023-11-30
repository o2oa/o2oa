package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.processplatform.core.entity.ticket.Ticket;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Handover;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionReplaceWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionReplaceWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionReplace extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReplace.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isBlank(wi.getTargetIdentity())) {
			throw new ExceptionFieldEmpty(Handover.targetIdentity_FIELDNAME);
		}
		String executorSeed = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String person = business.organization().person().getWithIdentity(wi.getTargetIdentity());
			if (StringUtils.isBlank(person)) {
				throw new ExceptionPersonNotExist(wi.getTargetIdentity());
			}
			wi.setTargetPerson(person);
			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME, Task.person_FIELDNAME));

			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			if (!task.getPerson().equals(wi.getPerson())) {
				throw new ExceptionAccessDenied(wi.getPerson());
			}

			executorSeed = task.getJob();
		}

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(new CallableImpl(id, wi)).get(300,
				TimeUnit.SECONDS);
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
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Wo wo = new Wo();
				wo.setValue(true);
				Task task = emc.find(id, Task.class);
				if (!task.getIdentity().equals(wi.getTargetIdentity())) {
					Work work = emc.find(task.getWork(), Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(task.getWork(), Work.class);
					}
					emc.beginTransaction(Work.class);
					emc.beginTransaction(Task.class);
					if(StringUtils.isBlank(task.getLabel())) {
						String taskJson = XGsonBuilder.instance().toJson(work.getManualTaskIdentityMatrix());
						if (StringUtils.isNotBlank(taskJson) && taskJson.indexOf(task.getIdentity()) > -1) {
							taskJson = taskJson.replace(task.getIdentity(), wi.getTargetIdentity());
							work.setManualTaskIdentityMatrix(
									XGsonBuilder.instance().fromJson(taskJson, ManualTaskIdentityMatrix.class));
						}
						task.setPerson(wi.getTargetPerson());
						task.setIdentity(wi.getTargetIdentity());
					}else{
						Long count = emc.countEqualAndEqual(Task.class, Task.identity_FIELDNAME, wi.getTargetIdentity(), Task.work_FIELDNAME, task.getWork());
						if(count > 0) {
							work.getTickets().disableDistinguishedName(wi.getTargetIdentity());
							emc.remove(task);
						}else{
							work.getTickets().reset(task.getLabel(), List.of(wi.getTargetIdentity()));
							task.setPerson(wi.getTargetPerson());
							task.setIdentity(wi.getTargetIdentity());
						}
					}
					if (work.getCreatorPerson().equals(wi.getPerson())) {
						work.setCreatorPerson(wi.getTargetPerson());
						work.setCreatorIdentity(wi.getTargetIdentity());
					}
					emc.commit();
				}

				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wi extends ActionReplaceWi {

		private static final long serialVersionUID = -6215838156429443320L;

		static WrapCopier<Wi, Handover> copier = WrapCopierFactory.wi(Wi.class, Handover.class,
				ListTools.toList(Handover.person_FIELDNAME, Handover.targetIdentity_FIELDNAME), null);

	}

	public static class Wo extends ActionReplaceWo {

		private static final long serialVersionUID = -8577678018996847686L;
	}

}
