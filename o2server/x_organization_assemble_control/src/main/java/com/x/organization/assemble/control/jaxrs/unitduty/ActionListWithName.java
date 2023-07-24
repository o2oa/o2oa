package com.x.organization.assemble.control.jaxrs.unitduty;

import java.util.ArrayList;
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

class ActionListWithName extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass(), name);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, name);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends UnitDuty {

		private static final long serialVersionUID = -127291000673692614L;

		@FieldDescribe("组织对象")
		private WoUnit woUnit;

		@FieldDescribe("身份对象")
		private List<WoIdentity> woIdentityList = new ArrayList<>();

		static WrapCopier<UnitDuty, Wo> copier = WrapCopierFactory.wo(UnitDuty.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public WoUnit getWoUnit() {
			return woUnit;
		}

		public void setWoUnit(WoUnit woUnit) {
			this.woUnit = woUnit;
		}

		public List<WoIdentity> getWoIdentityList() {
			return woIdentityList;
		}

		public void setWoIdentityList(List<WoIdentity> woIdentityList) {
			this.woIdentityList = woIdentityList;
		}

	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = 2465212973550376118L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

	public static class WoIdentity extends Identity {

		private static final long serialVersionUID = 7096544058621159846L;

		static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.wo(Identity.class, WoIdentity.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<Wo> list(Business business, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.equal(root.get(UnitDuty_.name), name);
		cq.select(root).where(p);
		List<UnitDuty> os = em.createQuery(cq).getResultList();
		List<Wo> wos = Wo.copier.copy(os);
		for (Wo wo : wos) {
			this.referenceUnit(business, wo);
			this.referenceIdentity(business, wo);
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

	private void referenceIdentity(Business business, Wo wo) throws Exception {
		if (ListTools.isNotEmpty(wo.getIdentityList())) {
			List<Identity> os = business.identity().pick(wo.getIdentityList());
			List<WoIdentity> wos = WoIdentity.copier.copy(os);
			wos = business.identity().sort(wos);
			wo.setWoIdentityList(wos);
		}
	}

}