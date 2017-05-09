package com.x.organization.assemble.control.alpha.jaxrs.group;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutGroup;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;

public class ActionListSupDirectWithPerson extends ActionBase {

	protected List<WrapOutGroup> execute(Business business, String personId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
		List<String> ids = business.group().listSupDirectWithPerson(person.getId());
		List<WrapOutGroup> wraps = outCopier.copy(emc.list(Group.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}