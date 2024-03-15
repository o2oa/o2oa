package com.x.portal.assemble.designer.jaxrs.scriptversion;

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
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.ScriptVersion;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			ScriptVersion scriptVersion = emc.find(id, ScriptVersion.class);
			if (null == scriptVersion) {
				throw new ExceptionEntityNotExist(id, ScriptVersion.class);
			}
			Script script = emc.find(scriptVersion.getScript(), Script.class);
			if (null == script) {
				throw new ExceptionEntityNotExist(scriptVersion.getScript(), Script.class);
			}
			Portal portal = emc.find(script.getPortal(), Portal.class);
			if (null == portal) {
				throw new ExceptionEntityNotExist(script.getPortal(), Portal.class);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(scriptVersion);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends ScriptVersion {

		private static final long serialVersionUID = 4717872535128072737L;
		static WrapCopier<ScriptVersion, Wo> copier = WrapCopierFactory.wo(ScriptVersion.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
