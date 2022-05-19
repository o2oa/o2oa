package com.x.processplatform.service.processing.processor.manual;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.EmpowerLog;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;
import com.x.base.core.project.utils.time.WorkTime;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.WorkContext;
import com.x.processplatform.service.processing.processor.AeiObjects;

/**
 * @author Zhou Rui
 */
public class ManualProcessor extends AbstractManualProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManualProcessor.class);

	private static final String DEPRECATED_WORK_FIELD_MANUALTASKIDENTITYLIST = "manualTaskIdentityList";

	public ManualProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Manual manual) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.manualArrive(aeiObjects.getWork().getActivityToken(), manual));
		// 根据manual计算出来的活动处理人
		ManualTaskIdentityMatrix manualTaskIdentityMatrix = manual
				.identitiesToManualTaskIdentityMatrix(calculateTaskIdentities(aeiObjects, manual));
		// 启用同类工作相同活动节点合并,如果有合并的工作,那么直接返回这个工作.
		Optional<Work> mergeWork = this.arrivingMergeSameJob(aeiObjects, manual, manualTaskIdentityMatrix);
		if (mergeWork.isPresent()) {
			return mergeWork.get();
		}
		this.arrivingPassSame(aeiObjects, manualTaskIdentityMatrix);
		aeiObjects.getWork().setManualTaskIdentityMatrix(manualTaskIdentityMatrix);
		return aeiObjects.getWork();
	}

	private Optional<Work> arrivingMergeSameJob(AeiObjects aeiObjects, Manual manual,
			ManualTaskIdentityMatrix manualTaskIdentityMatrix) throws Exception {
		if (!BooleanUtils.isTrue(manual.getManualMergeSameJobActivity())) {
			return Optional.empty();
		}
		List<String> exists = this.arrivingSameJobActivityExistIdentities(aeiObjects, manual);
		if (ListTools.isNotEmpty(exists)) {
			Optional<Work> other = aeiObjects.getWorks().stream()
					.filter(o -> StringUtils.equals(aeiObjects.getWork().getJob(), o.getJob())
							&& StringUtils.equals(aeiObjects.getWork().getActivity(), o.getActivity())
							&& (!Objects.equals(aeiObjects.getWork(), o)))
					.findFirst();
			if (other.isPresent()) {
				manualTaskIdentityMatrix.remove(exists);
				if (manualTaskIdentityMatrix.isEmpty()) {
					this.mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other.get());
					this.mergeRead(aeiObjects, aeiObjects.getWork(), other.get());
					this.mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other.get());
					this.mergeReview(aeiObjects, aeiObjects.getWork(), other.get());
					this.mergeAttachment(aeiObjects, aeiObjects.getWork(), other.get());
					this.mergeWorkLog(aeiObjects, aeiObjects.getWork(), other.get());
					if (ListTools.size(aeiObjects.getWork().getSplitTokenList()) > ListTools
							.size(other.get().getSplitTokenList())) {
						other.get().setSplitTokenList(aeiObjects.getWork().getSplitTokenList());
						other.get().setSplitToken(aeiObjects.getWork().getSplitToken());
						other.get().setSplitValue(aeiObjects.getWork().getSplitValue());
						other.get().setSplitting(true);
					}
					aeiObjects.getUpdateWorks().add(other.get());
					aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
					return other;
				}
			}
		}
		return Optional.empty();
	}

	private void arrivingPassSame(AeiObjects aeiObjects, ManualTaskIdentityMatrix matrix) throws Exception {
		// 查找是否有passSameTarget设置
		if (BooleanUtils.isTrue(aeiObjects.getProcessingAttributes().ifForceJoinAtArrive())) {
			return;
		}
		Optional<Route> route = aeiObjects.getRoutes().stream().filter(o -> BooleanUtils.isTrue(o.getPassSameTarget()))
				.findFirst();
		// 如果有passSameTarget,有到达ArriveWorkLog,不是调度到这个节点的
		if (route.isPresent() && (null != aeiObjects.getArriveWorkLog(aeiObjects.getWork()))) {
			WorkLog workLog = findPassSameTargetWorkLog(aeiObjects);
			if (null == workLog) {
				return;
			}
			LOGGER.debug("pass same target work:{}, workLog:{}.", aeiObjects::getWork, workLog::toString);
			List<String> identities = matrix.flat();
			aeiObjects.getJoinInquireTaskCompletedsWithActivityToken(workLog.getArrivedActivityToken()).stream()
					.forEach(o -> {
						try {
							List<String> values = ListUtils.intersection(identities,
									aeiObjects.business().organization().identity().listWithPerson(o.getPerson()));
							if (!values.isEmpty()) {
								TaskCompleted taskCompleted = arrivingPassSameCreateTaskCompleted(aeiObjects,
										route.get(), o, values.get(0));
								aeiObjects.getCreateTaskCompleteds().add(taskCompleted);
								matrix.completed(values);
							}
						} catch (Exception e) {
							LOGGER.error(e);
						}
					});
		}
	}

	private TaskCompleted arrivingPassSameCreateTaskCompleted(AeiObjects aeiObjects, Route route, TaskCompleted o,
			String identity) throws Exception {
		TaskCompleted taskCompleted = new TaskCompleted(aeiObjects.getWork(), route, o);
		taskCompleted.setIdentity(identity);
		taskCompleted.setUnit(aeiObjects.business().organization().unit().getWithIdentity(taskCompleted.getIdentity()));
		taskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_SAMETARGET);
		taskCompleted.setRouteName(route.getName());
		taskCompleted.setOpinion(route.getOpinion());
		Date now = new Date();
		taskCompleted.setStartTime(now);
		taskCompleted.setStartTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
		taskCompleted.setCompletedTime(now);
		taskCompleted.setCompletedTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
		taskCompleted.setDuration(0L);
		taskCompleted.setExpired(false);
		taskCompleted.setExpireTime(null);
		taskCompleted.setTask(null);
		taskCompleted.setLatest(true);
		return taskCompleted;
	}

	// 计算处理人
	private List<String> calculateTaskIdentities(AeiObjects aeiObjects, Manual manual) throws Exception {
		TaskIdentities taskIdentities = new TaskIdentities();
		// 先计算强制处理人
		if (!aeiObjects.getWork().getProperties().getManualForceTaskIdentityList().isEmpty()) {
			List<String> identities = new ArrayList<>();
			identities.addAll(aeiObjects.getWork().getProperties().getManualForceTaskIdentityList());
			identities = aeiObjects.business().organization().identity().list(identities);
			if (ListTools.isNotEmpty(identities)) {
				taskIdentities.addIdentities(identities);
			}
		}
		// 计算退回的结果
		if (taskIdentities.isEmpty()) {
			Route route = aeiObjects.business().element().get(aeiObjects.getWork().getDestinationRoute(), Route.class);
			if ((null != route) && (StringUtils.equals(route.getType(), Route.TYPE_BACK))) {
				calculateRouteTypeBack(aeiObjects, manual, taskIdentities);
			}
		}
		if (taskIdentities.isEmpty()) {
			taskIdentities = TranslateTaskIdentityTools.translate(aeiObjects, manual);
			this.ifTaskIdentitiesEmptyForceToCreatorOrMaintenance(aeiObjects, taskIdentities);
			// 处理授权
			this.writeToEmpowerMap(aeiObjects, taskIdentities);
		}
		return taskIdentities.identities();
	}

	private void calculateRouteTypeBack(AeiObjects aeiObjects, Manual manual, TaskIdentities taskIdentities)
			throws Exception {
		List<String> identities = new ArrayList<>();
		List<WorkLog> workLogs = new ArrayList<>();
		workLogs.addAll(aeiObjects.getUpdateWorkLogs());
		workLogs.addAll(aeiObjects.getCreateWorkLogs());
		for (WorkLog o : aeiObjects.getWorkLogs()) {
			if (!workLogs.contains(o)) {
				workLogs.add(o);
			}
		}
		WorkLogTree tree = new WorkLogTree(workLogs);
		Node node = tree.location(aeiObjects.getWork());
		if (null != node) {
			calculateRouteTypeBackIdentityByTaskCompleted(aeiObjects, manual, taskIdentities, identities, tree, node);
		}
	}

	private void calculateRouteTypeBackIdentityByTaskCompleted(AeiObjects aeiObjects, Manual manual,
			TaskIdentities taskIdentities, List<String> identities, WorkLogTree tree, Node node) throws Exception {
		for (Node n : tree.up(node)) {
			if (StringUtils.equals(manual.getId(), n.getWorkLog().getFromActivity())) {
				for (TaskCompleted t : aeiObjects.getTaskCompleteds()) {
					if (StringUtils.equals(n.getWorkLog().getFromActivityToken(), t.getActivityToken())
							&& BooleanUtils.isTrue(t.getJoinInquire())) {
						identities.add(t.getIdentity());
					}
				}
				break;
			}
		}
		identities = aeiObjects.business().organization().identity().list(identities);
		if (ListTools.isNotEmpty(identities)) {
			taskIdentities.addIdentities(identities);
		}
	}

	/**
	 * 如果没能计算到活动处理人,那么按照流程维护人,应用维护人,工作创建者,平台维护人顺序查找处理人
	 * 
	 * @param aeiObjects
	 * @param manual
	 * @param taskIdentities
	 * @throws Exception
	 */
	private void ifTaskIdentitiesEmptyForceToCreatorOrMaintenance(AeiObjects aeiObjects, TaskIdentities taskIdentities)
			throws Exception {
		if (taskIdentities.isEmpty()) {
			String identity = aeiObjects.business().organization().identity()
					.get(aeiObjects.getProcess().getMaintenanceIdentity());
			if (StringUtils.isEmpty(identity)) {
				identity = aeiObjects.business().organization().identity()
						.get(aeiObjects.getApplication().getMaintenanceIdentity());
			}
			if (StringUtils.isEmpty(identity)) {
				identity = aeiObjects.business().organization().identity()
						.get(aeiObjects.getWork().getCreatorIdentity());
			}
			if (StringUtils.isEmpty(identity)) {
				identity = aeiObjects.business().organization().identity()
						.get(Config.processPlatform().getMaintenanceIdentity());
			}
			if (StringUtils.isEmpty(identity)) {
				throw new ExceptionExpectedEmpty(aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(),
						aeiObjects.getActivity().getName(), aeiObjects.getActivity().getId());
			}
			taskIdentities.addIdentity(identity);
		}
	}

	// 更新授权,通过surface创建且workThroughManual=false 代表是草稿,那么不需要授权.

	private void writeToEmpowerMap(AeiObjects aeiObjects, TaskIdentities taskIdentities) throws Exception {
		// 先清空EmpowerMap
		aeiObjects.getWork().getProperties().setManualEmpowerMap(new LinkedHashMap<>());
		if (!(StringUtils.equals(aeiObjects.getWork().getWorkCreateType(), Work.WORKCREATETYPE_SURFACE)
				&& BooleanUtils.isFalse(aeiObjects.getWork().getWorkThroughManual()))) {
			List<String> values = taskIdentities.identities();
			values = ListUtils.subtract(values, aeiObjects.getProcessingAttributes().getIgnoreEmpowerIdentityList());
			taskIdentities.empower(aeiObjects.business().organization().empower().listWithIdentityObject(
					aeiObjects.getWork().getApplication(), aeiObjects.getProcess().getEdition(),
					aeiObjects.getWork().getProcess(), aeiObjects.getWork().getId(), values));
			for (TaskIdentity taskIdentity : taskIdentities) {
				if (StringUtils.isNotEmpty(taskIdentity.getFromIdentity())) {
					aeiObjects.getWork().getProperties().getManualEmpowerMap().put(taskIdentity.getIdentity(),
							taskIdentity.getFromIdentity());
				}
			}
		}
	}

	private WorkLog findPassSameTargetWorkLog(AeiObjects aeiObjects) throws Exception {
		WorkLogTree tree = new WorkLogTree(aeiObjects.getWorkLogs());
		List<WorkLog> parents = tree.parents(aeiObjects.getArriveWorkLog(aeiObjects.getWork()));
		LOGGER.debug("pass same target rollback parents:{}.", parents::toString);
		WorkLog workLog = null;
		for (WorkLog o : parents) {
			// choice, agent, invoke, service, delay, embed 继续向上查找manual
			ActivityType arrivedActivityType = o.getArrivedActivityType();
			if (Objects.equals(ActivityType.manual, arrivedActivityType)
					|| Objects.equals(ActivityType.begin, arrivedActivityType)
					|| Objects.equals(ActivityType.cancel, arrivedActivityType)
					// || Objects.equals(ActivityType.embed, arrivedActivityType)
					|| Objects.equals(ActivityType.end, arrivedActivityType)
					|| Objects.equals(ActivityType.merge, arrivedActivityType)
					|| Objects.equals(ActivityType.parallel, arrivedActivityType)
					|| Objects.equals(ActivityType.split, arrivedActivityType)) {
				if (Objects.equals(ActivityType.manual, arrivedActivityType)) {
					workLog = o;
				}
				break;
			}
		}
		return workLog;
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Manual manual) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<Work> results = new ArrayList<>();
		ManualTaskIdentityMatrix matrix = executingManualTaskIdentityMatrix(aeiObjects, manual);
		List<TaskCompleted> taskCompleteds = aeiObjects
				.getJoinInquireTaskCompletedsWithActivityToken(aeiObjects.getWork().getActivityToken());
		executingCompletedIdentityInTaskCompleteds(aeiObjects, matrix, taskCompleteds);
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.manualExecute(aeiObjects.getWork().getActivityToken(), manual,
				Objects.toString(manual.getManualMode(), ""), matrix.flat()));
		if (matrix.isEmpty()) {
			results.add(aeiObjects.getWork());
		} else {
			switch (manual.getManualMode()) {
			case parallel:
				this.parallel(aeiObjects, manual, matrix, taskCompleteds);
				break;
			case queue:
				this.queue(aeiObjects, manual, matrix, taskCompleteds);
				break;
			case grab:
			case single:
			default:
				this.single(aeiObjects, manual, matrix, taskCompleteds);
			}
			// 可能在处理过程中删除了所有的待办,比如有优先路由
			if (matrix.isEmpty()) {
				results.add(aeiObjects.getWork());
			}
		}
		aeiObjects.getWork().setManualTaskIdentityMatrix(matrix);
		return results;
	}

	@SuppressWarnings("unchecked")
	private ManualTaskIdentityMatrix executingManualTaskIdentityMatrix(AeiObjects aeiObjects, Manual manual)
			throws Exception {
		ManualTaskIdentityMatrix matrix = aeiObjects.getWork().getManualTaskIdentityMatrix();
		if (matrix.isEmpty()) {
			List<String> identities = new ArrayList<>();
			// 兼容7.2.0之前的版本
			if (PropertyUtils.isReadable(aeiObjects.getWork(), DEPRECATED_WORK_FIELD_MANUALTASKIDENTITYLIST)) {
				identities.addAll((List<String>) PropertyUtils.getProperty(aeiObjects.getWork(),
						DEPRECATED_WORK_FIELD_MANUALTASKIDENTITYLIST));
				identities = aeiObjects.business().organization().identity().list(identities);
			}
			if (identities.isEmpty() && aeiObjects
					.getJoinInquireTaskCompletedsWithActivityToken(aeiObjects.getWork().getActivityToken()).isEmpty()) {
				identities = calculateTaskIdentities(aeiObjects, manual);
				LOGGER.info("工作设置的处理人已经全部无效,且没有已办,重新计算当前环节所有处理人进行处理,标题:{}, id:{}, 设置的处理人:{}.",
						aeiObjects.getWork()::getTitle, aeiObjects.getWork()::getId, identities::toString);
				matrix = manual.identitiesToManualTaskIdentityMatrix(identities);
				// 重新绑定到对象上.
				aeiObjects.getWork().setManualTaskIdentityMatrix(matrix);
			}
		}
		return matrix;
	}

	private void executingCompletedIdentityInTaskCompleteds(AeiObjects aeiObjects, ManualTaskIdentityMatrix matrix,
			List<TaskCompleted> taskCompleteds) throws Exception {
		if (!matrix.isEmpty()) {
			List<String> identities = matrix.flat();
			List<String> people = ListTools.extractProperty(taskCompleteds, TaskCompleted.person_FIELDNAME,
					String.class, true, true);
			taskCompleteds.stream().forEach(o -> identities.removeAll(matrix.completed(o.getIdentity())));
			if (!identities.isEmpty()) {
				aeiObjects.business().organization().person().listPairIdentity(identities).stream().forEach(p -> {
					if (people.contains(p.getPerson())) {
						matrix.completed(p.getIdentity());
					}
				});
			}
		}
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Manual manual, List<Work> works) throws Exception {
		// Manual Work 还没有处理完 发生了停留,出发了停留事件
		if ((ListTools.isEmpty(works)) && (!aeiObjects.getCreateTasks().isEmpty())) {
			boolean hasManualStayScript = this.hasManualStayScript(manual);
			boolean processHasManualStayScript = this.hasManualStayScript(aeiObjects.getProcess());
			if (hasManualStayScript || processHasManualStayScript) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
				WorkContext workContext = (WorkContext) bindings.get(ScriptingFactory.BINDING_NAME_WORKCONTEXT);
				// 只有一条待办绑定到task
				if (aeiObjects.getCreateTasks().size() == 1) {
					workContext.bindTask(aeiObjects.getCreateTasks().get(0));
				}
				if (processHasManualStayScript) {
					JsonScriptingExecutor
							.eval(aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
									aeiObjects.getProcess(), Business.EVENT_MANUALSTAY), scriptContext);
				}
				if (hasManualStayScript) {
					JsonScriptingExecutor
							.eval(aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
									aeiObjects.getActivity(), Business.EVENT_MANUALSTAY), scriptContext);
				}
				// 解除绑定
				workContext.bindTask(null);
			}
		}
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Manual manual) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.manualInquire(aeiObjects.getWork().getActivityToken(), manual));
		List<Route> results = new ArrayList<>();
		// 仅有单条路由
		if (aeiObjects.getRoutes().size() == 1) {
			results.add(aeiObjects.getRoutes().get(0));
		} else if (aeiObjects.getRoutes().size() > 1) {
			// 存在多条路由
			List<TaskCompleted> taskCompleteds = aeiObjects
					.getJoinInquireTaskCompletedsWithActivityToken(aeiObjects.getWork().getActivityToken()).stream()
					.sorted(Comparator.comparing(TaskCompleted::getCreateTime)).collect(Collectors.toList());
			String name = this.choiceRouteName(taskCompleteds, manual, aeiObjects.getRoutes());
			for (Route o : aeiObjects.getRoutes()) {
				if (o.getName().equalsIgnoreCase(name)) {
					results.add(o);
					break;
				}
			}
		} else {
			throw new ExceptionManualNotRoute(manual.getId());
		}

		if (!results.isEmpty()) {
			// 清理掉强制的指定的处理人
			aeiObjects.getWork().getProperties().setManualForceTaskIdentityList(new ArrayList<>());
		}

		return results;
	}

	/**
	 * 选择离开活动的路由
	 * 
	 * @param taskCompleteds 按创建时间正序排列好的已办
	 * @param manual         人工活动节点
	 * @param routes         离开活动节点的路由列表
	 * @return 路由名称
	 * @throws Exception
	 */
	private String choiceRouteName(List<TaskCompleted> taskCompleteds, Manual manual, List<Route> routes)
			throws Exception {
		String result = "";
		// 将已办中的路由选择抽取出来
		List<String> selectedRouteNames = ListTools.extractField(taskCompleteds, TaskCompleted.routeName_FIELDNAME,
				String.class, false, false);
		// 进行优先路由的判断
		// 已经开始选择路由,如果选择了soleDirect那么就直接返回了,如果没有选择这个路由在进行sole的判断,顺序是 soleDirct -> sole
		// -> max -> latest.
		result = choiceSoleDirectIfExist(selectedRouteNames, routes);
		if (StringUtils.isEmpty(result)) {
			result = choiceSoleIfExist(selectedRouteNames, routes);
		}
		if (StringUtils.isEmpty(result)) {
			switch (manual.getManualMode()) {
			case parallel:
				result = choiceMaxCountOrLatest(taskCompleteds, routes);
				break;
			case queue:
			case grab:
			case single:
			default:
				result = choiceLatest(selectedRouteNames, routes);
			}
		}

		if (StringUtils.isEmpty(result)) {
			throw new ExceptionChoiceRouteNameError(
					ListTools.extractProperty(selectedRouteNames, JpaObject.id_FIELDNAME, String.class, false, false));
		}
		return result;
	}

	/**
	 * 判断是否有选择了直接返回优先路由的路由决策被选择,如果有就直接返回该路由名称,这里的遍历顺序需要保持正序,先选择先执行.
	 * 
	 * @param list
	 * @param routes
	 * @return
	 */
	private String choiceSoleDirectIfExist(List<String> list, List<Route> routes) {
		List<String> names = routes.stream().filter(r -> BooleanUtils.isTrue(r.getSoleDirect())).map(Route::getName)
				.collect(Collectors.toList());
		for (String str : list) {
			if (names.contains(str)) {
				return str;
			}
		}
		return null;
	}

	/**
	 * 判断是否有选择了优先路由的路由决策被选择,如果有就直接返回该路由名称,这里的遍历顺序需要保持正序,先选择先执行.
	 * 
	 * @param list
	 * @param routes
	 * @return
	 */
	private String choiceSoleIfExist(List<String> list, List<Route> routes) {
		List<String> names = routes.stream().filter(r -> BooleanUtils.isTrue(r.getSole())).map(Route::getName)
				.collect(Collectors.toList());
		for (String str : list) {
			if (names.contains(str)) {
				return str;
			}
		}
		return null;
	}

	/**
	 * 已办中获取数量最多的路由决策,如果有组路由决策数量一样多,那么选择时间上最后被选择的路由决策,获取后需要进行判断是否在routes列表中
	 * 
	 * @param list
	 * @param routes
	 * @return
	 * @throws Exception
	 */
	private String choiceMaxCountOrLatest(List<TaskCompleted> list, List<Route> routes) throws Exception {
		List<String> names = ListTools.extractField(routes, Route.name_FIELDNAME, String.class, false, false);
		return list.stream().collect(Collectors.groupingBy(TaskCompleted::getRouteName)).entrySet().stream()
				.sorted((o1, o2) -> {
					int c = o2.getValue().size() - o1.getValue().size();
					if (c == 0) {
						Date d1 = o1.getValue().stream()
								.sorted(Comparator.comparing(TaskCompleted::getCreateTime).reversed()).findFirst().get()
								.getCreateTime();
						Date d2 = o2.getValue().stream()
								.sorted(Comparator.comparing(TaskCompleted::getCreateTime).reversed()).findFirst().get()
								.getCreateTime();
						return ObjectUtils.compare(d2, d1);
					} else {
						return c;
					}
				}).map(Entry::getKey).filter(names::contains).findFirst().orElse(null);
	}

	/**
	 * 取得最后一个办理的路由决策,需要判断是否在路由列表中
	 * 
	 * @param list   通过已办取得的用户选择的路由列表,默认是正序
	 * @param routes 活动可选择的路由
	 * @return
	 */
	private String choiceLatest(List<String> list, List<Route> routes) {
		if (ListTools.isEmpty(list)) {
			return null;
		} else {
			List<String> names = routes.stream().map(Route::getName).collect(Collectors.toList());
			for (int i = list.size() - 1; i >= 0; i--) {
				if (names.contains(list.get(i))) {
					return list.get(i);
				}
			}
			return null;
		}
	}

	// 是否有优先路由
	private void single(AeiObjects aeiObjects, Manual manual, ManualTaskIdentityMatrix matrix,
			List<TaskCompleted> taskCompleteds) throws Exception {
		if (soleDirect(aeiObjects, taskCompleteds)) {
			matrix.clear();
		} else {
			task(aeiObjects, manual, matrix.read());
		}
	}

	private void parallel(AeiObjects aeiObjects, Manual manual, ManualTaskIdentityMatrix matrix,
			List<TaskCompleted> taskCompleteds) throws Exception {
		// 是否有优先路由
		if (soleDirect(aeiObjects, taskCompleteds)) {
			matrix.clear();
		} else {
			task(aeiObjects, manual, matrix.flat());
		}
	}

	private void queue(AeiObjects aeiObjects, Manual manual, ManualTaskIdentityMatrix matrix,
			List<TaskCompleted> taskCompleteds) throws Exception {
		if (soleDirect(aeiObjects, taskCompleteds)) {
			matrix.clear();
		} else {
			task(aeiObjects, manual, matrix.read());
		}
	}

	private boolean soleDirect(AeiObjects aeiObjects, List<TaskCompleted> taskCompleteds) throws Exception {
		// 存在优先路由,如果有人选择了优先路由那么直接流转.需要判断是否启用了soleDirect
		Optional<Route> route = aeiObjects.getRoutes().stream().filter(r -> BooleanUtils.isTrue(r.getSoleDirect()))
				.findFirst();
		if (route.isPresent()) {
			Optional<TaskCompleted> taskCompleted = taskCompleteds.stream()
					.filter(t -> StringUtils.equals(t.getRouteName(), route.get().getName())).findFirst();
			if (taskCompleted.isPresent()) {
				return true;
			}
		}
		return false;
	}

	private void task(AeiObjects aeiObjects, Manual manual, List<String> identities) throws Exception {
		String activityToken = aeiObjects.getWork().getActivityToken();
		aeiObjects.getTasks().stream().filter(t -> StringUtils.equalsIgnoreCase(t.getActivityToken(), activityToken))
				.forEach(t -> {
					if (!identities.contains(t.getIdentity())) {
						aeiObjects.deleteTask(t);
					} else {
						identities.remove(t.getIdentity());
					}
				});
		for (String identity : identities) {
			aeiObjects.createTask(this.createTask(aeiObjects, manual, identity));
		}
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Manual manual) throws Exception {
		// nothing
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
		// 如果work有截至时间
		if (null != aeiObjects.getWork().getExpireTime()) {
			if (null == task.getExpireTime()) {
				task.setExpireTime(aeiObjects.getWork().getExpireTime());
			} else {
				if (task.getExpireTime().after(aeiObjects.getWork().getExpireTime())) {
					task.setExpireTime(aeiObjects.getWork().getExpireTime());
				}
			}
		}
		// 已经有过期时间了,那么设置催办时间
		if (null != task.getExpireTime()) {
			task.setUrgeTime(DateUtils.addHours(task.getExpireTime(), -2));
		} else {
			task.setExpired(false);
			task.setUrgeTime(null);
			task.setUrged(false);
		}
	}

	// 从不过期
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
		WorkTime wt = Config.workTime();
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireDay(), 0))) {
			m += manual.getTaskExpireDay() * wt.minutesOfWorkDay();
		}
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireHour(), 0))) {
			m += manual.getTaskExpireHour() * 60;
		}
		if (m > 0) {
			Date expire = wt.forwardMinutes(new Date(), m);
			task.setExpireTime(expire);
		} else {
			task.setExpireTime(null);
		}
	}

	private void expireAppointNaturalDay(Task task, Manual manual) {
		Integer m = 0;
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireDay(), 0))) {
			m += manual.getTaskExpireDay() * 60 * 24;
		}
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireHour(), 0))) {
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
		ScriptContext scriptContext = aeiObjects.scriptContext();
		CompiledScript cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
				manual, Business.EVENT_MANUALTASKEXPIRE);
		scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptingFactory.BINDING_NAME_EXPIRE, expire);
		JsonScriptingExecutor.eval(cs, scriptContext, ExpireScriptResult.class, o -> {
			if (null != o) {
				expire.setDate(o.getDate());
				expire.setHour(o.getHour());
				expire.setWorkHour(o.getWorkHour());
			}
		});
		if (BooleanUtils.isTrue(NumberTools.greaterThan(expire.getWorkHour(), 0))) {
			Integer m = 0;
			m += expire.getWorkHour() * 60;
			if (m > 0) {
				task.setExpireTime(Config.workTime().forwardMinutes(new Date(), m));
			} else {
				task.setExpireTime(null);
			}
		} else if (BooleanUtils.isTrue(NumberTools.greaterThan(expire.getHour(), 0))) {
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

	private Task createTask(AeiObjects aeiObjects, Manual manual, String identity) throws Exception {
		String fromIdentity = aeiObjects.getWork().getProperties().getManualEmpowerMap().get(identity);
		String person = aeiObjects.business().organization().person().getWithIdentity(identity);
		String unit = aeiObjects.business().organization().unit().getWithIdentity(identity);
		Task task = new Task(aeiObjects.getWork(), identity, person, unit, fromIdentity, new Date(), null,
				aeiObjects.getRoutes(), manual.getAllowRapid());
		// 是第一条待办,进行标记，调度过的待办都标记为非第一个待办
		if (BooleanUtils.isTrue(aeiObjects.getProcessingAttributes().getForceJoinAtArrive())) {
			task.setFirst(false);
		} else {
			task.setFirst(ListTools.isEmpty(aeiObjects.getJoinInquireTaskCompleteds()));
		}
		this.calculateExpire(aeiObjects, manual, task);
		if (StringUtils.isNotEmpty(fromIdentity)) {
			aeiObjects.business().organization().empowerLog()
					.log(this.createEmpowerLog(aeiObjects.getWork(), fromIdentity, identity));
			String fromPerson = aeiObjects.business().organization().person().getWithIdentity(fromIdentity);
			String fromUnit = aeiObjects.business().organization().unit().getWithIdentity(fromIdentity);
			TaskCompleted empowerTaskCompleted = new TaskCompleted(aeiObjects.getWork());
			empowerTaskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_EMPOWER);
			empowerTaskCompleted.setIdentity(fromIdentity);
			empowerTaskCompleted.setUnit(fromUnit);
			empowerTaskCompleted.setPerson(fromPerson);
			empowerTaskCompleted.setEmpowerToIdentity(identity);
			aeiObjects.createTaskCompleted(empowerTaskCompleted);
			Read empowerRead = new Read(aeiObjects.getWork(), fromIdentity, fromUnit, fromPerson);
			aeiObjects.createRead(empowerRead);
		}
		return task;
	}

	private EmpowerLog createEmpowerLog(Work work, String fromIdentity, String toIdentity) {
		return new EmpowerLog().setApplication(work.getApplication()).setApplicationAlias(work.getApplicationAlias())
				.setApplicationName(work.getApplicationName()).setProcess(work.getProcess())
				.setProcessAlias(work.getProcessAlias()).setProcessName(work.getProcessName()).setTitle(work.getTitle())
				.setWork(work.getId()).setJob(work.getJob()).setFromIdentity(fromIdentity).setToIdentity(toIdentity)
				.setActivity(work.getActivity()).setActivityAlias(work.getActivityAlias())
				.setActivityName(work.getActivityName()).setEmpowerTime(new Date());
	}

	private List<String> arrivingSameJobActivityExistIdentities(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> exists = new ArrayList<>();
		aeiObjects.getTasks().stream()
				.filter(o -> StringUtils.equals(o.getActivity(), manual.getId())
						&& StringUtils.equals(o.getJob(), aeiObjects.getWork().getJob()))
				.forEach(o -> exists.add(o.getIdentity()));
		return exists;
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
				LOGGER.error(e);
			}
		}

	}
}
