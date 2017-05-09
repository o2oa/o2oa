package com.x.organization.assemble.custom.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.core.entity.Custom;

class ActionDelete extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String name) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Custom o = this.getWithName(emc, effectivePerson.getName(), name);
			if (null != o) {
				emc.beginTransaction(Custom.class);
				emc.remove(o);
				emc.commit();
				wrap = new WrapOutId(o.getId());
			}
			result.setData(wrap);
		}
		return result;
	}
}
