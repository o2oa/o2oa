package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkProcessingWo;
import com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkTerminateWo;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

	static DataItemConverter<Item> itemConverter = new DataItemConverter<>(Item.class);

	String getApplicationName(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		Application application = business.application().pick(id);
		if (null != application) {
			return application.getName();
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Work_.application), id));
		cq.select(root.get(Work_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	String getProcessName(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		Process process = business.process().pick(id);
		if (null != process) {
			return process.getName();
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Work_.process), id));
		cq.select(root.get(Work_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	Data loadData(Business business, Work work) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), work.getJob());
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		List<Item> list = em.createQuery(cq.where(p)).getResultList();
		if (list.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = itemConverter.assemble(list);
			if (jsonElement.isJsonObject()) {
				return gson.fromJson(jsonElement, Data.class);
			} else {
				/* 如果不是Object强制返回一个Map对象 */
				return new Data();
			}
		}
	}

	protected CompletableFuture<Boolean> checkControlVisitFuture(EffectivePerson effectivePerson, String flag) {
		return CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Control control = new JobControlBuilder(effectivePerson, business, flag).enableAllowVisit().build();
				value = control.getAllowVisit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return value;
		}, ThisApplication.forkJoinPool());
	}

	protected String createWorkProcessing(String processId, JsonElement jsonElement) throws Exception {
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "process", processId), jsonElement, null)
				.getData(WoId.class).getId();
	}

	public static class AbstractWo extends GsonPropertyObject {

		private static final long serialVersionUID = -6205312408256274367L;

		@FieldDescribe("活动节点")
		private WoActivity activity;

		@FieldDescribe("工作")
		private WoWork work;

		@FieldDescribe("工作日志对象")
		private List<WoWorkLog> workLogList = new ArrayList<>();

		@FieldDescribe("业务数据")
		private Data data;

		@FieldDescribe("附件对象")
		private List<WoAttachment> attachmentList = new ArrayList<>();

		@FieldDescribe("待办对象")
		private List<WoTask> taskList = new ArrayList<>();

		@FieldDescribe("待办对象")
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		@FieldDescribe("待阅对象")
		private List<WoRead> readList = new ArrayList<>();

		@FieldDescribe("已阅对象")
		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		@FieldDescribe("当前待办索引")
		private Integer currentTaskIndex = -1;

		@FieldDescribe("当前待阅索引")
		private Integer currentReadIndex = -1;

		@FieldDescribe("权限对象")
		private Control control;

		@FieldDescribe("表单对象")
		private WoForm form;

		public WoActivity getActivity() {
			return activity;
		}

		public void setActivity(WoActivity activity) {
			this.activity = activity;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
		}

		public Integer getCurrentReadIndex() {
			return currentReadIndex;
		}

		public void setCurrentReadIndex(Integer currentReadIndex) {
			this.currentReadIndex = currentReadIndex;
		}

		public WoWork getWork() {
			return work;
		}

		public void setWork(WoWork work) {
			this.work = work;
		}

		public List<WoWorkLog> getWorkLogList() {
			return workLogList;
		}

		public void setWorkLogList(List<WoWorkLog> workLogList) {
			this.workLogList = workLogList;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public List<WoAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WoAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

		public WoForm getForm() {
			return form;
		}

		public void setForm(WoForm form) {
			this.form = form;
		}

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<WoReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

	}

	public static class WoActivity extends GsonPropertyObject {

		private static final long serialVersionUID = 8013468482978783716L;

		private String id;

		private String name;

		private String description;

		private String alias;

		private String position;

		// private ActivityType activityType;

		private String resetRange;

		private Integer resetCount;

		private Boolean allowReset;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Integer getResetCount() {
			return resetCount;
		}

		public void setResetCount(Integer resetCount) {
			this.resetCount = resetCount;
		}

		public String getResetRange() {
			return resetRange;
		}

		public void setResetRange(String resetRange) {
			this.resetRange = resetRange;
		}

		public Boolean getAllowReset() {
			return allowReset;
		}

		public void setAllowReset(Boolean allowReset) {
			this.allowReset = allowReset;
		}

	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = 3269592171662996253L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class,
				JpaObject.singularAttributeField(Work.class, true, true), null);
	}

	public static class WoWorkLog extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		public static WrapCopier<WorkLog, WoWorkLog> copier = WrapCopierFactory.wo(WorkLog.class, WoWorkLog.class,
				JpaObject.singularAttributeField(WorkLog.class, true, true), null);

		private List<WoTask> taskList = new ArrayList<>();

		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		private List<WoRead> readList = new ArrayList<>();

		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public List<WoReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<WoReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		public static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -8067704098385000667L;

		public static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);

		@Override
		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		public static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);

	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = 1961904054350222311L;

		public static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, null, JpaObject.FieldsInvisible);

	}

	public static class WoAttachment extends Attachment {

		private static final long serialVersionUID = 1954637399762611493L;

		public static WrapCopier<Attachment, WoAttachment> copier = WrapCopierFactory.wo(Attachment.class,
				WoAttachment.class, null, JpaObject.FieldsInvisible);

	}

	public class WoForm extends Form {

		private static final long serialVersionUID = 8714459358196550018L;

	}

	// 启动工作时查找有没有此人创建的工作并且没有流转的
	protected String latest(Business business, Process process, String identity) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Task> tasks = emc.fetchEqualAndEqual(Task.class, ListTools.toList(Task.job_FIELDNAME),
				Task.process_FIELDNAME, process.getId(), Task.identity_FIELDNAME, identity);
		List<String> formTaskJobs = ListTools.extractField(tasks, Task.job_FIELDNAME, String.class, true, true);
		List<TaskCompleted> taskCompleteds = emc.fetchEqualAndIn(TaskCompleted.class,
				ListTools.toList(TaskCompleted.job_FIELDNAME), TaskCompleted.process_FIELDNAME, process.getId(),
				TaskCompleted.job_FIELDNAME, formTaskJobs);
		List<String> formTaskCompletedJobs = ListTools.extractField(taskCompleteds, TaskCompleted.job_FIELDNAME,
				String.class, true, true);
		List<String> jobs = ListUtils.subtract(formTaskJobs, formTaskCompletedJobs);
		if (ListTools.isEmpty(jobs)) {
			return null;
		}
		List<Work> works = emc.fetchIn(Work.class, ListTools.toList(Work.updateTime_FIELDNAME, Work.id_FIELDNAME),
				Work.job_FIELDNAME, jobs);
		Work work = works.stream()
				.sorted(Comparator.comparing(Work::getUpdateTime, Comparator.nullsFirst(Date::compareTo)).reversed())
				.findFirst().orElse(null);
		if (null != work) {
			// 在同时判断data是否没有数据
			Data data = this.loadData(business, work);
			List<String> keys = new ArrayList<>(data.keyList());
			keys.remove(Data.WORK_PROPERTY);
			keys.remove(Data.ATTACHMENTLIST_PROPERTY);
			if (keys.isEmpty()) {
				return work.getId();
			}
		}
		return null;
	}

	protected Record recordWorkProcessing(String recordType, String routeName, String opinion, String job,
			String workLogId, String identity, String series) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkProcessingWi req = new com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkProcessingWi();
		req.setRecordType(recordType);
		req.setRouteName(routeName);
		req.setOpinion(opinion);
		req.setWorkLog(workLogId);
		req.setDistinguishedName(identity);
		req.setSeries(series);
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "work", "processing"), req, job)
				.getData(ActionWorkProcessingWo.class);
	}

	protected Record recordWorkTerminate(String distinguishedName, String routeName, String opinion,
			String workCompleted, String job) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkTerminateWi req = new com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkTerminateWi();
		req.setDistinguishedName(distinguishedName);
		req.setRouteName(routeName);
		req.setOpinion(opinion);
		req.setWorkCompleted(workCompleted);
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "work", "terminate"), req, job)
				.getData(ActionWorkTerminateWo.class);
	}

}