package com.x.custom.index.assemble.control.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.custom.index.core.entity.Custom;

abstract class BaseAction extends StandardJaxrsAction {

	protected Custom getWithName(EntityManagerContainer emc, String person, String name) throws Exception {
		return emc.firstEqualAndEqual(Custom.class, Custom.PERSON_FIELDNAME, person, Custom.NAME_FIELDNAME, name);
	}
}
