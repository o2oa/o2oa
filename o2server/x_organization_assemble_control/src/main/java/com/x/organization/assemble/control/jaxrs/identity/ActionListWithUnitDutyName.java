package com.x.organization.assemble.control.jaxrs.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;

class ActionListWithUnitDutyName extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String unitDutyName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), unitDutyName);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, unitDutyName);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends Identity {

		private String matchUnitName;
		private String matchUnitLevelName;
		private Integer matchUnitLevel;

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public String getMatchUnitName() {
			return matchUnitName;
		}

		public void setMatchUnitName(String matchUnitName) {
			this.matchUnitName = matchUnitName;
		}

		public String getMatchUnitLevelName() {
			return matchUnitLevelName;
		}

		public void setMatchUnitLevelName(String matchUnitLevelName) {
			this.matchUnitLevelName = matchUnitLevelName;
		}

		public Integer getMatchUnitLevel() {
			return matchUnitLevel;
		}

		public void setMatchUnitLevel(Integer matchUnitLevel) {
			this.matchUnitLevel = matchUnitLevel;
		}

	}

	private List<Wo> list(Business business, String unitDutyName) throws Exception {
		List<Wo> wos = new ArrayList<Wo>();
		List<UnitDuty> os = business.entityManagerContainer().listEqual(UnitDuty.class, UnitDuty.name_FIELDNAME,
				unitDutyName);
		for (UnitDuty o : os) {
			Unit unit = business.unit().pick(o.getUnit());
			for (String identityId : o.getIdentityList()) {
				Identity identity = business.identity().pick(identityId);
				Wo wo = Wo.copier.copy(identity);
				wo.setMatchUnitLevel(unit.getLevel());
				wo.setMatchUnitLevelName(unit.getLevelName());
				wo.setMatchUnitName(unit.getName());
				wos.add(wo);
			}
		}
		return wos;
	}

}