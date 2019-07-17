package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Attachment_;

public class AttachmentFactory extends AbstractFactory {

	public AttachmentFactory(Business business) throws Exception {
		super(business);
	}
	
	public Attachment get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Attachment.class, ExceptionWhen.none);
	}

	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from( Attachment.class);
		cq.select(root.get(Attachment_.id));
		return em.createQuery(cq).getResultList();
	}

	public List<Attachment> list(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return new ArrayList<Attachment>();
		}
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Attachment> cq = cb.createQuery(Attachment.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = root.get(Attachment_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Attachment> listAttachmentWithProject(String project) throws Exception {
		if( StringUtils.isEmpty( project ) ){
			return new ArrayList<Attachment>();
		}
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Attachment> cq = cb.createQuery(Attachment.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal( root.get(Attachment_.projectId), project );
		p = cb.and( p, cb.equal( root.get(Attachment_.bundleObjType ), "PROJECT"));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Attachment> listAttachmentWithTask(String taskId ) throws Exception {
		if( StringUtils.isEmpty( taskId ) ){
			return new ArrayList<Attachment>();
		}
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Attachment> cq = cb.createQuery(Attachment.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal( root.get(Attachment_.taskId ), taskId );
		p = cb.and( p, cb.equal( root.get(Attachment_.bundleObjType ), "TASK"));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
