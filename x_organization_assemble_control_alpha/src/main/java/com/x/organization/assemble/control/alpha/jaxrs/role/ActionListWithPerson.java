package com.x.organization.assemble.control.alpha.jaxrs.role;

import java.util.List;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutRole;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

public class ActionListWithPerson extends ActionBase {

	protected List<WrapOutRole> execute(Business business, String personId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
		List<String> groups = business.group().listSupNestedWithPerson(person.getId());
		ListOrderedSet<String> ids = new ListOrderedSet<String>();
		ids.addAll(business.role().listWithPerson(person.getId()));
		for (String str : groups) {
			ids.addAll(business.role().listWithGroup(str));
		}
		List<WrapOutRole> wraps = outCopier.copy(emc.list(Role.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}