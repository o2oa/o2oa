package com.x.processplatform.assemble.surface.jaxrs.task;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionProcessing extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			emc.beginTransaction(Task.class);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			ProcessingRequest processingRequest = new ProcessingRequest();
			processingRequest.setRouteData(wi.getRouteData());
			if (!StringUtils.equalsIgnoreCase(task.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
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
			/* processing task */
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					"task/" + URLEncoder.encode(task.getId(), DefaultCharset.name) + "/processing", processingRequest);
			List<Wo> wos = this.referenceWorkLog(business, task);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> referenceWorkLog(Business business, Task task) throws Exception {
		List<Wo> os = Wo.copier.copy(business.entityManagerContainer().list(WorkLog.class,
				business.workLog().listWithFromActivityTokenForwardNotConnected(task.getActivityToken())));
		List<WoTaskCompleted> _taskCompleteds = WoTaskCompleted.copier
				.copy(business.taskCompleted().listWithJobObject(task.getJob()));
		List<WoTask> _tasks = WoTask.copier.copy(business.task().listWithJobObject(task.getJob()));
		os = business.workLog().sort(os);

		Map<String, List<WoTaskCompleted>> _map_taskCompleteds = _taskCompleteds.stream()
				.collect(Collectors.groupingBy(o -> o.getActivityToken()));

		Map<String, List<WoTask>> _map_tasks = _tasks.stream()
				.collect(Collectors.groupingBy(o -> o.getActivityToken()));

		for (Wo o : os) {
			List<WoTaskCompleted> _parts_taskCompleted = _map_taskCompleteds.get(o.getFromActivityToken());
			o.setTaskCompletedList(new ArrayList<WoTaskCompleted>());
			if (!ListTools.isEmpty(_parts_taskCompleted)) {
				for (WoTaskCompleted _taskCompleted : business.taskCompleted().sort(_parts_taskCompleted)) {
					o.getTaskCompletedList().add(_taskCompleted);
					if (_taskCompleted.getProcessingType().equals(ProcessingType.retract)) {
						TaskCompleted _retract = new TaskCompleted();
						o.copyTo(_retract);
						_retract.setRouteName("撤回");
						_retract.setOpinion("撤回");
						_retract.setStartTime(_retract.getRetractTime());
						_retract.setCompletedTime(_retract.getRetractTime());
						o.getTaskCompletedList().add(WoTaskCompleted.copier.copy(_retract));
					}
				}
			}
			List<WoTask> _parts_tasks = _map_tasks.get(o.getFromActivityToken());
			o.setTaskList(new ArrayList<WoTask>());
			if (!ListTools.isEmpty(_parts_tasks)) {
				o.setTaskList(business.task().sort(_parts_tasks));
			}
		}
		return os;
	}

	public static class Wo extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private List<WoTaskCompleted> taskCompletedList;

		private List<WoTask> taskList;

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2702712453822143654L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class ProcessingRequest extends GsonPropertyObject {

		private JsonElement routeData;

		public JsonElement getRouteData() {
			return routeData;
		}

		public void setRouteData(JsonElement routeData) {
			this.routeData = routeData;
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