package com.x.organization.assemble.custom.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.core.entity.Custom;

class ActionUpdate extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdate.class);

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String name, String wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Custom custom = this.getWithName(emc, effectivePerson.getName(), name);
			emc.beginTransaction(Custom.class);
			if (null != custom) {
				custom.setData(wrapIn);
				emc.check(custom, CheckPersistType.all);
			} else {
				custom = new Custom();
				custom.setPerson(effectivePerson.getName());
				custom.setName(name);
				custom.setData(wrapIn);
				emc.persist(custom, CheckPersistType.all);
			}
			emc.commit();
			wrap = new WrapOutId(custom.getId());
			result.setData(wrap);
		}
		return result;
	}
}
