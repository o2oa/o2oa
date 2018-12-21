package com.x.processplatform.assemble.designer.content.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Attachment_;

public class AttachmentFactory extends AbstractFactory {

	public AttachmentFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.application), id);
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplicationWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.application), id);
		p = cb.and(p, cb.equal(root.get(Attachment_.completed), completed));
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.process), id);
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcessWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.process), id);
		p = cb.and(p, cb.equal(root.get(Attachment_.completed), completed));
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}