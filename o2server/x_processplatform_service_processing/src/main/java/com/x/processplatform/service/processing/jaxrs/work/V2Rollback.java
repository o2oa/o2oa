package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class V2Rollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Rollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id, jsonElement);

		CallableImpl callable = new CallableImpl(param.getWorkId(), param.getWorkLogId(),
				param.getDistinguishedNameList());

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.getJob()).submit(callable).get(300,
				TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.setJob(work.getJob());
			param.setWorkId(work.getId());
			param.setWorkLogId(id);
			param.setDistinguishedNameList(wi.getDistinguishedNameList());
		}
		return param;
	}

	private class Param {

		private String job;
		private String workId;
		private String workLogId;
		private List<String> distinguishedNameList;

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getWorkLogId() {
			return workLogId;
		}

		public void setWorkLogId(String workLogId) {
			this.workLogId = workLogId;
		}

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private CallableImpl(String workId, String workLogId, List<String> distinguishedNameList) {
			this.workId = workId;
			this.workLogId = workLogId;
			this.distinguishedNameList = distinguishedNameList;
		}

		private String workId;
		private String workLogId;
		private List<String> distinguishedNameList;

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = getWork(business, workId);
				List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob());
				WorkLogTree tree = new WorkLogTree(emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob()));
				WorkLog workLog = getTargetWorkLog(workLogs, workLogId);
				Node workLogNode = tree.find(workLog);
				Nodes nodes = tree.down(workLogNode);
				List<String> activityTokens = activityTokenOfNodes(nodes);

				LOGGER.debug("activityTokens:{}.", () -> gson.toJson(activityTokens));

				emc.beginTransaction(Task.class);
				emc.beginTransaction(TaskCompleted.class);
				emc.beginTransaction(Read.class);
				emc.beginTransaction(ReadCompleted.class);
				emc.beginTransaction(WorkLog.class);
				emc.beginTransaction(Work.class);
				emc.beginTransaction(Record.class);

				deleteTasks(business, work.getJob(), activityTokens);
				deleteTaskCompleteds(business, work.getJob(), activityTokens);
				deleteReads(business, work.getJob(), activityTokens);
				deleteReadCompleteds(business, work.getJob(), activityTokens);
				deleteRecords(business, work.getJob(), activityTokens);
				deleteWorkLogs(business, work.getJob(), activityTokens);

				List<String> workIds = workOfNodes(nodes);

				workIds = ListUtils.subtract(workIds, ListTools.toList(work.getId()));

				deleteWorks(business, work.getJob(), workIds);

				update(business, work, workLog);

				List<String> manualTaskIdentityList = new ArrayList<>();

				List<TaskCompleted> taskCompleteds = new ArrayList<>();

				if (ListTools.isNotEmpty(distinguishedNameList)) {
					// 如果指定了回退人
					taskCompleteds = emc.listEqualAndEqualAndIn(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
							work.getJob(), TaskCompleted.activity_FIELDNAME, workLog.getFromActivity(),
							TaskCompleted.DISTINGUISHEDNAME_FIELDNAME, distinguishedNameList);
				} else {
					taskCompleteds = emc.listEqualAndEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
							work.getJob(), TaskCompleted.activity_FIELDNAME, workLog.getFromActivity(),
							TaskCompleted.joinInquire_FIELDNAME, true);
				}

				for (TaskCompleted o : taskCompleteds) {
					if (BooleanUtils.isTrue(o.getJoinInquire())) {
						emc.remove(o, CheckRemoveType.all);
					}
					manualTaskIdentityList.add(o.getIdentity());
				}
				updateManualTaskIdentityMatrix(business, work, manualTaskIdentityList);
				emc.commit();
			}

			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setId(workId);
			result.setData(wo);
			return result;
		}

		private Work getWork(Business business, String workId) throws Exception {
			Work work = business.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Application application = business.element().get(work.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(work.getApplication(), Application.class);
			}
			Process process = business.element().get(work.getProcess(), Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
			}
			return work;
		}

		private void update(Business business, Work work, WorkLog workLog) throws Exception {
			work.setActivity(workLog.getFromActivity());
			work.setActivityType(workLog.getFromActivityType());
			work.setActivityAlias(workLog.getFromActivityAlias());
			work.setActivityName(workLog.getFromActivityName());
			work.setActivityToken(workLog.getFromActivityToken());
			work.setSplitting(workLog.getSplitting());
			work.setSplitToken(workLog.getSplitToken());
			work.setSplitValue(workLog.getSplitValue());
			// 重新设置表单
			String formId = business.element().lookupSuitableForm(work.getProcess(), work.getActivity());
			if (StringUtils.isNotBlank(formId)) {
				work.setForm(formId);
			}
			workLog.setConnected(false);
			workLog.setArrivedActivity("");
			workLog.setArrivedActivityAlias("");
			workLog.setArrivedActivityName("");
			workLog.setArrivedActivityToken("");
			workLog.setArrivedActivityType(null);
			workLog.setArrivedGroup(null);
			workLog.setArrivedOpinionGroup(null);
			workLog.setArrivedTime(null);
		}

		private void updateManualTaskIdentityMatrix(Business business, Work work, List<String> manualTaskIdentityList)
				throws Exception {
			if (Objects.equals(ActivityType.manual, work.getActivityType())) {
				Manual manual = (Manual) business.element().get(work.getActivity(), ActivityType.manual);
				if (null != manual) {
					work.setTickets(manual.identitiesToTickets(manualTaskIdentityList));
				}

			}
		}

		private WorkLog getTargetWorkLog(List<WorkLog> list, String id) throws ExceptionEntityNotExist {
			WorkLog workLog = list.stream().filter(o -> StringUtils.equals(o.getId(), id)).findFirst().orElse(null);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(id, WorkLog.class);
			}
			return workLog;
		}

		private List<String> activityTokenOfNodes(Nodes nodes) {
			List<String> list = new ArrayList<>();
			for (Node o : nodes) {
				list.add(o.getWorkLog().getFromActivityToken());
			}
			return ListTools.trim(list, true, true);
		}

		private List<String> workOfNodes(Nodes nodes) {
			List<String> os = new ArrayList<>();
			for (Node o : nodes) {
				os.add(o.getWorkLog().getWork());
			}
			return ListTools.trim(os, true, true);
		}

		private void deleteTasks(Business business, String job, List<String> activityTokens) throws Exception {
			List<Task> os = business.entityManagerContainer().listEqualAndIn(Task.class, Task.job_FIELDNAME, job,
					Task.activityToken_FIELDNAME, activityTokens);
			for (Task o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.task_delete(o);
			}
		}

		private void deleteTaskCompleteds(Business business, String job, List<String> activityTokens) throws Exception {
			List<TaskCompleted> os = business.entityManagerContainer().listEqualAndIn(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, job, TaskCompleted.activityToken_FIELDNAME, activityTokens);
			for (TaskCompleted o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.taskCompleted_delete(o);
			}
		}

		private void deleteReads(Business business, String job, List<String> activityTokens) throws Exception {
			List<Read> os = business.entityManagerContainer().listEqualAndIn(Read.class, Read.job_FIELDNAME, job,
					Read.activityToken_FIELDNAME, activityTokens);
			for (Read o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.read_delete(o);
			}
		}

		private void deleteReadCompleteds(Business business, String job, List<String> activityTokens) throws Exception {
			List<ReadCompleted> os = business.entityManagerContainer().listEqualAndIn(ReadCompleted.class,
					ReadCompleted.job_FIELDNAME, job, ReadCompleted.activityToken_FIELDNAME, activityTokens);
			for (ReadCompleted o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.readCompleted_delete(o);
			}
		}

		private void deleteRecords(Business business, String job, List<String> activityTokens) throws Exception {
			List<Record> os = business.entityManagerContainer().listEqualAndIn(Record.class, Record.job_FIELDNAME, job,
					Record.fromActivityToken_FIELDNAME, activityTokens);
			for (Record o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
		}

		private void deleteWorkLogs(Business business, String job, List<String> activityTokens) throws Exception {
			List<WorkLog> os = business.entityManagerContainer().listEqualAndIn(WorkLog.class, WorkLog.JOB_FIELDNAME,
					job, WorkLog.FROMACTIVITYTOKEN_FIELDNAME, activityTokens);
			for (WorkLog o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);

			}
		}

		private void deleteWorks(Business business, String job, List<String> workIds) throws Exception {
			List<Work> os = business.entityManagerContainer().listEqualAndIn(Work.class, Work.job_FIELDNAME, job,
					JpaObject.id_FIELDNAME, workIds);
			for (Work o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.work_delete(o);
			}
		}
	}

	public static class Wi extends V2RollbackWi {

		private static final long serialVersionUID = 1549664177644024435L;

	}

	public static class Wo extends V2RollbackWo {

		private static final long serialVersionUID = 7732547960719161607L;
	}
}