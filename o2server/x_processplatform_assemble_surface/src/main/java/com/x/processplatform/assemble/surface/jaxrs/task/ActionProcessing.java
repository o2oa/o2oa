package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.RecordBuilder;
import com.x.processplatform.assemble.surface.TaskBuilder;
import com.x.processplatform.assemble.surface.TaskCompletedBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualProperties.DefineConfig;
import com.x.processplatform.core.entity.element.ManualProperties.GoBackConfig;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.entity.log.SignalStack;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionProcessingWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionProcessingWo;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingSignalWo;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2GoBackWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2GoBackWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionProcessing.class);

	private ActionResult<Wo> result = new ActionResult<>();

	private Wi wi;
	private Task task;
	private WorkLog workLog;
	private String taskCompletedId;
	private String type = TYPE_TASK;
	private Boolean asyncSupported = false;
	private EffectivePerson effectivePerson;
	private List<TaskCompleted> taskCompleteds = new ArrayList<>();
	private List<String> newTaskIds = new ArrayList<>();
	private Exception exception = null;

	private Record rec;
	private String series = StringTools.uniqueToken();
	private Route route;

	private static final String TYPE_APPENDTASK = "appendTask";
	private static final String TYPE_TASK = "task";

	private static final String STRING_PROCESSING = "processing";

	private static final String ACTION_GOBACK = "goBack";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			init(effectivePerson, business, id, jsonElement);
			updateTask(business);
		}

		LinkedBlockingQueue<Wo> responeQueue = new LinkedBlockingQueue<>();

		new Thread(() -> {
			Wo wo = new Wo();
			try {
				if (StringUtils.equals(wi.getAction(), ACTION_GOBACK)) {
					processingGoBack(gson.fromJson(wi.getOption(), OptionGoBack.class));
				} else if (StringUtils.equals(type, TYPE_APPENDTASK)) {
					processingAppendTask();
				} else {
					processingTask();
				}
				manualAfterProcessing();
				wo = Wo.copier.copy(rec);
			} catch (Exception e) {
				exception = e;
			} finally {
				try {
					responeQueue.put(wo);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					exception = e;
				}
			}
		}, String.format("%s:processing:%s", ActionProcessing.class.getName(), id)).start();

		startSignalThreadIfAsyncSupported(effectivePerson, id, responeQueue);

		Wo wo = responeQueue.poll(300, TimeUnit.SECONDS);

		if (exception != null) {
			throw exception;
		}
		result.setData(wo);
		return result;
	}

	/**
	 * 调用人工环节工作流转后执行脚本
	 * 
	 * @throws Exception
	 */
	private void manualAfterProcessing() throws Exception {
		ActionManualAfterProcessingWi req = new ActionManualAfterProcessingWi();
		req.setTask(task);
		req.setRecord(rec);
		ThisApplication.context().applications()
				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "manual", "after", "processing"), req, task.getJob())
				.getData(WrapBoolean.class);
	}

	private void startSignalThreadIfAsyncSupported(EffectivePerson effectivePerson, String id,
			LinkedBlockingQueue<Wo> responeQueue) {
		if (BooleanUtils.isNotFalse(this.asyncSupported)) {
			new Thread(() -> {
				RespProcessingSignal resp = null;
				try {
					resp = ThisApplication.context().applications()
							.getQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
									Applications.joinQueryUri("work", task.getWork(), "series", series, "activitytoken",
											this.task.getActivityToken(), STRING_PROCESSING, "signal"),
									task.getJob())
							.getData(RespProcessingSignal.class);
				} catch (Exception e) {
					exception = e;
				} finally {
					Wo wo = new Wo();
					wo.setOccurSignalStack(true);
					if ((null != resp) && (null != resp.getSignalStack()) && (!resp.getSignalStack().isEmpty())) {
						wo.setSignalStack(resp.getSignalStack());
					} else {
						wo.setSignalStack(new SignalStack());
					}
					try {
						responeQueue.put(wo);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						exception = e;
					}
				}
			}, String.format("%s:processingSignal:%s", ActionProcessing.class.getName(), id)).start();
		}
	}

	private void init(EffectivePerson effectivePerson, Business business, String id, JsonElement jsonElement)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		this.effectivePerson = effectivePerson;
		this.wi = this.convertToWrapIn(jsonElement, Wi.class);
		this.task = emc.find(id, Task.class);
		if (null == this.task) {
			throw new ExceptionEntityNotExist(id, Task.class);
		}
		// 获取当前环节已经完成的待办
		this.taskCompleteds = emc.listEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
				task.getActivityToken());
		this.workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, task.getJob(),
				WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());

		if (null == workLog) {
			throw new ExceptionEntityNotExist(WorkLog.class);
		}
		Work work = emc.find(this.task.getWork(), Work.class);
		if (null == work) {
			throw new ExceptionEntityNotExist(this.task.getWork(), Work.class);
		}
		if ((!effectivePerson.isCipher()) && effectivePerson.isNotPerson(this.task.getPerson())) {
			throw new ExceptionAccessDenied(effectivePerson, this.task);
		}
	}

	private void updateTask(Business business) throws Exception {
		business.entityManagerContainer().beginTransaction(Task.class);
		// 如果有输入新的路由决策覆盖原有决策
		if (StringUtils.isNotEmpty(this.wi.getRouteName())) {
			this.task.setRouteName(this.wi.getRouteName());
		}
		// 如果有新的流程意见那么覆盖原有流程意见,null表示没有传过来,""可能是前端传过来的改为空值.
		if (null != this.wi.getOpinion()) {
			this.task.setOpinion(this.wi.getOpinion());
		}
		// 强制覆盖多媒体意见
		this.task.setMediaOpinion(this.wi.getMediaOpinion());
		if (StringUtils.isEmpty(task.getOpinion())) {
			Process process = business.process().pick(task.getProcess());
			if ((null != process) && BooleanUtils.isTrue(process.getRouteNameAsOpinion())) {
				// 将路由名作为办理意见写入
				task.setOpinion(task.getRouteName());
			}
		}
		seeManualRoute(business);
		updateTaskRouteAlias();
		business.entityManagerContainer().commit();
	}

	private void updateTaskRouteAlias() {
		// 更新routeAlias
		if ((null != this.route) && (!StringUtils.equals(route.getAlias(), task.getRouteAlias()))) {
			this.task.setRouteAlias(this.route.getAlias());
		} else if (null == route) {
			this.task.setRouteAlias("");
		}
	}

	private void seeManualRoute(Business business) throws Exception {
		Manual manual = business.manual().pick(this.task.getActivity());
		if (null != manual) {
			for (Route o : business.route().pick(manual.getRouteList())) {
				if (StringUtils.equals(o.getName(), this.task.getRouteName())) {
					this.route = o;
					break;
				}
			}
			if (null != this.route) {
				this.asyncSupported = BooleanUtils.isNotFalse(this.route.getAsyncSupported());
				if (StringUtils.equals(this.route.getType(), Route.TYPE_APPENDTASK)
						&& StringUtils.equals(manual.getId(), this.route.getActivity())) {
					this.type = TYPE_APPENDTASK;
				}
			}
		}
	}

	private void processingAppendTask() throws Exception {
		this.processingAppendTaskAppend();
		this.taskCompletedId = this.processingProcessingTask(TaskCompleted.PROCESSINGTYPE_APPENDTASK);
		this.processingProcessingWork(ProcessingAttributes.TYPE_APPENDTASK);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.newTaskIds = emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, task.getJob(), Task.series_FIELDNAME,
					this.series);
		}
		this.rec = RecordBuilder.ofTaskProcessing(Record.TYPE_APPENDTASK, workLog, task, taskCompletedId, newTaskIds);
		// 加签也记录流程意见和路由决策
		this.rec.getProperties().setOpinion(this.task.getOpinion());
		this.rec.getProperties().setRouteName(this.task.getRouteName());
		rec.setId(RecordBuilder.processing(rec));
		this.processingUpdateTaskCompleted();
		this.processingUpdateTask();
	}

	// 8.2以后版本使用reset替代append
	private void processingAppendTaskAppend() throws Exception {
		V2ResetWi req = new V2ResetWi();
		req.setDistinguishedNameList(this.wi.getAppendTaskIdentityList());
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", "v2", this.task.getId(), "reset"), req, this.task.getJob())
				.getData(WrapStringList.class);
	}

	private void processingTask() throws Exception {
		this.taskCompletedId = this.processingProcessingTask(TaskCompleted.PROCESSINGTYPE_TASK);
		this.processingProcessingWork(ProcessingAttributes.TYPE_TASK);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.newTaskIds = emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, task.getJob(), Task.series_FIELDNAME,
					this.series);
		}
		// 流程流转到取消环节，此时工作已被删除.flag =true 代表存在,false 已经被删除
		boolean flag = true;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, task.getJob()) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, task.getJob()) == 0)) {
				flag = false;
			}
		}
		if (flag) {
			this.rec = RecordBuilder.ofTaskProcessing(Record.TYPE_TASK, workLog, task, taskCompletedId, newTaskIds);
			// 加签也记录流程意见和路由决策
			this.rec.getProperties().setOpinion(this.task.getOpinion());
			this.rec.getProperties().setRouteName(this.task.getRouteName());
			rec.setId(RecordBuilder.processing(rec));
			this.processingUpdateTaskCompleted();
			this.processingUpdateTask();
		} else {
			this.rec = RecordBuilder.ofTaskProcessing(Record.TYPE_TASK, workLog, task, taskCompletedId, newTaskIds);
			// 这里的record不需要写入到数据库,work和workCompleted都消失了,可能走了cancel环节,这里的rec仅作为返回值生成wo
			this.rec.getProperties().setOpinion(this.task.getOpinion());
			this.rec.getProperties().setRouteName(this.task.getRouteName());
			rec.setCompleted(true);
		}
	}

	private String processingProcessingTask(String processType) throws Exception {
		ProcessingWi req = new ProcessingWi();
		req.setProcessingType(processType);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), STRING_PROCESSING), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionProcessingTask(task.getId());
		} else {
			// 获得已办id
			return resp.getId();
		}
	}

	private void processingProcessingWork(String workProcessingType) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setIgnoreEmpowerIdentityList(wi.getIgnoreEmpowerIdentityList());
		req.setType(workProcessingType);
		req.setSeries(this.series);
		req.setPerson(task.getPerson());
		req.setIdentity(task.getIdentity());
		WoId resp = ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), STRING_PROCESSING), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionWorkProcessing(task.getId());
		}
	}

	private void processingUpdateTaskCompleted() throws Exception {

		TaskCompletedBuilder.updateNextTaskIdentity(taskCompletedId,
				rec.getProperties().getNextManualTaskIdentityList(), task.getJob());
	}

	private void processingUpdateTask() throws Exception {

		TaskBuilder.updatePrevTaskIdentity(newTaskIds, taskCompleteds, task);

		List<Task> empowerTasks = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (Task o : emc.list(Task.class, newTaskIds)) {
				if (StringUtils.isNotEmpty(o.getEmpowerFromIdentity())
						&& (!StringUtils.equals(o.getEmpowerFromIdentity(), o.getIdentity()))) {
					empowerTasks.add(o);
				}
			}
		}
		List<Record> records = new ArrayList<>();
		if (!empowerTasks.isEmpty()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				for (Task o : empowerTasks) {
					Record r = RecordBuilder.ofTaskEmpower(o,
							business.organization().person().getWithIdentity(o.getEmpowerFromIdentity()),
							business.organization().unit().getWithIdentity(o.getEmpowerFromIdentity()));
					records.add(r);
				}
			}
		}
		if (!records.isEmpty()) {
			records.stream().forEach(r -> {
				try {
					r.setId(RecordBuilder.processing(r));
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	/**
	 * 处理退回操作
	 * 
	 * @throws Exception
	 */
	private void processingGoBack(OptionGoBack option) throws Exception {
		WorkLogTree workLogTree = null;
		V2GoBackWi req = new V2GoBackWi();
		Manual manual = null;
		Work work = null;
		Triple<WorkLog, String, List<String>> param = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			manual = (Manual) business.getActivity(task.getActivity(), ActivityType.manual);
			if (null == manual) {
				throw new ExceptionEntityNotExist(task.getActivity(), Manual.class);
			}
			work = emc.find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
			}
			workLogTree = workLogTree(business, task.getJob());
			Node node = workLogTree.find(workLog);
			Nodes nodes = workLogTree.up(node);
			List<WorkLog> workLogs = goBackTruncateWorkLog(nodes, work.getGoBackActivityToken());
			param = goBackParam(business, manual, option, workLogs);
		}
		req.setActivity(param.first().getFromActivity());
		req.setActivityToken(param.first().getFromActivityToken());
		req.setWay(param.second());
		req.setDistinguishedNameList(param.third());
		this.taskCompletedId = this.processingProcessingTask(TaskCompleted.PROCESSINGTYPE_GOBACK);
		V2GoBackWo resp = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", task.getWork(), "goback"), req, this.task.getJob())
				.getData(V2GoBackWo.class);
		if (BooleanUtils.isNotTrue(resp.getValue())) {
			throw new ExceptionGoBack(req.getActivity(), req.getActivity());
		}
		this.processingWorkGoBack(task.getWork(), task.getJob());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.newTaskIds = emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, task.getJob(), Task.series_FIELDNAME,
					this.series);
		}
		// 流程流转到取消环节，此时工作已被删除.flag =true 代表存在,false 已经被删除
		boolean flag = true;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, task.getJob()) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, task.getJob()) == 0)) {
				flag = false;
			}
		}
		if (flag) {
			this.rec = RecordBuilder.ofTaskProcessing(Record.TYPE_TASK, workLog, task, taskCompletedId, newTaskIds);
			// 加签也记录流程意见和路由选择
			this.rec.getProperties().setOpinion(this.task.getOpinion());
			this.rec.getProperties().setRouteName(this.task.getRouteName());
			rec.setId(RecordBuilder.processing(rec));
			this.processingUpdateTaskCompleted();
			this.processingUpdateTask();
		} else {
			this.rec = RecordBuilder.ofTaskProcessing(Record.TYPE_TASK, workLog, task, taskCompletedId, newTaskIds);
			// 这里的record不需要写入到数据库,work和workCompleted都消失了,可能走了cancel环节,这里的rec仅作为返回值生成wo
			this.rec.getProperties().setOpinion(this.task.getOpinion());
			this.rec.getProperties().setRouteName(this.task.getRouteName());
			rec.setCompleted(true);
		}
	}

	/**
	 * 
	 * 先过滤掉connected=false<br>
	 * 过滤掉非manual环节<br>
	 * 过滤掉workLog的type=goBack<br>
	 * 如果有记录goBackActivityToken值,那么仅从这个位置开始.
	 * 
	 * @param nodes
	 * @param activityToken
	 * @return
	 */
	private List<WorkLog> goBackTruncateWorkLog(Nodes nodes, String activityToken) {
		List<WorkLog> list = new ArrayList<>();
		nodes.stream()
				.filter(o -> BooleanUtils.isTrue(o.getWorkLog().getConnected())
						&& Objects.equals(o.getWorkLog().getFromActivityType(), ActivityType.manual)
						&& (!StringUtils.equalsIgnoreCase(o.getWorkLog().getType(), ProcessingAttributes.TYPE_GOBACK)))
				.forEach(o -> {
					if (StringUtils.equalsIgnoreCase(o.getWorkLog().getFromActivityToken(), activityToken)) {
						list.clear();
					} else {
						list.add(o.getWorkLog());
					}
				});
		return list;
	}

	private void processingWorkGoBack(String workId, String job) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_GOBACK);
		req.setSeries(series);
		req.setForceJoinAtArrive(true);
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", workId, "processing"), req, job).getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionGoBackCallServiceProcessing(workId);
		}
	}

	private Triple<WorkLog, String, List<String>> goBackParam(Business business, Manual manual, OptionGoBack option,
			List<WorkLog> workLogs) throws Exception {
		Pair<WorkLog, String> pair = null;
		if ((null != manual.getGoBackConfig())
				&& StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getType(), GoBackConfig.TYPE_PREV)) {
			pair = this.goBackParamPrev(manual, option, workLogs);
		} else if ((null != manual.getGoBackConfig())
				&& StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getType(), GoBackConfig.TYPE_DEFINE)) {
			pair = this.goBackParamDefine(manual, option, workLogs);
		} else {
			pair = this.goBackParamAny(manual, option, workLogs);
		}
		if (null == pair.first()) {
			throw new ExceptionGoBackWorkLog(option.getActivity());
		}
		if (StringUtils.isBlank(pair.second())) {
			throw new ExceptionGoBackWay(option.getActivity());
		}
		List<String> third = business.entityManagerContainer()
				.listEqualAndEqualAndEqual(TaskCompleted.class, TaskCompleted.joinInquire_FIELDNAME, true,
						TaskCompleted.activityToken_FIELDNAME, pair.first().getFromActivityToken(),
						TaskCompleted.job_FIELDNAME, task.getJob())
				.stream().map(TaskCompleted::getIdentity).distinct().collect(Collectors.toList());
		if (ListTools.isEmpty(third)) {
			throw new ExceptionGoBackIdentityList();
		}
		return Triple.of(pair, third);
	}

	/**
	 * 在 type=prev的设置下获取调用后台goBack所需参数
	 * 
	 * @param manual
	 * @param option
	 * @param nodes
	 * @return
	 */
	private Pair<WorkLog, String> goBackParamPrev(Manual manual, OptionGoBack option, List<WorkLog> workLogs) {
		WorkLog first = null;
		String second = null;
		Optional<WorkLog> opt = workLogs.stream()
				.filter(o -> BooleanUtils.isTrue(o.getConnected())
						&& Objects.equals(o.getFromActivityType(), ActivityType.manual)
						&& (!StringUtils.equalsIgnoreCase(o.getType(), ProcessingAttributes.TYPE_GOBACK))
						&& StringUtils.equalsIgnoreCase(o.getFromActivity(), option.getActivity()))
				.findFirst();
		if (opt.isPresent()) {
			first = opt.get();
			second = StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getWay(), GoBackConfig.WAY_CUSTOM)
					? option.getWay()
					: manual.getGoBackConfig().getWay();
			second = goBackParamDefaultWay(second);
		}
		return Pair.of(first, second);
	}

	/**
	 * 在 type=define的设置下获取调用后台goBack所需参数
	 * 
	 * @param manual
	 * @param option
	 * @param nodes
	 * @return
	 */
	private Pair<WorkLog, String> goBackParamDefine(Manual manual, OptionGoBack option, List<WorkLog> workLogs) {
		WorkLog first = null;
		String second = null;
		Optional<WorkLog> opt = workLogs.stream()
				.filter(o -> BooleanUtils.isTrue(o.getConnected())
						&& (!StringUtils.equalsIgnoreCase(o.getType(), ProcessingAttributes.TYPE_GOBACK))
						&& StringUtils.equalsIgnoreCase(o.getFromActivity(), option.getActivity()))
				.findFirst();
		if (opt.isPresent()) {
			Optional<DefineConfig> optDefineConfig = manual.getGoBackConfig().getDefineConfigList().stream()
					.filter(o -> StringUtils.equalsIgnoreCase(o.getActivity(), opt.get().getFromActivity()))
					.findFirst();
			if (optDefineConfig.isPresent()) {
				first = opt.get();
				if (StringUtils.equalsIgnoreCase(optDefineConfig.get().getWay(), GoBackConfig.WAY_DEFAULT)) {
					if (StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getWay(), GoBackConfig.WAY_CUSTOM)) {
						second = option.getWay();
					} else {
						second = manual.getGoBackConfig().getWay();
					}
				} else if (StringUtils.equalsIgnoreCase(optDefineConfig.get().getWay(), GoBackConfig.WAY_CUSTOM)) {
					second = option.getWay();
				} else {
					second = optDefineConfig.get().getWay();
				}
				second = goBackParamDefaultWay(second);
			}
		}
		return Pair.of(first, second);
	}

	/**
	 * 在 type=any的设置下获取调用后台goBack所需参数
	 * 
	 * @param manual
	 * @param option
	 * @param nodes
	 * @return
	 */
	private Pair<WorkLog, String> goBackParamAny(Manual manual, OptionGoBack option, List<WorkLog> workLogs) {
		WorkLog first = null;
		String second = null;
		Optional<WorkLog> opt = workLogs.stream()
				.filter(o -> BooleanUtils.isTrue(o.getConnected())
						&& (!StringUtils.equalsIgnoreCase(o.getType(), ProcessingAttributes.TYPE_GOBACK))
						&& StringUtils.equalsIgnoreCase(o.getFromActivity(), option.getActivity()))
				.findFirst();
		if (opt.isPresent()) {
			first = opt.get();
			second = StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getWay(), GoBackConfig.WAY_CUSTOM)
					? option.getWay()
					: manual.getGoBackConfig().getWay();
			second = goBackParamDefaultWay(second);
		}
		return Pair.of(first, second);
	}

	private String goBackParamDefaultWay(String way) {
		if (StringUtils.equalsIgnoreCase(way, GoBackConfig.WAY_STEP)) {
			return GoBackConfig.WAY_STEP;
		}
		return GoBackConfig.WAY_JUMP;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionProcessing.Wo")
	public static class Wo extends ActionProcessingWo {

		private static final long serialVersionUID = -1771383649634969945L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class RespProcessingSignal extends ActionProcessingSignalWo {

		private static final long serialVersionUID = -8806173185445267895L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionProcessing.Wi")
	public static class Wi extends ActionProcessingWi {

		private static final long serialVersionUID = 76807621172437765L;

	}

	public static final class OptionGoBack {

		// 退回的活动对象
		private String activity;

		// 路由方式
		private String way;

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getWay() {
			return way;
		}

		public void setWay(String way) {
			this.way = way;
		}

	}

}