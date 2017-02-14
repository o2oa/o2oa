package com.x.processplatform.service.processing.jaxrs.readcompleted;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.Business;

public class ActionDelete {
	
	protected WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		ReadCompleted readCompleted = emc.find(id, ReadCompleted.class, ExceptionWhen.not_found);
		emc.beginTransaction(ReadCompleted.class);
		emc.remove(readCompleted, CheckRemoveType.all);
		emc.commit();
		return new WrapOutId(readCompleted.getWork());
	}
	
}
