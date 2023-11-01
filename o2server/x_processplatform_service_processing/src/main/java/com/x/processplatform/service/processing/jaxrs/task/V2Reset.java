package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.ticket.Ticket;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

/**
 * @since 8.2 tickets 重置处理人.
 */
class V2Reset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id, jsonElement);

		CallableImpl callable = new CallableImpl(id, param.getDistinguishedNameList());

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.getJob()).submit(callable).get(300,
				TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.setDistinguishedNameList(wi.getDistinguishedNameList());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.find(id, Task.class);
			Objects.requireNonNull(task);
			param.setJob(task.getJob());
		}
		return param;
	}

	public static class Param {

		private String job;

		private List<String> distinguishedNameList;

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private CallableImpl(String id, List<String> distinguishedNameList) {
			this.id = id;
			this.distinguishedNameList = distinguishedNameList;
		}

		private String id;
		private List<String> distinguishedNameList;

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.beginTransaction(Work.class);
				Business business = new Business(emc);
				Task task = emc.find(id, Task.class);
				if (null == task) {
					throw new ExceptionEntityExist(id, Task.class);
				}
				Work work = emc.find(task.getWork(), Work.class);
				if (null == work) {
					throw new ExceptionEntityExist(id, Work.class);
				}
				Manual manual = (Manual) business.element().get(work.getActivity(), ActivityType.manual);
				if (null == manual) {
					throw new ExceptionEntityExist(id, Manual.class);
				}
				Tickets tickets = work.getTickets();
				Optional<Ticket> opt = tickets.findTicketWithLabel(task.getLabel());
				if (opt.isPresent()) {
					tickets.reset(opt.get(), distinguishedNameList);
					wo.setValue(true);
				}
				work.setTickets(tickets);
				emc.commit();
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends V2ResetWi {

		private static final long serialVersionUID = -3609474511834394555L;

	}

	public static class Wo extends V2ResetWo {

		private static final long serialVersionUID = -3609474511834394555L;

	}

}