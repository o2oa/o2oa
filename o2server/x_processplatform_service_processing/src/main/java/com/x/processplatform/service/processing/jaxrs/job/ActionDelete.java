package com.x.processplatform.service.processing.jaxrs.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;

public class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		Callable<ActionResult<List<Wo>>> callable = new Callable<ActionResult<List<Wo>>>() {
			public ActionResult<List<Wo>> call() throws Exception {
				List<Wo> wos = new ArrayList<>();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					deleteTask(business, job);
					deleteTaskCompleted(business, job);
					deleteRead(business, job);
					deleteReadCompleted(business, job);
					deleteReview(business, job);
					deleteAttachment(business, job);
					deleteWorkLog(business, job);
					deleteItem(business, job);
					deleteDocumentVersion(business, job);
					deleteRecord(business, job);
					deleteSignScrawl(business, job);
					deleteSign(business, job);
					emc.beginTransaction(Work.class);
					for (Work o : emc.listEqual(Work.class, Work.job_FIELDNAME, job)) {
						emc.remove(o);
						MessageFactory.work_delete(o);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					emc.beginTransaction(WorkCompleted.class);
					for (WorkCompleted o : emc.listEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, job)) {
						emc.remove(o);
						MessageFactory.workCompleted_delete(o);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					emc.commit();
				}
				ActionResult<List<Wo>> result = new ActionResult<>();
				result.setData(wos);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2910714914090486687L;

	}
}
