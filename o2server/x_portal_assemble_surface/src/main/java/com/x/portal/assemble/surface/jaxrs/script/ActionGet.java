package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Script script = business.script().pick(id);
			if (null == script) {
				throw new ExceptionScriptNotExist(id);
			}
			Portal portal = business.portal().pick(script.getPortal());
			if (null == portal) {
				throw new ExceptionPortalNotExist(script.getPortal());
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
						portal.getId());
			}
			Wo wo = Wo.copier.copy(script);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = 727247858785981924L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}
}