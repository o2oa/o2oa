package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
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
import com.x.processplatform.service.processing.Business;

class ActionProjection extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		String job;

		Data data;

		Process process;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);

			}

			process = emc.find(work.getProcess(), Process.class);

			if (null == process) {
				throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
			}

			job = work.getJob();
			data = new WorkDataHelper(emc, work).get();
		}

		Callable<ActionResult<Wo>> callable = () -> {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Projection> projections = listProjections(process);
				if (ListTools.isNotEmpty(projections)) {
					projection(new Business(emc), job, data, projections);
					wo.setValue(true);
				}
			}
			result.setData(wo);
			return result;
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private void projection(Business business, String job, Data data, List<Projection> projections) throws Exception {
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

	public static class Wo extends WrapBoolean {
		private static final long serialVersionUID = -3206075665001702872L;
	}

}