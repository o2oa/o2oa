package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.component.assemble.control.Business;
import com.x.component.assemble.control.jaxrs.wrapout.WrapOutComponent;
import com.x.component.core.entity.Component;

public class ActionGet extends ActionBase {
	protected WrapOutComponent execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Component component = emc.find(id, Component.class, ExceptionWhen.not_found);
		WrapOutComponent wrap = outCopier.copy(component);
		return wrap;
	}
}
