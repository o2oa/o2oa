package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
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
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionRollbackWi;
import com.x.processplatform.core.express.service.processing.jaxrs.workcompleted.ActionRollbackWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.TaskTickets;
import com.x.query.core.entity.Item;

class ActionRollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, flag:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> flag,
				() -> jsonElement);

		Param param = init(flag, jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String flag, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.fetch(flag, WorkCompleted.class, ListTools.toList(
					WorkCompleted.job_FIELDNAME, WorkCompleted.application_FIELDNAME, WorkCompleted.process_FIELDNAME));
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
			Manual manual = (Manual) business.element().get(workLog.getFromActivity(), ActivityType.manual);
			if (null == manual) {
				throw new ExceptionEntityNotExist(workLog.getFromActivity(), Manual.class);
			}
			param.job = workCompleted.getJob();
			param.workCompleted = workCompleted;
			param.workLog = workLog;
			param.distinguishedNameList = wi.getDistinguishedNameList();
		}
		return param;
	}

	private class Param {

		private String job;
		private WorkCompleted workCompleted;
		private WorkLog workLog;
		private List<String> distinguishedNameList;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				WorkCompleted workCompleted = emc.find(param.workCompleted.getId(), WorkCompleted.class);
				WorkLog workLog = emc.find(param.workLog.getId(), WorkLog.class);
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
				emc.beginTransaction(Item.class);
				Work work = createWork(business, workCompleted, workLog);
				Manual manual = (Manual) business.element().get(workLog.getFromActivity(), ActivityType.manual);
				emc.persist(work, CheckPersistType.all);
				restoreData(business, workCompleted);
				disconnectWorkLog(work, workLog);
				rollbackTaskCompleted(business, work, nodes, workLog,
						emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob()));
				rollbackRead(business, work, nodes, workLog,
						emc.listEqual(Read.class, Read.job_FIELDNAME, work.getJob()));
				rollbackReadCompleted(business, work, nodes, workLog,
						emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob()));
				rollbackReview(business, work, nodes, emc.listEqual(Review.class, Review.job_FIELDNAME, work.getJob()));
				rollbackRecord(business, work, nodes, workLog,
						emc.listEqual(Record.class, Record.job_FIELDNAME, work.getJob()));
				rollbackWorkLog(business, work, nodes, workLogs);
				rollbackAttachment(business, work,
						emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, work.getJob()));
				if (ListTools.isNotEmpty(param.distinguishedNameList)) {
					work.setTickets(
							TaskTickets.translate(new AeiObjects(business, work, manual, new ProcessingAttributes()),
									manual, param.distinguishedNameList));
				} else {
					work.setTickets(null);
				}
				emc.remove(workCompleted);
				emc.commit();
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setId(work.getId());
				result.setData(wo);
				return result;
			}
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
			String formId = business.element().lookupSuitableForm(work.getProcess(), work.getActivity());
			if (StringUtils.isNotBlank(formId)) {
				work.setForm(formId);
			}
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

		private void restoreData(Business business, WorkCompleted workCompleted) throws Exception {
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				WorkDataHelper workDataHelper = new WorkDataHelper(business.entityManagerContainer(), workCompleted);
				workDataHelper.update(workCompleted.getData());
			}
		}

		private void rollbackTaskCompleted(Business business, Work work, Nodes nodes, WorkLog workLog,
				List<TaskCompleted> list) throws Exception {
			List<TaskCompleted> remains = new ArrayList<>();
			for (TaskCompleted o : list) {
				if (!nodes.containsWorkLogWithActivityToken(o.getActivityToken())) {
					business.entityManagerContainer().remove(o);
					MessageFactory.taskCompleted_delete(o);
				} else if (StringUtils.equals(o.getActivityToken(), workLog.getFromActivityToken())) {
					o.setJoinInquire(false);
					o.setCompleted(false);
					o.setWorkCompleted("");
					o.setWork(work.getId());
					remains.add(o);
				} else {
					o.setCompleted(false);
					o.setWorkCompleted("");
					o.setWork(work.getId());
					remains.add(o);
				}
			}
			// 调整latest标识,否则有不是latest的已办不会在已办列表中显示
			remains.stream().collect(Collectors.groupingBy(TaskCompleted::getPerson)).entrySet().stream()
					.map(Map.Entry::getValue).filter(o -> o.stream().noneMatch(t -> BooleanUtils.isTrue(t.getLatest())))
					.forEach(o -> {
						Optional<TaskCompleted> opt = o.stream()
								.sorted(Comparator.comparing(TaskCompleted::getCompletedTime,
										Comparator.nullsFirst(Date::compareTo).reversed()))
								.findFirst();
						if (opt.isPresent()) {
							opt.get().setLatest(true);
						}
					});
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

		private void rollbackAttachment(Business business, Work work, List<Attachment> list) {
			for (Attachment o : list) {
				o.setCompleted(false);
				o.setWork(work.getId());
				o.setWorkCompleted("");
			}
		}

		private void rollbackRecord(Business business, Work work, Nodes nodes, WorkLog workLog, List<Record> list)
				throws Exception {
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
	}

	public static class Wi extends ActionRollbackWi {

		private static final long serialVersionUID = -7222129694132489744L;

	}

	public static class Wo extends ActionRollbackWo {

		private static final long serialVersionUID = -8686903170476114111L;
	}

}
