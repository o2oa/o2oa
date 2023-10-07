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

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		Param param = init(id);

		CallableImpl callable = new CallableImpl(param.getWork(), param.getLabel(), wi.getDistinguishedNameList(),
				wi.getBefore(), wi.getMode());

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.getJob()).submit(callable).get(300,
				TimeUnit.SECONDS);

	}

	private Param init(String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class,
					ListTools.toList(Task.job_FIELDNAME, Task.work_FIELDNAME, Task.LABEL_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.setJob(task.getJob());
			param.setWork(task.getWork());
			param.setLabel(task.getLabel());
		}
		return param;
	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private String workId;
		private String label;
		private List<String> distinguishedNameList;
		private boolean before;
		private String mode;

		public CallableImpl(String workId, String label, List<String> distinguishedNameList, boolean before,
				String mode) {
			this.workId = workId;
			this.label = label;
			this.distinguishedNameList = distinguishedNameList;
			this.before = before;
			this.mode = mode;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Work work = emc.find(workId, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(workId, Work.class);
				}
				Tickets tickets = work.getTickets();
				wo.setValue(tickets.add(label, distinguishedNameList, before, mode));
			}
			result.setData(wo);
			return result;
		}

	}

	public static class Param {

		private String job;
		private String work;
		private String label;

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}

	public static class Wi extends V3AddWi {

		private static final long serialVersionUID = -3293122515327864483L;

	}

	public static class Wo extends V3AddWo {

		private static final long serialVersionUID = 6457473592503074552L;

	}

}
