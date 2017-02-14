package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Person;

public class ActionListLike extends ActionBase {

	protected List<WrapOutPerson> execute(Business business, String key) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.person().listLike(key);
		List<WrapOutPerson> wraps = outCopier.copy(emc.list(Person.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}