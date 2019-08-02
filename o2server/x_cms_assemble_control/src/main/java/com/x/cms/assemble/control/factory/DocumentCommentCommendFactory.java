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
import com.x.cms.core.entity.DocumentCommentCommend;
import com.x.cms.core.entity.DocumentCommentCommend_;

/**
 * 文档评论点赞基础功能服务类
 * 
 * @author O2LEE
 */
public class DocumentCommentCommendFactory extends AbstractFactory {
	
	public DocumentCommentCommendFactory(Business business) throws Exception {
		super(business);
	}

	public DocumentCommentCommend get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, DocumentCommentCommend.class, ExceptionWhen.none);
	}

	public List<String> listWithPerson( String personName, Integer maxCount ) throws Exception {
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 10;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommentCommend> root = cq.from( DocumentCommentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommentCommend_.commendPerson), personName );
		cq.select( root.get( DocumentCommentCommend_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	public List<String> listCommentIdsWithPerson( String personName, Integer maxCount ) throws Exception {
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 10;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommentCommend> root = cq.from( DocumentCommentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommentCommend_.commendPerson), personName );
		cq.select( root.get( DocumentCommentCommend_.commentId) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}

	public List<String> listIdsByDocumentComment( String comment, Integer maxCount ) throws Exception {
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 10;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommentCommend> root = cq.from( DocumentCommentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommentCommend_.commentId), comment );
		cq.select( root.get( DocumentCommentCommend_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	public List<String> listIdsByDocumentCommentAndPerson( String commentId, String personName, Integer maxCount ) throws Exception {
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 10;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommentCommend> root = cq.from( DocumentCommentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommentCommend_.commentId), commentId );
		p = cb.and( p, cb.equal(root.get( DocumentCommentCommend_.commendPerson), personName ) );
		cq.select( root.get( DocumentCommentCommend_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
}