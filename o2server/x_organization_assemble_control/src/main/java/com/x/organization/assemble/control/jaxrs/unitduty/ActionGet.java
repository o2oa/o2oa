package com.x.organization.assemble.control.jaxrs.unitduty;

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
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;

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
		UnitDuty o = business.unitDuty().pick(flag);
		if (null == o) {
			throw new ExceptionUnitDutyNotExist(flag);
		}
		Wo wo = Wo.copier.copy(o);
		this.referenceUnit(business, wo);
		this.referenceIdentity(business, wo);
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

	private void referenceIdentity(Business business, Wo wo) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = root.get(Identity_.id).in(wo.getIdentityList());
		List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
		List<WoIdentity> wos = WoIdentity.copier.copy(os);
		for (WoIdentity woIdentity : wos) {
			this.referencePerson(business, woIdentity);
		}
		wos = business.identity().sort(wos);
		wo.setWoIdentityList(wos);
	}

	private void referencePerson(Business business, WoIdentity woIdentity) throws Exception {
		Person person = business.person().pick(woIdentity.getPerson());
		if (null == person) {
			throw new ExceptionPersonNotExist(woIdentity.getPerson());
		}
		WoPerson woPerson = WoPerson.copier.copy(person);
		woIdentity.setWoPerson(woPerson);
	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = -7721760092867057759L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Wo extends UnitDuty {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<UnitDuty, Wo> copier = WrapCopierFactory.wo(UnitDuty.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("组织对象")
		private WoUnit woUnit;

		@FieldDescribe("身份对象")
		private List<WoIdentity> woIdentityList;

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

	public static class WoIdentity extends Identity {

		private static final long serialVersionUID = 7096544058621159846L;

		private WoPerson woPerson;

		static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.wo(Identity.class, WoIdentity.class, null,
				JpaObject.FieldsInvisible);

		public WoPerson getWoPerson() {
			return woPerson;
		}

		public void setWoPerson(WoPerson woPerson) {
			this.woPerson = woPerson;
		}
	}

	public static class WoPerson extends Person {

		private static final long serialVersionUID = 7096544058621159846L;

		static WrapCopier<Person, WoPerson> copier = WrapCopierFactory.wo(Person.class, WoPerson.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, "password", "icon"));
	}

}
