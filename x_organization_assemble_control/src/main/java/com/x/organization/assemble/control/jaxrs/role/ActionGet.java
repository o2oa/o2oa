package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutRole;
import com.x.organization.core.entity.Role;

public class ActionGet extends ActionBase {

	protected WrapOutRole execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Role o = emc.find(id, Role.class, ExceptionWhen.not_found);
		WrapOutRole wrap = outCopier.copy(o);
		return wrap;
	}

}