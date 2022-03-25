package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
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
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), id));
		cq.select(root.get(WorkCompleted_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	Data loadData(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), workCompleted.getJob());
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

		@FieldDescribe("业务数据")
		private Data data;

		@FieldDescribe("已完成工作")
		private WoWorkCompleted workCompleted;

		@FieldDescribe("待办,必然为空")
		private List<WoTask> taskList = new ArrayList<>();

		@FieldDescribe("已办")
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		@FieldDescribe("待阅")
		private List<WoRead> readList = new ArrayList<>();

		@FieldDescribe("已阅")
		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		@FieldDescribe("附件")
		private List<WoAttachment> attachmentList = new ArrayList<>();

		@FieldDescribe("工作日志")
		private List<WoWorkLog> workLogList = new ArrayList<>();

		@FieldDescribe("权限")
		private WoControl control;

		@FieldDescribe("当前待阅索引")
		private Integer currentReadIndex = -1;

		@FieldDescribe("表单")
		private WoForm form;

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public WoWorkCompleted getWorkCompleted() {
			return workCompleted;
		}

		public void setWorkCompleted(WoWorkCompleted workCompleted) {
			this.workCompleted = workCompleted;
		}

		public List<WoAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WoAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

		public List<WoWorkLog> getWorkLogList() {
			return workLogList;
		}

		public void setWorkLogList(List<WoWorkLog> workLogList) {
			this.workLogList = workLogList;
		}

		public WoForm getForm() {
			return form;
		}

		public void setForm(WoForm form) {
			this.form = form;
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

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public Integer getCurrentReadIndex() {
			return currentReadIndex;
		}

		public void setCurrentReadIndex(Integer currentReadIndex) {
			this.currentReadIndex = currentReadIndex;
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

	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = 2620843025774912687L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, null, JpaObject.FieldsInvisible);

	}

	public static class WoAttachment extends Attachment {

		private static final long serialVersionUID = -5308138678076613506L;

		static WrapCopier<Attachment, WoAttachment> copier = WrapCopierFactory.wo(Attachment.class, WoAttachment.class,
				null, JpaObject.FieldsInvisible);

	}

	public static class WoWorkLog extends WorkLog {

		private List<WoTask> taskList = new ArrayList<>();

		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		private List<WoRead> readList = new ArrayList<>();

		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		private static final long serialVersionUID = -8169472016302098902L;

		static WrapCopier<WorkLog, WoWorkLog> copier = WrapCopierFactory.wo(WorkLog.class, WoWorkLog.class, null,
				JpaObject.FieldsInvisible);

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
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

		private static final long serialVersionUID = 813720809948190092L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Task.opinionLob_FIELDNAME));

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = 850727404260313692L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, TaskCompleted.opinionLob_FIELDNAME));

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}
	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -8067704098385000667L;

		public static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Read.opinionLob_FIELDNAME));

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}
	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = 4288951128706765345L;
		public static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, WoReadCompleted.opinionLob_FIELDNAME));

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}
	}

	public static class WoControl extends WorkCompletedControl {

	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = 8714459358196550018L;

		public static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class,
				JpaObject.singularAttributeField(Form.class, true, true), null);
	}

//	protected <T extends AbstractWo> T get(Business business, EffectivePerson effectivePerson,
//			WorkCompleted workCompleted, Class<T> cls) throws Exception {
//		T t = cls.newInstance();
//		List<WoTaskCompleted> woTaskCompleteds = new ArrayList<>();
//		List<WoReadCompleted> woReadCompleteds = new ArrayList<>();
//		Application application = null;
//		Process process = null;
//		Activity activity = null;
//		Long reviewCount = 0L;
//
//		CompletableFutureXXXXX111111111111111111111111<Void> future_taskCompleteds = CompletableFutureXXXXX111111111111111111111111.runAsync(() -> {
//			try {
//				List<TaskCompleted> os = business.entityManagerContainer()
//						.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, workCompleted.getJob()).stream()
//						.sorted(Comparator.comparing(TaskCompleted::getStartTime,
//								Comparator.nullsLast(Date::compareTo)))
//						.collect(Collectors.toList());
//				woTaskCompleteds.addAll(WoTaskCompleted.copier.copy(os));
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		});
//		CompletableFutureXXXXX111111111111111111111111<Void> future_reads = CompletableFutureXXXXX111111111111111111111111.runAsync(() -> {
//			try {
//				List<Read> os = business.entityManagerContainer()
//						.listEqual(Read.class, Read.job_FIELDNAME, workCompleted.getJob()).stream()
//						.sorted(Comparator.comparing(Read::getStartTime, Comparator.nullsLast(Date::compareTo)))
//						.collect(Collectors.toList());
//				t.getReadList().addAll(WoRead.copier.copy(os));
//				t.setCurrentReadIndex(-1);
//				for (int i = 0; i < t.getReadList().size(); i++) {
//					if (StringUtils.equals(t.getReadList().get(i).getPerson(),
//							effectivePerson.getDistinguishedName())) {
//						t.setCurrentReadIndex(i);
//						break;
//					}
//				}
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		});
//		CompletableFutureXXXXX111111111111111111111111<Void> future_readCompleteds = CompletableFutureXXXXX111111111111111111111111.runAsync(() -> {
//			try {
//				List<ReadCompleted> os = business.entityManagerContainer()
//						.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, workCompleted.getJob()).stream()
//						.sorted(Comparator.comparing(ReadCompleted::getStartTime,
//								Comparator.nullsLast(Date::compareTo)))
//						.collect(Collectors.toList());
//				woReadCompleteds.addAll(WoReadCompleted.copier.copy(os));
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		});
//		CompletableFutureXXXXX111111111111111111111111<Void> future_attachments = CompletableFutureXXXXX111111111111111111111111.runAsync(() -> {
//			try {
//				List<Attachment> os = business.entityManagerContainer()
//						.listEqual(Attachment.class, Attachment.job_FIELDNAME, workCompleted.getJob()).stream()
//						.sorted(Comparator.comparing(Attachment::getCreateTime, Comparator.nullsLast(Date::compareTo)))
//						.collect(Collectors.toList());
//				t.setAttachmentList(WoAttachment.copier.copy(os));
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		});
//		CompletableFutureXXXXX111111111111111111111111<Void> future_workLogs = CompletableFutureXXXXX111111111111111111111111.runAsync(() -> {
//			try {
//				List<WorkLog> os = business.entityManagerContainer()
//						.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, workCompleted.getJob()).stream()
//						.sorted(Comparator.comparing(WorkLog::getCreateTime, Comparator.nullsLast(Date::compareTo)))
//						.collect(Collectors.toList());
//				t.setWorkLogList(WoWorkLog.copier.copy(os));
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		});
//		CompletableFutureXXXXX111111111111111111111111<Void> future_data = CompletableFutureXXXXX111111111111111111111111.runAsync(() -> {
//			try {
//				t.setData(this.loadData(business, workCompleted));
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		});
//		CompletableFutureXXXXX111111111111111111111111<Application> future_application = CompletableFutureXXXXX111111111111111111111111.supplyAsync(() -> {
//			Application o = null;
//			try {
//				o = business.application().pick(workCompleted.getApplication());
//			} catch (Exception e) {
//				logger.error(e);
//			}
//			return o;
//		});
//		CompletableFutureXXXXX111111111111111111111111<Process> future_process = CompletableFutureXXXXX111111111111111111111111.supplyAsync(() -> {
//			Process o = null;
//			try {
//				o = business.process().pick(workCompleted.getProcess());
//			} catch (Exception e) {
//				logger.error(e);
//			}
//			return o;
//		});
//		CompletableFutureXXXXX111111111111111111111111<Long> future_reviewCount = CompletableFutureXXXXX111111111111111111111111.supplyAsync(() -> {
//			Long o = 0L;
//			try {
//				o = business.entityManagerContainer().countEqualAndEqual(Review.class, Review.person_FIELDNAME,
//						effectivePerson.getDistinguishedName(), Review.job_FIELDNAME, workCompleted.getJob());
//			} catch (Exception e) {
//				logger.error(e);
//			}
//			return o;
//		});
//
//		future_taskCompleteds.get(300, TimeUnit.SECONDS1111);
//		future_reads.get(300, TimeUnit.SECONDS1111);
//		future_readCompleteds.get(300, TimeUnit.SECONDS1111);
//		future_attachments.get(300, TimeUnit.SECONDS1111);
//		future_workLogs.get(300, TimeUnit.SECONDS1111);
//		future_data.get(300, TimeUnit.SECONDS1111);
//		application = future_application.get(300, TimeUnit.SECONDS1111);
//		process = future_process.get(300, TimeUnit.SECONDS1111);
//		reviewCount = future_reviewCount.get(300, TimeUnit.SECONDS1111);
//
//		t.setWorkCompleted(WoWorkCompleted.copier.copy(workCompleted));
//		this.arrangeWorkLog(business, t, woTaskCompleteds, woReadCompleteds);
//
//		WoControl control = new WoControl();
//		/** 工作是否可以打开(管理员 或 有task,taskCompleted,read,readCompleted,review的人) */
//		control.setAllowVisit(false);
//		/** 工作是否可以处理待阅(有read的人) */
//		control.setAllowReadProcessing(false);
//		/** 工作是否可删除(管理员 或者 此活动在流程设计中允许删除且当前待办人是文件的创建者) */
//		control.setAllowDelete(false);
//		/** 设置allowVisit */
//		if ((t.getCurrentReadIndex() > -1) || (woTaskCompleteds.stream()
//				.filter(o -> StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName())).count() > 0)
//				|| (woReadCompleteds.stream()
//						.filter(o -> StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName()))
//						.count() > 0)
//				|| (reviewCount > 0)) {
//			control.setAllowVisit(true);
//		} else if (effectivePerson.isPerson(workCompleted.getCreatorPerson())) {
//			control.setAllowVisit(true);
//		} else if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowVisit(true);
//		}
//		/* 设置allowReadProcessing */
//		if (t.getCurrentReadIndex() > -1) {
//			control.setAllowReadProcessing(true);
//		}
//		/* 设置 allowDelete */
//		if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowDelete(true);
//		}
//		t.setControl(control);
//		return t;
//	}
//
//	/* 如果通过已完成工作发送的已办有可能activityToken为空 */
//	private void arrangeWorkLog(Business business, AbstractWo wo, List<WoTaskCompleted> woTaskCompleteds,
//			List<WoReadCompleted> woReadCompleteds) throws Exception {
//		/* read 和 readCompleted 的 workLog o.getActivityToken 有可能为空 */
//		ListTools.groupStick(wo.getWorkLogList(),
//				woTaskCompleteds.stream().filter(o -> StringUtils.isNotEmpty(o.getActivityToken()))
//						.collect(Collectors.toList()),
//				WorkLog.fromActivityToken_FIELDNAME, TaskCompleted.activityToken_FIELDNAME, "taskCompletedList");
//		/* read 和 readCompleted 的 workLog o.getActivityToken 有可能为空 */
//		ListTools.groupStick(wo.getWorkLogList(),
//				wo.getReadList().stream().filter(o -> StringUtils.isNotEmpty(o.getActivityToken()))
//						.collect(Collectors.toList()),
//				WorkLog.fromActivityToken_FIELDNAME, Read.activityToken_FIELDNAME, "readList");
//		/* read 和 readCompleted 的 workLog o.getActivityToken 有可能为空 */
//		ListTools.groupStick(wo.getWorkLogList(),
//				woReadCompleteds.stream().filter(o -> StringUtils.isNotEmpty(o.getActivityToken()))
//						.collect(Collectors.toList()),
//				WorkLog.fromActivityToken_FIELDNAME, ReadCompleted.activityToken_FIELDNAME, "readCompletedList");
//		/* 将没有actiivityToken对应的已办或者待办绑定到最后一个end节点 */
//		List<String> tokens = ListTools.extractProperty(wo.getWorkLogList(), WorkLog.fromActivityToken_FIELDNAME,
//				String.class, true, true);
//		WoWorkLog latestEnd = wo.getWorkLogList().stream()
//				.filter(o -> Objects.equals(o.getArrivedActivityType(), ActivityType.end)).sorted(Comparator
//						.comparing(WorkLog::getArrivedTime, Comparator.nullsFirst(Date::compareTo)).reversed())
//				.findFirst().orElse(null);
//		if (null != latestEnd) {
//			latestEnd.getReadList()
//					.addAll(wo.getReadList().stream().filter(o -> !tokens.contains(o.getActivityToken())).sorted(
//							Comparator.comparing(Read::getStartTime, Comparator.nullsFirst(Date::compareTo)).reversed())
//							.collect(Collectors.toList()));
//			latestEnd.getReadCompletedList().addAll(woReadCompleteds.stream()
//					.filter(o -> !tokens.contains(o.getActivityToken())).sorted(Comparator
//							.comparing(ReadCompleted::getStartTime, Comparator.nullsFirst(Date::compareTo)).reversed())
//					.collect(Collectors.toList()));
//		}
//	}
}
