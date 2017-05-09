package com.x.organization.assemble.control.alpha.jaxrs.identity;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Identity;

public class ActionGet extends ActionBase {

	protected WrapOutIdentity execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Identity identity = emc.find(id, Identity.class, ExceptionWhen.not_found);
		WrapOutIdentity wrap = outCopier.copy(identity);
		this.fillOnlineStatus(business, wrap);
		return wrap;
	}

}
