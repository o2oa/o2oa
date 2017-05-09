package com.x.portal.assemble.designer.jaxrs.source;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutSource;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Source;

class ActionListWithPortal extends ActionBase {
	ActionResult<List<WrapOutSource>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutSource>> result = new ActionResult<>();
			Portal portal = emc.find(id, Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(id);
			}
			if (!business.portal().editable(effectivePerson, portal)) {
				throw new PortalInvisibleException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			List<String> ids = business.source().listWithPortal(portal.getId());
			List<WrapOutSource> wraps = outCopier.copy(emc.list(Source.class, ids));
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}