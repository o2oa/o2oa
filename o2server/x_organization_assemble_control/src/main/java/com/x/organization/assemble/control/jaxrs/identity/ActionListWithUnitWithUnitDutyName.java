package com.x.organization.assemble.control.jaxrs.identity;

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
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

class ActionListWithUnitWithUnitDutyName extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String unitFlag, String unitDutyName)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), unitFlag, unitDutyName);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, unitFlag, unitDutyName);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends Identity {

		private static final long serialVersionUID = -127291000673692614L;

		private WoPerson woPerson;

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public WoPerson getWoPerson() {
			return woPerson;
		}

		public void setWoPerson(WoPerson woPerson) {
			this.woPerson = woPerson;
		}

	}

	public static class WoPerson extends Person {

		private static final long serialVersionUID = -8775294698857946888L;

		static WrapCopier<Person, WoPerson> copier = WrapCopierFactory.wo(Person.class, WoPerson.class, null,
				ListTools.toList(ListTools.toList(JpaObject.FieldsInvisible, "password", "icon")));
	}

	private List<Wo> list(Business business, String unitFlag, String unitDutyName) throws Exception {
		List<Wo> wos = new ArrayList<>();
		Unit unit = business.unit().pick(unitFlag);
		if (null != unit) {
			List<String> identityIds = this.listIdentityId(business, unit, unitDutyName);
			EntityManager em = business.entityManagerContainer().get(Identity.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
			Root<Identity> root = cq.from(Identity.class);
			Predicate p = root.get(Identity_.id).in(identityIds);
			List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
			wos = Wo.copier.copy(os);
			wos = business.identity().sort(wos);
			for (Wo wo : wos) {
				this.referencePerson(business, wo);
			}
		}
		return wos;
	}

	private List<String> listIdentityId(Business business, Unit unit, String unitDutyName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.equal(root.get(UnitDuty_.name), unitDutyName);
		p = cb.and(p, cb.equal(root.get(UnitDuty_.unit), unit.getId()));
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
		List<String> identityIds = new ArrayList<>();
		for (UnitDuty o : os) {
			identityIds.addAll(o.getIdentityList());
		}
		identityIds = ListTools.trim(identityIds, true, true);
		return identityIds;
	}

	private void referencePerson(Business business, Wo wo) throws Exception {
		Person person = business.person().pick(wo.getPerson());
		if (null == person) {
			throw new ExceptionPersonNotExist(wo.getPerson());
		}
		WoPerson woPerson = WoPerson.copier.copy(person);
		wo.setWoPerson(woPerson);
	}

}