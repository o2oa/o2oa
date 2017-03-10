package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;

class ActionListSubDirectWithGroup extends ActionBase {

	protected ActionResult<List<WrapOutPerson>> execute(String groupId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPerson>> result = new ActionResult<List<WrapOutPerson>>();
			Group group = emc.find(groupId, Group.class, ExceptionWhen.not_found);
			List<String> ids = group.getPersonList();
			List<WrapOutPerson> wraps = outCopier.copy(emc.list(Person.class, ids));
			SortTools.asc(wraps, false, "name");
			this.updateIcon(wraps);
			result.setData(wraps);
			return result;
		}
	}

}
