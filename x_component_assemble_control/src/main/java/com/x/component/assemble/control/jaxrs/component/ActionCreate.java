package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.WrapOutId;
import com.x.component.assemble.control.Business;
import com.x.component.assemble.control.jaxrs.wrapin.WrapInComponent;
import com.x.component.core.entity.Component;

public class ActionCreate extends ActionBase {
	protected WrapOutId execute(Business business, WrapInComponent wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Component component = inCopier.copy(wrapIn);
		emc.beginTransaction(Component.class);
		emc.persist(component, CheckPersistType.all);
		emc.commit();
		WrapOutId wrap = new WrapOutId(component.getId());
		return wrap;
	}
}
