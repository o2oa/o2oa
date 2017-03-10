package com.x.processplatform.service.processing.processor.manual;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.StringTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkLog_;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class ManualProcessor extends AbstractManualProcessor {

	private static Logger logger = LoggerFactory.getLogger(ManualProcessor.class);

	public ManualProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work, Data data,
			Manual manual) throws Exception {
		/** 先记录所有的待办人,必须在这里先进行计算,否则返回值中无法提示下一个处理人. */
		List<String> identities = TranslateTaskIdentityTools.translate(this.business(), attributes, work, data, manual);
		/* 检查翻译出来的人员是否存在 */
		identities = this.business().organization().identity().check(identities);
		if (identities.isEmpty()) {
			/* 如果活动没有找到任何可用的处理人,那么强制设置处理人为文档创建者 */
			logger.info(
					"manual arrvie processing, work title:{}, id:{}, actvity name:{}, id:{}, process name:{}, id{}, has none task identity. force set task identity:{}.",
					work.getTitle(), work.getId(), manual.getName(), manual.getId(), work.getProcessName(),
					work.getProcess(), work.getCreatorIdentity());
			identities.add(work.getCreatorIdentity());
			this.business().work().addHint(work, "在[" + manual.getName() + "]环节没有找到任何可用的处理人,强制设置处理人为创建者.");
		}
		work.setManualTaskIdentityList(identities);
		/** 如果启用了passSameTarget 那么开始执行 */
		List<Route> routes = this.business().element().listRouteWithManual(manual.getId());
		Route route = this.passSameTarget_route(routes);
		if (null != route) {
			this.passSameTarget(configurator, attributes, work, data, manual, route);
		}
		return work;
	}

	@Override
	protected List<Work> executing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Manual manual) throws Exception {
		List<Work> results = new ArrayList<>();
		boolean passThrough = false;
		this.entityManagerContainer().beginTransaction(Task.class);
		if (!passThrough) {
			switch (manual.getManualMode()) {
			case single:
				passThrough = this.single(manual, work, data, attributes);
				break;
			case parallel:
				passThrough = this.parallel(manual, work, data, attributes);
				break;
			case queue:
				passThrough = this.queue(manual, work, data, attributes);
				break;
			default:
				throw new Exception("unknown manualMode:" + manual.getManualMode());
			}
		}
		if (passThrough) {
			/* 已经确定要通过此节点,清除可能是多余的待办 */
			this.entityManagerContainer().delete(Task.class, this.business().task().listWithWork(work.getId()));
			results.add(work);
		}
		return results;
	}

	@Override
	protected List<Route> inquiring(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Manual manual, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		/* 仅有单条路由 */
		if (routes.size() == 1) {
			results.add(routes.get(0));
		} else if (routes.size() > 1) {
			/* 存在多条路由 */
			List<TaskCompleted> taskCompletedList = this.listEffectiveTaskCompleted(work);
			String name = this.choiceRouteName(taskCompletedList);
			for (Route o : routes) {
				if (o.getName().equalsIgnoreCase(name)) {
					results.add(o);
					break;
				}
			}
		}
		return results;
	}

	/* 通过已办存根选择某条路由 */
	private String choiceRouteName(List<TaskCompleted> list) throws Exception {
		String result = "";
		List<String> os = new ArrayList<>();
		for (TaskCompleted o : ListTools.trim(list, false, false)) {
			if ((!o.getProcessingType().equals(ProcessingType.reset))
					&& (!o.getProcessingType().equals(ProcessingType.retract))) {
				/* 跳过重置处理人的路由 */
				os.add(o.getRouteName());
			}
		}
		result = ListTools.maxCountElement(os);
		if (StringUtils.isEmpty(result)) {
			throw new Exception("can not choice routeName.");
		}
		return result;
	}

	private boolean single(Manual manual, Work work, Data data, ProcessingAttributes attributes) throws Exception {
		boolean passThrough = false;
		/** 找到所有的已办 */
		List<TaskCompleted> list = listEffectiveTaskCompleted(work);
		if (!list.isEmpty()) {
			/* 所有预计的处理人中已经有已办,这个环节已经产生了已办，可以离开换个环节。 */
			passThrough = true;
		} else {
			passThrough = false;
			/* 取到期望的待办人员，由于要进行处理需要转换成可读写List */
			List<String> expected = this.business().organization().identity().check(work.getManualTaskIdentityList());
			if (expected.isEmpty() || StringUtils.isBlank(StringUtils.join(expected, ""))) {
				throw new Exception("expected is empty.");
			}
			List<Task> existed = this.entityManagerContainer().fetchAttribute(
					this.business().task().listWithActivityToken(work.getActivityToken()), Task.class, "identity");
			for (Task o : existed) {
				if (!expected.remove(o.getIdentity())) {
					/* 删除多余待办 */
					this.entityManagerContainer().delete(Task.class, o.getId());
				}
			}
			/* 这里剩余的应该是没有生成待办的人员 */
			if (!expected.isEmpty()) {
				/* 可选择路由的名称列表 */
				List<String> routeList = new ArrayList<>();
				List<String> routeNameList = new ArrayList<>();
				for (Route o : this.business().element().listRouteWithManual(manual.getId())) {
					routeList.add(o.getId());
					routeNameList.add(o.getName());
				}
				for (String str : expected) {
					Task task = this.createTask(this.business(), manual, work, attributes, data, str);
					task.setRouteList(routeList);
					task.setRouteNameList(routeNameList);
					this.entityManagerContainer().persist(task, CheckPersistType.all);
					/* 创建提醒 */
					this.concreteTaskMessageToQueue(task);
				}
			}
		}
		return passThrough;
	}

	private boolean parallel(Manual manual, Work work, Data data, ProcessingAttributes attributes) throws Exception {
		boolean passThrough = false;
		/* 取到期望的待办人员，由于要进行处理需要转换成可读写List */
		List<String> expected = this.business().organization().identity().check(work.getManualTaskIdentityList());
		if (expected.isEmpty() || StringUtils.isBlank(StringUtils.join(expected, ""))) {
			throw new Exception("expected is empty.");
		}
		/* 取得本环节已经处理的已办 */
		List<TaskCompleted> done = this.listEffectiveTaskCompleted(work);
		/* 将已经处理的人从期望值中移除 */
		for (TaskCompleted o : done) {
			expected.remove(o.getIdentity());
		}
		if (expected.isEmpty()) {
			/* 所有人已经处理完成。 */
			passThrough = true;
		} else {
			passThrough = false;
			/* 还有人没有处理，开始判断待办,取到本环节的所有待办 */
			List<Task> existed = this.entityManagerContainer().fetchAttribute(
					this.business().task().listWithActivityToken(work.getActivityToken()), Task.class, "identity");
			for (Task o : existed) {
				if (!expected.remove(o.getIdentity())) {
					/* 删除多余待办 */
					this.entityManagerContainer().delete(Task.class, o.getId());
				}
			}
			/* 这里剩余的应该是没有生成待办的人员 */
			if (!expected.isEmpty()) {
				/* 可选择路由的名称列表 */
				List<String> routeList = new ArrayList<>();
				List<String> routeNameList = new ArrayList<>();
				for (Route o : this.business().element().listRouteWithManual(manual.getId())) {
					routeList.add(o.getId());
					routeNameList.add(o.getName());
				}
				for (String str : expected) {
					Task task = this.createTask(this.business(), manual, work, attributes, data, str);
					task.setRouteList(routeList);
					task.setRouteNameList(routeNameList);
					this.entityManagerContainer().persist(task, CheckPersistType.all);
					/* 创建提醒 */
					this.concreteTaskMessageToQueue(task);
				}
			}
		}
		return passThrough;
	}

	private boolean queue(Manual manual, Work work, Data data, ProcessingAttributes attributes) throws Exception {
		boolean passThrough = false;
		/* 取到期望的待办人员，由于要进行处理需要转换成可读写List */
		List<String> expected = this.business().organization().identity().check(work.getManualTaskIdentityList());
		if (expected.isEmpty() || StringUtils.isBlank(StringUtils.join(expected, ""))) {
			throw new Exception("expected is empty.");
		}
		List<TaskCompleted> done = this.listEffectiveTaskCompleted(work);
		/* 将已经处理的人从期望值中移除 */
		for (TaskCompleted o : done) {
			expected.remove(o.getIdentity());
		}
		if (expected.isEmpty()) {
			/* 所有人已经处理完成。 */
			passThrough = true;
		} else {
			passThrough = false;
			String next = expected.get(0);
			/* 还有人没有处理，开始判断待办,取到本环节的所有待办,理论上只能有一条待办 */
			List<Task> existed = this.entityManagerContainer().fetchAttribute(
					this.business().task().listWithActivityToken(work.getActivityToken()), Task.class, "identity");
			/* 理论上只能有一条待办 */
			boolean find = false;
			for (Task o : existed) {
				if (!StringUtils.equals(o.getIdentity(), next)) {
					this.entityManagerContainer().delete(Task.class, o.getId());
				} else {
					find = true;
				}
			}
			/* 当前处理人没有待办 */
			if (!find) {
				/* 可选择路由的名称列表 */
				List<String> routeList = new ArrayList<>();
				List<String> routeNameList = new ArrayList<>();
				for (Route o : this.business().element().listRouteWithManual(manual.getId())) {
					routeList.add(o.getId());
					routeNameList.add(o.getName());
				}
				Task task = this.createTask(this.business(), manual, work, attributes, data, next);
				task.setRouteList(routeList);
				task.setRouteNameList(routeNameList);
				this.entityManagerContainer().persist(task, CheckPersistType.all);
				/* 创建提醒 */
				this.concreteTaskMessageToQueue(task);
			}
		}
		return passThrough;
	}

	/* 所有有效的已办 */
	private List<TaskCompleted> listEffectiveTaskCompleted(Work work) throws Exception {
		List<String> ids = this.business().taskCompleted().listWithActivityTokenInIdentityList(work.getActivityToken(),
				work.getManualTaskIdentityList());
		List<TaskCompleted> list = new ArrayList<>();
		for (TaskCompleted o : this.business().entityManagerContainer().list(TaskCompleted.class, ids)) {
			if (!o.getProcessingType().equals(ProcessingType.retract)) {
				list.add(o);
			}
		}
		return list;
	}

	/** 如果设置了自动处理,那么判断是否可以直接通过 */
	private void passSameTarget(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Manual manual, Route route) throws Exception {
		logger.debug(
				"manual pass same target, work title:{}, id:{}, actvity name:{}, id:{}, process name:{}, id{}, enable intelligence.",
				work.getTitle(), work.getId(), manual.getName(), manual.getId(), work.getProcessName(),
				work.getProcess());
		List<TaskCompleted> prevManualTaskCompleteds = this.passSameTarget_listPrevManualTaskCompleted(work);
		/** 上一环节已经处理过的复制一个TaskCompleted */
		List<TaskCompleted> list = new ArrayList<>();
		for (String str : work.getManualTaskIdentityList()) {
			for (TaskCompleted o : prevManualTaskCompleteds) {
				if (StringUtils.equals(o.getIdentity(), str)) {
					TaskCompleted taskCompleted = this.passSameTarget_concreteTaskCompleted(route, o, work);
					list.add(taskCompleted);
				}
			}
		}
		if (ListTools.isNotEmpty(list)) {
			this.entityManagerContainer().beginTransaction(TaskCompleted.class);
			for (TaskCompleted o : list) {
				this.entityManagerContainer().persist(o, CheckPersistType.all);
			}
		}
		// List<String> prevIdentities =
		// ListTools.extractProperty(prevManualTaskCompleteds, "identity",
		// String.class,
		// true, true);
		// if (ListTools.containsAll(prevIdentities,
		// work.getManualTaskIdentityList())) {
		// /** 所有处理人在上以一个环节已经处理过 */
		// this.business().work().addHint(work, "在[" + manual.getName() +
		// "]环节由于处理人上一个环节中处理过.");
		// this.entityManagerContainer().beginTransaction(TaskCompleted.class);
		// for (String str : work.getManualTaskIdentityList()) {
		// for (TaskCompleted o : prevManualTaskCompleteds) {
		// if (StringUtils.equals(o.getIdentity(), str)) {
		// TaskCompleted taskCompleted =
		// this.passSameTarget_concreteTaskCompleted(route, o, work);
		// this.entityManagerContainer().persist(taskCompleted,
		// CheckPersistType.all);
		// }
		// }
		// }
		// }
	}

	/** 列示前面一个manual环节的所有TaskCompleted,如果费manual环节返回为空 */
	private List<TaskCompleted> passSameTarget_listPrevManualTaskCompleted(Work work) throws Exception {
		List<TaskCompleted> list = new ArrayList<>();
		String workLogId = this.getWithArrivedActivityToken(work.getActivityToken());
		if (StringUtils.isNotEmpty(workLogId)) {
			WorkLog workLog = this.entityManagerContainer().find(workLogId, WorkLog.class);
			if (null != workLog) {
				if (Objects.equals(ActivityType.manual, workLog.getFromActivityType())) {
					List<String> ids = this.business().taskCompleted()
							.listWithActivityToken(workLog.getFromActivityToken());
					if (ListTools.isNotEmpty(ids)) {
						list.addAll(this.entityManagerContainer().list(TaskCompleted.class, ids));
					}
				}
			}
		}
		return list;
	}

	private Route passSameTarget_route(List<Route> routes) {
		Route route = null;
		for (Route o : ListTools.trim(routes, true, false)) {
			if (BooleanUtils.isTrue(o.getPassSameTarget())) {
				if (null == route) {
					route = o;
				} else {
					return null;
				}
			}
		}
		return route;
	}

	private TaskCompleted passSameTarget_concreteTaskCompleted(Route route, TaskCompleted taskCompleted, Work work)
			throws Exception {
		TaskCompleted o = new TaskCompleted();
		taskCompleted.copyTo(o, JpaObject.FieldsUnmodifies);
		work.copyTo(o);
		o.setProcessingType(ProcessingType.passSameTarget);
		o.setRouteName(route.getName());
		o.setRetractTime(null);
		Date now = new Date();
		o.setStartTime(now);
		o.setStartTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
		o.setCompletedTime(now);
		o.setCompletedTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
		o.setDuration(0L);
		o.setExpired(false);
		o.setExpireTime(null);
		o.setTask(null);
		return o;
	}

	private void test() throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		em.flush();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WorkLog> cq = cb.createQuery(WorkLog.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		List<WorkLog> list = em.createQuery(cq).getResultList();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!能找到所有的workLog");
		for (WorkLog o : list) {
			System.out.println(XGsonBuilder.toJson(o));
		}
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!能找到所有的workLog");
	}

	private void test1(String str) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		em.flush();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WorkLog> cq = cb.createQuery(WorkLog.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.arrivedActivityToken), str);
		cq.where(p);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(cq.toString());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		List<WorkLog> list = em.createQuery(cq).getResultList();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!能找到所有的workLog1");
		for (WorkLog o : list) {
			System.out.println(XGsonBuilder.toJson(o));
		}
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!能找到所有的workLog1");
	}

	private String getWithArrivedActivityToken(String arrivedActivityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		/** 这里如果不flush会导致无法查找到 */
		// em.flush();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.arrivedActivityToken), arrivedActivityToken);
		p = cb.and(p, cb.equal(root.get(WorkLog_.connected), true));
		/** 如果不flush看上去像是内容会被缓存,加一个随机字符避免被缓存 */
		p = cb.and(cb.notEqual(root.get(WorkLog_.id), StringTools.uniqueToken()));
		cq.select(root.get(WorkLog_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}
}