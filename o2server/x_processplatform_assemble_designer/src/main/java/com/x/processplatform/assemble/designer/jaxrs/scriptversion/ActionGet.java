package com.x.processplatform.assemble.designer.jaxrs.scriptversion;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.ScriptVersion;

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
			Application application = emc.find(script.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(script.getApplication(), Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(scriptVersion);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends ScriptVersion {

		private static final long serialVersionUID = 1541438199059150837L;

		static WrapCopier<ScriptVersion, Wo> copier = WrapCopierFactory.wo(ScriptVersion.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
