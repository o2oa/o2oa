package com.x.processplatform.assemble.surface.jaxrs.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGetWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetWorkOrWorkCompleted.class);

	private Boolean canManageApplicationOrProcess = null;

	private WorkLogTree workLogTree = null;

	private Boolean hasReadWithJob = null;

	private Boolean hasTaskWithWork = null;

	private Boolean hasTaskCompletedWithJob = null;

	private Boolean hasPauseTaskWithWork = null;

	private Map<String, Boolean> hasTaskCompletedWithActivityToken = new HashMap<>();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CompletableFuture<Wo> getFuture = this.getFuture(effectivePerson, workOrWorkCompleted);
		CompletableFuture<Boolean> checkControlFuture = this.checkControlFuture(effectivePerson, workOrWorkCompleted);
		result.setData(getFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
		if (BooleanUtils
				.isFalse(checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}
		return result;
	}

	private CompletableFuture<Wo> getFuture(EffectivePerson effectivePerson, String workOrWorkCompleted) {
		return CompletableFuture.supplyAsync(() -> {
			Wo wo = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(workOrWorkCompleted, Work.class);
				if (null != work) {
					wo = this.work(business, effectivePerson, work);
				} else {
					WorkCompleted workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
					if (null != workCompleted) {
						wo = this.workCompleted(business, effectivePerson, workCompleted);
					}
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return wo;
		}, ThisApplication.threadPool());
	}

	private Wo workCompleted(Business business, EffectivePerson effectivePerson, WorkCompleted workCompleted)
			throws Exception {
		Wo wo = new Wo();
		wo.setAllowVisit(true);
		wo.setAllowReadProcessing(this.hasReadWithJob(business, effectivePerson, workCompleted.getJob()));
		wo.setAllowRollback(
				this.canManageApplicationOrProcess(business, effectivePerson, workCompleted.getApplication(),
						workCompleted.getProcess()) || BooleanUtils.isTrue(workCompleted.getAllowRollback()));
		return wo;
	}

	private Wo work(Business business, EffectivePerson effectivePerson, Work work) throws Exception {

		Wo wo = new Wo();

		Activity activity = business.getActivity(work);

		// 是否可以看到
		wo.setAllowVisit(true);
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
		// 是否可以回滚
		wo.setAllowRollback(PropertyTools.getOrElse(activity, Manual.allowRollback_FIELDNAME, Boolean.class, false)
				&& this.canManageApplicationOrProcess(business, effectivePerson, work.getApplication(),
						work.getProcess()));
		// 是否可以提醒,如果有待办排除,不需要自己提醒自己
		wo.setAllowPress(PropertyTools.getOrElse(activity, Manual.allowPress_FIELDNAME, Boolean.class, false)
				&& this.hasTaskCompletedWithJob(business, effectivePerson, work.getJob())
				&& (!wo.getAllowProcessing()));
		// 是否可以看到
		wo.setAllowVisit(true);
		return wo;

	}

	/**
	 * 是否可以召回有三个判断点 1.活动环节设置允许召回 2.多人活动(串并行)中没有人已经处理过,也就是没有当前活动的已办
	 * 3.回溯活动如果经过一些非人工环节那么也可以召回.
	 * 
	 * @param business
	 * @param effectivePerson
	 * @param work
	 * @param wo
	 * @param activity
	 * @throws Exception
	 */

	private void setAllowRetract(Business business, EffectivePerson effectivePerson, Work work, Wo wo,
			Activity activity) throws Exception {
		if (BooleanUtils.isTrue(PropertyTools.getOrElse(activity, Manual.allowRetract_FIELDNAME, Boolean.class, false))
				&& (business.entityManagerContainer().countEqualAndEqualAndNotEqual(TaskCompleted.class,
						TaskCompleted.job_FIELDNAME, work.getJob(), TaskCompleted.activityToken_FIELDNAME,
						work.getActivityToken(), TaskCompleted.joinInquire_FIELDNAME, false) == 0)) {
			Node node = this.workLogTree(business, work.getJob()).location(work);
			if (null != node) {
				Nodes ups = node.upTo(ActivityType.manual, ActivityType.agent, ActivityType.choice, ActivityType.delay,
						ActivityType.embed, ActivityType.invoke, ActivityType.parallel, ActivityType.split,
						ActivityType.publish);
				for (Node o : ups) {
					if (this.hasTaskCompletedWithActivityToken(business, effectivePerson,
							o.getWorkLog().getFromActivityToken())) {
						wo.setAllowRetract(true);
						break;
					}
				}
			}
		}
	}

	private void setAllowAddSplit(EffectivePerson effectivePerson, Business business, Activity activity, Work work,
			Wo wo) throws Exception {
		if (BooleanUtils.isTrue(PropertyTools.getOrElse(activity, Manual.allowAddSplit_FIELDNAME, Boolean.class, false))
				&& BooleanUtils.isTrue(work.getSplitting())) {
			Node node = this.workLogTree(business, work.getJob()).location(work);
			Nodes nodes = new Nodes();
			nodes.add(node);
			for (int i = 0; i < work.getSplitTokenList().size(); i++) {
				List<Node> temps = new ArrayList<>();
				for (Node n : nodes) {
					Nodes ups = n.upTo(ActivityType.split);
					temps.addAll(ups);
					for (Node u : ups) {
						Nodes manuals = u.upTo(ActivityType.manual);
						for (Node m : manuals) {
							if (this.hasTaskCompletedWithActivityToken(business, effectivePerson,
									m.getWorkLog().getFromActivityToken())) {
								wo.setAllowAddSplit(true);
								break;
							}
						}
					}
				}
				nodes.clear();
				nodes.addAll(temps);
			}
		}
	}

	private boolean hasTaskCompletedWithActivityToken(Business business, EffectivePerson effectivePerson,
			String activityToken) {
		return this.hasTaskCompletedWithActivityToken.computeIfAbsent(activityToken, k -> {
			try {
				return business.entityManagerContainer().countEqualAndEqual(TaskCompleted.class,
						TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName(),
						TaskCompleted.activityToken_FIELDNAME, activityToken) > 0;
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return false;
		});
	}

	private boolean hasTaskWithWork(Business business, EffectivePerson effectivePerson, String work) throws Exception {
		if (null == this.hasTaskWithWork) {
			this.hasTaskWithWork = business.entityManagerContainer().countEqualAndEqual(Task.class,
					Task.person_FIELDNAME, effectivePerson.getDistinguishedName(), Task.work_FIELDNAME, work) > 0;
		}
		return this.hasTaskWithWork;
	}

	private boolean hasPauseTaskWithWork(Business business, EffectivePerson effectivePerson, String work)
			throws Exception {
		if (null == this.hasPauseTaskWithWork) {
			this.hasPauseTaskWithWork = business.entityManagerContainer().countEqualAndEqualAndEqual(Task.class,
					Task.person_FIELDNAME, effectivePerson.getDistinguishedName(), Task.work_FIELDNAME, work,
					Task.pause_FIELDNAME, true) > 0;
		}
		return this.hasPauseTaskWithWork;
	}

	private boolean hasTaskCompletedWithJob(Business business, EffectivePerson effectivePerson, String job)
			throws Exception {
		if (null == this.hasTaskCompletedWithJob) {
			this.hasTaskCompletedWithJob = business.entityManagerContainer().countEqualAndEqual(TaskCompleted.class,
					TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME,
					job) > 0;
		}
		return this.hasTaskCompletedWithJob;
	}

	private boolean hasReadWithJob(Business business, EffectivePerson effectivePerson, String job) throws Exception {
		if (null == this.hasReadWithJob) {
			this.hasReadWithJob = business.entityManagerContainer().countEqualAndEqual(Read.class,
					Read.person_FIELDNAME, effectivePerson.getDistinguishedName(), Read.job_FIELDNAME, job) > 0;
		}
		return this.hasReadWithJob;
	}

	private WorkLogTree workLogTree(Business business, String job) throws Exception {
		if (null == this.workLogTree) {
			this.workLogTree = new WorkLogTree(business.entityManagerContainer().fetchEqual(WorkLog.class,
					WorkLogTree.RELY_WORKLOG_ITEMS, WorkLog.JOB_FIELDNAME, job));
		}
		return this.workLogTree;
	}

	private boolean canManageApplicationOrProcess(Business business, EffectivePerson effectivePerson,
			String application, String process) throws Exception {
		if (null == canManageApplicationOrProcess) {
			this.canManageApplicationOrProcess = business.canManageApplicationOrProcess(effectivePerson, application,
					process);
		}
		return this.canManageApplicationOrProcess;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.control.ActionGetWorkOrWorkCompleted$Wo")
	public static class Wo extends AbstractControl {

		private static final long serialVersionUID = -4677744478291468477L;

	}

}