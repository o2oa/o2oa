package com.x.organization.assemble.custom.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.core.entity.Custom;

class ActionGet extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<String> execute(EffectivePerson effectivePerson, String name) throws Exception {
		ActionResult<String> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Custom o = this.getWithName(emc, effectivePerson.getName(), name);
			if (null != o) {
				result.setData(o.getData());
			}
		}
		return result;
	}
}
