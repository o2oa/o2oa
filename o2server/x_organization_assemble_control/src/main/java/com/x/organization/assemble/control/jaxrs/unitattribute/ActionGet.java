package com.x.organization.assemble.control.jaxrs.unitattribute;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass(), flag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, flag);
				CacheManager.put(business.cache(), cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo get(Business business, String flag) throws Exception {
		UnitAttribute o = business.unitAttribute().pick(flag);
		if (null == o) {
			throw new ExceptionUnitAttributeNotExist(flag);
		}
		Wo wo = Wo.copier.copy(o);
		this.referenceUnit(business, wo);
		return wo;
	}

	private void referenceUnit(Business business, Wo wo) throws Exception {
		Unit o = business.unit().pick(wo.getUnit());
		if (null == o) {
			throw new ExceptionUnitNotExist(wo.getUnit());
		}
		WoUnit woUnit = WoUnit.copier.copy(o);
		wo.setWoUnit(woUnit);
	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = -7721760092867057759L;
		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Wo extends UnitAttribute {

		private static final long serialVersionUID = -127291000673692614L;

		@FieldDescribe("组织对象")
		private WoUnit woUnit;

		static WrapCopier<UnitAttribute, Wo> copier = WrapCopierFactory.wo(UnitAttribute.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public WoUnit getWoUnit() {
			return woUnit;
		}

		public void setWoUnit(WoUnit woUnit) {
			this.woUnit = woUnit;
		}

	}

}