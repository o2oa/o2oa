package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.Review_;
import com.x.cms.core.entity.tools.CriteriaBuilderTools;
import com.x.cms.core.entity.tools.filter.QueryFilter;
/**
 * 文档权限控制信息服务类
 */
public class ReviewFactory extends AbstractFactory {
	
	public ReviewFactory( Business business) throws Exception {
		super(business);
	}

	public Review get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Review.class, ExceptionWhen.none);
	}
	
	public List<String> listByAppId( String appId, Integer maxCount ) throws Exception {
		if( maxCount == null ) {
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.appId ), appId );
		cq.select( root.get( Review_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	public List<String> listByCategoryId( String categoryId, Integer maxCount ) throws Exception {
		if( maxCount == null ) {
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.categoryId ), categoryId );
		cq.select(root.get( Review_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	public List<String> listByDocument( String docId, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.docId ), docId );
		cq.select(root.get( Review_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}	
	
	public List<String> listByDocumentAndPerson(String docId, String person) throws Exception {
		if( StringUtils.isEmpty( docId ) ) {
			throw new Exception("doc id can not be empty!");
		}
		if( StringUtils.isEmpty( person ) ) {
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.docId ), docId );
		p = cb.and( p, cb.equal( root.get( Review_.permissionObj ), person));
		cq.select(root.get( Review_.id)).where(p);
		return em.createQuery( cq ).getResultList();
	}
	
	public Long countByCategoryId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal( root.get(Review_.categoryId), categoryId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	public Long countByDocuemnt( String docId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal( root.get(Review_.docId), docId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	public Long countByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal( root.get(Review_.appId), appId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 根据条件查询符合条件的文档信息数量
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( String personName, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = cb.equal( root.get( Review_.permissionObj ), "*");
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Review_.permissionObj ), personName ) );
		}
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 根据条件查询符合条件的文档信息ID
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Review> listWithFilter( Integer maxCount, String orderField, String orderType, String personName, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = cb.equal( root.get( Review_.permissionObj ), "*");
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Review_.permissionObj ), personName ) );
		}
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );
		
		List<Order> orders = new ArrayList<>();
		if( !Review.isTop_FIELDNAME.equals( orderField )) {
			Order isTopOrder = CriteriaBuilderTools.getOrder( cb, root, Review_.class, Review.isTop_FIELDNAME, "desc" );
			if( isTopOrder != null ){
				orders.add( isTopOrder );
			}
		}
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Review_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}
		if( ListTools.isNotEmpty( orders )) {
			cq.orderBy( orders );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	
	
	/**
	 * 根据条件查询符合条件的文档信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param maxCount
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Review> listWithFilter( Integer maxCount, String sequenceFieldValue, String orderField, String orderType, String personName, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = cb.equal( root.get( Review_.permissionObj ), "*");
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Review_.permissionObj ), personName ) );
		}
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );
		if( StringUtils.isNotEmpty( sequenceFieldValue ) ) {
			Predicate p_seq = cb.isNotNull( root.get( Review_.docSequence ) );
			if( "desc".equalsIgnoreCase( orderType )){
				p_seq = cb.and( p_seq, cb.lessThan( root.get( Review_.docSequence ), sequenceFieldValue.toString() ));
			}else{
				p_seq = cb.and( p_seq, cb.greaterThan( root.get( Review_.docSequence ), sequenceFieldValue.toString() ));
			}
			p = cb.and( p, p_seq);
		}
		
		List<Order> orders = new ArrayList<>();
		if( !Document.isTop_FIELDNAME.equals( orderField )) {
			Order isTopOrder = CriteriaBuilderTools.getOrder( cb, root, Document_.class, Document.isTop_FIELDNAME, "desc" );
			if( isTopOrder != null ){
				orders.add( isTopOrder );
			}
		}
		
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Review_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}
		
		if( ListTools.isNotEmpty( orders )) {
			cq.orderBy( orders );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	/**
	 * 根据条件查询指定条数符合条件的文档信息Review列表
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param queryFilter
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<Review> listWithFilter( String orderField, String orderType, String personName, QueryFilter queryFilter, int maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = cb.equal( root.get( Review_.permissionObj ), "*");
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Review_.permissionObj ), personName ) );
		}
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );
		
		//排序，添加排序列，默认使用sequence
		List<Order> orders = new ArrayList<>();
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Review_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}
		
		if( !Document.isFieldInSequence(orderField)) {
			//如果是其他的列，很可能排序值不唯一，所以使用多一列排序列来确定每次查询的顺序
			orderWithField = CriteriaBuilderTools.getOrder( cb, root, Review_.class, Review.id_FIELDNAME, orderType );
			if( orderWithField != null ){
				orders.add( orderWithField );
			}
		}		
		if( ListTools.isNotEmpty(  orders )){
			cq.orderBy( orders );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	public List<String> listDocIdsWithConditionInReview(String personName, QueryFilter queryFilter, Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = cb.equal( root.get( Review_.permissionObj ), "*");
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Review_.permissionObj ), personName ) );
		}
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );
		cq.select(root.get( Review_.docId )).where(p);
		
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	public List<String> listDocIdsWithConditionInReview(String personName, String orderField, String orderType, QueryFilter queryFilter, Integer maxCount) throws Exception {
		List<String> docIds = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = cb.equal( root.get( Review_.permissionObj ), "*");
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Review_.permissionObj ), personName ) );
		}
		if( StringUtils.isNotEmpty( orderField ) ) {
			Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Review_.class, orderField, orderType );
			if( orderWithField != null ){
				cq.orderBy( orderWithField );
			}
		}
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );

		List<Review> reviewList = em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
		if( ListTools.isNotEmpty(reviewList) ) {
			for( Review review : reviewList ) {
				docIds.add( review.getDocId() );
			}
		}
		return docIds;
	}
}