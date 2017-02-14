package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.service.processing.Business;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Task task = emc.find(id, Task.class, ExceptionWhen.not_found);
		emc.beginTransaction(Task.class);
		emc.remove(task, CheckRemoveType.all);
		emc.commit();
		return new WrapOutId(task.getId());
	}

}