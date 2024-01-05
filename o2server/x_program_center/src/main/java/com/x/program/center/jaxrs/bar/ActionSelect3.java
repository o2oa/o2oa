package com.x.program.center.jaxrs.bar;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.validation.Bar;
import com.x.program.center.core.entity.validation.Bar_;

class ActionSelect3 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSelect3.class);

	ActionResult<List<String>> execute(EffectivePerson effectivePerson, String field, String value, Integer count)
			throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<String>> result = new ActionResult<>();
		List<String> wos = select(field, value, count);
		result.setData(wos);
		return result;
	}

	private List<String> select(String field, String value, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Bar.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Bar> root = cq.from(Bar.class);
			Predicate p = cb.isMember(value, root.get(field));
			cq.select(root.get(Bar_.id)).where(p).orderBy(cb.asc(root.get(Bar_.name)));
			return em.createQuery(cq).setFirstResult(1).setMaxResults(count).getResultList();
		}
	}

}
