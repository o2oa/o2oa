package com.x.organization.assemble.control.jaxrs.unitduty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

class ActionListWithUnit extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String unitFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass(), unitFlag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				Unit unit = business.unit().pick(unitFlag);
				if (null == unit) {
					throw new ExceptionUnitNotExist(unitFlag);
				}
				List<Wo> wos = this.list(business, unit);
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

		@FieldDescribe("身份对象")
		private List<WoIdentity> woIdentityList = new ArrayList<>();

		public List<WoIdentity> getWoIdentityList() {
			return woIdentityList;
		}

		public void setWoIdentityList(List<WoIdentity> woIdentityList) {
			this.woIdentityList = woIdentityList;
		}

	}

	public static class WoIdentity extends Identity {

		private static final long serialVersionUID = 7096544058621159846L;

		static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.wo(Identity.class, WoIdentity.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<Wo> list(Business business, Unit unit) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.equal(root.get(UnitDuty_.unit), unit.getId());
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
		List<Wo> wos = Wo.copier.copy(os);
		for (Wo wo : wos) {
			this.referenceIdentity(business, wo);
		}
		wos = business.unitDuty().sort(wos);
		return wos;
	}

	private void referenceIdentity(Business business, Wo wo) throws Exception {
		if (ListTools.isNotEmpty(wo.getIdentityList())) {
			List<Identity> os = business.identity().pick(wo.getIdentityList());
			List<WoIdentity> wos = WoIdentity.copier.copy(os);
			wos = business.identity().sort(wos);
			wo.setWoIdentityList(wos);
		}
	}

}
