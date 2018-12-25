package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Role;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Role role = business.role().pick(flag);
			if (null == role) {
				throw new ExceptionRoleNotExist(flag);
			}
			if (!business.editable(effectivePerson, role)) {
				throw new ExceptionDenyDeleteRole(effectivePerson, flag);
			}
			emc.beginTransaction(Role.class);
			role = emc.find(role.getId(), Role.class);
			emc.remove(role, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Role.class);
			Wo wo = new Wo();
			wo.setId(role.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

}