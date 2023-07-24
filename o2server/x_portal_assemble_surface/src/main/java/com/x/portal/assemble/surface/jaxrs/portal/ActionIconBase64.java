package com.x.portal.assemble.surface.jaxrs.portal;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;

class ActionIconBase64 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Portal o = business.portal().pick(id);
			if (null == o) {
				throw new ExceptionPortalNotExist(id);
			}
			Wo wo = new Wo();
			wo.setValue(StringUtils.isEmpty(o.getIcon()) ? DEFAULT_PORTAL_ICON_BASE64 : o.getIcon());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

	}

}
