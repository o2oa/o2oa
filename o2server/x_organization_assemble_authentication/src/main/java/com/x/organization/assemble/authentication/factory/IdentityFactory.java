package com.x.organization.assemble.authentication.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.assemble.authentication.AbstractFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;

public class IdentityFactory extends AbstractFactory {

	public IdentityFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithPerson(String personId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), personId);
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}