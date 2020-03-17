package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.jaxrs.taskcompleted.ActionListWithJob.Wo;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;

class ActionListPrevManual extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String activityToken = null;
			String processId = null;
			String person = null;
			String job = null;
			Task task = emc.flag(flag, Task.class);
			if (null != task) {
				activityToken = task.getActivityToken();
				processId = task.getProcess();
				person = task.getPerson();
				job = task.getJob();
			} else {
				TaskCompleted taskCompleted = emc.flag(flag, TaskCompleted.class);
				if (null != taskCompleted) {
					activityToken = taskCompleted.getActivityToken();
					processId = taskCompleted.getProcess();
					person = taskCompleted.getPerson();
					job = taskCompleted.getJob();
				}
			}
			if (StringUtils.isEmpty(activityToken)) {
				throw new ExceptionNotFoundTaskOrTaskCompleted(flag);
			}
			Process process = business.process().pick(processId);
			if (null == process) {
				throw new ExceptionEntityNotExist(processId, Process.class);
			}
			Application application = business.application().pick(process.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
			}
			if (effectivePerson.isNotPerson(person)
					&& (!business.canManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<WorkLog> workLogs = this.workLogs(business, job);
			final String token = activityToken;
			WorkLog workLog = workLogs.stream().filter(o -> {
				return StringUtils.equals(o.getFromActivityToken(), token);
			}).findFirst().orElse(null);
			if (null == workLog) {
				throw new ExceptionNoneWorkLog();
			}
			WorkLogTree tree = new WorkLogTree(workLogs);
			Node node = tree.find(workLog);
			Nodes nodes = node.upTo(ActivityType.manual);
			List<TaskCompleted> taskCompleteds = new ArrayList<>();
			for (Node n : nodes) {
				List<TaskCompleted> os = emc.listEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
						n.getWorkLog().getFromActivityToken());
				taskCompleteds.addAll(os);
			}
			taskCompleteds = taskCompleteds.stream()
					.sorted(Comparator.comparing(TaskCompleted::getCompletedTime).reversed())
					.collect(Collectors.toList());
			List<Wo> wos = Wo.copier.copy(taskCompleteds);
			result.setData(wos);
			return result;
		}
	}

	private List<WorkLog> workLogs(Business business, String job) throws Exception {
		return business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.job_FIELDNAME, job);
	}

	public static class Wo extends TaskCompleted {

		private static final long serialVersionUID = -355092481236046649L;

		static WrapCopier<TaskCompleted, Wo> copier = WrapCopierFactory.wo(TaskCompleted.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
