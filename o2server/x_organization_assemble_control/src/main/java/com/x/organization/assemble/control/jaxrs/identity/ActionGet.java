package com.x.organization.assemble.control.jaxrs.identity;

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
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

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

	public static class Wo extends Identity {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		@FieldDescribe("组织对象")
		private WoUnit woUnit;

		@FieldDescribe("个人对象")
		private WoPerson woPerson;

		public WoUnit getWoUnit() {
			return woUnit;
		}

		public void setWoUnit(WoUnit woUnit) {
			this.woUnit = woUnit;
		}

		public WoPerson getWoPerson() {
			return woPerson;
		}

		public void setWoPerson(WoPerson woPerson) {
			this.woPerson = woPerson;
		}

	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = 2465212973550376118L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级身份数量")
		private Long subDirectIdentityCount = 0L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		public Long getSubDirectUnitCount() {
			return subDirectUnitCount;
		}

		public void setSubDirectUnitCount(Long subDirectUnitCount) {
			this.subDirectUnitCount = subDirectUnitCount;
		}

		public Long getSubDirectIdentityCount() {
			return subDirectIdentityCount;
		}

		public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
			this.subDirectIdentityCount = subDirectIdentityCount;
		}

	}

	public static class WoPerson extends Person {

		private static final long serialVersionUID = -8775294698857946888L;

		static WrapCopier<Person, WoPerson> copier = WrapCopierFactory.wo(Person.class, WoPerson.class, null,
				ListTools.toList(ListTools.toList(JpaObject.FieldsInvisible, "password", "icon")));
	}

	private Wo get(Business business, String flag) throws Exception {
		Identity identity = business.identity().pick(flag);
		if (null == identity) {
			throw new ExceptionIdentityNotExist(flag);
		}
		Wo wo = Wo.copier.copy(identity);
		this.referenceUnit(business, wo);
		this.referencePerson(business, wo);
		return wo;
	}

	private void referenceUnit(Business business, Wo wo) throws Exception {
		if (StringUtils.isNotEmpty(wo.getUnit())) {
			Unit unit = business.unit().pick(wo.getUnit());
			if (null == unit) {
				throw new ExceptionUnitNotExist(wo.getUnit());
			}
			WoUnit woUnit = WoUnit.copier.copy(unit);
			woUnit.setSubDirectIdentityCount(this.countSubDirectIdentity(business, woUnit));
			woUnit.setSubDirectUnitCount(this.countSubDirectUnit(business, woUnit));
			wo.setWoUnit(woUnit);
		}
	}

	private void referencePerson(Business business, Wo wo) throws Exception {
		Person person = business.person().pick(wo.getPerson());
		if (null == person) {
			throw new ExceptionPersonNotExist(wo.getPerson());
		}
		WoPerson woPerson = WoPerson.copier.copy(person);
		wo.setWoPerson(woPerson);
	}

	private Long countSubDirectUnit(Business business, WoUnit woUnit) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.superior), woUnit.getId());
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		return count;
	}

	private Long countSubDirectIdentity(Business business, WoUnit woUnit) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.unit), woUnit.getId());
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		return count;
	}
}
