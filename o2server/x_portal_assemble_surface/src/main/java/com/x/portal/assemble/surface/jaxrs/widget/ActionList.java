package com.x.portal.assemble.surface.jaxrs.widget;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String portalId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Portal portal = business.portal().pick(portalId);
			if (null == portal) {
				throw new ExceptionPortalNotExist(portalId);
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
						portal.getId());
			}
			List<String> ids = business.widget().listWithPortal(portal.getId());
			for (String id : ids) {
				Widget o = business.widget().pick(id);
				if (null == o) {
					throw new ExceptionWidgetNotExist(id);
				} else {
					wos.add(Wo.copier.copy(o));
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Page {

		private static final long serialVersionUID = 3454132769791427909L;
		/** 不输出data数据,单独处理 */
		static WrapCopier<Widget, Wo> copier = WrapCopierFactory.wo(Widget.class, Wo.class,
				JpaObject.singularAttributeField(Widget.class, true, true), JpaObject.FieldsInvisible);

	}
}