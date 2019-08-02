package com.x.processplatform.service.processing.processor.manual;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.EmpowerLog;
import com.x.base.core.project.organization.Empower;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;
import com.x.base.core.project.utils.time.WorkTime;
import com.x.processplatform.core.entity.content.Hint;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.TranslateTaskIdentityTools;

public class ManualProcessor extends AbstractManualProcessor {

	private static Logger logger = LoggerFactory.getLogger(ManualProcessor.class);

	public ManualProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Manual manual) throws Exception {
		/*
		 * 先记录所有的待办人,必须在这里先进行计算,否则返回值中无法提示下一个处理人.
		 * 这里先计算处理人存放在manualTaskIdentityList中主要是为了脚本中可以预先知道可能的处理人进行业务处理
		 */
		List<String> identities = TranslateTaskIdentityTools.translate(aeiObjects, manual);
		if (identities.isEmpty()) {
			/* 如果活动没有找到任何可用的处理人,那么强制设置处理人为文档创建者,或者配置的 maintenanceIdentity */
			String effectiveCreatorIdentity = aeiObjects.business().organization().identity()
					.get(aeiObjects.getWork().getCreatorIdentity());
			if (StringUtils.isNotEmpty(effectiveCreatorIdentity)) {
				logger.info("人工活动到达未能找到指定的处理人, 标题:{}, id:{}, 强制指定处理人为活动的创建身份:{}.", aeiObjects.getWork().getTitle(),
						aeiObjects.getWork().getId(), effectiveCreatorIdentity);
				identities.add(effectiveCreatorIdentity);
			} else {
				effectiveCreatorIdentity = aeiObjects.business().organization().identity()
						.get(Config.processPlatform().getMaintenanceIdentity());
				if (StringUtils.isNotEmpty(effectiveCreatorIdentity)) {
					logger.info("人工活动到达未能找到指定的处理人, 标题:{}, id:{}, 强制指定处理人为系统维护身份:{}.", aeiObjects.getWork().getTitle(),
							aeiObjects.getWork().getId(), effectiveCreatorIdentity);
					identities.add(effectiveCreatorIdentity);
				} else {
					throw new ExceptionExpectedEmpty(aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(),
							manual.getName(), manual.getId());
				}
			}
			aeiObjects.createHint(Hint.EmptyTaskIdentityOnManual(aeiObjects.getWork(), manual));
		}
		aeiObjects.getWork().setManualTaskIdentityList(identities);
		/* 查找是否有passSameTarget设置 */
		Route passSameTargetRoute = aeiObjects.getRoutes().stream()
				.filter(o -> BooleanUtils.isTrue(o.getPassSameTarget())).findFirst().orElse(null);
		/* 如果有passSameTarget,有到达ArriveWorkLog,不是调度到这个节点的 */
		if ((null != passSameTargetRoute) && ((null != aeiObjects.getArriveWorkLog(aeiObjects.getWork())))
				&& (BooleanUtils.isNotTrue(aeiObjects.getWork().getForceRouteArriveCurrentActivity()))) {
			logger.debug("pass same target work:{}.", aeiObjects.getWork());
			WorkLog rollbackWorkLog = findPassSameTargetWorkLog(aeiObjects);
			logger.debug("pass same target workLog:{}.", rollbackWorkLog);
			if (null != rollbackWorkLog) {
				final String arriveActivityToken = rollbackWorkLog.getArrivedActivityToken();
				aeiObjects.getTaskCompleteds().stream()
						.filter(o -> aeiObjects.getWork().getManualTaskIdentityList().contains(o.getIdentity())
								&& StringUtils.equals(o.getActivityToken(), arriveActivityToken))
						.forEach(o -> {
							TaskCompleted obj = new TaskCompleted(aeiObjects.getWork(), manual, passSameTargetRoute, o);
							try {
								obj.setProcessingType(ProcessingType.sameTarget);
								obj.setRouteName(passSameTargetRoute.getName());
								Date now = new Date();
								obj.setStartTime(now);
								obj.setStartTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
								obj.setCompletedTime(now);
								obj.setCompletedTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
								obj.setDuration(0L);
								obj.setExpired(false);
								obj.setExpireTime(null);
								obj.setTask(null);
								obj.setLatest(true);
								aeiObjects.getCreateTaskCompleteds().add(obj);
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
			}
		}
		return aeiObjects.getWork();
	}

	private WorkLog findPassSameTargetWorkLog(AeiObjects aeiObjects) throws Exception {
		WorkLogTree tree = new WorkLogTree(aeiObjects.getWorkLogs());
		List<WorkLog> parents = tree.parents(aeiObjects.getArriveWorkLog(aeiObjects.getWork()));
		logger.debug("pass same target rollback parents:{}.", parents);
		WorkLog workLog = null;
		for (WorkLog o : parents) {
			if (Objects.equals(ActivityType.manual, o.getArrivedActivityType())) {
				workLog = o;
				break;
			} else if (Objects.equals(ActivityType.choice, o.getArrivedActivityType())) {
				continue;
			} else if (Objects.equals(ActivityType.agent, o.getArrivedActivityType())) {
				continue;
			} else if (Objects.equals(ActivityType.invoke, o.getArrivedActivityType())) {
				continue;
			} else if (Objects.equals(ActivityType.service, o.getArrivedActivityType())) {
				continue;
			} else {
				break;
			}
		}
		logger.debug("pass same target find workLog:{}.", workLog);
		return workLog;
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Manual manual) throws Exception {
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<Work> results = new ArrayList<>();
		boolean passThrough = false;
		/*
		 * 如果采用List<String> identities =
		 * TranslateTaskIdentityTools.translate(aeiObjects, manual)
		 * 将全部重新计算人员,那么重置处理人将被去掉
		 */
		TaskIdentities taskIdentities = new TaskIdentities(
				aeiObjects.business().organization().identity().list(aeiObjects.getWork().getManualTaskIdentityList()));
		if (taskIdentities.isEmpty()) {
			List<String> identities = TranslateTaskIdentityTools.translate(aeiObjects, manual);
			taskIdentities.addIdentities(identities);
			logger.info("工作设置的处理人已经全部无效,重新计算当前环节所有处理人进行处理,标题:{}, id:{}, 设置的处理人:{}.", aeiObjects.getWork().getTitle(),
					aeiObjects.getWork().getId(), identities);
		}
		if (taskIdentities.isEmpty()) {
			/* 如果活动没有找到任何可用的处理人,那么强制设置处理人为文档创建者,或者配置的 maintenanceIdentity */
			String effectiveCreatorIdentity = aeiObjects.business().organization().identity()
					.get(aeiObjects.getWork().getCreatorIdentity());
			if (StringUtils.isNotEmpty(effectiveCreatorIdentity)) {
				logger.info("人工活动执行未找到指定的处理身份, 标题:{}, id:{}, 强制指定为工作的创建身份:{}.", aeiObjects.getWork().getTitle(),
						aeiObjects.getWork().getId(), effectiveCreatorIdentity);
				taskIdentities.addIdentity(effectiveCreatorIdentity);
			} else {
				effectiveCreatorIdentity = aeiObjects.business().organization().identity()
						.get(Config.processPlatform().getMaintenanceIdentity());
				if (StringUtils.isNotEmpty(effectiveCreatorIdentity)) {
					logger.info("人工活动执行未找到指定的处理身份, 标题:{}, id:{}, 强制指定为系统维护身份:{}.", aeiObjects.getWork().getTitle(),
							aeiObjects.getWork().getId(), effectiveCreatorIdentity);
					taskIdentities.addIdentity(effectiveCreatorIdentity);
				} else {
					throw new ExceptionExpectedEmpty(aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(),
							manual.getName(), manual.getId());
				}
			}
			// identities.add(effectiveCreatorIdentity);
			aeiObjects.createHint(Hint.EmptyTaskIdentityOnManual(aeiObjects.getWork(), manual));
		}

		aeiObjects.getWork().setManualTaskIdentityList(taskIdentities.identities());

		List<Empower> trusts = aeiObjects.business().organization().empower().listWithIdentityObject(
				aeiObjects.getWork().getApplication(), aeiObjects.getWork().getProcess(),
				aeiObjects.getWork().getManualTaskIdentityList());

		taskIdentities.update(trusts);

		switch (manual.getManualMode()) {
		case single:
			passThrough = this.single(aeiObjects, manual, taskIdentities);
			break;
		case parallel:
			passThrough = this.parallel(aeiObjects, manual, taskIdentities);
			break;
		case queue:
			passThrough = this.queue(aeiObjects, manual, taskIdentities);
			break;
		case grab:
			passThrough = this.single(aeiObjects, manual, taskIdentities);
			break;
		default:
			throw new ExceptionManualModeError(manual.getId());
		}
		if (passThrough) {
			results.add(aeiObjects.getWork());
		}
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Manual manual) throws Exception {
//		for (Task o : aeiObjects.getCreateTasks()) {
//			TaskMessage message = new TaskMessage(o.getPerson(), o.getWork(), o.getId());
//			logger.debug("concrete task message:{}.", XGsonBuilder.toText(message));
//			Collaboration.send(message);
//		}
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<Route> results = new ArrayList<>();
		/* 仅有单条路由 */
		if (aeiObjects.getRoutes().size() == 1) {
			results.add(aeiObjects.getRoutes().get(0));
		} else if (aeiObjects.getRoutes().size() > 1) {
			/* 存在多条路由 */
			List<TaskCompleted> taskCompletedList = aeiObjects.getTaskCompleteds().stream()
					.filter(o -> StringUtils.equals(o.getActivityToken(), aeiObjects.getWork().getActivityToken())
							&& aeiObjects.getWork().getManualTaskIdentityList().contains(o.getIdentity())
							&& (!Objects.equals(o.getProcessingType(), ProcessingType.retract))
							&& (!Objects.equals(o.getProcessingType(), ProcessingType.reset)))
					.collect(Collectors.toList());
			String name = this.choiceRouteName(taskCompletedList, aeiObjects.getRoutes());
			for (Route o : aeiObjects.getRoutes()) {
				if (o.getName().equalsIgnoreCase(name)) {
					results.add(o);
					break;
				}
			}
		}
		return results;
	}

	/* 通过已办存根选择某条路由 */
	private String choiceRouteName(List<TaskCompleted> list, List<Route> routes) throws Exception {
		String result = "";
		List<String> os = new ArrayList<>();
		ListTools.trim(list, false, false).stream()
				.filter(o -> (!Objects.equals(o.getProcessingType(), ProcessingType.reset))
						&& (!Objects.equals(o.getProcessingType(), ProcessingType.retract)))
				.forEach(o -> {
					/* 跳过重置处理人的路由 */
					os.add(o.getRouteName());
				});
		/* 进行独占路由的判断 */
		Route soleRoute = routes.stream().filter(o -> BooleanUtils.isTrue(o.getSole())).findFirst().orElse(null);
		if ((null != soleRoute) && os.contains(soleRoute.getName())) {
			result = soleRoute.getName();
		} else {
			/* 进行默认的策略,选择占比多的 */
			result = ListTools.maxCountElement(os);
		}
//		for (TaskCompleted o : ListTools.trim(list, false, false)) {
//			if ((!o.getProcessingType().equals(ProcessingType.reset))
//					&& (!o.getProcessingType().equals(ProcessingType.retract))) {
//				/** 跳过重置处理人的路由 */
//				os.add(o.getRouteName());
//			}
//		}
		if (StringUtils.isEmpty(result)) {
			throw new ExceptionChoiceRouteNameError(
					ListTools.extractProperty(list, JpaObject.id_FIELDNAME, String.class, false, false));
		}
		return result;
	}

	private boolean single(AeiObjects aeiObjects, Manual manual, TaskIdentities taskIdentities) throws Exception {
		boolean passThrough = false;
		/* 找到所有的已办 */
		Long count = aeiObjects.getTaskCompleteds().stream().filter(o -> {
			if (StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getActivityToken())
					&& (!o.getProcessingType().equals(ProcessingType.retract))
					&& (!o.getProcessingType().equals(ProcessingType.reset))) {
				return true;
			} else {
				return false;
			}
		}).count();
		if (count > 0) {
			/* 已经确定要通过此节点,清除可能是多余的待办 */
			aeiObjects.getTasks().stream().filter(o -> {
				return StringUtils.equals(aeiObjects.getWork().getId(), o.getWork());
			}).forEach(o -> {
				aeiObjects.deleteTask(o);
			});
			/** 所有预计的处理人中已经有已办,这个环节已经产生了已办，可以离开换个环节。 */
			passThrough = true;
		} else {
			/* 取到期望的待办人员，由于要进行处理需要转换成可读写List */
			if (ListTools.isEmpty(taskIdentities)) {
				throw new ExceptionExpectedEmpty(aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(),
						manual.getName(), manual.getId());
			}
			/* 删除多余的待办 */
			aeiObjects.getTasks().stream()
					.filter(o -> StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getActivityToken())
							&& (!ListTools.contains(taskIdentities.identities(), o.getIdentity())))
					.forEach(o -> {
						aeiObjects.deleteTask(o);
					});
			/* 将待办已经产生的人从预期值中删除 */
			aeiObjects.getTasks().stream()
					.filter(o -> StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getActivityToken())
							&& (ListTools.contains(taskIdentities.identities(), o.getIdentity())))
					.forEach(o -> {
						taskIdentities.removeIdentity(o.getIdentity());
					});
			/* 这里剩余的应该是没有生成待办的人员 */
			if (!taskIdentities.isEmpty()) {
				for (TaskIdentity taskIdentity : taskIdentities) {
					aeiObjects.createTask(this.createTask(aeiObjects, manual, taskIdentity));
				}
			}
		}
		return passThrough;
	}

	private boolean parallel(AeiObjects aeiObjects, Manual manual, TaskIdentities taskIdentities) throws Exception {
		boolean passThrough = false;
		/** 取得本环节已经处理的已办 */
		List<TaskCompleted> taskCompleteds = this.listEffectiveTaskCompleted(aeiObjects.getWork(), taskIdentities);
		if (ListTools.isEmpty(taskIdentities)) {
			if (ListTools.isNotEmpty(taskCompleteds)) {
				/** 预计的处理人全部不存在,且已经有人处理过了 */
				passThrough = true;
			} else {
				/** 即没有预计的处理人也没有已经办理过的记录那么只能报错 */
				throw new ExceptionExpectedEmpty(aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(),
						manual.getName(), manual.getId());
			}
		}
		/* 将已经处理的人从期望值中移除 */
		aeiObjects.getTaskCompleteds().stream().filter(o -> {
			return StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getActivityToken());
		}).forEach(o -> {
			taskIdentities.removeIdentity(o.getIdentity());
		});
		/* 清空可能的多余的待办 */
		aeiObjects.getTasks().stream().filter(o -> {
			return StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getActivityToken())
					&& (!ListTools.contains(taskIdentities.identities(), o.getIdentity()));
		}).forEach(o -> {
			aeiObjects.deleteTask(o);
		});
		if (taskIdentities.isEmpty()) {
			/* 所有人已经处理完成。 */
			passThrough = true;
		} else {
			passThrough = false;
			/* 先清空已经有待办的身份 */
			aeiObjects.getTasks().stream().filter(o -> {
				return StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getActivityToken());
			}).forEach(o -> {
				taskIdentities.removeIdentity(o.getIdentity());
			});
			/* 这里剩余的应该是没有生成待办的人员 */
			if (!taskIdentities.isEmpty()) {
				for (TaskIdentity taskIdentity : taskIdentities) {
					aeiObjects.createTask(this.createTask(aeiObjects, manual, taskIdentity));
				}
			}
		}
		return passThrough;
	}

	private boolean queue(AeiObjects aeiObjects, Manual manual, TaskIdentities taskIdentities) throws Exception {
		boolean passThrough = false;
		if (taskIdentities.isEmpty()) {
			throw new ExceptionExpectedEmpty(aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(),
					manual.getName(), manual.getId());
		}
		List<TaskCompleted> done = this.listEffectiveTaskCompleted(aeiObjects.getWork(), taskIdentities);
		/** 将已经处理的人从期望值中移除 */
		for (TaskCompleted o : done) {
			taskIdentities.removeIdentity(o.getIdentity());
		}
		if (taskIdentities.isEmpty()) {
			/** 所有人已经处理完成。 */
			passThrough = true;
		} else {
			passThrough = false;
			TaskIdentity taskIdentity = taskIdentities.get(0);
			/** 还有人没有处理，开始判断待办,取到本环节的所有待办,理论上只能有一条待办 */
			List<Task> existed = this.entityManagerContainer().fetch(
					this.business().task().listWithActivityToken(aeiObjects.getWork().getActivityToken()), Task.class,
					ListTools.toList(Task.identity_FIELDNAME));
			/** 理论上只能有一条待办 */
			boolean find = false;
			for (Task _o : existed) {
				if (!StringUtils.equals(_o.getIdentity(), taskIdentity.getIdentity())) {
					this.entityManagerContainer().delete(Task.class, _o.getId());
					MessageFactory.task_delete(_o);
				} else {
					find = true;
				}
			}
			/** 当前处理人没有待办 */
			if (!find) {
				aeiObjects.createTask(this.createTask(aeiObjects, manual, taskIdentity));
			}
		}
		return passThrough;
	}

	/** 所有有效的已办,去除 reset,retract */
	private List<TaskCompleted> listEffectiveTaskCompleted(Work work, TaskIdentities taskIdentities) throws Exception {
		List<String> ids = this.business().taskCompleted().listWithActivityTokenInIdentityList(work.getActivityToken(),
				taskIdentities.identities());
		List<TaskCompleted> list = new ArrayList<>();
		for (TaskCompleted o : this.business().entityManagerContainer().list(TaskCompleted.class, ids)) {
			if ((!o.getProcessingType().equals(ProcessingType.retract))
					&& (!o.getProcessingType().equals(ProcessingType.reset))) {
				list.add(o);
			}
		}
		return list;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Manual manual) throws Exception {
	}

	private void calculateExpire(AeiObjects aeiObjects, Manual manual, Task task) throws Exception {
		if (null != manual.getTaskExpireType()) {
			switch (manual.getTaskExpireType()) {
			case never:
				this.expireNever(task);
				break;
			case appoint:
				this.expireAppoint(manual, task);
				break;
			case script:
				this.expireScript(aeiObjects, manual, task);
				break;
			default:
				break;
			}
		}
		/** 如果work有截至时间 */
		if (null != aeiObjects.getWork().getExpireTime()) {
			if (null == task.getExpireTime()) {
				task.setExpireTime(aeiObjects.getWork().getExpireTime());
			} else {
				if (task.getExpireTime().after(aeiObjects.getWork().getExpireTime())) {
					task.setExpireTime(aeiObjects.getWork().getExpireTime());
				}
			}
		}
		/** 已经有过期时间了,那么设置催办时间 */
		if (null != task.getExpireTime()) {
			task.setUrgeTime(DateUtils.addHours(task.getExpireTime(), -2));
		} else {
			task.setExpired(false);
			task.setUrgeTime(null);
			task.setUrged(false);
		}
	}

	/*
	 * 从不过期
	 */
	private void expireNever(Task task) {
		task.setExpireTime(null);
	}

	private void expireAppoint(Manual manual, Task task) throws Exception {
		if (BooleanUtils.isTrue(manual.getTaskExpireWorkTime())) {
			this.expireAppointWorkTime(task, manual);
		} else {
			this.expireAppointNaturalDay(task, manual);
		}
	}

	private void expireAppointWorkTime(Task task, Manual manual) throws Exception {
		Integer m = 0;
		WorkTime wt = new WorkTime();
		if (NumberTools.greaterThan(manual.getTaskExpireDay(), 0)) {
			m += manual.getTaskExpireDay() * wt.minutesOfWorkDay();
		}
		if (NumberTools.greaterThan(manual.getTaskExpireHour(), 0)) {
			m += manual.getTaskExpireHour() * 60;
		}
		if (m > 0) {
			Date expire = wt.forwardMinutes(new Date(), m);
			task.setExpireTime(expire);
		} else {
			task.setExpireTime(null);
		}
	}

	private void expireAppointNaturalDay(Task task, Manual manual) throws Exception {
		Integer m = 0;
		if (NumberTools.greaterThan(manual.getTaskExpireDay(), 0)) {
			m += manual.getTaskExpireDay() * 60 * 24;
		}
		if (NumberTools.greaterThan(manual.getTaskExpireHour(), 0)) {
			m += manual.getTaskExpireHour() * 60;
		}
		if (m > 0) {
			Calendar cl = Calendar.getInstance();
			cl.add(Calendar.MINUTE, m);
			task.setExpireTime(cl.getTime());
		} else {
			task.setExpireTime(null);
		}
	}

	private void expireScript(AeiObjects aeiObjects, Manual manual, Task task) throws Exception {
		ExpireScriptResult expire = new ExpireScriptResult();
		ScriptHelper sh = ScriptHelperFactory.create(aeiObjects, new BindingPair("task", task),
				new BindingPair("expire", expire));
		sh.eval(aeiObjects.getWork().getApplication(), manual.getTaskExpireScript(), manual.getTaskExpireScriptText());

		if (NumberTools.greaterThan(expire.getWorkHour(), 0)) {
			Integer m = 0;
			m += expire.getWorkHour() * 60;
			if (m > 0) {
				WorkTime wt = new WorkTime();
				task.setExpireTime(wt.forwardMinutes(new Date(), m));
			} else {
				task.setExpireTime(null);
			}
		} else if (NumberTools.greaterThan(expire.getHour(), 0)) {
			Integer m = 0;
			m += expire.getHour() * 60;
			if (m > 0) {
				Calendar cl = Calendar.getInstance();
				cl.add(Calendar.MINUTE, m);
				task.setExpireTime(cl.getTime());
			} else {
				task.setExpireTime(null);
			}
		} else if (null != expire.getDate()) {
			task.setExpireTime(expire.getDate());
		} else {
			task.setExpireTime(null);
		}
	}

	private Task createTask(AeiObjects aeiObjects, Manual manual, TaskIdentity taskIdentity) throws Exception {
		String person = aeiObjects.business().organization().person().getWithIdentity(taskIdentity.getIdentity());
		String unit = aeiObjects.business().organization().unit().getWithIdentity(taskIdentity.getIdentity());
		Task task = new Task(aeiObjects.getWork(), taskIdentity.getIdentity(), person, unit,
				taskIdentity.getFromIdentity(), new Date(), null, aeiObjects.getRoutes(), manual.getAllowRapid());
		/* 是第一条待办,进行标记 */
		if (ListTools.isEmpty(aeiObjects.getTaskCompleteds())) {
			task.setFirst(true);
		} else {
			task.setFirst(false);
		}
		this.calculateExpire(aeiObjects, manual, task);
		if (StringUtils.isNotEmpty(taskIdentity.getFromIdentity())) {
			aeiObjects.business().organization().empowerLog()
					.log(this.createEmpowerLog(aeiObjects.getWork(), taskIdentity));
			task.setTrustIdentity(taskIdentity.getFromIdentity());
		}
		return task;
	}

	private EmpowerLog createEmpowerLog(Work work, TaskIdentity taskIdentity) {
		EmpowerLog empowerLog = new EmpowerLog().setApplication(work.getApplication())
				.setApplicationAlias(work.getApplicationAlias()).setApplicationName(work.getApplicationName())
				.setProcess(work.getProcess()).setProcessAlias(work.getProcessAlias())
				.setProcessName(work.getProcessName()).setTitle(work.getTitle()).setWork(work.getId())
				.setJob(work.getJob()).setFromIdentity(taskIdentity.getFromIdentity())
				.setToIdentity(taskIdentity.getIdentity()).setActivity(work.getActivity())
				.setActivityAlias(work.getActivityAlias()).setActivityName(work.getActivityName())
				.setTrustTime(new Date());
		return empowerLog;
	}

	public class ExpireScriptResult {
		Integer hour;
		Integer workHour;
		Date date;

		public Integer getHour() {
			return hour;
		}

		public void setHour(Integer hour) {
			this.hour = hour;
		}

		public Integer getWorkHour() {
			return workHour;
		}

		public void setWorkHour(Integer workHour) {
			this.workHour = workHour;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public void setDate(String str) {
			try {
				this.date = DateTools.parse(str);
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}
}