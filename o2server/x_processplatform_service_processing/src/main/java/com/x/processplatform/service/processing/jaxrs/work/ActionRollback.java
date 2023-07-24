package com.x.processplatform.service.processing.jaxrs.work;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.ExceptionDeprecatedAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionRollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		throw new ExceptionDeprecatedAction(V2Rollback.class.getName());
//		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//		Wo wo = new Wo();
//
//		String workId = null;
//		String job = null;
//		String executorSeed = null;
//
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
//			if (null == work) {
//				throw new ExceptionEntityNotExist(id, Work.class);
//			}
//			executorSeed = work.getJob();
//			workId = work.getId();
//			job = work.getJob();
//		}
//
//		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
//			public ActionResult<Wo> call() throws Exception {
//				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//					Business business = new Business(emc);
//					Work work = emc.find(id, Work.class);
//					if (null == work) {
//						throw new ExceptionEntityNotExist(id, Work.class);
//					}
//					Application application = business.element().get(work.getApplication(), Application.class);
//					if (null == application) {
//						throw new ExceptionEntityNotExist(work.getApplication(), Application.class);
//					}
//					Process process = business.element().get(work.getProcess(), Process.class);
//					if (null == process) {
//						throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
//					}
//					WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);
//					if (null == workLog) {
//						throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
//					}
//					if (BooleanUtils.isTrue(workLog.getSplitting())) {
//						throw new ExceptionSplittingNotRollback(work.getId(), workLog.getId());
//					}
//					Activity activity = business.element().getActivity(workLog.getFromActivity());
//					if (null == activity) {
//						throw new ExceptionActivityNotExist(workLog.getFromActivity());
//					}
//
//					List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, workLog.getJob());
//
//					WorkLogTree workLogTree = new WorkLogTree(workLogs);
//
//					Node node = workLogTree.find(workLog);
//
//					Nodes nodes = workLogTree.rootTo(node);
//
//					emc.beginTransaction(Work.class);
//					emc.beginTransaction(WorkLog.class);
//					emc.beginTransaction(Task.class);
//					emc.beginTransaction(TaskCompleted.class);
//					emc.beginTransaction(Read.class);
//					emc.beginTransaction(ReadCompleted.class);
//					emc.beginTransaction(Review.class);
//
//					rollbackWork(work, workLog);
//
//					rollbackForm(business, work, node, application);
//
//					disconnectWorkLog(work, workLog);
//
//					rollbackTask(business, emc.listEqual(Task.class, Task.job_FIELDNAME, work.getJob()));
//
//					rollbackTaskCompleted(business, work, nodes, workLog,
//							emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob()));
//
//					rollbackRead(business, work, nodes, workLog,
//							emc.listEqual(Read.class, Read.job_FIELDNAME, work.getJob()));
//
//					rollbackReadCompleted(business, work, nodes, workLog,
//							emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob()));
//
//					rollbackReview(business, nodes, emc.listEqual(Review.class, Review.job_FIELDNAME, work.getJob()));
//
//					rollbackRecord(business, nodes, emc.listEqual(Record.class, Record.job_FIELDNAME, work.getJob()));
//
//					rollbackWorkLog(business, work, nodes, workLogs);
//
//					emc.commit();
//					wo.setId(work.getId());
//					ActionResult<Wo> result = new ActionResult<>();
//					result.setData(wo);
//					return result;
//				}
//			}
//		};
//
//		ActionResult<Wo> result = ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get(300,
//				TimeUnit.SECONDS);
//
//		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
//				Applications.joinQueryUri("work", workId, "processing"), null, job);
//
//		return result;

	}

//	private void rollbackWork(Work work, WorkLog workLog) {
//		work.setSplitting(false);
//		work.setActivityName(workLog.getFromActivityName());
//		work.setActivity(workLog.getFromActivity());
//		work.setActivityAlias(workLog.getFromActivityAlias());
//		work.setActivityArrivedTime(workLog.getFromTime());
//		work.setActivityDescription("");
//		work.setActivityToken(workLog.getFromActivityToken());
//		work.setActivityType(workLog.getFromActivityType());
//		// 清除掉当前的待办人准备重新生成
//		work.getManualTaskIdentityList().clear();
//		work.setWorkStatus(WorkStatus.processing);
//	}
//
//	private void rollbackForm(Business business, Work work, Node node, Application application) throws Exception {
//		String id = "";
//		List<Node> list = new ArrayList<>();
//		List<Node> temp = new ArrayList<>();
//		list.add(node);
//		do {
//			temp.clear();
//			for (Node n : list) {
//				Activity activity = business.element().getActivity(n.getWorkLog().getFromActivity());
//				id = activity.getForm();
//				if (StringUtils.isNotEmpty(id)) {
//					Form form = business.element().get(id, Form.class);
//					if (null != form) {
//						work.setForm(id);
//					}
//					return;
//				} else {
//					temp.addAll(node.parents());
//				}
//			}
//			list.clear();
//			list.addAll(temp);
//		} while (!list.isEmpty());
//		if (StringUtils.isNotEmpty(application.getDefaultForm())) {
//			Form form = business.element().get(application.getDefaultForm(), Form.class);
//			if (null != form) {
//				work.setForm(id);
//			}
//		}
//	}
//
//	private void disconnectWorkLog(Work work, WorkLog workLog) {
//		workLog.setConnected(false);
//		workLog.setArrivedActivity("");
//		workLog.setArrivedActivityAlias("");
//		workLog.setArrivedActivityName("");
//		workLog.setArrivedActivityToken("");
//		workLog.setArrivedActivityType(null);
//		workLog.setArrivedTime(null);
//		workLog.setDuration(0L);
//		workLog.setWorkCompleted("");
//		workLog.setWork(work.getId());
//	}
//
//	private void rollbackTask(Business business, List<Task> list) throws Exception {
//		for (Task o : list) {
//			business.entityManagerContainer().remove(o);
//			MessageFactory.task_delete(o);
//		}
//	}
//
//	private void rollbackTaskCompleted(Business business, Work work, Nodes nodes, WorkLog workLog,
//			List<TaskCompleted> list) throws Exception {
//		for (TaskCompleted o : list) {
//			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
//					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
//				business.entityManagerContainer().remove(o);
//				MessageFactory.taskCompleted_delete(o);
//			} else {
//				o.setCompleted(false);
//				o.setWorkCompleted("");
//				o.setWork(work.getId());
//			}
//		}
//	}
//
//	private void rollbackRead(Business business, Work work, Nodes nodes, WorkLog workLog, List<Read> list)
//			throws Exception {
//		for (Read o : list) {
//			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
//					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
//				business.entityManagerContainer().remove(o);
//				MessageFactory.read_delete(o);
//			} else {
//				o.setCompleted(false);
//				o.setWorkCompleted("");
//				o.setWork(work.getId());
//			}
//		}
//	}
//
//	private void rollbackReadCompleted(Business business, Work work, Nodes nodes, WorkLog workLog,
//			List<ReadCompleted> list) throws Exception {
//		for (ReadCompleted o : list) {
//			if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())
//					|| StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
//				business.entityManagerContainer().remove(o);
//				MessageFactory.readCompleted_delete(o);
//			} else {
//				o.setCompleted(false);
//				o.setWorkCompleted("");
//				o.setWork(work.getId());
//			}
//		}
//	}
//
//	private void rollbackReview(Business business, Nodes nodes, List<Review> list) throws Exception {
//		Date date = nodes.latestArrivedTime();
//		if (null != date) {
//			for (Review o : list) {
//				if (null != o.getStartTime() && o.getStartTime().after(date)) {
//					business.entityManagerContainer().remove(o);
//					MessageFactory.review_delete(o);
//				} else {
//					o.setCompleted(false);
//					o.setCompletedTime(null);
//					o.setCompletedTimeMonth("");
//					o.setWorkCompleted("");
//				}
//			}
//		}
//	}
//
//	private void rollbackRecord(Business business, Nodes nodes, List<Record> list) throws Exception {
//		Date date = nodes.latestArrivedTime();
//		if (null != date) {
//			for (Record o : list) {
//				if (null != o.getCreateTime() && o.getCreateTime().after(date)) {
//					business.entityManagerContainer().remove(o);
//				}
//			}
//		}
//	}
//
//	private void rollbackWorkLog(Business business, Work work, Nodes nodes, List<WorkLog> list) throws Exception {
//		for (WorkLog o : list) {
//			if (!nodes.containsWorkLog(o)) {
//				business.entityManagerContainer().remove(o);
//			} else {
//				o.setCompleted(false);
//				o.setWorkCompleted("");
//				o.setWork(work.getId());
//			}
//		}
//	}
//
//	public static class Wi extends ProcessingAttributes {
//
//		private static final long serialVersionUID = -8317517462483674906L;
//
//		@FieldDescribe("工作日志标识")
//		private String workLog;
//
//		public String getWorkLog() {
//			return workLog;
//		}
//
//		public void setWorkLog(String workLog) {
//			this.workLog = workLog;
//		}
//
//	}
//
	public static class Wo extends WoId {

		private static final long serialVersionUID = 3836331836701686038L;
	}
}