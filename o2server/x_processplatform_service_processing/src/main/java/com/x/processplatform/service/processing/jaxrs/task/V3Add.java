package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
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
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V3AddWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V3AddWo;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

/**
 * @since 8.2 tickets 加签
 */
class V3Add extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Add.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = init(id, jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.task.getJob()).submit(callable).get(300,
				TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Param param = new Param();
		param.distinguishedNameList = wi.getDistinguishedNameList();
		param.before = wi.getBefore();
		param.mode = wi.getMode();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class,
					ListTools.toList(Task.job_FIELDNAME, Task.work_FIELDNAME, Task.LABEL_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.task = task;
		}
		return param;
	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		public CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Work work = emc.find(param.task.getWork(), Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(param.task.getWork(), Work.class);
				}
				emc.beginTransaction(Work.class);
				Tickets tickets = work.getTickets();
				wo.setValue(tickets.add(param.task.getLabel(), param.distinguishedNameList, param.before, param.mode));
				work.setTickets(tickets);
				emc.commit();
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Param {

		private Task task;
		private List<String> distinguishedNameList;
		private boolean before;
		private String mode;

	}

	public static class Wi extends V3AddWi {

		private static final long serialVersionUID = -3293122515327864483L;

	}

	public static class Wo extends V3AddWo {

		private static final long serialVersionUID = 6457473592503074552L;

	}

}
