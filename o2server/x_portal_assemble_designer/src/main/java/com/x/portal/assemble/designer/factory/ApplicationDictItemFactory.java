package com.x.portal.assemble.designer.factory;


import com.x.general.core.entity.ApplicationDictItem;
import com.x.general.core.entity.ApplicationDictItem_;
import com.x.portal.assemble.designer.AbstractFactory;
import com.x.portal.assemble.designer.Business;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class ApplicationDictItemFactory extends AbstractFactory {

	public ApplicationDictItemFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.application), id);
		cq.select(root.get(ApplicationDictItem_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplicationDict(String applicationDict) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		cq.select(root.get(ApplicationDictItem_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<ApplicationDictItem> listWithApplicationDictObject(String applicationDict) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}
}
