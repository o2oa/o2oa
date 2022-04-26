package com.x.organization.assemble.express.jaxrs.unit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ActionListWithTypeObject extends BaseAction {

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String type) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), type);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, type);
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends com.x.base.core.project.organization.Unit {

	}

	private List<Wo> list(Business business, String type) throws Exception {
		List<Wo> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.isMember(type, root.get(Unit_.typeList));
		List<String> unitIds = em.createQuery(cq.select(root.get(Unit_.id)).where(p)).getResultList();
		List<Unit> units = business.unit().pick(unitIds);
		units = business.unit().sort(units);
		for (Unit o : units) {
			wos.add(this.convert(business, o, Wo.class));
		}
		return wos;
	}

}
