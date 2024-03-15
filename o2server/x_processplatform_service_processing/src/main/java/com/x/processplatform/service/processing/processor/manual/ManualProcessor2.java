//package com.x.processplatform.service.processing.processor.manual;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.Set;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import javax.script.Bindings;
//import javax.script.ScriptContext;
//
//import org.apache.commons.collections4.ListUtils;
//import org.apache.commons.collections4.SetUtils;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.ObjectUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.tuple.Triple;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
//import com.x.base.core.project.scripting.JsonScriptingExecutor;
//import com.x.base.core.project.scripting.ScriptingFactory;
//import com.x.base.core.project.tools.DateTools;
//import com.x.base.core.project.tools.ListTools;
//import com.x.base.core.project.tools.StringTools;
//import com.x.processplatform.core.entity.content.Read;
//import com.x.processplatform.core.entity.content.Task;
//import com.x.processplatform.core.entity.content.TaskCompleted;
//import com.x.processplatform.core.entity.content.Work;
//import com.x.processplatform.core.entity.content.WorkLog;
//import com.x.processplatform.core.entity.content.WorkProperties.GoBackStore;
//import com.x.processplatform.core.entity.element.Activity;
//import com.x.processplatform.core.entity.element.ActivityType;
//import com.x.processplatform.core.entity.element.Manual;
//import com.x.processplatform.core.entity.element.ManualProperties;
//import com.x.processplatform.core.entity.element.Route;
//import com.x.processplatform.core.entity.element.util.WorkLogTree;
//import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
//import com.x.processplatform.core.entity.log.Signal;
//import com.x.processplatform.service.processing.Business;
//import com.x.processplatform.service.processing.WorkContext;
//import com.x.processplatform.service.processing.processor.AeiObjects;
//
///**
// * @author Zhou Rui
// */
//public class ManualProcessor2 extends AbstractManualProcessor {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(ManualProcessor2.class);
//
//	// private static final String DEPRECATED_WORK_FIELD_MANUALTASKIDENTITYLIST =
//	// "manualTaskIdentityList";
//
//	public ManualProcessor2(EntityManagerContainer entityManagerContainer) throws Exception {
//		super(entityManagerContainer);
//	}
//
//	@Override
//	protected Work arriving(AeiObjects aeiObjects, Manual manual) throws Exception {
//		// 发送ProcessingSignal
//		aeiObjects.getProcessingAttributes().push(Signal.manualArrive(aeiObjects.getWork().getActivityToken(), manual));
//		// 根据manual计算出来的活动处理人
//		ManualTaskIdentityMatrix manualTaskIdentityMatrix = manual
//				.identitiesToManualTaskIdentityMatrix(calculateTaskIdentities(aeiObjects, manual));
//		// 启用同类工作相同活动节点合并,如果有合并的工作,那么直接返回这个工作.
//		Optional<Work> mergeWork = this.arrivingMergeSameJob(aeiObjects, manual, manualTaskIdentityMatrix);
//		if (mergeWork.isPresent()) {
//			return mergeWork.get();
//		}
//		this.arrivingPassSame(aeiObjects, manualTaskIdentityMatrix);
//		aeiObjects.getWork().setManualTaskIdentityMatrix(manualTaskIdentityMatrix);
//		return aeiObjects.getWork();
//	}
//
//	private Optional<Work> arrivingMergeSameJob(AeiObjects aeiObjects, Manual manual,
//			ManualTaskIdentityMatrix manualTaskIdentityMatrix) throws Exception {
//		if (!BooleanUtils.isTrue(manual.getManualMergeSameJobActivity())) {
//			return Optional.empty();
//		}
//		List<String> exists = this.arrivingSameJobActivityExistIdentities(aeiObjects, manual);
//		if (ListTools.isNotEmpty(exists)) {
//			Optional<Work> other = aeiObjects.getWorks().stream()
//					.filter(o -> StringUtils.equals(aeiObjects.getWork().getJob(), o.getJob())
//							&& StringUtils.equals(aeiObjects.getWork().getActivity(), o.getActivity())
//							&& (!Objects.equals(aeiObjects.getWork(), o)))
//					.findFirst();
//			if (other.isPresent()) {
//				manualTaskIdentityMatrix.remove(exists);
//				if (manualTaskIdentityMatrix.isEmpty()) {
//					this.mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other.get());
//					this.mergeRead(aeiObjects, aeiObjects.getWork(), other.get());
//					this.mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other.get());
//					this.mergeReview(aeiObjects, aeiObjects.getWork(), other.get());
//					this.mergeAttachment(aeiObjects, aeiObjects.getWork(), other.get());
//					this.mergeWorkLog(aeiObjects, aeiObjects.getWork(), other.get());
//					if (ListTools.size(aeiObjects.getWork().getSplitTokenList()) > ListTools
//							.size(other.get().getSplitTokenList())) {
//						other.get().setSplitTokenList(aeiObjects.getWork().getSplitTokenList());
//						other.get().setSplitToken(aeiObjects.getWork().getSplitToken());
//						other.get().setSplitValue(aeiObjects.getWork().getSplitValue());
//						other.get().setSplitting(true);
//					}
//					aeiObjects.getUpdateWorks().add(other.get());
//					aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
//					return other;
//				}
//			}
//		}
//		return Optional.empty();
//	}
//
//	private void arrivingPassSame(AeiObjects aeiObjects, ManualTaskIdentityMatrix matrix) throws Exception {
//		// 查找是否有passSameTarget设置
//		if (BooleanUtils.isTrue(aeiObjects.getProcessingAttributes().ifForceJoinAtArrive())) {
//			return;
//		}
//		Optional<Route> route = aeiObjects.getRoutes().stream().filter(o -> BooleanUtils.isTrue(o.getPassSameTarget()))
//				.findFirst();
//		// 如果有passSameTarget,有到达ArriveWorkLog,不是调度到这个节点的
//		if (route.isPresent() && (null != aeiObjects.getArriveWorkLog(aeiObjects.getWork()))) {
//			Optional<WorkLog> optional = findPassSameTargetWorkLog(aeiObjects);
//			if (optional.isEmpty()) {
//				return;
//			}
//			LOGGER.debug("pass same target work:{}, workLog:{}.", aeiObjects::getWork, optional.get()::toString);
//			List<String> identities = matrix.flat();
//			aeiObjects.getJoinInquireTaskCompletedsWithActivityToken(optional.get().getFromActivityToken()).stream()
//					.forEach(o -> {
//						try {
//							List<String> values = ListUtils.intersection(identities,
//									aeiObjects.business().organization().identity().listWithPerson(o.getPerson()));
//							if (!values.isEmpty()) {
//								TaskCompleted taskCompleted = arrivingPassSameCreateTaskCompleted(aeiObjects,
//										route.get(), o, values.get(0));
//								aeiObjects.getCreateTaskCompleteds().add(taskCompleted);
//								matrix.completed(values);
//							}
//						} catch (Exception e) {
//							LOGGER.error(e);
//						}
//					});
//		}
//	}
//
//	private TaskCompleted arrivingPassSameCreateTaskCompleted(AeiObjects aeiObjects, Route route, TaskCompleted o,
//			String identity) throws Exception {
//		TaskCompleted taskCompleted = new TaskCompleted(aeiObjects.getWork(), route, o);
//		taskCompleted.setIdentity(identity);
//		taskCompleted.setUnit(aeiObjects.business().organization().unit().getWithIdentity(taskCompleted.getIdentity()));
//		taskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_SAMETARGET);
//		taskCompleted.setJoinInquire(true);
//		taskCompleted.setRouteName(route.getName());
//		taskCompleted.setOpinion(route.getOpinion());
//		Date now = new Date();
//		taskCompleted.setStartTime(now);
//		taskCompleted.setStartTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
//		taskCompleted.setCompletedTime(now);
//		taskCompleted.setCompletedTimeMonth(DateTools.format(now, DateTools.format_yyyyMM));
//		taskCompleted.setDuration(0L);
//		taskCompleted.setExpired(false);
//		taskCompleted.setExpireTime(null);
//		taskCompleted.setTask(null);
//		taskCompleted.setLatest(true);
//		return taskCompleted;
//	}
//
//	// 计算处理人
//	private List<String> calculateTaskIdentities(AeiObjects aeiObjects, Manual manual) throws Exception {
//		TaskIdentities taskIdentities = new TaskIdentities();
//		// 先计算强制处理人
//		if (!aeiObjects.getWork().getProperties().getManualForceTaskIdentityList().isEmpty()) {
//			List<String> identities = new ArrayList<>();
//			identities.addAll(aeiObjects.getWork().getProperties().getManualForceTaskIdentityList());
//			identities = aeiObjects.business().organization().identity().list(identities);
//			if (ListTools.isNotEmpty(identities)) {
//				taskIdentities.addIdentities(identities);
//			}
//		}
//		// 计算退回的结果
//		if (taskIdentities.isEmpty()) {
//			Route route = aeiObjects.business().element().get(aeiObjects.getWork().getDestinationRoute(), Route.class);
//			if ((null != route) && (StringUtils.equals(route.getType(), Route.TYPE_BACK))) {
//				calculateRouteTypeBack(aeiObjects, manual, taskIdentities);
//			}
//		}
//		if (taskIdentities.isEmpty()) {
//			taskIdentities = TranslateTaskIdentityTools.translate(aeiObjects, manual);
//			this.ifTaskIdentitiesEmptyForceToCreatorOrMaintenance(aeiObjects, manual, taskIdentities);
//			// 处理授权
//			this.writeToEmpowerMap(aeiObjects, taskIdentities);
//		}
//		return taskIdentities.identities();
//	}
//
//	private void calculateRouteTypeBack(AeiObjects aeiObjects, Manual manual, TaskIdentities taskIdentities)
//			throws Exception {
//		List<WorkLog> workLogs = Stream
//				.concat(Stream.concat(aeiObjects.getUpdateWorkLogs().stream(), aeiObjects.getCreateWorkLogs().stream()),
//						aeiObjects.getWorkLogs().stream())
//				.distinct().collect(Collectors.toList());
//		WorkLogTree tree = new WorkLogTree(workLogs);
//		Node node = tree.location(aeiObjects.getWork());
//		if (null != node) {
//			calculateRouteTypeBackIdentityByTaskCompleted(aeiObjects, manual, taskIdentities, tree, node);
//		}
//	}
//
////    private void calculateRouteTypeBackIdentityByTaskCompleted(AeiObjects aeiObjects, Manual manual,
////            TaskIdentities taskIdentities, List<String> identities, WorkLogTree tree, Node node) throws Exception {
////        for (Node n : tree.up(node)) {
////            if (StringUtils.equals(manual.getId(), n.getWorkLog().getFromActivity())) {
////                for (TaskCompleted t : aeiObjects.getTaskCompleteds()) {
////                    if (StringUtils.equals(n.getWorkLog().getFromActivityToken(), t.getActivityToken())
////                            && BooleanUtils.isTrue(t.getJoinInquire())) {
////                        identities.add(t.getIdentity());
////                    }
////                }
////                break;
////            }
////        }
////        identities = aeiObjects.business().organization().identity().list(identities);
////        if (ListTools.isNotEmpty(identities)) {
////            taskIdentities.addIdentities(identities);
////        }
////    }
//
//	private void calculateRouteTypeBackIdentityByTaskCompleted(AeiObjects aeiObjects, Manual manual,
//			TaskIdentities taskIdentities, WorkLogTree tree, Node node) throws Exception {
//		for (Node n : tree.up(node)) {
//			if (StringUtils.equals(manual.getId(), n.getWorkLog().getFromActivity())) {
//				for (TaskCompleted t : aeiObjects.getTaskCompleteds()) {
//					if (StringUtils.equals(n.getWorkLog().getFromActivityToken(), t.getActivityToken())
//							&& BooleanUtils.isTrue(t.getJoinInquire())) {
//						String identity = aeiObjects.business().organization().identity().get(t.getIdentity());
//						if (StringUtils.isNotEmpty(identity)) {
//							TaskIdentity taskIdentity = new TaskIdentity();
//							taskIdentity.setIdentity(identity);
//							if (StringUtils.isNotEmpty(t.getEmpowerFromIdentity())) {
//								taskIdentity.setFromIdentity(t.getEmpowerFromIdentity());
//							}
//							taskIdentities.add(taskIdentity);
//						}
//					}
//				}
//				break;
//			}
//		}
//	}
//
//	/**
//	 * 如果没能计算到活动处理人,先判断人员活动是否有设置人员,如果有那么先返回工作创建者,再按照流程维护人,应用维护人,工作创建者,平台维护人顺序查找处理人
//	 * 
//	 * @param aeiObjects
//	 * @param manual
//	 * @param taskIdentities
//	 * @throws Exception
//	 */
//	private void ifTaskIdentitiesEmptyForceToCreatorOrMaintenance(AeiObjects aeiObjects, Manual manual,
//			TaskIdentities taskIdentities) throws Exception {
//		if (!taskIdentities.isEmpty()) {
//			return;
//		}
//		String identity = null;
//		if (!ifManualAssignTaskIdentity(manual)) {
//			identity = aeiObjects.business().organization().identity().get(aeiObjects.getWork().getCreatorIdentity());
//		}
//		if (StringUtils.isEmpty(identity) && StringUtils.isNotBlank(aeiObjects.getProcess().getMaintenanceIdentity())) {
//			identity = aeiObjects.business().organization().identity()
//					.get(aeiObjects.getProcess().getMaintenanceIdentity());
//		}
//		if (StringUtils.isEmpty(identity)
//				&& StringUtils.isNotBlank(aeiObjects.getApplication().getMaintenanceIdentity())) {
//			identity = aeiObjects.business().organization().identity()
//					.get(aeiObjects.getApplication().getMaintenanceIdentity());
//		}
//		if (StringUtils.isEmpty(identity)) {
//			identity = aeiObjects.business().organization().identity().get(aeiObjects.getWork().getCreatorIdentity());
//		}
//		if (StringUtils.isEmpty(identity)
//				&& StringUtils.isNotBlank(Config.processPlatform().getMaintenanceIdentity())) {
//			identity = aeiObjects.business().organization().identity()
//					.get(Config.processPlatform().getMaintenanceIdentity());
//		}
//		if (StringUtils.isEmpty(identity)) {
//			throw new ExceptionExpectedEmpty(aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(),
//					aeiObjects.getActivity().getName(), aeiObjects.getActivity().getId());
//		}
//		taskIdentities.addIdentity(identity);
//	}
//
//	/**
//	 * 判读是否在活动中制定了处理人,这里没有对脚本的注解和空行进行判断
//	 * 
//	 * @param manual
//	 * @return
//	 */
//	private boolean ifManualAssignTaskIdentity(Manual manual) {
//		/* 指定了的身份 */
//		if (ListTools.isNotEmpty(manual.getTaskIdentityList())) {
//			return true;
//		}
//		/* 指定了流程参与者 */
//		if ((null != manual.getTaskParticipant()) && (StringUtils.equalsAnyIgnoreCase(
//				manual.getTaskParticipant().getType(), ManualProperties.Participant.TYPE_CREATOR,
//				ManualProperties.Participant.TYPE_MAINTENANCE)
//				|| (StringUtils.equalsIgnoreCase(manual.getTaskParticipant().getType(),
//						ManualProperties.Participant.TYPE_ACTIVITY) && (null != manual.getTaskParticipant().getData())
//						&& manual.getTaskParticipant().getData().isJsonArray()
//						&& (!manual.getTaskParticipant().getData().getAsJsonArray().isEmpty())))) {
//			return true;
//		}
//		/* 选择了职务 */
//		if (StringUtils.isNotBlank(manual.getTaskDuty())) {
//			return true;
//		}
//		/* 指定data数据路径值 */
//		if (ListTools.isNotEmpty(manual.getTaskDataPathList())) {
//			return true;
//		}
//		/* 使用脚本计算 */
//		if (StringUtils.isNotEmpty(manual.getTaskScript())) {
//			return true;
//		} else if (StringTools.ifScriptHasEffectiveCode(manual.getTaskScriptText())) {
//			return true;
//		}
//		/* 指定处理组织 */
//		if (ListTools.isNotEmpty(manual.getTaskUnitList())) {
//			return true;
//		}
//		/* 指定处理群组 */
//		return (ListTools.isNotEmpty(manual.getTaskGroupList()));
//	}
//
//	// 更新授权,通过surface创建且workThroughManual=false 代表是草稿,那么不需要授权.
//
//	private void writeToEmpowerMap(AeiObjects aeiObjects, TaskIdentities taskIdentities) throws Exception {
//		// 先清空EmpowerMap
//		aeiObjects.getWork().getProperties().setManualEmpowerMap(new LinkedHashMap<>());
//		if (!(StringUtils.equals(aeiObjects.getWork().getWorkCreateType(), Work.WORKCREATETYPE_SURFACE)
//				&& BooleanUtils.isFalse(aeiObjects.getWork().getWorkThroughManual()))) {
//			List<String> values = taskIdentities.identities();
//
//			values = ListUtils.subtract(values, aeiObjects.getProcessingAttributes().getIgnoreEmpowerIdentityList());
//
//			taskIdentities.empower(aeiObjects.business().organization().empower().listWithIdentityObject(
//					aeiObjects.getWork().getApplication(), aeiObjects.getProcess().getEdition(),
//					aeiObjects.getWork().getProcess(), aeiObjects.getWork().getId(), values));
//			for (TaskIdentity taskIdentity : taskIdentities) {
//				if (StringUtils.isNotEmpty(taskIdentity.getFromIdentity())) {
//					aeiObjects.getWork().getProperties().getManualEmpowerMap().put(taskIdentity.getIdentity(),
//							taskIdentity.getFromIdentity());
//				}
//			}
//		}
//	}
//
//	private Optional<WorkLog> findPassSameTargetWorkLog(AeiObjects aeiObjects) throws Exception {
//		WorkLog workLog = aeiObjects.getArriveWorkLog(aeiObjects.getWork());
//		if (null == workLog) {
//			return Optional.empty();
//		}
//		WorkLogTree tree = new WorkLogTree(aeiObjects.getWorkLogs());
//
//		List<WorkLog> list = new ArrayList<>();
//		list.add(workLog);
//		list.addAll(tree.parents(workLog));
//		LOGGER.debug("pass same target rollback parents:{}.", list::toString);
//		for (WorkLog o : list) {
//			// choice, agent, invoke, service, delay, embed, split, parallel 继续向上查找manual
//			ActivityType activityType = o.getFromActivityType();
//			if (Objects.equals(ActivityType.begin, activityType) || Objects.equals(ActivityType.cancel, activityType)
//					|| Objects.equals(ActivityType.end, activityType)
//					|| Objects.equals(ActivityType.merge, activityType)) {
//				// nothing
//			} else if (Objects.equals(ActivityType.manual, activityType)) {
//				return Optional.of(o);
//			}
//		}
//		return Optional.empty();
//	}
//
//	@Override
//	protected void arrivingCommitted(AeiObjects aeiObjects, Manual manual) throws Exception {
//		// nothing
//	}
//
//	@Override
//	protected List<Work> executing(AeiObjects aeiObjects, Manual manual) throws Exception {
//		List<Work> results = new ArrayList<>();
//		// ManualTaskIdentityMatrix matrix =
//		// executingManualTaskIdentityMatrix(aeiObjects, manual);
//		ManualTaskIdentityMatrix matrix = aeiObjects.getWork().getManualTaskIdentityMatrix();
////		List<TaskCompleted> taskCompleteds = aeiObjects.getJoinInquireTaskCompletedsRouteNameAvailableWithActivityToken(
////				aeiObjects.getWork().getActivityToken());
//		// 由于退回存在空名称的路由
//		List<TaskCompleted> taskCompleteds = aeiObjects
//				.getJoinInquireTaskCompletedsWithActivityToken(aeiObjects.getWork().getActivityToken());
//		executingCompletedIdentityInTaskCompleteds(aeiObjects, manual, matrix, taskCompleteds);
//		// 发送ProcessingSignal
//		aeiObjects.getProcessingAttributes().push(Signal.manualExecute(aeiObjects.getWork().getActivityToken(), manual,
//				Objects.toString(manual.getManualMode(), ""), matrix.flat()));
//		if (matrix.isEmpty() && (!taskCompleteds.isEmpty())) {
//			results.add(aeiObjects.getWork());
//			List<Task> tasks = aeiObjects.getTasks().stream().filter(
//					t -> StringUtils.equalsIgnoreCase(t.getActivityToken(), aeiObjects.getWork().getActivityToken()))
//					.collect(Collectors.toList());
//			tasks.stream().forEach(aeiObjects::deleteTask);
//			uncompletedTaskToRead(aeiObjects, manual, tasks);
//			// aeiObjects.business().organization().identity().listObject(null)
//		} else {
//			if (matrix.isEmpty()) {
//				// 在添加分支的情况下需要在这里重新计算matrix
//				matrix = manual.identitiesToManualTaskIdentityMatrix(calculateTaskIdentities(aeiObjects, manual));
//			}
//			switch (manual.getManualMode()) {
//			case parallel:
//				this.parallel(aeiObjects, manual, matrix, taskCompleteds);
//				break;
//			case queue:
//				this.queue(aeiObjects, manual, matrix, taskCompleteds);
//				break;
//			case grab:
//			case single:
//			default:
//				this.single(aeiObjects, manual, matrix, taskCompleteds);
//			}
//			// 可能在处理过程中删除了所有的待办,比如有优先路由
//			if (matrix.isEmpty()) {
//				results.add(aeiObjects.getWork());
//			}
//		}
//		aeiObjects.getWork().setManualTaskIdentityMatrix(matrix);
//		return results;
//	}
//
////    @SuppressWarnings("unchecked")
////    @Deprecated(forRemoval = true, since = "8.0")
////    private ManualTaskIdentityMatrix executingManualTaskIdentityMatrix(AeiObjects aeiObjects, Manual manual)
////            throws Exception {
////        ManualTaskIdentityMatrix matrix = aeiObjects.getWork().getManualTaskIdentityMatrix();
////        List<String> exists = matrix.flat();
////        matrix.remove(ListUtils.subtract(exists, aeiObjects.business().organization().identity().list(exists)));
////        if (matrix.isEmpty()) {
////            List<String> identities = new ArrayList<>();
////            // 兼容7.2.0之前的版本
////            List<String> deprecatedIdentities = new ArrayList<>();
////            if (PropertyUtils.isReadable(aeiObjects.getWork(), DEPRECATED_WORK_FIELD_MANUALTASKIDENTITYLIST)) {
////                deprecatedIdentities.addAll((List<String>) PropertyUtils.getProperty(aeiObjects.getWork(),
////                        DEPRECATED_WORK_FIELD_MANUALTASKIDENTITYLIST));
////                identities.addAll(deprecatedIdentities);
////                identities = aeiObjects.business().organization().identity().list(identities);
////            }
////            if (identities.isEmpty() && aeiObjects.getJoinInquireTaskCompletedsRouteNameAvailableWithActivityToken(
////                    aeiObjects.getWork().getActivityToken()).isEmpty()) {
////                identities = calculateTaskIdentities(aeiObjects, manual);
////                LOGGER.info("工作设置的处理人 {} 已经全部无效, 且没有已办, 重新计算当前环节所有处理人进行处理, 标题:{}, id:{}, 强制设置的处理人:{}.",
////                        () -> StringUtils.join(deprecatedIdentities), aeiObjects.getWork()::getTitle,
////                        aeiObjects.getWork()::getId, identities::toString);
////            }
////            matrix = manual.identitiesToManualTaskIdentityMatrix(identities);
////        }
////        // 重新绑定到对象上.
////        aeiObjects.getWork().setManualTaskIdentityMatrix(matrix);
////        return matrix;
////    }
//
//	/**
//	 * 将已办人员从办理身份矩阵中剔除,如果选择了'同一处理人不同身份待办合并处理一次',按人员再剔除一遍
//	 * 
//	 * @param aeiObjects
//	 * @param manual
//	 * @param matrix
//	 * @param taskCompleteds
//	 * @throws Exception
//	 */
//	private void executingCompletedIdentityInTaskCompleteds(AeiObjects aeiObjects, Manual manual,
//			ManualTaskIdentityMatrix matrix, List<TaskCompleted> taskCompleteds) throws Exception {
//		if (!matrix.isEmpty()) {
//			List<String> identities = matrix.flat();
//			taskCompleteds.stream().forEach(o -> identities.removeAll(matrix.completed(o.getIdentity())));
//			// 如果选择了'同一处理人不同身份待办合并处理一次',按人员再剔除一遍
//			if (BooleanUtils.isNotFalse(manual.getProcessingTaskOnceUnderSamePerson()) && (!identities.isEmpty())) {
//				List<String> people = ListTools.extractProperty(taskCompleteds, TaskCompleted.person_FIELDNAME,
//						String.class, true, true);
//				aeiObjects.business().organization().person().listPairIdentity(identities).stream().forEach(p -> {
//					if (people.contains(p.getPerson())) {
//						matrix.completed(p.getIdentity());
//					}
//				});
//			}
//		}
//	}
//
//	@Override
//	protected void executingCommitted(AeiObjects aeiObjects, Manual manual, List<Work> works) throws Exception {
//		// Manual Work 还没有处理完 发生了停留,出发了停留事件
//		if ((ListTools.isEmpty(works)) && (!aeiObjects.getCreateTasks().isEmpty())) {
//			boolean hasManualStayScript = this.hasManualStayScript(manual);
//			boolean processHasManualStayScript = this.hasManualStayScript(aeiObjects.getProcess());
//			if (hasManualStayScript || processHasManualStayScript) {
//				ScriptContext scriptContext = aeiObjects.scriptContext();
//				Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
//				WorkContext workContext = (WorkContext) bindings.get(ScriptingFactory.BINDING_NAME_WORKCONTEXT);
//				// 只有一条待办绑定到task
//				if (aeiObjects.getCreateTasks().size() == 1) {
//					workContext.bindTask(aeiObjects.getCreateTasks().get(0));
//				}
//				if (processHasManualStayScript) {
//					JsonScriptingExecutor
//							.eval(aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
//									aeiObjects.getProcess(), Business.EVENT_MANUALSTAY), scriptContext);
//				}
//				if (hasManualStayScript) {
//					JsonScriptingExecutor
//							.eval(aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
//									aeiObjects.getActivity(), Business.EVENT_MANUALSTAY), scriptContext);
//				}
//				// 解除绑定
//				workContext.bindTask(null);
//			}
//		}
//	}
//
//	@Override
//	protected List<Route> inquiring(AeiObjects aeiObjects, Manual manual) throws Exception {
//		// 发送ProcessingSignal
//		aeiObjects.getProcessingAttributes()
//				.push(Signal.manualInquire(aeiObjects.getWork().getActivityToken(), manual));
//		List<Route> results = new ArrayList<>();
//		Optional<Route> optional = inquiringFromGoBackStore(aeiObjects);
//		if (optional.isPresent()) {
//			markJumpAtWorkLog(aeiObjects, aeiObjects.getWork().getGoBackStore());
//			// 设置处理人
//			aeiObjects.getWork().getProperties().setManualForceTaskIdentityList(
//					aeiObjects.getWork().getGoBackStore().getManualTaskIdentityMatrix().flat());
//			// 清理掉goBackStore
//			aeiObjects.getWork().setGoBackStore(null);
//			// 清理掉退回到的activityToken标志
//			aeiObjects.getWork().setGoBackActivityToken(null);
//			results.add(optional.get());
//			return results;
//		}
//		// 仅有单条路由
//		if (aeiObjects.getRoutes().size() == 1) {
//			results.add(aeiObjects.getRoutes().get(0));
//		} else if (aeiObjects.getRoutes().size() > 1) {
//			// 存在多条路由
//			optional = inquiringFromTaskCompleted(aeiObjects, manual, results);
//			if (optional.isPresent()) {
//				results.add(optional.get());
//			}
//		} else {
//			// 无法找到合适的路由那么默认选择走第一条
//			results.add(aeiObjects.getRoutes().get(0));
//			// throw new ExceptionManualNotRoute(manual.getId());
//		}
//		if (!results.isEmpty()) {
//			// 清理掉强制的指定的处理人
//			aeiObjects.getWork().getProperties().setManualForceTaskIdentityList(new ArrayList<>());
//			// 清理掉goBackStore
//			aeiObjects.getWork().setGoBackStore(null);
//			// 清理掉退回到的activityToken标志
//			aeiObjects.getWork().setGoBackActivityToken(null);
//		}
//		return results;
//	}
//
//	private void markJumpAtWorkLog(AeiObjects aeiObjects, GoBackStore goBackStore) throws Exception {
//		Optional<WorkLog> opt = aeiObjects.getWorkLogs().stream().filter(o -> BooleanUtils.isNotTrue(o.getConnected()))
//				.filter(o -> StringUtils.equals(o.getFromActivityToken(), aeiObjects.getWork().getActivityToken()))
//				.findFirst();
//		if (opt.isPresent()) {
//			opt.get().setGoBackFromActivityToken(goBackStore.getActivityToken());
//			opt.get().setGoBackFromActivity(goBackStore.getActivity());
//			opt.get().setGoBackFromActivityType(goBackStore.getActivityType());
//		}
//
//	}
//
//	private Optional<Route> inquiringFromTaskCompleted(AeiObjects aeiObjects, Manual manual, List<Route> results)
//			throws Exception {
//		Collection<String> routeNames = aeiObjects.getRoutes().stream().map(Route::getName).collect(Collectors.toSet());
//		List<TaskCompleted> taskCompleteds = aeiObjects
//				.getJoinInquireTaskCompletedsWithActivityToken(aeiObjects.getWork().getActivityToken()).stream()
//				.filter(t -> routeNames.contains(t.getRouteName())).collect(Collectors.toList());
//		String name = this.choiceRouteName(taskCompleteds, aeiObjects.getRoutes(), manual);
//		return aeiObjects.getRoutes().stream().filter(r -> StringUtils.equalsIgnoreCase(name, r.getName())).findFirst();
//
//	}
//
//	/**
//	 * 如果work中有goBackStore那么从goBackStore中创建一个临时路由作为返回值
//	 * 
//	 * @param aeiObjects
//	 * @throws Exception
//	 */
//	private Optional<Route> inquiringFromGoBackStore(AeiObjects aeiObjects) throws Exception {
//		if (null != aeiObjects.getWork().getGoBackStore()) {
//			Activity activity = aeiObjects.business().element()
//					.getActivity(aeiObjects.getWork().getGoBackStore().getActivity());
//			if (null != activity) {
//				Route route = new Route();
//				route.setActivity(activity.getId());
//				route.setActivityType(activity.getActivityType());
//				return Optional.of(route);
//			}
//		}
//		return Optional.empty();
//	}
//
//	/**
//	 * 判断的逻辑如下:
//	 * 1.是否有用户选择了"直接返回优先路由(soleDirect)",同时判断该值是否在路由列表中(可能修改路由名称),如果有就直接返回该值.
//	 * 2.是否有用户选择了"优先路由(sole)",同时判断该值是否在路由列表中(可能修改路由名称),如果有就直接返回该值.
//	 * 3.如果没有soleDirect或者sole被选择,那么根据活动类型进行判断,如果是并行活动(parallel)那么选择最多的路由决策,如果有多个路由决策同样数量,那么选择时间上最晚的那组,如果是single(单人),queue(串行),grab(抢办)那么最后的路由决策作为返回值(需要判断是否在路由列表中).
//	 * 
//	 * @param taskCompleteds 按创建时间正序排列好的已办
//	 * @param manual         人工活动节点
//	 * @param routes         离开活动节点的路由列表
//	 * @return 路由名称
//	 * @throws Exception
//	 */
//	private String choiceRouteName(List<TaskCompleted> taskCompleteds, List<Route> routes, Manual manual)
//			throws Exception {
//		final Triple<List<TaskCompleted>, List<Route>, Manual> triple = Triple.of(taskCompleteds, routes, manual);
//		Optional<String> optional = Stream
//				.<Function<Triple<List<TaskCompleted>, List<Route>, Manual>, Optional<String>>>of(
//						this::chooseSoleDirectIfExist, this::chooseSoleIfExist, this::chooseMaxCountOrLatest)
//				.map(f -> f.apply(triple)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
//		if (optional.isPresent()) {
//			return optional.get();
//		} else {
//			throw new ExceptionChoiceRouteNameError(ListTools.extractProperty(taskCompleteds,
//					TaskCompleted.ROUTENAME_FIELDNAME, String.class, false, false));
//		}
//	}
//
//	/**
//	 * 判断是否有选择了直接返回优先路由的路由决策被选择,如果有就直接返回该路由名称,这里的遍历顺序需要保持正序,先选择先执行.
//	 * 
//	 * @param list
//	 * @param routes
//	 * @return
//	 */
//
//	private Optional<String> chooseSoleDirectIfExist(final Triple<List<TaskCompleted>, List<Route>, Manual> triple) {
//		return chooseIfExist(triple, r -> BooleanUtils.isTrue(r.getSoleDirect()));
//	}
//
//	/**
//	 * 判断是否有选择了优先路由的路由决策被选择,如果有就直接返回该路由名称,这里的遍历顺序需要保持正序,先选择先执行.
//	 * 
//	 * @param list
//	 * @param routes
//	 * @return
//	 */
//	private Optional<String> chooseSoleIfExist(final Triple<List<TaskCompleted>, List<Route>, Manual> triple) {
//		return chooseIfExist(triple, r -> BooleanUtils.isTrue(r.getSole()));
//	}
//
//	private Optional<String> chooseIfExist(final Triple<List<TaskCompleted>, List<Route>, Manual> triple,
//			final Predicate<Route> predicate) {
//		final List<TaskCompleted> taskCompleteds = triple.getLeft();
//		final List<Route> routes = triple.getMiddle();
//		final Collection<String> names = routes.stream().filter(predicate).map(Route::getName)
//				.collect(Collectors.toSet());
//		return taskCompleteds.stream().map(TaskCompleted::getRouteName).filter(names::contains).findFirst();
//	}
//
//	/**
//	 * 已办中获取数量最多的路由决策,如果有组路由决策数量一样多,那么选择时间上最后被选择的路由决策,获取后需要进行判断是否在routes列表中
//	 * 
//	 * @param list
//	 * @param routes
//	 * @return
//	 * @throws Exception
//	 */
//	private Optional<String> chooseMaxCountOrLatest(Triple<List<TaskCompleted>, List<Route>, Manual> triple) {
////        if (!Objects.equals(ManualMode.parallel, triple.getRight().getManualMode())) {
////            return triple.getLeft().stream().sorted(Comparator.comparing(TaskCompleted::getCreateTime).reversed())
////                    .findFirst().map(TaskCompleted::getRouteName);
////        }
//		return triple.getLeft().stream().collect(Collectors.groupingBy(TaskCompleted::getRouteName)).entrySet().stream()
//				.max((o1, o2) -> {
//					int c = o1.getValue().size() - o2.getValue().size();
//					if (c == 0) {
//						return ObjectUtils.compare(
//								o1.getValue().stream().mapToLong(t -> t.getCreateTime().getTime()).max().getAsLong(),
//								o2.getValue().stream().mapToLong(t -> t.getCreateTime().getTime()).max().getAsLong());
//					} else {
//						return c;
//					}
//				}).map(Entry::getKey);
//	}
//
//	// 是否有优先路由
//	private void single(AeiObjects aeiObjects, Manual manual, ManualTaskIdentityMatrix matrix,
//			List<TaskCompleted> taskCompleteds) throws Exception {
//		if (soleDirect(aeiObjects, taskCompleteds)) {
//			matrix.clear();
//			List<Task> tasks = aeiObjects.getTasks().stream().filter(
//					t -> StringUtils.equalsIgnoreCase(t.getActivityToken(), aeiObjects.getWork().getActivityToken()))
//					.collect(Collectors.toList());
//			tasks.stream().forEach(aeiObjects::deleteTask);
//			uncompletedTaskToRead(aeiObjects, manual, tasks);
//		} else {
//			task(aeiObjects, manual, matrix.read());
//		}
//	}
//
//	private void parallel(AeiObjects aeiObjects, Manual manual, ManualTaskIdentityMatrix matrix,
//			List<TaskCompleted> taskCompleteds) throws Exception {
//		// 是否有优先路由
//		if (soleDirect(aeiObjects, taskCompleteds)) {
//			matrix.clear();
//			List<Task> tasks = aeiObjects.getTasks().stream().filter(
//					t -> StringUtils.equalsIgnoreCase(t.getActivityToken(), aeiObjects.getWork().getActivityToken()))
//					.collect(Collectors.toList());
//			tasks.stream().forEach(aeiObjects::deleteTask);
//			uncompletedTaskToRead(aeiObjects, manual, tasks);
//		} else {
//			task(aeiObjects, manual, matrix.flat());
//		}
//	}
//
//	private void queue(AeiObjects aeiObjects, Manual manual, ManualTaskIdentityMatrix matrix,
//			List<TaskCompleted> taskCompleteds) throws Exception {
//		if (soleDirect(aeiObjects, taskCompleteds)) {
//			matrix.clear();
//			List<Task> tasks = aeiObjects.getTasks().stream().filter(
//					t -> StringUtils.equalsIgnoreCase(t.getActivityToken(), aeiObjects.getWork().getActivityToken()))
//					.collect(Collectors.toList());
//			tasks.stream().forEach(aeiObjects::deleteTask);
//			uncompletedTaskToRead(aeiObjects, manual, tasks);
//		} else {
//			task(aeiObjects, manual, matrix.read());
//		}
//	}
//
//	/**
//	 * 存在优先路由,如果有人选择了优先路由那么直接流转.需要判断是否启用了soleDirect,允许存在多个优先路由
//	 * 
//	 * @param aeiObjects
//	 * @param taskCompleteds
//	 * @return
//	 * @throws Exception
//	 */
//	private boolean soleDirect(AeiObjects aeiObjects, List<TaskCompleted> taskCompleteds) throws Exception {
//		final List<String> soleRouteNames = aeiObjects.getRoutes().stream()
//				.filter(r -> BooleanUtils.isTrue(r.getSoleDirect())).map(Route::getName).collect(Collectors.toList());
//		if (!soleRouteNames.isEmpty()) {
//			Optional<TaskCompleted> taskCompleted = taskCompleteds.stream()
//					.filter(t -> soleRouteNames.contains(t.getRouteName())).findFirst();
//			if (taskCompleted.isPresent()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private void task(AeiObjects aeiObjects, Manual manual, List<String> identities) throws Exception {
//		String activityToken = aeiObjects.getWork().getActivityToken();
//		final Set<String> routeNameSet = new HashSet<>(
//				aeiObjects.getRoutes().stream().map(Route::getName).collect(Collectors.toList()));
//		aeiObjects.getTasks().stream().filter(t -> StringUtils.equalsIgnoreCase(t.getActivityToken(), activityToken))
//				.forEach(t -> {
//					if (!identities.contains(t.getIdentity())) {
//						// 不在处理身份中
//						LOGGER.warn("delete a task whose identity does not match, id:{}, identity:{}.", t::getId,
//								t::getIdentity);
//						aeiObjects.deleteTask(t);
//					} else if (!SetUtils.isEqualSet(routeNameSet, new HashSet<>(t.getRouteNameList()))) {
//						// 路由名称发生变化.
//						LOGGER.warn(
//								"update a task whose route name does not match, id:{}, route name:{}, expected route name:{}.",
//								t::getId, () -> StringUtils.join(t.getRouteNameList()),
//								() -> StringUtils.join(routeNameSet));
//						try {
//							aeiObjects.getUpdateTasks().add(t.updateRoute(aeiObjects.getRoutes()));
//							identities.remove(t.getIdentity());
//						} catch (Exception e) {
//							LOGGER.error(e);
//						}
//					} else {
//						identities.remove(t.getIdentity());
//					}
//				});
//		identities.stream().forEach(o -> {
//			try {
//				Task task = Tasks.createTask(aeiObjects, manual, o);
//				aeiObjects.createTask(task);
//				// 将用户可能已经存在的同一环节已办全部标记为不参与流转
//				aeiObjects.getJoinInquireTaskCompletedsWithActivityToken(task.getActivityToken()).stream()
//						.filter(p -> StringUtils.equalsIgnoreCase(p.getPerson(), task.getPerson())).forEach(tc -> {
//							tc.setJoinInquire(false);
//							aeiObjects.getUpdateTaskCompleteds().add(tc);
//						});
//			} catch (Exception e) {
//				LOGGER.error(e);
//			}
//		});
//	}
//
//	@Override
//	protected void inquiringCommitted(AeiObjects aeiObjects, Manual manual) throws Exception {
//		// nothing
//	}
//
//	private List<String> arrivingSameJobActivityExistIdentities(AeiObjects aeiObjects, Manual manual) throws Exception {
//		List<String> exists = new ArrayList<>();
//		aeiObjects.getTasks().stream()
//				.filter(o -> StringUtils.equals(o.getActivity(), manual.getId())
//						&& StringUtils.equals(o.getJob(), aeiObjects.getWork().getJob()))
//				.forEach(o -> exists.add(o.getIdentity()));
//		return exists;
//	}
//
//	private void uncompletedTaskToRead(AeiObjects aeiObjects, Manual manual, List<Task> tasks) {
//		if (BooleanUtils.isTrue(manual.getManualUncompletedTaskToRead())) {
//			tasks.stream().forEach(o -> {
//				try {
//					String identity = aeiObjects.business().organization().identity().get(o.getIdentity());
//					String unit = aeiObjects.business().organization().unit().getWithIdentity(identity);
//					String person = aeiObjects.business().organization().person().getWithIdentity(identity);
//					if (StringUtils.isNotEmpty(identity) && StringUtils.isNotEmpty(unit)
//							&& StringUtils.isNotEmpty(person)) {
//						aeiObjects.getCreateReads().add(new Read(aeiObjects.getWork(), identity, unit, person));
//					}
//				} catch (Exception e) {
//					LOGGER.error(e);
//				}
//			});
//		}
//	}
//}
