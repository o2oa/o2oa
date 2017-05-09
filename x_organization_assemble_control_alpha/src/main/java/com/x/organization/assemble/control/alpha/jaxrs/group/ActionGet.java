package com.x.organization.assemble.control.alpha.jaxrs.group;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutGroup;
import com.x.organization.core.entity.Group;

public class ActionGet extends ActionBase {

	protected WrapOutGroup execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Group o = emc.find(id, Group.class, ExceptionWhen.not_found);
		WrapOutGroup wrap = outCopier.copy(o);
		return wrap;
	}

}