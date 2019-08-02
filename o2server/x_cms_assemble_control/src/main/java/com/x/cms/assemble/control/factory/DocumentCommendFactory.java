package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.DocumentCommend_;

/**
 * 文档点赞基础功能服务类
 * 
 * @author O2LEE
 */
public class DocumentCommendFactory extends AbstractFactory {
	
	public DocumentCommendFactory(Business business) throws Exception {
		super(business);
	}

	public DocumentCommend get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, DocumentCommend.class, ExceptionWhen.none);
	}

	public List<String> listWithPerson( String personName, Integer maxCount ) throws Exception {
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 10;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.commendPerson), personName );
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}

	public List<String> listByDocument( String docId, Integer maxCount ) throws Exception {
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 10;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.documentId), docId );
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	public List<String> listByDocAndPerson( String docId, String personName, Integer maxCount ) throws Exception {
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 10;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.documentId), docId );
		p = cb.and( p, cb.equal(root.get( DocumentCommend_.commendPerson), personName ) );
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
}