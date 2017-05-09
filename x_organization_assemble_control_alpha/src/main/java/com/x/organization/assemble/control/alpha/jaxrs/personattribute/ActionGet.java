package com.x.organization.assemble.control.alpha.jaxrs.personattribute;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutPersonAttribute;
import com.x.organization.core.entity.PersonAttribute;

public class ActionGet extends ActionBase {

	protected WrapOutPersonAttribute execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		PersonAttribute o = emc.find(id, PersonAttribute.class, ExceptionWhen.not_found);
		WrapOutPersonAttribute wrap = outCopier.copy(o);
		return wrap;
	}

}
