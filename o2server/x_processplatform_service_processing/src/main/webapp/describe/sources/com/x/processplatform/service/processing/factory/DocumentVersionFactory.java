package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.DocumentVersion_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class DocumentVersionFactory extends AbstractFactory {

	public DocumentVersionFactory(Business business) throws Exception {
		super(business);
	}

 
	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DocumentVersion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DocumentVersion> root = cq.from(DocumentVersion.class);
		Predicate p = cb.equal(root.get(DocumentVersion_.job), job);
		cq.select(root.get(DocumentVersion_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}