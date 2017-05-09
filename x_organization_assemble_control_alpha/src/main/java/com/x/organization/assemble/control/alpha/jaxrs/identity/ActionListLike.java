package com.x.organization.assemble.control.alpha.jaxrs.identity;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;

public class ActionListLike extends ActionBase {

	protected List<WrapOutIdentity> execute(Business business, String key) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.identity().listLike(key);
		List<WrapOutIdentity> wraps = outCopier.copy(emc.list(Identity.class, ids));
		SortTools.asc(wraps, false, "name");
		/* 将depatmentName扩展到WrapOutIdentity */
		for (WrapOutIdentity o : wraps) {
			o.setDepartmentName(emc.fetchAttribute(o.getDepartment(), Department.class, "name").getName());
		}
		this.fillOnlineStatus(business, wraps);
		return wraps;
	}

}