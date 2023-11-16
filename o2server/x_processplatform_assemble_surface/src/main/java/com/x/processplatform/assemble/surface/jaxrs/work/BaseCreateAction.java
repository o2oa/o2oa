package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.Organization;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;

import io.swagger.v3.oas.annotations.media.Schema;

class BaseCreateAction extends BaseAction {

	protected void processingCreateWork(String workId) throws Exception {
		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", workId, "processing"), null);
	}

	protected String createWork(String processId, JsonElement jsonElement) throws Exception {
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "process", processId), jsonElement)
				.getData(WoId.class).getId();
	}

	/**
	 * 如果不是草稿那么需要进行设置
	 * 
	 * @param identity
	 * @param workId
	 * @param title
	 * @param parentWork
	 * @throws Exception
	 */
	protected void updateWork(String identity, String workId, String title, String parentWork) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Organization organization = business.organization();
			emc.beginTransaction(Work.class);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			work.setTitle(title);
			// 写入父work标识
			if (StringUtils.isNotBlank(parentWork)) {
				work.setParentWork(parentWork);
			}
			work.setCreatorIdentity(identity);
			work.setCreatorPerson(organization.person().getWithIdentity(identity));
			work.setCreatorUnit(organization.unit().getWithIdentity(identity));
			if (StringUtils.isNotEmpty(work.getCreatorUnit())) {
				Unit unit = organization.unit().getObject(work.getCreatorUnit());
				work.setCreatorUnitLevelName(unit.getLevelName());
			}
			emc.commit();
		}
	}

	/**
	 * 标志工作跳过新建检查
	 * 
	 * @param workId
	 * @throws Exception
	 */
	protected void updateWorkDraftCheck(String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			emc.beginTransaction(Work.class);
			work.setDataChanged(true);
			emc.commit();
		}
	}

	/**
	 * 拼装返回结果
	 *
	 * @param effectivePerson
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	protected List<Wo> assemble(EffectivePerson effectivePerson, String workId) throws Exception {
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			// 如果work是从开始->执行任务->结束,这里work已经结束那么有可能这里已经没有work了.
			if (null != work) {
				List<String> ids = business.workLog()
						.listWithFromActivityTokenForwardNotConnected(work.getActivityToken());
				// 先取得没有结束的WorkLog
				List<WorkLog> list = emc.list(WorkLog.class, ids);
				wos = this.refercenceWorkLog(business, list);
				// 标识当前用户的待办
				for (Wo o : wos) {
					o.setCurrentTaskIndex(-1);
					for (int i = 0; i < o.getTaskList().size(); i++) {
						WoTask t = o.getTaskList().get(i);
						if (StringUtils.equals(effectivePerson.getDistinguishedName(), t.getPerson())) {
							o.setCurrentTaskIndex(i);
						}
					}
				}
			}
		}
		return wos;
	}

	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreate$Wo")
	public static class Wo extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号.")
		@Schema(description = "排序号.")
		private Long rank;

		@FieldDescribe("已办对象.")
		@Schema(description = "已办对象.")
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		@FieldDescribe("待办对象.")
		@Schema(description = "待办对象.")
		private List<WoTask> taskList = new ArrayList<>();

		@FieldDescribe("当前待办序号.")
		@Schema(description = "当前待办序号.")
		private Integer currentTaskIndex;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
		}

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

	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreate$WoTask")
	public static class WoTask extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreate$WoTaskCompleted")
	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		public static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);

		private Long rank;

		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	protected List<Wo> refercenceWorkLog(Business business, List<WorkLog> list) throws Exception {
		List<Wo> os = new ArrayList<>();
		for (WorkLog o : list) {
			Wo wo = Wo.copier.copy(o);
			if (BooleanUtils.isNotTrue(o.getConnected())) {
				this.referenceTask(business, wo);
			}
			this.referenceTaskCompleted(business, wo);
			os.add(wo);
		}
		return os.stream()
				.sorted(Comparator.comparing(Wo::getArrivedTime, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());
	}

	protected String decideCreatorIdentity(Business business, EffectivePerson effectivePerson, String identity)
			throws Exception {
		if (TokenType.cipher.equals(effectivePerson.getTokenType())) {
			return business.organization().identity().get(identity);
		} else {
			List<String> identities = business.organization().identity()
					.listWithPerson(effectivePerson.getDistinguishedName());
			if (ListTools.isEmpty(identities)) {
				throw new ExceptionNoneIdentity(effectivePerson.getDistinguishedName());
			}
			if (identities.contains(identity)) {
				return identity;
			} else {
				return identities.get(0);
			}
		}
	}

	private void referenceTask(Business business, Wo wo) throws Exception {
		List<String> ids = business.task().listWithActivityToken(wo.getFromActivityToken());
		List<WoTask> list = WoTask.copier.copy(business.entityManagerContainer().list(Task.class, ids));
		wo.setTaskList(list.stream()
				.sorted(Comparator.comparing(WoTask::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList()));
	}

	private void referenceTaskCompleted(Business business, Wo wo) throws Exception {
		List<String> ids = business.taskCompleted().listWithActivityToken(wo.getFromActivityToken());
		List<WoTaskCompleted> list = WoTaskCompleted.copier
				.copy(business.entityManagerContainer().list(TaskCompleted.class, ids));
		wo.setTaskCompletedList(list.stream().sorted(Comparator.comparing(WoTaskCompleted::getCompletedTime,
				Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList()));
	}

}
