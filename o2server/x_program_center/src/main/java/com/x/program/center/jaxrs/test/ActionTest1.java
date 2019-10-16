package com.x.program.center.jaxrs.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.core.entity.Person;

class ActionTest1 extends BaseAction {

	ActionResult<List<Object>> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<List<Object>> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Person.class);
			List<Object> os = em.createQuery(this.criteriaQuery(Person.class, Person.class)).setMaxResults(1)
					.getResultList();
			result.setData(os);
		}
		return result;
	}

	private <T extends JpaObject, W> CriteriaQuery<Object> criteriaQuery(Class<T> t, Class<W> w) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(t);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object> cq = cb.createQuery();
			Root<T> root = cq.from(t);
			Predicate p = cb.isNotNull(root.get("id"));
			return cq.where(p);
		}

	}

}