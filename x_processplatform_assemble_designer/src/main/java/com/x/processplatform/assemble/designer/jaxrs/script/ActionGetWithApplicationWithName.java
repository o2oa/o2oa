package com.x.processplatform.assemble.designer.jaxrs.script;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutScript;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

class ActionGetWithApplicationWithName extends ActionBase {
	ActionResult<WrapOutScript> execute(EffectivePerson effectivePerson, String applicationId, String name)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutScript> result = new ActionResult<>();
			WrapOutScript wrap = new WrapOutScript();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationId);
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			String id = business.script().getWithApplicationWithName(application.getId(), name);
			if (StringUtils.isNotEmpty(id)) {
				Script script = emc.find(id, Script.class);
				wrap = outCopier.copy(script);
			} else {
				throw new Exception("script not existed with name or alias : " + name + ".");
			}
			result.setData(wrap);
			return result;
		}
	}
}
