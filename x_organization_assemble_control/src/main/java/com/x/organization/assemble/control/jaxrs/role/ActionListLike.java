package com.x.organization.assemble.control.jaxrs.role;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutRole;
import com.x.organization.core.entity.Role;

public class ActionListLike extends ActionBase {

	protected List<WrapOutRole> execute(Business business, String key) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.role().listLike(key);
		List<WrapOutRole> wraps = outCopier.copy(emc.list(Role.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}