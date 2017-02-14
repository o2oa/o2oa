package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionGet extends ActionBase {

	ActionResult<WrapOutApplicationDict> execute(EffectivePerson effectivePerson, String applicationDictFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutApplicationDict> result = new ActionResult<>();
			Business business = new Business(emc);
			ApplicationDict dict = business.applicationDict().pick(applicationDictFlag, ExceptionWhen.not_found);
			WrapOutApplicationDict wrap = copier.copy(dict);
			wrap.setData(this.get(business, dict));
			result.setData(wrap);
			return result;
		}
	}

}
