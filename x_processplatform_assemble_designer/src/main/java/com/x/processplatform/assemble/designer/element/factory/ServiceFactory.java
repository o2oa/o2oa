package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Service_;

public class ServiceFactory extends AbstractFactory {

	public ServiceFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Service.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Service> root = cq.from(Service.class);
		Predicate p = cb.equal(root.get(Service_.process), id);
		cq.select(root.get(Service_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}