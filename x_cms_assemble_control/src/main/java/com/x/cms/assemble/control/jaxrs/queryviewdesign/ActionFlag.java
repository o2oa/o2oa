package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.core.entity.element.QueryView;


class ActionFlag extends ActionBase {
	ActionResult<WrapOutQueryView> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryView> result = new ActionResult<>();
			//Business business = new Business(emc);
			QueryView queryView = emc.flag( flag, QueryView.class, ExceptionWhen.not_found, false, QueryView.FLAGS );
			//Application application = emc.find(queryView.getApplication(), Application.class, ExceptionWhen.not_found);
			//business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			WrapOutQueryView wrap = outCopier.copy(queryView);
			result.setData(wrap);
			return result;
		}
	}
}
