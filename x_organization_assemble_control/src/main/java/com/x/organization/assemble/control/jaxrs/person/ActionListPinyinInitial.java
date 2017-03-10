package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Person;

class ActionListPinyinInitial extends ActionBase {

	protected ActionResult<List<WrapOutPerson>> execute(String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.person().listPinyinInitial(key);
			List<WrapOutPerson> wraps = outCopier.copy(emc.list(Person.class, ids));
			SortTools.asc(wraps, false, "name");
			this.updateIcon(wraps);
			result.setData(wraps);
			return result;
		}
	}
}