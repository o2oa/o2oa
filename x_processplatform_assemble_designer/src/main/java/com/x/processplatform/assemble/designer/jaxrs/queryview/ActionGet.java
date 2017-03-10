package com.x.processplatform.assemble.designer.jaxrs.queryview;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryView;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionGet extends ActionBase {
	ActionResult<WrapOutQueryView> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryView> result = new ActionResult<>();
			Business business = new Business(emc);
			QueryView queryView = emc.find(id, QueryView.class);
			if (null == queryView) {
				throw new QueryViewNotExistedException(id);
			}
			Application application = emc.find(queryView.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(queryView.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			WrapOutQueryView wrap = outCopier.copy(queryView);
			result.setData(wrap);
			return result;
		}
	}
}
