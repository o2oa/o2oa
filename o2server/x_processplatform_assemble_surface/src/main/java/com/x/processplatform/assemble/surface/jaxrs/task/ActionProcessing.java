package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;

class ActionProcessing extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionProcessing.class);

	ActionResult<List<Wo>> result = new ActionResult<>();
	List<Wo> wos = new ArrayList<>();

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		Audit audit = logger.audit(effectivePerson);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Task task = null;
		boolean appendTask = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			if (!StringUtils.equalsIgnoreCase(task.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
			emc.beginTransaction(Task.class);
			/* 如果有输入新的路由决策覆盖原有决策 */
			if (StringUtils.isNotEmpty(wi.getRouteName())) {
				task.setRouteName(wi.getRouteName());
			}
			/* 如果有新的流程意见那么覆盖原有流程意见 */
			if (StringUtils.isNotEmpty(wi.getOpinion())) {
				task.setOpinion(wi.getOpinion());
			}
			/* 强制覆盖多媒体意见 */
			task.setMediaOpinion(wi.getMediaOpinion());
			emc.commit();

			appendTask = this.appendTask(business, task, wi);
		}
		if (appendTask) {
			ReqAppendTask req = new ReqAppendTask();
			req.setIdentityList(wi.getAppendTaskIdentityList());
			ThisApplication.context().applications()
					.putQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("task", task.getId(), "append"), req, task.getJob())
					.getData(WrapStringList.class);
		}
		ProcessingRequest processingRequest = new ProcessingRequest();
		if (appendTask) {
			processingRequest.setProcessingType(ProcessingType.appendTask);
		} else {
			processingRequest.setProcessingType(ProcessingType.processing);
		}
		processingRequest.setRouteData(wi.getRouteData());
		WoId taskProcessingResp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "processing"), processingRequest, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(taskProcessingResp.getId())) {
			throw new ExceptionTaskProcessing(task.getId());
		}

		WoId workProcessingResp = ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), "processing"), null, task.getJob())
				.getData(WoId.class);

		if (StringUtils.isBlank(workProcessingResp.getId())) {
			throw new ExceptionWorkProcessing(task.getId());
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, task.getJob());
			WorkLog currentWorkLog = null;
			for (WorkLog o : workLogs) {
				if (StringUtils.equals(o.getFromActivityToken(), task.getActivityToken())) {
					currentWorkLog = o;
					break;
				}
			}
			if (null != currentWorkLog) {
				WorkLogTree tree = new WorkLogTree(workLogs);
				Node node = tree.find(currentWorkLog);
				if (null != node) {
					List<WoTask> woTasks = new ArrayList<>();
					List<String> activityTokens = new ArrayList<>();
					for (Node n : node.downNextManual()) {
						activityTokens.add(n.getWorkLog().getFromActivityToken());
					}
					woTasks.addAll(emc.fetchEqualAndIn(Task.class, WoTask.copier, Task.job_FIELDNAME, task.getJob(),
							Task.activityToken_FIELDNAME, activityTokens));
//					woTasks.addAll(emc.fetchEqualAndEqual(Task.class, WoTask.copier, Task.job_FIELDNAME, task.getJob(),
//							Task.work_FIELDNAME, node.getWorkLog().getWork()));
					woTasks = ListTools.trim(woTasks, true, true);
					for (Entry<String, List<WoTask>> en : woTasks.stream()
							.collect(Collectors.groupingBy(WoTask::getActivity)).entrySet()) {
						Wo wo = new Wo();
						wo.setActivity(en.getValue().get(0).getActivity());
						wo.setActivityName(en.getValue().get(0).getActivityName());
						wo.setTaskList(en.getValue());
						wos.add(wo);
					}
				}
			}
			result.setData(wos);
		}
		audit.log(null, "审批");
		return result;
	}

	private boolean appendTask(Business business, Task task, Wi wi) throws Exception {
		Manual manual = business.manual().pick(task.getActivity());
		if (null != manual) {
			Route route = null;
			for (Route o : business.route().pick(manual.getRouteList())) {
				if (StringUtils.equals(o.getName(), task.getRouteName())) {
					route = o;
					break;
				}
			}
			if ((null != route) && (StringUtils.equals(route.getType(), Route.TYPE_APPENDTASK))
					&& StringUtils.equals(manual.getId(), route.getActivity())) {
				return true;
			}
		}
		return false;
	}

//	private List<Wo> listCurrentTask(Business business, String job) throws Exception {
//		List<WoTask> woTasks = business.entityManagerContainer().fetchEqual(Task.class, WoTask.copier,
//				Task.job_FIELDNAME, job);
//		List<Wo> wos = new ArrayList<>();
//		for (Entry<String, List<WoTask>> en : woTasks.stream().collect(Collectors.groupingBy(WoTask::getActivity))
//				.entrySet()) {
//			Wo wo = new Wo();
//			wo.setActivity(en.getValue().get(0).getActivity());
//			wo.setActivityName(en.getValue().get(0).getActivityName());
//			wo.setTaskList(en.getValue());
//			wos.add(wo);
//		}
//		return wos;
//	}

	public static class Wo extends GsonPropertyObject {

		private String activityName;
		private String activity;

		private List<WoTask> taskList;

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2702712453822143654L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class,
				ListTools.toList(Task.id_FIELDNAME, Task.activity_FIELDNAME, Task.activityName_FIELDNAME,
						Task.person_FIELDNAME, Task.unit_FIELDNAME),
				null);

	}

	public static class ProcessingRequest extends GsonPropertyObject {

		@FieldDescribe("流转类型.")
		private ProcessingType processingType;

		@FieldDescribe("路由数据.")
		private JsonElement routeData;

		public JsonElement getRouteData() {
			return routeData;
		}

		public void setRouteData(JsonElement routeData) {
			this.routeData = routeData;
		}

		public ProcessingType getProcessingType() {
			return processingType;
		}

		public void setProcessingType(ProcessingType processingType) {
			this.processingType = processingType;
		}

	}

	public static class ReqAppendTask extends GsonPropertyObject {

		@FieldDescribe("添加的待办身份.")
		private List<String> identityList;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("路由名称")
		private String routeName;

		@FieldDescribe("意见")
		private String opinion;

		@FieldDescribe("多媒体意见")
		private String mediaOpinion;

		@FieldDescribe("路由数据")
		private JsonElement routeData;

		@FieldDescribe("新添加的待办处理人")
		private List<String> appendTaskIdentityList;

		public List<String> getAppendTaskIdentityList() {
			return appendTaskIdentityList;
		}

		public void setAppendTaskIdentityList(List<String> appendTaskIdentityList) {
			this.appendTaskIdentityList = appendTaskIdentityList;
		}

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public String getMediaOpinion() {
			return mediaOpinion;
		}

		public void setMediaOpinion(String mediaOpinion) {
			this.mediaOpinion = mediaOpinion;
		}

		public JsonElement getRouteData() {
			return routeData;
		}

		public void setRouteData(JsonElement routeData) {
			this.routeData = routeData;
		}
	}

}