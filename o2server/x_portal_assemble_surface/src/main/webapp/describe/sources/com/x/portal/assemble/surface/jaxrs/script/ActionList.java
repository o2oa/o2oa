package com.x.portal.assemble.surface.jaxrs.script;

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
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

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
			List<String> ids = business.script().listWithPortal(portal.getId());
			for (String id : ids) {
				Script o = business.script().pick(id);
				if (null == o) {
					throw new ExceptionScriptNotExist(id);
				} else {
					wos.add(Wo.copier.copy(o));
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -5240059905993945729L;
		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class,
				JpaObject.singularAttributeField(Script.class, true, true), JpaObject.FieldsInvisible);

	}
}