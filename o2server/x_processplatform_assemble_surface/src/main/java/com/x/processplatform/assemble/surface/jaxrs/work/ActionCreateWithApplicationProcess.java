package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.organization.core.express.Organization;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

/*
 * 根据应用名称和流程名称进行创建,和直接用process创建基本相同
 * */
class ActionCreateWithApplicationProcess extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreateWithApplicationProcess.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag, String processFlag,
			JsonElement jsonElement) throws Exception {
		Audit audit = logger.audit(effectivePerson);
		/* 新建工作id */
		String workId = "";
		/* 已存在草稿id */
		String lastestWorkId = "";
		String identity = null;
		List<Wo> wos = new ArrayList<>();
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			identity = this.decideCreatorIdentity(business, effectivePerson, wi);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			Process process = business.process().pick(application, processFlag);
			if (null == process) {
				throw new ExceptionProcessNotExist(processFlag);
			}
			List<String> roles = business.organization().role().listWithPerson(effectivePerson);
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(), application.getId());
			}
			if (BooleanUtils.isTrue(wi.getLatest())) {
				/* 判断是否是要直接打开之前创建的草稿,草稿的判断标准:有待办无任何已办 */
				lastestWorkId = this.latest(business, process, identity);
				workId = lastestWorkId;
			}
			if (StringUtils.isEmpty(workId)) {
				WoId woId = ThisApplication.context().applications()
						.postQuery(x_processplatform_service_processing.class,
								"work/process/" + URLEncoder.encode(process.getId(), DefaultCharset.name), wi.getData())
						.getData(WoId.class);
				workId = woId.getId();
			}
		}
		/* 设置Work信息 */
		if (BooleanUtils.isFalse(wi.getLatest()) || (StringUtils.isEmpty(lastestWorkId))) {
			/* 如果不是草稿那么需要进行设置 */
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Organization organization = business.organization();
				emc.beginTransaction(Work.class);
				Work work = emc.find(workId, Work.class);
				if (null == work) {
					throw new ExceptionWorkNotExist(workId);
				}
				work.setTitle(wi.getTitle());
				work.setCreatorIdentity(identity);
				work.setCreatorPerson(organization.person().getWithIdentity(identity));
				work.setCreatorUnit(organization.unit().getWithIdentity(identity));
				if (StringUtils.isNotEmpty(work.getCreatorUnit())) {
					Unit unit = organization.unit().getObject(work.getCreatorUnit());
					work.setCreatorUnitLevelName(unit.getLevelName());
				}
				emc.commit();
			}
			/* 驱动工作 */
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					"work/" + URLEncoder.encode(workId, DefaultCharset.name) + "/processing", null);
		} else {
			/* 如果是草稿,准备后面的直接打开 */
			workId = lastestWorkId;
		}
		/* 拼装返回结果 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			List<String> ids = business.workLog().listWithFromActivityTokenForwardNotConnected(work.getActivityToken());
			/* 先取得没有结束的WorkLog */
			List<WorkLog> list = emc.list(WorkLog.class, ids);
			wos = this.refercenceWorkLog(business, list);
			/* 标识当前用户的待办 */
			for (Wo o : wos) {
				o.setCurrentTaskIndex(-1);
				for (int i = 0; i < o.getTaskList().size(); i++) {
					WoTask t = o.getTaskList().get(i);
					if (StringUtils.equals(effectivePerson.getDistinguishedName(), t.getPerson())) {
						o.setCurrentTaskIndex(i);
					}
				}
			}
			audit.log(null, "填单");
		}
		result.setData(wos);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("直接打开指定人员已经有的草稿,草稿判断:工作没有已办,只有一条此人的待办.")
		private Boolean latest;

		@FieldDescribe("标题.")
		private String title;

		@FieldDescribe("启动人员身份.")
		private String identity;

		@FieldDescribe("工作数据.")
		private JsonElement data;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public Boolean getLatest() {
			return latest;
		}

		public void setLatest(Boolean latest) {
			this.latest = latest;
		}

	}

	public static class Wo extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号")
		private Long rank;

		@FieldDescribe("已办对象")
		private List<WoTaskCompleted> taskCompletedList;

		@FieldDescribe("待办对象")
		private List<WoTask> taskList;

		@FieldDescribe("当前待办序号")
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

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private WorkControl control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public WorkControl getControl() {
			return control;
		}

		public void setControl(WorkControl control) {
			this.control = control;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		public static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);

		private Long rank;

		private WorkControl control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public WorkControl getControl() {
			return control;
		}

		public void setControl(WorkControl control) {
			this.control = control;
		}

	}

	private List<Wo> refercenceWorkLog(Business business, List<WorkLog> list) throws Exception {
		List<Wo> os = new ArrayList<>();
		for (WorkLog o : list) {
			Wo wo = Wo.copier.copy(o);
			if (BooleanUtils.isNotTrue(o.getConnected())) {
				this.referenceTask(business, wo);
			} else {
				/** 已经完成的不会有待办，返回一个空数组 */
				wo.setTaskList(new ArrayList<WoTask>());
			}
			this.referenceTaskCompleted(business, wo);
			os.add(wo);
		}
		SortTools.asc(os, false, "arrivedTime");
		return os;
	}

	private String decideCreatorIdentity(Business business, EffectivePerson effectivePerson, Wi wi) throws Exception {
		if (TokenType.cipher.equals(effectivePerson.getTokenType())) {
			return business.organization().identity().get(wi.getIdentity());
		} else if (StringUtils.isNotEmpty(wi.getIdentity())) {
			List<String> identities = business.organization().identity()
					.listWithPerson(effectivePerson.getDistinguishedName());
			if (ListTools.isEmpty(identities)) {
				throw new ExceptionNoneIdentity(effectivePerson.getDistinguishedName());
			} else if (identities.size() == 1) {
				return identities.get(0);
			} else {
				/* 有多个身份需要逐一判断是否包含. */
				for (String o : identities) {
					if (StringUtils.equals(o, wi.getIdentity())) {
						return o;
					}
				}
			}
		} else {
			List<String> list = business.organization().identity()
					.listWithPerson(effectivePerson.getDistinguishedName());
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		throw new Exception("decideCreatorIdentity error:" + wi.toString());
	}

	private void referenceTask(Business business, Wo wo) throws Exception {
		List<String> ids = business.task().listWithActivityToken(wo.getFromActivityToken());
		List<WoTask> list = WoTask.copier.copy(business.entityManagerContainer().list(Task.class, ids));
		SortTools.asc(list, false, "startTime");
		wo.setTaskList(list);
	}

	private void referenceTaskCompleted(Business business, Wo wo) throws Exception {
		List<String> ids = business.taskCompleted().listWithActivityToken(wo.getFromActivityToken());
		List<WoTaskCompleted> list = WoTaskCompleted.copier
				.copy(business.entityManagerContainer().list(TaskCompleted.class, ids));
		Collections.sort(list, new Comparator<WoTaskCompleted>() {
			public int compare(WoTaskCompleted o1, WoTaskCompleted o2) {
				return ObjectUtils.compare(o1.getCompletedTime(), o2.getCompletedTime(), true);
			}
		});
		/* 补充召回 */
		List<WoTaskCompleted> results = new ArrayList<>();
		for (WoTaskCompleted o : list) {
			results.add(o);
			if (o.getProcessingType().equals(ProcessingType.retract)) {
				WoTaskCompleted retract = new WoTaskCompleted();
				o.copyTo(retract);
				retract.setRouteName("撤回");
				retract.setOpinion("撤回");
				retract.setStartTime(retract.getRetractTime());
				retract.setCompletedTime(retract.getRetractTime());
				results.add(retract);
			}
		}
		wo.setTaskCompletedList(results);
	}

}