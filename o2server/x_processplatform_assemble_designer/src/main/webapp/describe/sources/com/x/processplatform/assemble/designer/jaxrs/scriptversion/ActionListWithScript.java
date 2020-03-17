package com.x.processplatform.assemble.designer.jaxrs.scriptversion;

import java.util.List;

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

class ActionListWithScript extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String scriptId) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);
			Script script = emc.find(scriptId, Script.class);

			if (null == script) {
				throw new ExceptionEntityNotExist(scriptId, Script.class);
			}

			Application application = emc.find(script.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(script.getApplication(), Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			List<Wo> wos = emc.fetchEqual(ScriptVersion.class, Wo.copier, ScriptVersion.script_FIELDNAME,
					script.getId());

			result.setData(wos);
			return result;

		}
	}

	public static class Wo extends ScriptVersion {

		private static final long serialVersionUID = -2398096870126935605L;
		static WrapCopier<ScriptVersion, Wo> copier = WrapCopierFactory.wo(ScriptVersion.class, Wo.class,
				JpaObject.singularAttributeField(ScriptVersion.class, true, true), null);

	}
}