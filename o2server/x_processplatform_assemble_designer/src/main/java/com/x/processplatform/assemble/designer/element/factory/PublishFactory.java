package com.x.processplatform.assemble.designer.element.factory;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Publish_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author sword
 */
public class PublishFactory extends AbstractFactory {

	public PublishFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Publish.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Publish> root = cq.from(Publish.class);
		Predicate p = cb.equal(root.get(Publish_.process), processId);
		cq.select(root.get(Publish_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Publish> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Publish.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Publish> cq = cb.createQuery(Publish.class);
		Root<Publish> root = cq.from(Publish.class);
		Predicate p = cb.equal(root.get(Publish_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的invoke */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Publish.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Publish> root = cq.from(Publish.class);
		Predicate p = cb.equal(root.get(Publish_.form), formId);
		cq.select(root.get(Publish_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}
