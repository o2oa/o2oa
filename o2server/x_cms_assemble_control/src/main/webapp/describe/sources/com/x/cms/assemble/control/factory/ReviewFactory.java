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
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.Review_;

/**
 * Review管理表基础功能服务类
 * 
 * @author O2LEE
 */
public class ReviewFactory extends AbstractFactory {

	public ReviewFactory( Business business ) throws Exception {
		super(business);
	}
	
	public Review get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Review.class, ExceptionWhen.none );
	}
	
	public List<String> listWithDocument( String docId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		cq.select(root.get(Review_.id));
		Predicate p = cb.equal( root.get(Review_.documentId), docId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
//	public List<Review> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( Review.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Review> cq = cb.createQuery( Review.class );
//		Root<Review> root = cq.from( Review.class );
//		Predicate p = root.get(Review_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
}