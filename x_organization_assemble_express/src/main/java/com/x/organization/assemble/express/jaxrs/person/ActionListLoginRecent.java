package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

class ActionListLoginRecent extends ActionBase {
	ActionResult<List<WrapOutPerson>> execute(Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
			count = NumberUtils.min(count, 1000);
			List<WrapOutPerson> wraps = new ArrayList<>();
			EntityManager em = emc.get(Person.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Person> cq = cb.createQuery(Person.class);
			Root<Person> root = cq.from(Person.class);
			Predicate p = cb.isNotNull(root.get(Person_.lastLoginTime));
			cq.select(root).where(p).orderBy(cb.desc(root.get(Person_.lastLoginTime)));
			List<Person> os = em.createQuery(cq).setMaxResults(count).getResultList();
			wraps = outCopier.copy(os);
			result.setData(wraps);
			return result;
		}
	}
}
