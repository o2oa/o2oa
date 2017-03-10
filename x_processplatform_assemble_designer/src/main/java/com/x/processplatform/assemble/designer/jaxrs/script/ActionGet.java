package com.x.processplatform.assemble.designer.jaxrs.script;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutScript;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

class ActionGet extends ActionBase {
	ActionResult<WrapOutScript> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutScript> result = new ActionResult<>();
			WrapOutScript wrap = new WrapOutScript();
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class);
			if (null == script) {
				throw new ScriptNotExistedException(id);
			}
			Application application = emc.find(script.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(script.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			wrap = outCopier.copy(script);
			result.setData(wrap);
			return result;
		}
	}
}
