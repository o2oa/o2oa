package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

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

	public static class AbstractWo extends GsonPropertyObject {

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
		private WoControl control;

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

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
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

//		public ActivityType getActivityType() {
//			return activityType;
//		}
//
//		public void setActivityType(ActivityType activityType) {
//			this.activityType = activityType;
//		}

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

		// private Integer currentTaskIndex;

		// public Integer getCurrentTaskIndex() {
		// return currentTaskIndex;
		// }
		//
		// public void setCurrentTaskIndex(Integer currentTaskIndex) {
		// this.currentTaskIndex = currentTaskIndex;
		// }

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

	public static class WoControl extends GsonPropertyObject {
		/* 是否可以看到 */
		private Boolean allowVisit;
		/* 是否可以直接流转 */
		private Boolean allowProcessing;
		/* 是否可以处理待阅 */
		private Boolean allowReadProcessing;
		/* 是否可以保存数据 */
		private Boolean allowSave;
		/* 是否可以重置处理人 */
		private Boolean allowReset;
		/* 是否可以待阅处理人 */
		private Boolean allowReadReset;
		/* 是否可以召回 */
		private Boolean allowRetract;
		/* 是否可以调度 */
		private Boolean allowReroute;
		/* 是否可以删除 */
		private Boolean allowDelete;
		/* 是否可以删除 */
		private Boolean allowAddSplit;

		public Boolean getAllowSave() {
			return allowSave;
		}

		public void setAllowSave(Boolean allowSave) {
			this.allowSave = allowSave;
		}

		public Boolean getAllowReset() {
			return allowReset;
		}

		public void setAllowReset(Boolean allowReset) {
			this.allowReset = allowReset;
		}

		public Boolean getAllowRetract() {
			return allowRetract;
		}

		public void setAllowRetract(Boolean allowRetract) {
			this.allowRetract = allowRetract;
		}

		public Boolean getAllowReroute() {
			return allowReroute;
		}

		public void setAllowReroute(Boolean allowReroute) {
			this.allowReroute = allowReroute;
		}

		public Boolean getAllowProcessing() {
			return allowProcessing;
		}

		public void setAllowProcessing(Boolean allowProcessing) {
			this.allowProcessing = allowProcessing;
		}

		public Boolean getAllowDelete() {
			return allowDelete;
		}

		public void setAllowDelete(Boolean allowDelete) {
			this.allowDelete = allowDelete;
		}

		public Boolean getAllowVisit() {
			return allowVisit;
		}

		public void setAllowVisit(Boolean allowVisit) {
			this.allowVisit = allowVisit;
		}

		public Boolean getAllowReadProcessing() {
			return allowReadProcessing;
		}

		public void setAllowReadProcessing(Boolean allowReadProcessing) {
			this.allowReadProcessing = allowReadProcessing;
		}

		public Boolean getAllowReadReset() {
			return allowReadReset;
		}

		public void setAllowReadReset(Boolean allowReadReset) {
			this.allowReadReset = allowReadReset;
		}

		public Boolean getAllowAddSplit() {
			return allowAddSplit;
		}

		public void setAllowAddSplit(Boolean allowAddSplit) {
			this.allowAddSplit = allowAddSplit;
		}

	}

	public class WoForm extends Form {

		private static final long serialVersionUID = 8714459358196550018L;

	}

	protected <T extends AbstractWo> T get(Business business, EffectivePerson effectivePerson, Work work, Class<T> cls)
			throws Exception {
		T t = cls.newInstance();
		List<WoTaskCompleted> woTaskCompleteds = new ArrayList<>();
		List<WoReadCompleted> woReadCompleteds = new ArrayList<>();
		Application application = null;
		Process process = null;
		Activity activity = null;
		Long reviewCount = 0L;

		CompletableFuture<Void> future_tasks = CompletableFuture.runAsync(() -> {
			try {
				List<Task> os = business.entityManagerContainer()
						.listEqual(Task.class, WoTask.job_FIELDNAME, work.getJob()).stream()
						.sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				t.getTaskList().addAll(WoTask.copier.copy(os));
				t.setCurrentTaskIndex(-1);
				for (int i = 0; i < t.getTaskList().size(); i++) {
					if (StringUtils.equals(t.getTaskList().get(i).getPerson(),
							effectivePerson.getDistinguishedName())) {
						t.setCurrentTaskIndex(i);
						break;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});

		CompletableFuture<Void> future_taskCompleteds = CompletableFuture.runAsync(() -> {
			try {
				List<TaskCompleted> os = business.entityManagerContainer()
						.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob()).stream()
						.sorted(Comparator.comparing(TaskCompleted::getStartTime,
								Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				woTaskCompleteds.addAll(WoTaskCompleted.copier.copy(os));
			} catch (Exception e) {
				logger.error(e);
			}
		});

		CompletableFuture<Void> future_reads = CompletableFuture.runAsync(() -> {
			try {
				List<Read> os = business.entityManagerContainer()
						.listEqual(Read.class, Read.job_FIELDNAME, work.getJob()).stream()
						.sorted(Comparator.comparing(Read::getStartTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				t.getReadList().addAll(WoRead.copier.copy(os));
				t.setCurrentReadIndex(-1);
				for (int i = 0; i < t.getReadList().size(); i++) {
					if (StringUtils.equals(t.getReadList().get(i).getPerson(),
							effectivePerson.getDistinguishedName())) {
						t.setCurrentReadIndex(i);
						break;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
		CompletableFuture<Void> future_readCompleteds = CompletableFuture.runAsync(() -> {
			try {
				List<ReadCompleted> os = business.entityManagerContainer()
						.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob()).stream()
						.sorted(Comparator.comparing(ReadCompleted::getStartTime,
								Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				woReadCompleteds.addAll(WoReadCompleted.copier.copy(os));
			} catch (Exception e) {
				logger.error(e);
			}
		});
		CompletableFuture<Void> future_attachments = CompletableFuture.runAsync(() -> {
			try {
				List<Attachment> os = business.entityManagerContainer()
						.listEqual(Attachment.class, Attachment.job_FIELDNAME, work.getJob()).stream()
						.sorted(Comparator.comparing(Attachment::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				t.setAttachmentList(WoAttachment.copier.copy(os));
				/* 创建附件类型字段 */
				// for (WoAttachment a : t.getAttachmentList()) {
				// a.contentType();
				// }
			} catch (Exception e) {
				logger.error(e);
			}
		});
		CompletableFuture<Void> future_workLogs = CompletableFuture.runAsync(() -> {
			try {
				List<WorkLog> os = business.entityManagerContainer()
						.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob()).stream()
						.sorted(Comparator.comparing(WorkLog::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				t.setWorkLogList(WoWorkLog.copier.copy(os));
			} catch (Exception e) {
				logger.error(e);
			}
		});
		CompletableFuture<Void> future_data = CompletableFuture.runAsync(() -> {
			try {
				t.setData(this.loadData(business, work));
			} catch (Exception e) {
				logger.error(e);
			}
		});
		CompletableFuture<Application> future_application = CompletableFuture.supplyAsync(() -> {
			Application o = null;
			try {
				o = business.application().pick(work.getApplication());
			} catch (Exception e) {
				logger.error(e);
			}
			return o;
		});
		CompletableFuture<Process> future_process = CompletableFuture.supplyAsync(() -> {
			Process o = null;
			try {
				o = business.process().pick(work.getProcess());
			} catch (Exception e) {
				logger.error(e);
			}
			return o;
		});
		CompletableFuture<Activity> future_activity = CompletableFuture.supplyAsync(() -> {
			Activity o = null;
			try {
				o = business.getActivity(work);
				if (null != o) {
					/** 这里由于Activity是个抽象类,没有具体的Field字段,所以无法用WrapCoiper进行拷贝 */
					WoActivity woActivity = new WoActivity();
					o.copyTo(woActivity);
					t.setActivity(woActivity);
				}
			} catch (Exception e) {
				logger.error(e);
			}
			return o;
		});
		CompletableFuture<Long> future_reviewCount = CompletableFuture.supplyAsync(() -> {
			Long o = 0L;
			try {
				o = business.entityManagerContainer().countEqualAndEqual(Review.class, Review.person_FIELDNAME,
						effectivePerson.getDistinguishedName(), Review.job_FIELDNAME, work.getJob());
			} catch (Exception e) {
				logger.error(e);
			}
			return o;
		});
		future_tasks.get(300, TimeUnit.SECONDS);
		future_taskCompleteds.get(300, TimeUnit.SECONDS);
		future_reads.get(300, TimeUnit.SECONDS);
		future_readCompleteds.get(300, TimeUnit.SECONDS);
		future_attachments.get(300, TimeUnit.SECONDS);
		future_workLogs.get(300, TimeUnit.SECONDS);
		future_data.get(300, TimeUnit.SECONDS);
		application = future_application.get(300, TimeUnit.SECONDS);
		process = future_process.get(300, TimeUnit.SECONDS);
		activity = future_activity.get(300, TimeUnit.SECONDS);
		reviewCount = future_reviewCount.get(300, TimeUnit.SECONDS);

		t.setWork(WoWork.copier.copy(work));
		this.arrangeWorkLog(business, t, woTaskCompleteds, woReadCompleteds);
		// this.referenceActivityTaskReadControl(business, effectivePerson, work, t);

		WoControl control = new WoControl();
		/** 工作是否可以打开(管理员 或 有task,taskCompleted,read,readCompleted,review的人) */
		control.setAllowVisit(false);
		/** 工作是否可以流转(有task的人) */
		control.setAllowProcessing(false);
		/** 工作是否可以处理待阅(有read的人) */
		control.setAllowReadProcessing(false);
		/** 工作是否可保存(管理员 或者 有本人的task) */
		control.setAllowSave(false);
		/** 工作是否可重置(有本人待办 并且 活动设置允许重置 */
		control.setAllowReset(false);
		/** 工作是否可以撤回(当前人是上一个处理人 并且 还没有其他人处理过) */
		control.setAllowRetract(false);
		/** 工作是否可调度(管理员 并且 此活动在流程设计中允许调度) */
		control.setAllowReroute(false);
		/** 工作是否可删除(管理员 或者 此活动在流程设计中允许删除且当前待办人是文件的创建者) */
		control.setAllowDelete(false);
		/** 是否可以添加拆分分支 */
		control.setAllowAddSplit(false);
		/** 设置allowVisit */
		if ((t.getCurrentTaskIndex() > -1) || (t.getCurrentReadIndex() > -1) || (woTaskCompleteds.stream()
				.filter(o -> StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName())).count() > 0)
				|| (woReadCompleteds.stream()
						.filter(o -> StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName()))
						.count() > 0)
				|| (reviewCount > 0)) {
			control.setAllowVisit(true);
		} else if (effectivePerson.isPerson(work.getCreatorPerson())) {
			control.setAllowVisit(true);
		} else if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowVisit(true);
		}
		/** 设置allowProcessing */
		if (t.getCurrentTaskIndex() > -1) {
			control.setAllowProcessing(true);
		}
		/** 设置allowReadProcessing */
		if (t.getCurrentReadIndex() > -1) {
			control.setAllowReadProcessing(true);
		}
		/** 设置 allowSave */
		if (t.getCurrentTaskIndex() > -1) {
			control.setAllowSave(true);
		} else if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowSave(true);
		}
		/** 设置 allowReset */
		if (BooleanUtils.isTrue((Boolean) PropertyUtils.getProperty(t.getActivity(), Manual.allowReset_FIELDNAME))
				&& (t.getCurrentTaskIndex() > -1)) {
			control.setAllowReset(true);
		}
		/** 设置 allowRetract */
		if (Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(activity.get(Manual.allowRetract_FIELDNAME, Boolean.class))) {
			/** 标志文件还没有处理过 */
			if (woTaskCompleteds.stream()
					.filter(o -> StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName())
							&& StringUtils.equals(o.getActivityToken(), work.getActivityToken()))
					.count() == 0) {
				/** 找到到达当前活动的workLog */
				WoWorkLog currentWorkLog = t.getWorkLogList().stream()
						.filter(o -> StringUtils.equals(o.getArrivedActivityToken(), work.getActivityToken()))
						.findFirst().orElse(null);
				if (null != currentWorkLog) {
					/** 查找上一个环节的已办,如果只有一个,且正好是当前人的,那么可以召回 */
					if (woTaskCompleteds.stream().filter(
							o -> StringUtils.equals(o.getActivityToken(), currentWorkLog.getFromActivityToken()))
							.count() == 1) {
						if (woTaskCompleteds.stream().filter(
								o -> StringUtils.equals(currentWorkLog.getFromActivityToken(), o.getActivityToken())
										&& StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName()))
								.count() == 1) {
							control.setAllowRetract(true);
						}
					}
				}
			}
		}
		/** 设置 allowReroute */
		if (BooleanUtils.isTrue(activity.getAllowReroute())) {
			/** 如果活动设置了可以调度 */
			if (effectivePerson.isManager()) {
				/** 管理员可以调度 */
				control.setAllowReroute(true);
			} else if (business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.ProcessPlatformManager)) {
				/** 有流程管理角色的可以 */
				control.setAllowReroute(true);
			} else if ((null != process) && effectivePerson.isPerson(process.getControllerList())) {
				/** 如果是流程的管理员那么可以调度 */
				control.setAllowReroute(true);
			} else if ((null != application) && effectivePerson.isPerson(application.getControllerList())) {
				/** 如果是应用的管理员那么可以调度 */
				control.setAllowReroute(true);
			}
		}
		/* 设置 allowDelete */
		if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		} else if (Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(activity.get(Manual.allowDeleteWork_FIELDNAME, Boolean.class))) {
			if (t.getCurrentTaskIndex() > -1
					|| StringUtils.equals(work.getCreatorPerson(), effectivePerson.getDistinguishedName())) {
				control.setAllowDelete(true);
			}
		}
		/* 设置 allowAddSplit */
		if (Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(activity.get(Manual.allowAddSplit_FIELDNAME, Boolean.class))) {
			control.setAllowAddSplit(true);
		}
		t.setControl(control);
		return t;
	}

	private void arrangeWorkLog(Business business, AbstractWo wo, List<WoTaskCompleted> woTaskCompleteds,
			List<WoReadCompleted> woReadCompleteds) throws Exception {
		ListTools.groupStick(wo.getWorkLogList(), wo.getTaskList(), WorkLog.fromActivityToken_FIELDNAME,
				Task.activityToken_FIELDNAME, "taskList");
		ListTools.groupStick(wo.getWorkLogList(), woTaskCompleteds, WorkLog.fromActivityToken_FIELDNAME,
				TaskCompleted.activityToken_FIELDNAME, "taskCompletedList");
		ListTools.groupStick(wo.getWorkLogList(), wo.getReadList(), WorkLog.fromActivityToken_FIELDNAME,
				Read.activityToken_FIELDNAME, "readList");
		ListTools.groupStick(wo.getWorkLogList(), woReadCompleteds, WorkLog.fromActivityToken_FIELDNAME,
				ReadCompleted.activityToken_FIELDNAME, "readCompletedList");
	}

	/* 启动工作时查找有没有此人创建的工作并且没有流转的 */
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
			/* 在同时判断data是否没有数据 */
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

}