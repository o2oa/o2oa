package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
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
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingSignalWo;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2GoBackWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2GoBackWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionProcessing.class);

	private ActionResult<Wo> result = new ActionResult<>();

	private static final String TYPE_APPENDTASK = "appendTask";
	private static final String TYPE_TASK = "task";

	private static final String STRING_PROCESSING = "processing";

	private static final String ACTION_GOBACK = "goBack";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(effectivePerson, id, jsonElement);

		LinkedBlockingQueue<Wo> responeQueue = new LinkedBlockingQueue<>();

		new Thread(() -> {
			Wo wo = new Wo();
			try {
				Record rec = null;
				if (StringUtils.equals(param.action, ACTION_GOBACK)) {
					rec = processingGoBack(param, gson.fromJson(param.option, OptionGoBack.class));
				} else if (StringUtils.equals(param.type, TYPE_APPENDTASK)) {
					rec = processingAppendTask(param);
				} else {
					rec = processingTask(param);
				}
				manualAfterProcessing(param, rec);
				wo = Wo.copier.copy(rec);
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				try {
					responeQueue.put(wo);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}, String.format("%s:processing:%s", ActionProcessing.class.getName(), id)).start();

		startSignalThreadIfAsyncSupported(param, id, responeQueue);

		Wo wo = responeQueue.poll(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	private Param init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			param.action = wi.getAction();
			param.option = wi.getOption();
			param.routeName = wi.getRouteName();
			param.opinion = wi.getOpinion();
			param.mediaOpinion = wi.getMediaOpinion();
			param.ignoreEmpowerIdentityList = wi.getIgnoreEmpowerIdentityList();
			// 兼容合并distinguishedNameList和appendTaskIdentityList
			param.distinguishedNameList = business.organization().distinguishedName().list(Stream
					.concat(Stream.<List<String>>of(wi.getDistinguishedNameList()),
							Stream.<List<String>>of(wi.getAppendTaskIdentityList()))
					.filter(Objects::nonNull).flatMap(o -> o.stream()).distinct().filter(StringUtils::isNotBlank)
					.collect(Collectors.toList()));
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.task = task;
			Work work = emc.find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
			}
			param.work = work;
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowProcessing().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage())
					&& BooleanUtils.isNotTrue(control.getAllowProcessing())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			WorkLog workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, task.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			param.workLog = workLog;
			// 后续要用到routeName要先进行updateTask
			updateTask(business, param);
			Pair<Manual, Route> pair = initGetManualAndRoute(business, task);
			param.manual = pair.first();
			param.route = pair.second();
			if (null != param.route) {
				param.asyncSupported = BooleanUtils.isTrue(param.route.getAsyncSupported());
				if (StringUtils.equals(param.route.getType(), Route.TYPE_APPENDTASK)
						&& StringUtils.equals(param.manual.getId(), param.route.getActivity())) {
					param.type = TYPE_APPENDTASK;
				}
			}
		}
		return param;
	}

	private Pair<Manual, Route> initGetManualAndRoute(Business business, Task task) throws Exception {
		Manual manual = business.manual().pick(task.getActivity());
		if (null == manual) {
			throw new ExceptionEntityNotExist(task.getActivity(), Manual.class);
		}
		Route route = null;
		List<Route> routes = business.route().pick(manual.getRouteList());
		if (routes.size() == 1) {
			route = routes.get(0);
		} else {
			for (Route o : routes) {
				if (StringUtils.equals(o.getName(), task.getRouteName())) {
					route = o;
					break;
				}
			}
		}
		return Pair.of(manual, route);
	}

	private class Param {
		private JsonElement option;
		private String action;
		private boolean asyncSupported = false;
		private String routeName;
		private String opinion;
		private String mediaOpinion;
		private Work work;
		private Task task;
		private WorkLog workLog;
		private String type = TYPE_TASK;
		private List<String> distinguishedNameList = new ArrayList<>();
		private List<String> ignoreEmpowerIdentityList = new ArrayList<>();
		private String series = StringTools.uniqueToken();
		private Route route;
		private Manual manual;
	}

	/**
	 * 调用人工环节工作流转后执行脚本
	 * 
	 * @throws Exception
	 */
	private void manualAfterProcessing(Param param, Record rec) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWi();
		req.setTask(param.task);
		req.setRecord(rec);
		ThisApplication.context().applications().postQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", "manual", "after", STRING_PROCESSING), req, param.work.getJob())
				.getData(
						com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWo.class);
	}

	private void startSignalThreadIfAsyncSupported(Param param, String id, LinkedBlockingQueue<Wo> responeQueue) {
		if (BooleanUtils.isNotFalse(param.asyncSupported)) {
			new Thread(() -> {
				RespProcessingSignal resp = null;
				try {
					resp = ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", param.work.getId(), "series", param.series,
									"activitytoken", param.work.getActivityToken(), STRING_PROCESSING, "signal"),
							param.work.getJob()).getData(RespProcessingSignal.class);
				} catch (Exception e) {
					LOGGER.error(e);
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
					}
				}
			}, String.format("%s:processingSignal:%s", ActionProcessing.class.getName(), id)).start();
		}
	}

	private void updateTask(Business business, Param param) throws Exception {
		business.entityManagerContainer().beginTransaction(Task.class);
		// 如果有输入新的路由决策覆盖原有决策
		if (StringUtils.isNotEmpty(param.routeName)) {
			param.task.setRouteName(param.routeName);
		}
		// 如果有新的流程意见那么覆盖原有流程意见,null表示没有传过来,""可能是前端传过来的改为空值.
		if (null != param.opinion) {
			param.task.setOpinion(param.opinion);
		}
		// 强制覆盖多媒体意见
		param.task.setMediaOpinion(param.mediaOpinion);
		if (StringUtils.isEmpty(param.task.getOpinion())) {
			Process process = business.process().pick(param.task.getProcess());
			if ((null != process) && BooleanUtils.isTrue(process.getRouteNameAsOpinion())) {
				// 将路由名作为办理意见写入
				param.task.setOpinion(param.task.getRouteName());
			}
		}
		// 更新routeAlias
		if ((null != param.route) && (!StringUtils.equals(param.route.getAlias(), param.task.getRouteAlias()))) {
			param.task.setRouteAlias(param.route.getAlias());
		}
		business.entityManagerContainer().commit();
	}

	private Record processingAppendTask(Param param) throws Exception {
		this.processingAppendTaskAppend(param);
		String taskCompletedId = this.processingProcessingTask(param, TaskCompleted.PROCESSINGTYPE_APPENDTASK);
		this.processingProcessingWork(param, ProcessingAttributes.TYPE_APPENDTASK);
		return this.recordTaskProcessing(Record.TYPE_APPENDTASK, param.workLog.getJob(), param.workLog.getId(),
				taskCompletedId, param.series);
	}

	// 8.2以后版本使用reset替代append
	private void processingAppendTaskAppend(Param param) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi req = new com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi();
		req.setDistinguishedNameList(param.distinguishedNameList);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", "v2", param.task.getId(), "reset"), req, param.task.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWo.class);
	}

	private Record processingTask(Param param) throws Exception {
		String taskCompletedId = this.processingProcessingTask(param, TaskCompleted.PROCESSINGTYPE_TASK);
		this.processingProcessingWork(param, ProcessingAttributes.TYPE_TASK);
		boolean flag = true;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 流程流转到取消环节，此时工作已被删除.flag =true 代表存在,false 已经被删除
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, param.task.getJob()) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, param.task.getJob()) == 0)) {
				flag = false;
			}
		}
		Record rec = null;
		if (flag) {
			rec = this.recordTaskProcessing(Record.TYPE_TASK, param.workLog.getJob(), param.workLog.getId(),
					taskCompletedId, param.series);
		} else {
//			// 这里的record不需要写入到数据库,work和workCompleted都消失了,可能走了cancel环节,这里的rec仅作为返回值生成wo
			rec = new Record(param.workLog);
			rec.setType(Record.TYPE_TASK);
			rec.setOpinion(param.task.getOpinion());
			rec.setRouteName(param.task.getRouteName());
			rec.setCompleted(true);
		}
		return rec;
	}

	private String processingProcessingTask(Param param, String processType) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWi req = new com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWi();
		req.setProcessingType(processType);
		com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWo resp = ThisApplication
				.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", param.task.getId(), STRING_PROCESSING), req,
						param.task.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWo.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionProcessingTask(param.task.getId());
		} else {
			// 获得已办id
			return resp.getId();
		}
	}

	private void processingProcessingWork(Param param, String workProcessingType) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setIgnoreEmpowerIdentityList(param.ignoreEmpowerIdentityList);
		req.setType(workProcessingType);
		req.setSeries(param.series);
		req.setPerson(param.task.getPerson());
		req.setIdentity(param.task.getIdentity());
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", param.task.getWork(), STRING_PROCESSING), req, param.task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionWorkProcessing(param.task.getId());
		}
	}

	/**
	 * 处理退回操作
	 * 
	 * @throws Exception
	 */
	private Record processingGoBack(Param param, OptionGoBack optionGoBack) throws Exception {
		Triple<WorkLog, String, List<String>> triple = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (null == param.manual) {
				throw new ExceptionEntityNotExist(param.task.getActivity(), Manual.class);
			}
			if (null == param.work) {
				throw new ExceptionEntityNotExist(param.task.getWork(), Work.class);
			}
			WorkLogTree workLogTree = workLogTree(business, param.task.getJob());
			Node node = workLogTree.find(param.workLog);
			Nodes nodes = workLogTree.up(node);
			List<WorkLog> workLogs = nodes.stream().filter(o -> BooleanUtils.isTrue(o.getWorkLog().getConnected())
					&& Objects.equals(o.getWorkLog().getFromActivityType(), ActivityType.manual)
					&& (!StringUtils.equalsIgnoreCase(o.getWorkLog().getType(), ProcessingAttributes.TYPE_GOBACK)))
					.map(Node::getWorkLog).collect(Collectors.toList());
			triple = goBackParam(business, param, optionGoBack, workLogs);
		}
		V2GoBackWi req = new V2GoBackWi();
		req.setActivity(triple.first().getFromActivity());
		req.setActivityToken(triple.first().getFromActivityToken());
		req.setWay(triple.second());
		req.setDistinguishedNameList(triple.third());
		String taskCompletedId = this.processingProcessingTask(param, TaskCompleted.PROCESSINGTYPE_GOBACK);
		V2GoBackWo resp = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", param.work.getId(), "goback"), req, param.work.getJob())
				.getData(V2GoBackWo.class);
		if (BooleanUtils.isNotTrue(resp.getValue())) {
			throw new ExceptionGoBack(req.getActivity(), req.getActivity());
		}
		this.processingWorkGoBack(param.work.getId(), param.series, param.work.getJob());
		boolean flag = true;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 流程流转到取消环节，此时工作已被删除.flag =true 代表存在,false 已经被删除
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, param.work.getJob()) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, param.work.getJob()) == 0)) {
				flag = false;
			}
		}
		Record rec = null;
		if (flag) {
			return this.recordTaskProcessing(Record.TYPE_GOBACK, param.workLog.getJob(), param.workLog.getId(),
					taskCompletedId, param.series);
		} else {
			// 这里的record不需要写入到数据库,work和workCompleted都消失了,可能走了cancel环节,这里的rec仅作为返回值生成wo
			rec = new Record(param.workLog);
			rec.setType(Record.TYPE_GOBACK);
			rec.setOpinion(param.task.getOpinion());
			rec.setRouteName(param.task.getRouteName());
			rec.setCompleted(true);
		}
		return rec;
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
//	private List<WorkLog> goBackTruncateWorkLog(Nodes nodes, String activityToken) {
//		List<WorkLog> list = new ArrayList<>();
//		nodes.stream()
//				.filter(o -> BooleanUtils.isTrue(o.getWorkLog().getConnected())
//						&& Objects.equals(o.getWorkLog().getFromActivityType(), ActivityType.manual)
//						&& (!StringUtils.equalsIgnoreCase(o.getWorkLog().getType(), ProcessingAttributes.TYPE_GOBACK)))
//				.forEach(o -> {
//					if (StringUtils.equalsIgnoreCase(o.getWorkLog().getFromActivityToken(), activityToken)) {
//						list.clear();
//					} else {
//						list.add(o.getWorkLog());
//					}
//				});
//		return list;
//	}

	private void processingWorkGoBack(String workId, String series, String job) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_GOBACK);
		req.setSeries(series);
		req.setForceJoinAtArrive(true);
		com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo resp = ThisApplication
				.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", workId, STRING_PROCESSING), req, job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionGoBackCallServiceProcessing(workId);
		}
	}

	private Triple<WorkLog, String, List<String>> goBackParam(Business business, Param param, OptionGoBack option,
			List<WorkLog> workLogs) throws Exception {
		Pair<WorkLog, String> pair = null;
		if ((null != param.manual.getGoBackConfig())
				&& StringUtils.equalsIgnoreCase(param.manual.getGoBackConfig().getType(), GoBackConfig.TYPE_PREV)) {
			pair = this.goBackParamPrev(param.manual, option, workLogs);
		} else if ((null != param.manual.getGoBackConfig())
				&& StringUtils.equalsIgnoreCase(param.manual.getGoBackConfig().getType(), GoBackConfig.TYPE_DEFINE)) {
			pair = this.goBackParamDefine(param.manual, option, workLogs);
		} else {
			pair = this.goBackParamAny(param.manual, option, workLogs);
		}
		if (null == pair.first()) {
			throw new ExceptionGoBackWorkLog(option.getActivity());
		}
		if (StringUtils.isBlank(pair.second())) {
			throw new ExceptionGoBackWay(option.getActivity());
		}
		List<String> third = business.entityManagerContainer()
				.listEqualAndEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
						pair.first().getFromActivityToken(), TaskCompleted.job_FIELDNAME, param.task.getJob())
				.stream().filter(o -> StringUtils.equalsIgnoreCase(o.getAct(), TaskCompleted.ACT_CREATE))
				.flatMap(o -> Stream.of(o.getIdentity(), o.getDistinguishedName())).filter(StringUtils::isNotBlank)
				.distinct().collect(Collectors.toList());
		if (ListTools.isEmpty(third)) {
			third = business.entityManagerContainer()
					.listEqualAndEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
							pair.first().getFromActivityToken(), TaskCompleted.job_FIELDNAME, param.task.getJob())
					.stream().filter(o -> BooleanUtils.isNotFalse(o.getJoinInquire()))
					.flatMap(o -> Stream.of(o.getIdentity(), o.getDistinguishedName())).filter(StringUtils::isNotBlank)
					.distinct().collect(Collectors.toList());
		}
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