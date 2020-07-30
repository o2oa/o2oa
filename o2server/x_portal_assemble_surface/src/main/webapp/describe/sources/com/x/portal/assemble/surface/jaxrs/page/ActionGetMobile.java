package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionGetMobile extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Page page = business.page().pick(id);
			if (null == page) {
				throw new ExceptionPageNotExist(id);
			}
			Portal portal = business.portal().pick(page.getPortal());
			if (null == portal) {
				throw new ExceptionPortalNotExist(page.getPortal());
			}
			if (isNotLoginPage(id) && (!business.portal().visible(effectivePerson, portal))) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
						portal.getId());
			}
			Wo wo = Wo.copier.copy(page);
			wo.setData(page.getMobileDataOrData());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Page {

		private static final long serialVersionUID = -8067704098385000667L;

		/** 不输出data数据,单独处理 */
		static WrapCopier<Page, Wo> copier = WrapCopierFactory.wo(Page.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Page.data_FIELDNAME, Page.mobileData_FIELDNAME));
	}
}