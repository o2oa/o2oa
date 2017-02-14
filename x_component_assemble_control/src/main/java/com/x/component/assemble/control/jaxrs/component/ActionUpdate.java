package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.component.assemble.control.Business;
import com.x.component.assemble.control.jaxrs.wrapin.WrapInComponent;
import com.x.component.core.entity.Component;

public class ActionUpdate extends ActionBase {
	protected WrapOutId execute(Business business, String id, WrapInComponent wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Component component = emc.find(id, Component.class, ExceptionWhen.not_found);
		emc.beginTransaction(Component.class);
		inCopier.copy(wrapIn, component);
		emc.check(component, CheckPersistType.all);
		emc.commit();
		WrapOutId wrap = new WrapOutId(component.getId());
		return wrap;
	}
}
