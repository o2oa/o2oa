package com.x.portal.assemble.designer.jaxrs.pageversion;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.PageVersion;
import com.x.portal.core.entity.Portal;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			PageVersion pageVersion = emc.find(id, PageVersion.class);
			if (null == pageVersion) {
				throw new ExceptionEntityNotExist(id, PageVersion.class);
			}
			Page page = emc.find(pageVersion.getPage(), Page.class);
			if (null == page) {
				throw new ExceptionEntityNotExist(pageVersion.getPage(), Page.class);
			}
			Portal portal = emc.find(page.getPortal(), Portal.class);
			if (null == portal) {
				throw new ExceptionEntityNotExist(page.getPortal(), Portal.class);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(pageVersion);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends PageVersion {

		private static final long serialVersionUID = 711299017409725991L;
		static WrapCopier<PageVersion, Wo> copier = WrapCopierFactory.wo(PageVersion.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
