package com.x.processplatform.service.processing.jaxrs.readcompleted;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.WrapOutId;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;

public class ActionDelete extends BaseAction {

	protected WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		ReadCompleted readCompleted = emc.find(id, ReadCompleted.class, ExceptionWhen.not_found);
		emc.beginTransaction(ReadCompleted.class);
		emc.remove(readCompleted, CheckRemoveType.all);
		emc.commit();
		MessageFactory.readCompleted_delete(readCompleted);
		return new WrapOutId(readCompleted.getWork());
	}

}