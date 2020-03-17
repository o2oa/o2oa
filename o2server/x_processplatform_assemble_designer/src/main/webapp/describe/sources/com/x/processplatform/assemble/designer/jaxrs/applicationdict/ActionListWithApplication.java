package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionListWithApplication extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationId);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			List<String> ids = business.applicationDict().listWithApplication(applicationId);
			wos = Wo.copier.copy(emc.list(ApplicationDict.class, ids));
			wos = business.applicationDict().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends ApplicationDict {

		private static final long serialVersionUID = -939143099553857829L;

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}