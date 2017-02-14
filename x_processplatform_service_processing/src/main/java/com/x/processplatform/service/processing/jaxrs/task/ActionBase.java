package com.x.processplatform.service.processing.jaxrs.task;

import java.util.Date;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;

public class ActionBase {

	protected WrapOutId processing(Business business, String id, ProcessingType type) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Task task = emc.find(id, Task.class, ExceptionWhen.not_found);
		/* 将待办转为已办 */
		emc.beginTransaction(TaskCompleted.class);
		TaskCompleted taskCompleted = this.createTaskCompleted(business, task, type);
		taskCompleted.setProcessingType(ProcessingType.processing);
		emc.persist(taskCompleted, CheckPersistType.all);
		emc.commit();
		emc.beginTransaction(Task.class);
		emc.remove(task, CheckRemoveType.all);
		emc.commit();
		ProcessingAttributes processingAttributes = new ProcessingAttributes();
		processingAttributes.put("taskCompleted", taskCompleted.getId());
		Processing processing = new Processing(new ProcessingAttributes());
		processing.processing(task.getWork());
		return new WrapOutId(task.getId());
	}

	protected TaskCompleted createTaskCompleted(Business business, Task task, ProcessingType type) throws Exception {
		TaskCompleted taskCompleted = new TaskCompleted();
		task.copyTo(taskCompleted, JpaObject.ID_DISTRIBUTEFACTOR);
		taskCompleted.setProcessingType(type);
		taskCompleted.setTask(task.getId());
		Date date = new Date();
		taskCompleted.setCompletedTime(date);
		taskCompleted.setCompletedTimeMonth(DateTools.format(date, DateTools.format_yyyyMM));
		taskCompleted.setCompleted(false);
		taskCompleted.setDuration(business.workTime().betweenMinutes(taskCompleted.getStartTime(), date));
		if ((null != task.getExpireTime()) && (date.after(task.getExpireTime()))) {
			taskCompleted.setExpired(true);
		} else {
			taskCompleted.setExpired(false);
		}
		return taskCompleted;
	}
}
