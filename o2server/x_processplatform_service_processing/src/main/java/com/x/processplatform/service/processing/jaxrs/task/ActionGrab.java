package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualMode;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionGrabWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionGrab extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGrab.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);
	}

	private Param init(String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.job = task.getJob();
			param.id = task.getId();
		}
		return param;
	}

	private class Param {

		private String job;
		private String id;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Task task = emc.find(param.id, Task.class);
				Work work = emc.find(task.getWork(), Work.class);
				if (!Objects.equals(work.getActivityType(), ActivityType.manual)) {
					throw new ExceptionWorkNotAtManual(work.getId());
				}
				Manual manual = (Manual) business.element().get(work.getActivity(), ActivityType.manual);
				if (!Objects.equals(manual.getManualMode(), ManualMode.grab)) {
					throw new ExceptionWorkNotGrab(work.getId());
				}
				emc.beginTransaction(Task.class);
				emc.beginTransaction(Work.class);
				Tickets tickets = work.getTickets();
				for (Task o : listTask(business, work)) {
					if (o != task) {
						emc.remove(o);
						tickets.disableDistinguishedName(task.getDistinguishedName());
						MessageFactory.task_delete(o);
					}
				}
				work.setTickets(tickets);
				emc.commit();
				wo.setId(task.getId());
				result.setData(wo);
			}
			return result;
		}

		private List<Task> listTask(Business business, Work work) throws Exception {
			return business.entityManagerContainer().listEqualAndEqual(Task.class, Task.activityToken_FIELDNAME,
					work.getActivityToken(), Task.work_FIELDNAME, work.getId());
		}
	}

	public static class Wo extends ActionGrabWo {

		private static final long serialVersionUID = -4131982975664508585L;
	}

}