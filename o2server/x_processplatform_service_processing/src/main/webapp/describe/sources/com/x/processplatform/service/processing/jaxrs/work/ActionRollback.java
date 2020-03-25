package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;

class ActionRollback extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();

		String workId = null;
		String job = null;
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
			workId = work.getId();
			job = work.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Work work = emc.find(id, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(id, Work.class);
					}
					Application application = business.element().get(work.getApplication(), Application.class);
					if (null == application) {
						throw new ExceptionEntityNotExist(work.getApplication(), Application.class);
					}
					Process process = business.element().get(work.getProcess(), Process.class);
					if (null == process) {
						throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
					}
					WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);
					if (null == workLog) {
						throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
					}
					if (BooleanUtils.isTrue(workLog.getSplitting())) {
						throw new ExceptionSplittingNotRollback(work.getId(), workLog.getId());
					}
					Activity activity = business.element().getActivity(workLog.getFromActivity());
					if (null == activity) {
						throw new ExceptionActivityNotExist(workLog.getFromActivity());
					}

					List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, workLog.getJob());

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

					rollbackWork(business, work, workLog, activity);

					disconnectWorkLog(work, workLog);

					rollbackTask(business, emc.listEqual(Task.class, Task.job_FIELDNAME, work.getJob()));

					rollbackTaskCompleted(business, work, nodes, workLog,
							emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob()));

					rollbackRead(business, work, nodes, workLog,
							emc.listEqual(Read.class, Read.job_FIELDNAME, work.getJob()));

					rollbackReadCompleted(business, work, nodes, workLog,
							emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob()));

					rollbackReview(business, nodes, emc.listEqual(Review.class, Review.job_FIELDNAME, work.getJob()));

					rollbackWorkLog(business, work, nodes, workLogs);

					emc.commit();
					wo.setId(work.getId());
					ActionResult<Wo> result = new ActionResult<>();
					result.setData(wo);
					return result;
				}
			}
		};

		ActionResult<Wo> result = ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", workId, "processing"), null, job);

		return result;

	}

	private void rollbackWork(Business business, Work work, WorkLog workLog, Activity activity) throws Exception {
		work.setSplitting(false);
		work.setActivityName(workLog.getFromActivityName());
		work.setActivity(workLog.getFromActivity());
		work.setActivityAlias(workLog.getFromActivityAlias());
		work.setActivityArrivedTime(workLog.getFromTime());
		work.setActivityDescription("");
		work.setActivityToken(workLog.getFromActivityToken());
		work.setActivityType(workLog.getFromActivityType());
		/* 清除掉当前的待办人准备重新生成 */
		work.getManualTaskIdentityList().clear();
		String formId = PropertyTools.getOrElse(activity, Manual.form_FIELDNAME, String.class, "");
		if (StringUtils.isNotEmpty(formId)) {
			/* 默认流程导入的时候表单字段里面填写了不存在的值,所以这里要进行校验是否存在 */
			Form form = business.element().get(formId, Form.class);
			if (null != form) {
				work.setForm(formId);
			}
		}
//		work.setErrorRetry(0);
		work.setWorkStatus(WorkStatus.processing);
	}

	private void disconnectWorkLog(Work work, WorkLog workLog) {
		workLog.setConnected(false);
		workLog.setArrivedActivity("");
		workLog.setArrivedActivityAlias("");
		workLog.setArrivedActivityName("");
		workLog.setArrivedActivityToken("");
		workLog.setArrivedActivityType(null);
		workLog.setArrivedTime(null);
		workLog.setDuration(0L);
		workLog.setWorkCompleted("");
		workLog.setWork(work.getId());
	}

	private void rollbackTask(Business business, List<Task> list) throws Exception {
		for (Task o : list) {
			business.entityManagerContainer().remove(o);
			MessageFactory.task_delete(o);
		}
	}

	private void rollbackTaskCompleted(Business business, Work work, Nodes nodes, WorkLog workLog,
			List<TaskCompleted> list) throws Exception {
		for (TaskCompleted o : list) {
			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
				business.entityManagerContainer().remove(o);
				MessageFactory.taskCompleted_delete(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	private void rollbackRead(Business business, Work work, Nodes nodes, WorkLog workLog, List<Read> list)
			throws Exception {
		for (Read o : list) {
			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
				business.entityManagerContainer().remove(o);
				MessageFactory.read_delete(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	private void rollbackReadCompleted(Business business, Work work, Nodes nodes, WorkLog workLog,
			List<ReadCompleted> list) throws Exception {
		for (ReadCompleted o : list) {
			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
				business.entityManagerContainer().remove(o);
				MessageFactory.readCompleted_delete(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	private void rollbackReview(Business business, Nodes nodes, List<Review> list) throws Exception {
		Date date = nodes.latestArrivedTime();
		if (null != date) {
			for (Review o : list) {
				if (null != o.getStartTime() && o.getStartTime().after(date)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.review_delete(o);
				} else {
					o.setCompleted(false);
					o.setCompletedTime(null);
					o.setCompletedTimeMonth("");
					o.setWorkCompleted("");
				}
			}
		}
	}

	private void rollbackWorkLog(Business business, Work work, Nodes nodes, List<WorkLog> list) throws Exception {
		for (WorkLog o : list) {
			if (!nodes.containsWorkLog(o)) {
				business.entityManagerContainer().remove(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
			}
		}
	}

	public static class Wi extends ProcessingAttributes {

		@FieldDescribe("工作日志标识")
		private String workLog;

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

	}

	public static class Wo extends WoId {
	}
}