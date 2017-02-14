package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;

public class ActionListSubDirectWithGroup extends ActionBase {

	protected List<WrapOutPerson> execute(Business business, String groupId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Group group = emc.find(groupId, Group.class, ExceptionWhen.not_found);
		List<String> ids = group.getPersonList();
		List<WrapOutPerson> wraps = outCopier.copy(emc.list(Person.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
