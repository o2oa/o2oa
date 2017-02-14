package com.x.organization.assemble.control.jaxrs.personattribute;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutPersonAttribute;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

public class ActionListWithPerson extends ActionBase {

	protected List<WrapOutPersonAttribute> execute(Business business, String personId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
		List<String> ids = business.personAttribute().listWithPerson(person.getId());
		List<WrapOutPersonAttribute> wraps = outCopier.copy(emc.list(PersonAttribute.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}