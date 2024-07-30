package com.x.processplatform.assemble.surface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.entity.ticket.Ticket;

public class WorkControlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkControlBuilder.class);

	private EffectivePerson effectivePerson;
	private Business business;
	private Work work;

	public WorkControlBuilder(EffectivePerson effectivePerson, Business business, Work work) {
		this.effectivePerson = effectivePerson;
		this.business = business;
		this.work = work;
	}

	// 是否可以管理
	private boolean ifAllowManage = false;
	// 是否可以看到
	private boolean ifAllowVisit = false;
	// 是否可以直接流转
	private boolean ifAllowProcessing = false;
	// 是否可以处理待阅
	private boolean ifAllowReadProcessing = false;
	// 是否可以保存数据
	private boolean ifAllowSave = false;
	// 是否可以重置处理人
	private boolean ifAllowReset = false;
	// 是否可以加签
	private boolean ifAllowAddTask = false;
	// 是否可以调度
	private boolean ifAllowReroute = false;
	// 是否可以删除
	private boolean ifAllowDelete = false;
	// 是否可以增加会签分支
	private boolean ifAllowAddSplit = false;
	// 是否可以召回
	private boolean ifAllowRetract = false;
	// 是否可以回滚
	private boolean ifAllowRollback = false;
	// 是否可以提醒
	private boolean ifAllowPress = false;
	// 是否可以待办挂起(暂停待办计时)
	private boolean ifAllowPause = false;
	// 是否可以取消待办挂起(恢复待办计时)
	private boolean ifAllowResume = false;
	// 是否可以退回
	private boolean ifAllowGoBack = false;
	// 是否可以终止
	private boolean ifAllowTerminate = false;

	public WorkControlBuilder enableAllowManage() {
		this.ifAllowManage = true;
		return this;
	}

	public WorkControlBuilder enableAllowVisit() {
		this.ifAllowVisit = true;
		return this;
	}

	public WorkControlBuilder enableAllowProcessing() {
		this.ifAllowProcessing = true;
		return this;
	}

	public WorkControlBuilder enableAllowReadProcessing() {
		this.ifAllowReadProcessing = true;
		return this;
	}

	public WorkControlBuilder enableAllowSave() {
		this.ifAllowSave = true;
		return this;
	}

	public WorkControlBuilder enableAllowReset() {
		this.ifAllowReset = true;
		return this;
	}

	public WorkControlBuilder enableAllowAddTask() {
		this.ifAllowAddTask = true;
		return this;
	}

	public WorkControlBuilder enableAllowReroute() {
		this.ifAllowReroute = true;
		return this;
	}

	public WorkControlBuilder enableAllowDelete() {
		this.ifAllowDelete = true;
		return this;
	}

	public WorkControlBuilder enableAllowAddSplit() {
		this.ifAllowAddSplit = true;
		return this;
	}

	public WorkControlBuilder enableAllowRetract() {
		this.ifAllowRetract = true;
		return this;
	}

	public WorkControlBuilder enableAllowRollback() {
		this.ifAllowRollback = true;
		return this;
	}

	public WorkControlBuilder enableAllowPress() {
		this.ifAllowPress = true;
		return this;
	}

	public WorkControlBuilder enableAllowPause() {
		this.ifAllowPause = true;
		return this;
	}

	public WorkControlBuilder enableAllowResume() {
		this.ifAllowResume = true;
		return this;
	}

	public WorkControlBuilder enableAllowGoBack() {
		this.ifAllowGoBack = true;
		return this;
	}

	public WorkControlBuilder enableAllowTerminate() {
		this.ifAllowTerminate = true;
		return this;
	}

	public WorkControlBuilder enableAll() {
		enableAllowManage();
		enableAllowVisit();
		enableAllowProcessing();
		enableAllowReadProcessing();
		enableAllowSave();
		enableAllowReset();
		enableAllowAddTask();
		enableAllowReroute();
		enableAllowDelete();
		enableAllowAddSplit();
		enableAllowRetract();
		enableAllowRollback();
		enableAllowPress();
		enableAllowPause();
		enableAllowResume();
		enableAllowGoBack();
		enableAllowTerminate();
		return this;
	}

	private Boolean canManage = null;

	/**
	 * 判断是否可以对应用或者流程管理,额外判断是否review有permissionWrite标志
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean canManage() throws Exception {
		if (null == canManage) {
			this.canManage = this.business.ifPersonCanManageApplicationOrProcess(this.effectivePerson,
					this.work.getApplication(), this.work.getProcess())
					|| this.business.ifPersonHasPermissionWriteReviewWithJob(this.effectivePerson, this.work.getJob());
		}
		return this.canManage;
	}

	private Boolean readable = null;

	private boolean readable() throws Exception {
		if (null == readable) {
			this.readable = ((!BooleanUtils.isTrue(Config.ternaryManagement().getSecurityClearanceEnable()))
					|| business.ifPersonHasSufficientSecurityClearance(effectivePerson.getDistinguishedName(),
							work.getObjectSecurityClearance()))
					&& (business.ifPersonHasTaskReadTaskCompletedReadCompletedReviewWithJob(
							effectivePerson.getDistinguishedName(), work.getJob())
							|| business.ifJobHasBeenCorrelation(effectivePerson.getDistinguishedName(), work.getJob()));
		}
		return this.readable;
	}

	// 初始值必须是null,用于判断hasTaskWithWork是否已经经过计算
	private Optional<Task> hasTaskWithWork = null;

	private Optional<Task> hasTaskWithWork() throws Exception {
		if (null == hasTaskWithWork) {
			this.hasTaskWithWork = business.ifPersonHasTaskWithWork(effectivePerson.getDistinguishedName(),
					work.getId());
		}
		return this.hasTaskWithWork;
	}

	private Boolean hasReadWithJob = null;

	private boolean hasReadWithJob() throws Exception {
		if (null == hasReadWithJob) {
			this.hasReadWithJob = business.ifPersonHasReadWithJob(effectivePerson.getDistinguishedName(),
					work.getJob());
		}
		return this.hasReadWithJob;
	}

	private Activity activity = null;

	private Activity activity() throws Exception {
		if (null == activity) {
			this.activity = business.getActivity(work);
		}
		return this.activity;
	}

	private Boolean hasTaskCompletedWithJob = null;

	private boolean hasTaskCompletedWithJob() throws Exception {
		if (null == hasTaskCompletedWithJob) {
			this.hasTaskCompletedWithJob = business
					.ifPersonHasTaskCompletedWithJob(effectivePerson.getDistinguishedName(), work.getJob());
		}
		return this.hasTaskCompletedWithJob;
	}

	private Boolean hasPauseTaskWithWork = null;

	private boolean hasPauseTaskWithWork() throws Exception {
		if (null == hasPauseTaskWithWork) {
			this.hasPauseTaskWithWork = business.ifPersonHasPauseTaskWithWork(effectivePerson.getDistinguishedName(),
					work.getId());
		}
		return this.hasPauseTaskWithWork;
	}

	private WorkLogTree workLogTree = null;

	private WorkLogTree workLogTree() throws Exception {
		if (null == this.workLogTree) {
			this.workLogTree = new WorkLogTree(business.entityManagerContainer().fetchEqual(WorkLog.class,
					WorkLogTree.RELY_WORKLOG_ITEMS, WorkLog.JOB_FIELDNAME, work.getJob()));
		}
		return this.workLogTree;
	}

	private Long taskCountWithWork = null;

	private Long taskCountWithWork() throws Exception {
		if (null == taskCountWithWork) {
			this.taskCountWithWork = business.entityManagerContainer().countEqual(Task.class, Task.work_FIELDNAME,
					work.getId());
		}
		return this.taskCountWithWork;
	}

	private Map<String, Boolean> hasTaskCompletedWithActivityToken = new HashMap<>();

	private boolean hasTaskCompletedWithActivityToken(EffectivePerson effectivePerson, Business business,
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

	public Control build() {
		Control control = new Control();
		if (null == work) {
			return control;
		}
		control.setWorkTitle(work.getTitle());
		control.setWorkJob(work.getJob());
		Arrays.<Pair<Boolean, Consumer<Control>>>asList(Pair.of(ifAllowManage, this::computeAllowManage),
				Pair.of(ifAllowVisit, this::computeAllowVisit),
				Pair.of(ifAllowProcessing, this::computeAllowProcessing),
				Pair.of(ifAllowReadProcessing, this::computeAllowReadProcessing),
				Pair.of(ifAllowSave, this::computeAllowSave), Pair.of(ifAllowReset, this::computeAllowReset),
				Pair.of(ifAllowAddTask, this::computeAllowAddTask), Pair.of(ifAllowReroute, this::computeAllowReroute),
				Pair.of(ifAllowDelete, this::computeAllowDelete), Pair.of(ifAllowAddSplit, this::computeAllowAddSplit),
				Pair.of(ifAllowRetract, this::computeAllowRetract),
				Pair.of(ifAllowRollback, this::computeAllowRollback), Pair.of(ifAllowPress, this::computeAllowPress),
				Pair.of(ifAllowPause, this::computeAllowPause), Pair.of(ifAllowResume, this::computeAllowResume),
				Pair.of(ifAllowGoBack, this::computeAllowGoBack),
				Pair.of(ifAllowTerminate, this::computeAllowTerminate)).stream().filter(Pair::first)
				.forEach(o -> o.second().accept(control));
		recalculate(work, control);
		return control;
	}

	private void computeAllowManage(Control control) {
		try {
			control.setAllowManage(canManage());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowVisit(Control control) {
		try {
			control.setAllowVisit(canManage() || readable());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowProcessing(Control control) {
		try {
			control.setAllowProcessing(hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowReadProcessing(Control control) {
		try {
			control.setAllowReadProcessing(hasReadWithJob());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowSave(Control control) {
		try {
			control.setAllowSave(canManage() || hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowReset(Control control) {
		try {
			control.setAllowReset(PropertyTools.getOrElse(activity(), Manual.allowReset_FIELDNAME, Boolean.class, false)
					&& hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowAddTask(Control control) {
		try {
			control.setAllowAddTask(
					PropertyTools.getOrElse(activity(), Manual.ALLOWADDTASK_FIELDNAME, Boolean.class, true)
							&& hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowReroute(Control control) {
		try {
			control.setAllowReroute(canManage()
					&& PropertyTools.getOrElse(activity(), Activity.allowReroute_FIELDNAME, Boolean.class, false));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 管理员可以删除,或者活动设置了可以删除&&有待办
	 *
	 * @param control
	 */
	private void computeAllowDelete(Control control) {
		try {
			control.setAllowDelete(
					(PropertyTools.getOrElse(activity(), Manual.allowDeleteWork_FIELDNAME, Boolean.class, false)
							&& (canManage() || hasTaskWithWork().isPresent())));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowAddSplit(Control control) {
		try {
			control.setAllowAddSplit(false);
			if (BooleanUtils
					.isTrue(PropertyTools.getOrElse(activity(), Manual.allowAddSplit_FIELDNAME, Boolean.class, false))
					&& BooleanUtils.isTrue(work.getSplitting())) {
				Node node = this.workLogTree().location(work);
				if (null != node) {
					computeAllowAddSplitLoopNode(control, work);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowAddSplitLoopNode(Control control, Work work) throws Exception {
		List<Node> list = this.workLogTree().nodes().stream()
				.filter(o -> Objects.equals(work.getSplitToken(), o.getWorkLog().getSplitToken())
						&& Objects.equals(ActivityType.split, o.getWorkLog().getFromActivityType()))
				.collect(Collectors.toList());
		for (Node n : list) {
			Nodes manuals = n.upTo(ActivityType.manual);
			for (Node m : manuals) {
				if (this.hasTaskCompletedWithActivityToken(effectivePerson, business,
						m.getWorkLog().getFromActivityToken())) {
					control.setAllowAddSplit(true);
					return;
				}
			}
		}
	}

//	private void computeAllowAddSplitLoopNode(Control control, Node node) {
//		Nodes nodes = new Nodes();
//		nodes.add(node);
//		for (int i = 0; i < work.getSplitTokenList().size(); i++) {
//			List<Node> temps = new ArrayList<>();
//			for (Node n : nodes) {
//				Nodes ups = n.upTo(ActivityType.split);
//				temps.addAll(ups);
//				for (Node u : ups) {
//					Nodes manuals = u.upTo(ActivityType.manual);
//					for (Node m : manuals) {
//						if (this.hasTaskCompletedWithActivityToken(effectivePerson, business,
//								m.getWorkLog().getFromActivityToken())) {
//							control.setAllowAddSplit(true);
//							return;
//						}
//					}
//				}
//			}
//			nodes.clear();
//			nodes.addAll(temps);
//		}
//	}

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
	private void computeAllowRetract(Control control) {
		try {
			control.setAllowRetract(false);
			if (BooleanUtils
					.isTrue(PropertyTools.getOrElse(activity(), Manual.allowRetract_FIELDNAME, Boolean.class, false))
					&& (business.entityManagerContainer().countEqualAndEqual(TaskCompleted.class,
							TaskCompleted.job_FIELDNAME, work.getJob(), TaskCompleted.activityToken_FIELDNAME,
							work.getActivityToken()) == 0)) {
				Node node = this.workLogTree().location(work);
				if (null != node) {
					Nodes ups = node.upTo(ActivityType.manual, ActivityType.agent, ActivityType.choice,
							ActivityType.delay, ActivityType.embed, ActivityType.invoke, ActivityType.parallel,
							ActivityType.split, ActivityType.publish);
					for (Node o : ups) {
						if (this.hasTaskCompletedWithActivityToken(effectivePerson, business,
								o.getWorkLog().getFromActivityToken())) {
							control.setAllowRetract(true);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowRollback(Control control) {
		try {
			control.setAllowRollback(canManage()
					&& PropertyTools.getOrElse(activity(), Manual.allowRollback_FIELDNAME, Boolean.class, false));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 条件为1.允许提醒,2.有已办,3.非只有当前人一条待办.
	 *
	 * @param control
	 */
	private void computeAllowPress(Control control) {
		try {
			boolean tag = PropertyTools.getOrElse(activity(), Manual.allowPress_FIELDNAME, Boolean.class, false)
					&& hasTaskCompletedWithJob() && (!((taskCountWithWork() == 1) && hasTaskWithWork().isPresent()));
			control.setAllowPress(tag);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowPause(Control control) {
		try {
			control.setAllowPause(false);
			if (PropertyTools.getOrElse(activity(), Manual.allowPause_FIELDNAME, Boolean.class, false)
					&& control.getAllowSave()) {
				// 如果已经处于挂起状态,那么允许恢复
				control.setAllowPause(!hasPauseTaskWithWork());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowResume(Control control) {
		try {
			control.setAllowResume(false);
			if (PropertyTools.getOrElse(activity(), Manual.allowPause_FIELDNAME, Boolean.class, false)
					&& control.getAllowSave()) {
				// 如果已经处于挂起状态,那么允许恢复
				control.setAllowResume(hasPauseTaskWithWork());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowGoBack(Control control) {
		try {
			control.setAllowGoBack(false);
			if (activity().getClass().isAssignableFrom(Manual.class)) {
				Manual manual = (Manual) activity;
				if (hasTaskWithWork().isPresent() && BooleanUtils.isNotFalse(manual.getAllowGoBack())) {
					Optional<Ticket> opt = work.getTickets().findTicketWithLabel(hasTaskWithWork.get().getLabel());
					if (opt.isPresent() && (BooleanUtils.isNotFalse(manual.getGoBackConfig().getMultiTaskEnable())
							|| ListUtils.intersection(
									work.getTickets().bubble().stream().map(Ticket::label).collect(Collectors.toList()),
									opt.get().fellow()).isEmpty())) {
						control.setAllowGoBack(true);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowTerminate(Control control) {
		try {
			control.setAllowTerminate(false);
			if (activity().getClass().isAssignableFrom(Manual.class)) {
				Manual manual = (Manual) activity;
				if (BooleanUtils.isTrue(manual.getAllowTerminate()) && (canManage() || hasTaskWithWork().isPresent())) {
					control.setAllowTerminate(true);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 在退回处理过程中如果有getGoBackStore说明下一步需要jump,那么禁用以下功能.
	 *
	 * @param wo
	 * @param work
	 */
	private void recalculate(Work work, Control ctrl) {
		if (null != work.getGoBackStore()) {
			recalculateHasGoBackStore(ctrl);
		}
	}

	private void recalculateHasGoBackStore(Control ctrl) {
		if (null != ctrl.getAllowAddTask()) {
			ctrl.setAllowAddTask(false);
		}
		if (null != ctrl.getAllowReset()) {
			ctrl.setAllowReset(false);
		}
		if (null != ctrl.getAllowPause()) {
			ctrl.setAllowPause(false);
		}
		if (null != ctrl.getAllowResume()) {
			ctrl.setAllowResume(false);
		}
		if (null != ctrl.getAllowAddSplit()) {
			ctrl.setAllowAddSplit(false);
		}
		if (null != ctrl.getAllowRetract()) {
			ctrl.setAllowRetract(false);
		}
		if (null != ctrl.getAllowGoBack()) {
			ctrl.setAllowGoBack(false);
		}
		if (null != ctrl.getAllowRollback()) {
			ctrl.setAllowRollback(false);
		}
	}
}
