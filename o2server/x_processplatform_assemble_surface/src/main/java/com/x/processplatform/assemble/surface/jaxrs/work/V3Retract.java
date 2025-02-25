package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWo;

class V3Retract extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Retract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String job = null;
		TaskCompleted taskCompleted;
		WorkLog workLog;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			List<Task> tasks = new ArrayList<>();
			List<String> jobs = new ArrayList<>();

			for (String id : wi.getRetractTaskList()) {
				Task task = emc.find(id, Task.class);
				if (null == task) {
					throw new ExceptionEntityNotExist(id, Task.class);
				}
				tasks.add(task);
				jobs.add(task.getJob());
			}

			if (jobs.stream().distinct().count() != 1) {
				throw new ExceptionEntityNotExist(Task.class);
			}

			job = jobs.get(0);

			Optional<TaskCompleted> opt = emc
					.listEqualAndEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, jobs.get(0),
							TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName(),
							TaskCompleted.joinInquire_FIELDNAME, true)
					.stream().sorted(Comparator.comparing(TaskCompleted::getCreateTime).reversed()).findFirst();

			if (opt.isEmpty()) {
				throw new ExceptionEntityNotExist(TaskCompleted.class);
			}

			taskCompleted = opt.get();

			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, job);

			List<WorkLog> fromWorkLogs = workLogs.stream()
					.filter(o -> Objects.equals(taskCompleted.getActivityToken(), o.getFromActivityToken()))
					.collect(Collectors.toList());

			if (fromWorkLogs.isEmpty()) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}

			workLog = fromWorkLogs.get(0);

			List<WorkLog> currentTaskWorkLogs = WorkLog.downTo(workLogs, fromWorkLogs, ActivityType.manual);

			List<String> actvityTokens = currentTaskWorkLogs.stream()
					.filter(o -> BooleanUtils.isNotTrue(o.getConnected())).map(WorkLog::getFromActivityToken)
					.filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());

			for (Task o : tasks) {
				if (!actvityTokens.contains(o.getActivityToken())) {
					throw new ExceptionRetract(o.getId());
				}
			}
		}

		String series = StringTools.uniqueToken();

		this.retract(effectivePerson.getDistinguishedName(), taskCompleted.getId(), wi.getRetractTaskList(), series);

		Record rec = this.recordWorkProcessing(Record.TYPE_RETRACT, "", "", job, workLog.getId(),
				taskCompleted.getIdentity(), series);

		result.setData(Wo.copier.copy(rec));

		return result;

	}

	private void retract(String job, String taskCompletedId, List<String> taskIds, String series) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWi();
		req.setTaskCompleted(taskCompletedId);
		req.setRetractTaskList(taskIds);
		com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWo resp = ThisApplication.context()
				.applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v3", "retract"), req, job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWo.class);
		if (StringUtils.isNotBlank(resp.getWork())) {
			processing(job, resp.getWork(), series);
		}
	}

	private void processing(String job, String work, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_RETRACT);
		req.setSeries(series);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", work, "processing"), req, job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo.class);
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 6034396222207463624L;

		@FieldDescribe("撤回待办标识")
		private List<String> retractTaskList;

		public List<String> getRetractTaskList() {
			return retractTaskList;
		}

		public void setRetractTaskList(List<String> retractTaskList) {
			this.retractTaskList = retractTaskList;
		}

	}

	public static class Wo extends V3RetractWo {

		private static final long serialVersionUID = -5007785846454720742L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
