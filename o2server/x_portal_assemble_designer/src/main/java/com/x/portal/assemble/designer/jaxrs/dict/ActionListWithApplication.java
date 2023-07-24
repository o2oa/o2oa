package com.x.portal.assemble.designer.jaxrs.dict;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.general.core.entity.ApplicationDict;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import java.util.List;

class ActionListWithApplication extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String portalId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Portal portal = emc.find(portalId, Portal.class);
			if (null == portal) {
				throw new ExceptionEntityNotExist(portalId);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> ids = business.applicationDict().listWithApplication(portalId);
			List<Wo> wos = Wo.copier.copy(emc.list(ApplicationDict.class, ids));
			wos = business.applicationDict().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends ApplicationDict {

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
