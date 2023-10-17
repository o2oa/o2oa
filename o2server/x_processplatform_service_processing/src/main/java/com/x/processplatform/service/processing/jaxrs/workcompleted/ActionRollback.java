package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.ThisApplication;

class ActionRollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(flag, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(flag, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

					Business business = new Business(emc);

					WorkCompleted workCompleted = emc.flag(flag, WorkCompleted.class);

					if (null == workCompleted) {
						throw new ExceptionEntityNotExist(flag, WorkCompleted.class);
					}

					Application application = business.element().get(workCompleted.getApplication(), Application.class);

					if (null == application) {
						throw new ExceptionEntityNotExist(workCompleted.getApplication(), Application.class);
					}

					Process process = business.element().get(workCompleted.getProcess(), Process.class);

					if (null == process) {
						throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
					}

					WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);

					if (null == workLog) {
						throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
					}

					if (BooleanUtils.isTrue(workLog.getSplitting())) {
						throw new ExceptionSplittingNotRollback(workCompleted.getId(), workLog.getId());
					}

					List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, workLog.getJob());

					WorkLogTree workLogTree = new WorkLogTree(workLogs);

					Node node = workLogTree.find(workLog);

					Nodes nodes = workLogTree.rootTo(node);

					emc.beginTransaction(Work.class);
					emc.beginTransaction(WorkCompleted.class);
					emc.beginTransaction(WorkLog.class);
					emc.beginTransaction(Attachment.class);
					emc.beginTransaction(TaskCompleted.class);
					emc.beginTransaction(Read.class);
					emc.beginTransaction(ReadCompleted.class);
					emc.beginTransaction(Review.class);
					emc.beginTransaction(Record.class);

					Work work = createWork(business, workCompleted, workLog);
					emc.persist(work, CheckPersistType.all);

					disconnectWorkLog(work, workLog);

					rollbackTaskCompleted(business, work, nodes, workLog,
							emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob()));

					rollbackRead(business, work, nodes, workLog,
							emc.listEqual(Read.class, Read.job_FIELDNAME, work.getJob()));

					rollbackReadCompleted(business, work, nodes, workLog,
							emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob()));

					rollbackReview(business, work, nodes,
							emc.listEqual(Review.class, Review.job_FIELDNAME, work.getJob()));

					rollbackRecord(business, work, nodes, workLog,
							emc.listEqual(Record.class, Record.job_FIELDNAME, work.getJob()));

					rollbackWorkLog(business, work, nodes, workLogs);

					rollbackAttachment(business, work,
							emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, work.getJob()));

					emc.remove(workCompleted);

					emc.commit();

					wo.setId(work.getId());
				}
				return "";
			}
		};

		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", wo.getId(), "processing"), null, executorSeed);

		result.setData(wo);
		return result;
	}

	private Work createWork(Business business, WorkCompleted workCompleted, WorkLog workLog) throws Exception {
		Work work = new Work(workCompleted);
		work.setSplitting(false);
		work.setActivityName(workLog.getFromActivityName());
		work.setActivity(workLog.getFromActivity());
		work.setActivityAlias(workLog.getFromActivityAlias());
		work.setActivityArrivedTime(workLog.getFromTime());
		work.setActivityDescription("");
		work.setActivityToken(workLog.getFromActivityToken());
		work.setActivityType(workLog.getFromActivityType());
		String formId =business.element().lookupSuitableForm(work.getProcess(), work.getActivity()); 
        if (StringUtils.isNotBlank(formId)) {
            work.setForm(formId);                   
        }
//		work.setErrorRetry(0);
		work.setWorkStatus(WorkStatus.processing);
		// 因为workCompleted没有workCreateType属性，回溯到任何环节都必须要有待办，默认置为assign
		work.setWorkCreateType(Work.WORKCREATETYPE_ASSIGN);
		return work;
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

	private void rollbackReview(Business business, Work work, Nodes nodes, List<Review> list) throws Exception {
		Date date = nodes.latestArrivedTime();
		if (null != date) {
			for (Review o : list) {
				if (null != o.getStartTime() && o.getStartTime().after(date)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.review_delete(o);
				} else {
					o.setCompleted(false);
					o.setWorkCompleted("");
					o.setWork(work.getId());
				}
			}
		}
	}

	private void rollbackAttachment(Business business, Work work, List<Attachment> list) throws Exception {
		for (Attachment o : list) {
			o.setCompleted(false);
			o.setWork(work.getId());
			o.setWorkCompleted("");
		}
	}

	private void rollbackRecord(Business business, Work work, Nodes nodes, WorkLog workLog,
									   List<Record> list) throws Exception {
		for (Record o : list) {
			if (!nodes.containsWorkLogWithActivityToken(o.getFromActivityToken())
					|| StringUtils.equals(o.getFromActivityToken(), workLog.getFromActivityToken())) {
				business.entityManagerContainer().remove(o);
			} else {
				o.setCompleted(false);
				o.setWorkCompleted("");
				o.setWork(work.getId());
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

		private static final long serialVersionUID = 8702103487706268746L;
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

		private static final long serialVersionUID = -8686903170476114111L;
	}

}
