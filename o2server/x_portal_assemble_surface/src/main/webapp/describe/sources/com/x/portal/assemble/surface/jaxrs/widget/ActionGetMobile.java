package com.x.portal.assemble.surface.jaxrs.widget;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;

class ActionGetMobile extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Widget widget = business.widget().pick(id);
			if (null == widget) {
				throw new ExceptionWidgetNotExist(id);
			}
			Portal portal = business.portal().pick(widget.getPortal());
			if (null == portal) {
				throw new ExceptionPortalNotExist(widget.getPortal());
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
						portal.getId());
			}
			Wo wo = Wo.copier.copy(widget);
			wo.setData(widget.getMobileDataOrData());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Widget {

		private static final long serialVersionUID = -8067704098385000667L;

		/** 不输出data数据,单独处理 */
		static WrapCopier<Widget, Wo> copier = WrapCopierFactory.wo(Widget.class, Wo.class,
				JpaObject.singularAttributeField(Widget.class, true, true), JpaObject.FieldsInvisible);
	}
}