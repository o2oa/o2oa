package com.x.processplatform.assemble.designer.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutScript;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

class ActionListWithApplication extends ActionBase {
	ActionResult<List<WrapOutScript>> execute(EffectivePerson effectivePerson, String applicationId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutScript>> result = new ActionResult<>();
			List<WrapOutScript> wraps = new ArrayList<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationId);
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			List<String> ids = business.script().listWithApplication(application.getId());
			for (Script o : emc.list(Script.class, ids)) {
				wraps.add(outCopier.copy(o));
			}
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}
