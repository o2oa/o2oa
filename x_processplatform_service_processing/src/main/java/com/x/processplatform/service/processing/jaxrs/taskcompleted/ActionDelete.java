package com.x.processplatform.service.processing.jaxrs.taskcompleted;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.service.processing.Business;

public class ActionDelete {

	protected WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class, ExceptionWhen.not_found);
		emc.beginTransaction(TaskCompleted.class);
		emc.remove(taskCompleted, CheckRemoveType.all);
		emc.commit();
		return new WrapOutId(taskCompleted.getWork());
	}

}