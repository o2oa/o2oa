package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutPortal;
import com.x.portal.core.entity.Portal;

class ActionList extends ActionBase {
	ActionResult<List<WrapOutPortal>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutPortal>> result = new ActionResult<>();
			List<String> ids = this.listEditable(business, effectivePerson);
			List<WrapOutPortal> wraps = outCopier.copy(emc.list(Portal.class, ids));
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}