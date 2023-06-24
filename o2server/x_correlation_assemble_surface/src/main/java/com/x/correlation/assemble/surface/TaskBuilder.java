package com.x.correlation.assemble.surface;

import java.util.Date;
import java.util.List;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity.PrevTask;

public class TaskBuilder {

	private TaskBuilder() {
		// nothing
	}

	/**
	 * 更新待办的上一处理人
	 * 
	 * @param newlyTaskIds
	 * @param prevSeriesTaskCompleteds
	 * @param task
	 * @throws Exception
	 */

	public static void updatePrevTaskIdentity(List<String> newlyTaskIds, List<TaskCompleted> prevSeriesTaskCompleteds,
			Task task) throws Exception {
		// 记录上一处理人信息
		if (ListTools.isNotEmpty(newlyTaskIds)) {
			WrapUpdatePrevTaskIdentity req = new WrapUpdatePrevTaskIdentity();
			req.setTaskList(newlyTaskIds);
			prevSeriesTaskCompleteds.stream().forEach(o -> {
				PrevTask prevTask = new PrevTask();
				prevTask.setCompletedTime(o.getCompletedTime());
				prevTask.setStartTime(o.getStartTime());
				prevTask.setOpinion(o.getOpinion());
				prevTask.setPerson(o.getPerson());
				prevTask.setIdentity(o.getIdentity());
				prevTask.setUnit(o.getUnit());
				prevTask.setRouteName(o.getRouteName());
				prevTask.setActivity(o.getActivity());
				prevTask.setActivityName(o.getActivityName());
				prevTask.setActivityToken(o.getActivityToken());
				prevTask.setActivityType(o.getActivityType());
				req.getPrevTaskIdentityList().add(prevTask.getIdentity());
				req.getPrevTaskList().add(prevTask);
			});
			PrevTask prevTask = new PrevTask();
			prevTask.setCompletedTime(new Date());
			prevTask.setStartTime(task.getStartTime());
			prevTask.setOpinion(task.getOpinion());
			prevTask.setPerson(task.getPerson());
			prevTask.setIdentity(task.getIdentity());
			prevTask.setUnit(task.getUnit());
			prevTask.setRouteName(task.getRouteName());
			prevTask.setActivity(task.getActivity());
			prevTask.setActivityName(task.getActivityName());
			prevTask.setActivityToken(task.getActivityToken());
			prevTask.setActivityType(task.getActivityType());
			req.getPrevTaskIdentityList().add(prevTask.getIdentity());
			req.setPrevTaskIdentity(prevTask.getIdentity());
			req.getPrevTaskList().add(prevTask);
			req.setPrevTask(prevTask);
			// 去重
			req.setPrevTaskIdentityList(ListTools.trim(req.getPrevTaskIdentityList(), true, true));
			ThisApplication.context().applications()
					.putQuery(false, x_processplatform_service_processing.class,
							Applications.joinQueryUri("task", "prev", "task", "identity"), req, task.getJob())
					.getData(WrapBoolean.class);
		}
	}

}
