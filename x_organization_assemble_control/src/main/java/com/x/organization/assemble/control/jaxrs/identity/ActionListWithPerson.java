package com.x.organization.assemble.control.jaxrs.identity;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

public class ActionListWithPerson extends ActionBase {

	protected List<WrapOutIdentity> execute(Business business, String personId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
		List<String> ids = business.identity().listWithPerson(person.getId());
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