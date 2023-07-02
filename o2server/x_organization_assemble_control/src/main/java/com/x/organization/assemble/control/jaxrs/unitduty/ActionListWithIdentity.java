package com.x.organization.assemble.control.jaxrs.unitduty;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

class ActionListWithIdentity extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String identityFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass(), identityFlag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, identityFlag);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends UnitDuty {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<UnitDuty, Wo> copier = WrapCopierFactory.wo(UnitDuty.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("组织对象")
		private WoUnit woUnit;

		public WoUnit getWoUnit() {
			return woUnit;
		}

		public void setWoUnit(WoUnit woUnit) {
			this.woUnit = woUnit;
		}

	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = 2465212973550376118L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

	private List<Wo> list(Business business, String identityFlag) throws Exception {
		Identity identity = business.identity().pick(identityFlag);
		if (null == identity) {
			throw new ExceptionIdentityNotExist(identityFlag);
		}
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(identity.getId(), root.get(UnitDuty_.identityList));
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
		List<Wo> wos = Wo.copier.copy(os);
		for (Wo wo : wos) {
			this.referenceUnit(business, wo);
		}
		wos = business.unitDuty().sort(wos);
		return wos;
	}

	private void referenceUnit(Business business, Wo wo) throws Exception {
		if (StringUtils.isNotEmpty(wo.getUnit())) {
			Unit unit = business.unit().pick(wo.getUnit());
			if (null == unit) {
				throw new ExceptionUnitNotExist(wo.getUnit());
			}
			WoUnit woUnit = WoUnit.copier.copy(unit);
			wo.setWoUnit(woUnit);
		}
	}

}
