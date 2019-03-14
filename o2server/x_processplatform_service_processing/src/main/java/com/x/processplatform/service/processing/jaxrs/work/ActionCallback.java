package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;

class ActionCallback extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			WorkLog workLog = emc.find(wi.getWorkLogId(), WorkLog.class);

			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLogId(), WorkLog.class);
			}

			if (BooleanUtils.isTrue(workLog.getSplitting())) {
				throw new ExceptionSplittingCannotCallback(work.getId(), workLog.getId());
			}

			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob());

			WorkLogTree workLogTree = new WorkLogTree(workLogs);

			Node node = workLogTree.find(workLog);

			Nodes nodes = workLogTree.rootTo(node);

			emc.beginTransaction(Work.class);
			emc.beginTransaction(WorkLog.class);
			emc.beginTransaction(Task.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);

			work.setActivityName(workLog.getFromActivityName());
			work.setActivity(workLog.getFromActivity());
			work.setActivityAlias(workLog.getFromActivityAlias());
			work.setActivityArrivedTime(workLog.getFromTime());
			work.setActivityDescription("");
			work.setActivityToken(workLog.getFromActivityToken());
			work.setActivityType(workLog.getFromActivityType());

			workLog.setConnected(false);
			workLog.setArrivedActivity("");
			workLog.setArrivedActivityAlias("");
			workLog.setArrivedActivityName("");
			workLog.setArrivedActivityToken("");
			workLog.setArrivedActivityType(null);
			workLog.setArrivedTime(null);
			workLog.setDuration(0L);

			List<WorkLog> removeWorkLogs = new ArrayList<>();

			for (WorkLog o : workLogs) {
				if (!nodes.containsWorkLog(o)) {
					removeWorkLogs.add(o);
				}
			}

			List<String> removeActivityToken = ListTools.extractProperty(removeWorkLogs,
					WorkLog.fromActivityToken_FIELDNAME, String.class, true, true);

			for (Task o : emc.listEqual(Task.class, Task.work_FIELDNAME, work.getId())) {
				emc.remove(o);
			}

			for (TaskCompleted o : emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob())) {
				if (removeActivityToken.contains(o.getActivityToken())) {
					emc.remove(o);
				}
			}

			for (ReadCompleted o : emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob())) {
				if (removeActivityToken.contains(o.getActivityToken())) {
					emc.remove(o);
				}
			}

			for (Read o : emc.listEqual(Read.class, Read.job_FIELDNAME, work.getJob())) {
				if (removeActivityToken.contains(o.getActivityToken())) {
					emc.remove(o);
				}
			}

			for (WorkLog o : removeWorkLogs) {
				emc.remove(o);
			}

			emc.commit();

			Processing processing = new Processing(wi);
			processing.processing(work.getId());
			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends ProcessingAttributes {

		private String workLogId;

		public String getWorkLogId() {
			return workLogId;
		}

		public void setWorkLogId(String workLogId) {
			this.workLogId = workLogId;
		}

	}

	public static class Wo extends WoId {
	}

}