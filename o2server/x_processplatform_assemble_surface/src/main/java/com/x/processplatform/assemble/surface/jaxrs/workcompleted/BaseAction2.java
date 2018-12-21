package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

abstract class BaseAction2 extends StandardJaxrsAction {

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

	protected <T extends AbstractWo> T get(Business business, EffectivePerson effectivePerson,
			WorkCompleted workCompleted, Class<T> cls) throws Exception {
		T t = cls.newInstance();
		t.setWorkCompleted(WoWorkCompleted.copier.copy(workCompleted));
		t.setAttachmentList(this.listAttachment(business, workCompleted));
		t.setWorkLogList(this.referenceWorkLog(business, workCompleted));
		t.setData(this.loadData(business, workCompleted));
		WoControl control = business.getControl(effectivePerson, workCompleted, WoControl.class);
		t.setControl(control);
		t.setReadList(this.listRead(business, workCompleted));
		return t;
	}

	private List<WoAttachment> listAttachment(Business business, WorkCompleted workCompleted) throws Exception {
		List<WoAttachment> list = new ArrayList<>();
		List<Attachment> os = business.entityManagerContainer().list(Attachment.class,
				business.attachment().listWithJob(workCompleted.getJob()));
		list = WoAttachment.copier.copy(os);
		list = business.attachment().sort(list);
		return list;
	}

	private List<WoWorkLog> referenceWorkLog(Business business, WorkCompleted workCompleted) throws Exception {
		List<WoWorkLog> os = WoWorkLog.copier.copy(business.workLog().listWithJobObject(workCompleted.getJob()));
		List<WoTaskCompleted> _taskCompleteds = WoTaskCompleted.copier
				.copy(business.taskCompleted().listWithJobObject(workCompleted.getJob()));
		List<WoTask> _tasks = WoTask.copier.copy(business.task().listWithJobObject(workCompleted.getJob()));
		os = business.workLog().sort(os);

		Map<String, List<WoTaskCompleted>> _map_taskCompleteds = _taskCompleteds.stream()
				.collect(Collectors.groupingBy(o -> o.getActivityToken()));

		Map<String, List<WoTask>> _map_tasks = _tasks.stream()
				.collect(Collectors.groupingBy(o -> o.getActivityToken()));

		for (WoWorkLog o : os) {
			List<WoTaskCompleted> _parts_taskCompleted = _map_taskCompleteds.get(o.getFromActivityToken());
			o.setTaskCompletedList(new ArrayList<WoTaskCompleted>());
			if (!ListTools.isEmpty(_parts_taskCompleted)) {
				for (WoTaskCompleted _taskCompleted : business.taskCompleted().sort(_parts_taskCompleted)) {
					o.getTaskCompletedList().add(_taskCompleted);
					if (_taskCompleted.getProcessingType().equals(ProcessingType.retract)) {
						TaskCompleted _retract = new TaskCompleted();
						o.copyTo(_retract);
						_retract.setRouteName("撤回");
						_retract.setOpinion("撤回");
						_retract.setStartTime(_retract.getRetractTime());
						_retract.setCompletedTime(_retract.getRetractTime());
						o.getTaskCompletedList().add(WoTaskCompleted.copier.copy(_retract));
					}
				}
			}
			List<WoTask> _parts_tasks = _map_tasks.get(o.getFromActivityToken());
			o.setTaskList(new ArrayList<WoTask>());
			if (!ListTools.isEmpty(_parts_tasks)) {
				o.setTaskList(business.task().sort(_parts_tasks));
			}
			//
			// o.setTaskList(WoTask.copier.copy(business.task().listTask(o)));
			// o.setTaskCompletedList(WoTaskCompleted.copier.copy(business.taskCompleted().listTaskCompleted(o)));
		}
		return os;
	}

	private List<WoRead> listRead(Business business, WorkCompleted workCompleted) throws Exception {
		List<Read> os = business.read().listWithWorkCompletedObject(workCompleted);
		os = os.stream().sorted(Comparator.comparing(Read::getStartTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return WoRead.copier.copy(os);
	}

	public static class AbstractWo extends GsonPropertyObject {

		@FieldDescribe("业务数据")
		private Data data;

		@FieldDescribe("已完成工作")
		private WoWorkCompleted workCompleted;

		@FieldDescribe("待阅")
		private List<WoRead> readList = new ArrayList<>();

		@FieldDescribe("附件")
		private List<WoAttachment> attachmentList = new ArrayList<>();

		@FieldDescribe("工作日志")
		private List<WoWorkLog> workLogList = new ArrayList<>();

		@FieldDescribe("权限")
		private WoControl control;

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

		private List<WoTask> taskList;

		private List<WoTaskCompleted> taskCompletedList;

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

	public static class WoControl extends WorkCompletedControl {

	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = 8714459358196550018L;

		public static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class,
				JpaObject.singularAttributeField(Form.class, true, true), null);
	}
}
