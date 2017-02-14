package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.component.assemble.control.Business;
import com.x.component.core.entity.Component;

public class ActionDelete extends ActionBase {
	protected WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Component component = emc.find(id, Component.class,ExceptionWhen.not_found);
		emc.beginTransaction(Component.class);
		emc.remove(component, CheckRemoveType.all);
		emc.commit();
		WrapOutId wrap = new WrapOutId(component.getId());
		return wrap;
	}
}
