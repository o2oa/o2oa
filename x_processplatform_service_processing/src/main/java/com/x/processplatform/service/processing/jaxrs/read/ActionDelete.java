package com.x.processplatform.service.processing.jaxrs.read;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.service.processing.Business;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Read read = emc.find(id, Read.class, ExceptionWhen.not_found);
		emc.beginTransaction(Read.class);
		emc.remove(read, CheckRemoveType.all);
		emc.commit();
		return new WrapOutId(read.getId());
	}

}
