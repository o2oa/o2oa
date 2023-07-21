package com.x.organization.assemble.control.jaxrs.unitattribute;

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
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitAttribute_;

class ActionListWithUnit extends BaseAction {

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String unitFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Unit unit = business.unit().pick(unitFlag);
			if (null == unit) {
				throw new ExceptionUnitNotExist(unitFlag);
			}
			CacheKey cacheKey = new CacheKey(this.getClass(), unitFlag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<UnitAttribute> os = this.list(business, unit);
				List<Wo> wos = Wo.copier.copy(os);
				wos = business.unitAttribute().sort(wos);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends UnitAttribute {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<UnitAttribute, Wo> copier = WrapCopierFactory.wo(UnitAttribute.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<UnitAttribute> list(Business business, Unit unit) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitAttribute> cq = cb.createQuery(UnitAttribute.class);
		Root<UnitAttribute> root = cq.from(UnitAttribute.class);
		Predicate p = cb.equal(root.get(UnitAttribute_.unit), unit.getId());
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

}