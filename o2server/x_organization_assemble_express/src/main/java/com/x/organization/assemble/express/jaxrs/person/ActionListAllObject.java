package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;

class ActionListAllObject extends BaseAction {

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business);
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends com.x.base.core.project.organization.Person {

	}

	private List<Wo> list(Business business) throws Exception {
		List<Wo> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		List<Person> os = em.createQuery(cq.select(root)).getResultList();
		os = business.person().sort(os);
		for (Person o : os) {
			wos.add(this.convert(business, o, Wo.class));
		}
		return wos;
	}

}