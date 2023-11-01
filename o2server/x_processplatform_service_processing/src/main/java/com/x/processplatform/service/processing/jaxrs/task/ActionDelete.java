package com.x.processplatform.service.processing.jaxrs.task;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionDeleteWo;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = init(id);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id) throws Exception {

		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME, Task.work_FIELDNAME));

			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}

			Work work = emc.fetch(task.getWork(), Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
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
				Task task = emc.find(param.id, Task.class);
				Work work = emc.find(task.getWork(), Work.class);
				emc.beginTransaction(Task.class);
				emc.beginTransaction(Work.class);
				emc.remove(task, CheckRemoveType.all);
				Tickets tickets = work.getTickets();
				tickets.disableDistinguishedName(task.getDistinguishedName());
				work.setTickets(tickets);
				emc.commit();
				MessageFactory.task_delete(task);
				wo.setId(task.getId());
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wo extends ActionDeleteWo {

		private static final long serialVersionUID = -6545480426707636819L;

	}

}