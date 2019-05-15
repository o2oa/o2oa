package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;

class ActionReroute extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String activityId, ActivityType activityType)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			Control control = this.getControl(business, effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowReroute())) {
				throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} not allow reroute.");
			}
			Activity activity = business.getActivity(work);
			Activity destinationActivity = business.getActivity(activityId, activityType);
			/* 如果是管理员那么就不判断这里的条件了 */
			if (effectivePerson.isNotManager() && (!BooleanUtils.isTrue(activity.getAllowReroute()))) {
				throw new ExceptionRerouteDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						destinationActivity.getName());
			}
			if (!StringUtils.equals(work.getProcess(), activity.getProcess())) {
				throw new ExceptionProcessNotMatch();
			}
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("work", work.getId(), "reroute", "activity", destinationActivity.getId()),
					null);
			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Control extends GsonPropertyObject {
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

	}

	private Control getControl(Business business, EffectivePerson effectivePerson, Work work) throws Exception {
		Activity activity = business.getActivity(work);
		List<Task> taskList = business.task().listWithWorkObject(work);
		SortTools.asc(taskList, "startTime");
		Task task = null;
		for (int i = 0; i < taskList.size(); i++) {
			Task o = taskList.get(i);
			if (StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName())) {
				task = o;
				break;
			}
		}
		List<Read> readList = business.read().listWithWorkObject(work);
		SortTools.asc(readList, "startTime");
		Read read = null;
		for (int i = 0; i < readList.size(); i++) {
			Read o = readList.get(i);
			if (StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName())) {
				read = o;
				break;
			}
		}
		Application application = business.application().pick(work.getApplication());
		Process process = business.process().pick(work.getProcess());
		Long taskCompletedCount = business.taskCompleted()
				.countWithPersonWithWork(effectivePerson.getDistinguishedName(), work);
		Long readCompletedCount = business.readCompleted()
				.countWithPersonWithWork(effectivePerson.getDistinguishedName(), work);
		Long reviewCount = business.review().countWithPersonWithWork(effectivePerson.getDistinguishedName(), work);
		Control control = new Control();
		/* 工作是否可以打开(管理员 或 有task,taskCompleted,read,readCompleted,review的人) */
		control.setAllowVisit(false);
		/* 工作是否可以流转(有task的人) */
		control.setAllowProcessing(false);
		/* 工作是否可以处理待阅(有read的人) */
		control.setAllowReadProcessing(false);
		/* 工作是否可保存(管理员 或者 有本人的task) */
		control.setAllowSave(false);
		/* 工作是否可重置(有本人待办 并且 活动设置允许重置 */
		control.setAllowReset(false);
		/* 工作是否可以撤回(当前人是上一个处理人 并且 还没有其他人处理过) */
		control.setAllowRetract(false);
		/* 工作是否可调度(管理员 并且 此活动在流程设计中允许调度) */
		control.setAllowReroute(false);
		/* 工作是否可删除(管理员 或者 此活动在流程设计中允许删除且当前待办人是文件的创建者) */
		control.setAllowDelete(false);
		/* 设置allowVisit */
		if ((null != task) || (null != read) || (taskCompletedCount > 0) || (readCompletedCount > 0)
				|| (reviewCount > 0)) {
			control.setAllowVisit(true);
		} else if (effectivePerson.isPerson(work.getCreatorPerson())) {
			control.setAllowVisit(true);
		} else if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowVisit(true);
		}
		/* 设置allowProcessing */
		if (null != task) {
			control.setAllowProcessing(true);
		}
		/* 设置allowReadProcessing */
		if (null != read) {
			control.setAllowReadProcessing(true);
		}
		/* 设置 allowSave */
		if (null != task) {
			control.setAllowSave(true);
		} else if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowSave(true);
		}
		/* 设置 allowReset */
		if (Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(((Manual) activity).getAllowReset()) && null != task) {
			control.setAllowReset(true);
		}
		/* 设置 allowRetract */
		if (Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(((Manual) activity).getAllowRetract())) {
			/* 标志文件还没有处理过 */
			if (0 == business.taskCompleted().countWithPersonWithActivityToken(effectivePerson.getDistinguishedName(),
					work.getActivityToken())) {
				/* 找到到达当前活动的workLog */
				WorkLog workLog = business.workLog().getWithArrivedActivityTokenObject(work.getActivityToken());
				if (null != workLog) {
					/* 查找上一个环节的已办,如果只有一个,且正好是当前人的,那么可以召回 */
					List<TaskCompleted> taskCompletedList = business.taskCompleted()
							.listWithActivityTokenObject(workLog.getFromActivityToken());
					if (taskCompletedList.size() == 1 && StringUtils.equals(effectivePerson.getDistinguishedName(),
							taskCompletedList.get(0).getPerson())) {
						control.setAllowRetract(true);
					}
				}
			}
		}
		/* 设置 allowReroute */
		if (effectivePerson.isManager()) {
			/** 管理员可以调度 */
			control.setAllowReroute(true);
		} else if (business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.ProcessPlatformManager)) {
			/** 有流程管理角色的可以 */
			control.setAllowReroute(true);
		} else if (BooleanUtils.isTrue(activity.getAllowReroute())) {
			/** 如果活动设置了可以调度 */
			if ((null != process) && effectivePerson.isPerson(process.getControllerList())) {
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
				&& BooleanUtils.isTrue(((Manual) activity).getAllowDeleteWork())) {
			if (null != task && StringUtils.equals(work.getCreatorPerson(), effectivePerson.getDistinguishedName())) {
				control.setAllowDelete(true);
			}
		}
		return control;
	}

}
