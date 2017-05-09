package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutPortalSummary;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionListSummary extends ActionBase {

	ActionResult<List<WrapOutPortalSummary>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPortalSummary>> result = new ActionResult<>();
			List<WrapOutPortalSummary> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = this.listEditable(business, effectivePerson);
			/* 由于有多值字段所以需要全部取出 */
			for (Portal o : emc.list(Portal.class, ids)) {
				WrapOutPortalSummary wrap = summaryOutCopier.copy(o);
				List<String> os = business.page().listWithPortal(o.getId());
				wrap.setPageList(pageOutCopier.copy(emc.list(Page.class, os)));
				wraps.add(wrap);
			}
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}

}