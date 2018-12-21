package com.x.portal.assemble.designer.jaxrs.widget;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;

class ActionListWithPortal extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Portal portal = emc.find(id, Portal.class);
			if (null == portal) {
				throw new ExceptionEntityNotExist(id, Portal.class);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Widget> os = emc.listEqual(Widget.class, Widget.portal_FIELDNAME, portal.getId());
			List<Wo> wos = Wo.copier.copy(os);
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Widget {

		private static final long serialVersionUID = -7592184343034018992L;

		static WrapCopier<Widget, Wo> copier = WrapCopierFactory.wo(Widget.class, Wo.class,
				JpaObject.singularAttributeField(Widget.class, true, true), null);

	}

}