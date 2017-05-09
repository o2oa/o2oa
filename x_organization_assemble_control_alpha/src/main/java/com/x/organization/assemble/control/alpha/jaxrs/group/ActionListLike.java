package com.x.organization.assemble.control.alpha.jaxrs.group;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutGroup;
import com.x.organization.core.entity.Group;

public class ActionListLike extends ActionBase {

	protected List<WrapOutGroup> execute(Business business, String key) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.group().listLike(key);
		List<WrapOutGroup> wraps = outCopier.copy(emc.list(Group.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}