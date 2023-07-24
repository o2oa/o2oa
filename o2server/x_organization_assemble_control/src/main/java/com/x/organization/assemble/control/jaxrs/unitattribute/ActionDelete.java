package com.x.organization.assemble.control.jaxrs.unitattribute;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			UnitAttribute o = emc.find(id, UnitAttribute.class);
			if (null == o) {
				throw new ExceptionUnitAttributeNotExist(id);
			}
			Unit unit = business.unit().pick(o.getUnit());
			if (null == unit) {
				throw new ExceptionUnitNotExist(o.getUnit());
			}
			if (!business.editable(effectivePerson, unit)) {
				throw new ExceptionDenyEditUnit(effectivePerson, unit.getName());
			}
			emc.beginTransaction(UnitAttribute.class);
			o = emc.find(o.getId(), UnitAttribute.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(UnitAttribute.class);
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

}