package com.x.processplatform.assemble.surface;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Manual;

public class WorkControlBuilder {

	public WorkControlBuilder build() {
		return new WorkControlBuilder();
	}

	public Control of(Business business, EffectivePerson effectivePerson, Work work) {
		Control control = new Control();
		if (null == work) {
			return control;
		}
		Activity activity = business.getActivity(work);
		boolean canManage = business.canManageApplicationOrProcess(effectivePerson, work.getApplication(),
				work.getProcess());
		boolean readable = business.ifPersonHasTaskReadTaskCompletedReadCompletedReviewWithJob(
				effectivePerson.getDistinguishedName(), work.getJob())
				|| business.ifJobHasBeenCorrelation(effectivePerson.getDistinguishedName(), work.getJob());

		// 是否可以看到
		control.setAllowVisit(canManage || readable);
		// 是否可以直接流转
		wo.setAllowProcessing(this.hasTaskWithWork(business, effectivePerson, work.getId()));
		// 是否可以处理待阅
		wo.setAllowReadProcessing(this.hasReadWithJob(business, effectivePerson, work.getJob()));
		// 是否可以保存数据
		wo.setAllowSave(
				this.canManageApplicationOrProcess(business, effectivePerson, work.getApplication(), work.getProcess())
						|| this.hasTaskWithWork(business, effectivePerson, work.getId()));
		// 是否可以重置处理人
		wo.setAllowReset(PropertyTools.getOrElse(activity, Manual.allowReset_FIELDNAME, Boolean.class, false)
				&& this.hasTaskWithWork(business, effectivePerson, work.getId()));
		// 是否可以加签,默认可以加签
		wo.setAllowAddTask(PropertyTools.getOrElse(activity, Manual.ALLOWADDTASK_FIELDNAME, Boolean.class, true)
				&& wo.getAllowSave());
		// 是否可以调度
		wo.setAllowReroute(PropertyTools.getOrElse(activity, Activity.allowReroute_FIELDNAME, Boolean.class, false)
				&& this.canManageApplicationOrProcess(business, effectivePerson, work.getApplication(),
						work.getProcess()));
		// 是否可以删除
		wo.setAllowDelete(PropertyTools.getOrElse(activity, Manual.allowDeleteWork_FIELDNAME, Boolean.class, false)
				&& wo.getAllowSave());
		// 是否可以挂起待办,暂停待办计时
		if (PropertyTools.getOrElse(activity, Manual.allowPause_FIELDNAME, Boolean.class, false) && wo.getAllowSave()) {
			// 如果已经处于挂起状态,那么允许恢复
			if (this.hasPauseTaskWithWork(business, effectivePerson, work.getId())) {
				wo.setAllowResume(true);
			} else {
				wo.setAllowPause(true);
			}
		}

		// 是否可以增加会签分支
		setAllowAddSplit(effectivePerson, business, activity, work, wo);
		// 是否可以召回
		setAllowRetract(business, effectivePerson, work, wo, activity);
		// 是否可以退回
		setAllowGoBack(business, work, wo, activity);
		// 是否可以回滚
		wo.setAllowRollback(PropertyTools.getOrElse(activity, Manual.allowRollback_FIELDNAME, Boolean.class, false)
				&& this.canManageApplicationOrProcess(business, effectivePerson, work.getApplication(),
						work.getProcess()));
		// 是否可以提醒
		wo.setAllowPress(PropertyTools.getOrElse(activity, Manual.allowPress_FIELDNAME, Boolean.class, false)
				&& this.hasTaskCompletedWithJob(business, effectivePerson, work.getJob()));

		// 相互之间有影响的重新计算.
		recalculate(wo, work);
		return control;
	}

}
