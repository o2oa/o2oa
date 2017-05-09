package com.x.organization.assemble.personal.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.assemble.personal.AbstractFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;

public class IdentityFactory extends AbstractFactory {

	public IdentityFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), id);
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}