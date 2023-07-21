package com.x.organization.assemble.authentication.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.assemble.authentication.AbstractFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Bind;
import com.x.organization.core.entity.Bind_;

public class BindFactory extends AbstractFactory {

	public BindFactory(Business business) throws Exception {
		super(business);
	}

	/* "根据给定的Person Name获取Person Id */
	public String getWithMeta(String meta) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Bind.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Bind> root = cq.from(Bind.class);
		Predicate p = cb.equal(root.get(Bind_.meta), meta);
		cq.select(root.get(Bind_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

}