package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Projection;
import com.x.processplatform.core.entity.element.util.ProjectionFactory;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProjectionWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionProjection extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionProjection.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id) throws Exception {

		Param param = new Param();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			param.job = work.getJob();

			Process process = emc.find(work.getProcess(), Process.class);

			if (null == process) {
				throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
			}

			param.process = process;

			param.data = new WorkDataHelper(emc, work).get();
		}

		return param;

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
			wo.setValue(false);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Projection> projections = listProjections(param.process);
				if (ListTools.isNotEmpty(projections)) {
					projection(new Business(emc), param.job, param.data, projections);
					wo.setValue(true);
				}
			}
			result.setData(wo);
			return result;
		}

		private void projection(Business business, String job, Data data, List<Projection> projections)
				throws Exception {
			EntityManagerContainer emc = business.entityManagerContainer();
			emc.beginTransaction(Work.class);
			emc.beginTransaction(Task.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);
			for (Work o : emc.listEqual(Work.class, Work.job_FIELDNAME, job)) {
				ProjectionFactory.projectionWork(projections, data, o);
			}
			for (Task o : emc.listEqual(Task.class, Task.job_FIELDNAME, job)) {
				ProjectionFactory.projectionTask(projections, data, o);
			}
			for (TaskCompleted o : emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, job)) {
				ProjectionFactory.projectionTaskCompleted(projections, data, o);
			}
			for (Read o : emc.listEqual(Read.class, Read.job_FIELDNAME, job)) {
				ProjectionFactory.projectionRead(projections, data, o);
			}
			for (ReadCompleted o : emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, job)) {
				ProjectionFactory.projectionReadCompleted(projections, data, o);
			}
			for (Review o : emc.listEqual(Review.class, Review.job_FIELDNAME, job)) {
				ProjectionFactory.projectionReview(projections, data, o);
			}
			emc.commit();
		}

		private List<Projection> listProjections(Process process) {
			List<Projection> list = new ArrayList<>();
			String text = process.getProjection();
			if (XGsonBuilder.isJsonArray(text)) {
				list = XGsonBuilder.instance().fromJson(text, new TypeToken<List<Projection>>() {
				}.getType());
			}
			return list;
		}
	}

	private class Param {

		private String job;
		private Data data;
		private Process process;

	}

	public static class Wo extends ActionProjectionWo {

		private static final long serialVersionUID = -3206075665001702872L;

	}

}