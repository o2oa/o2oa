package com.x.processplatform.assemble.designer.jaxrs.script;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

class ActionGetWithApplicationWithName extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationId, String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationId);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			String id = business.script().getWithApplicationWithName(application.getId(), name);
			if (StringUtils.isNotEmpty(id)) {
				Script script = emc.find(id, Script.class);
				Wo wo = Wo.copier.copy(script);
				result.setData(wo);
			} else {
				throw new ExceptionScriptNotExist(name);
			}
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = 2475165883507548650L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
