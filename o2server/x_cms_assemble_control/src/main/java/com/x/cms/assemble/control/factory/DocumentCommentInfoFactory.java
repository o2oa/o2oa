package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.cms.core.entity.DocumentCommentInfo_;
import org.apache.commons.lang3.StringUtils;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentCommentContent;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.entity.tools.CriteriaBuilderTools;
import com.x.cms.core.entity.tools.DateOperation;
import com.x.cms.core.entity.tools.filter.QueryFilter;

/**
 * 文档评论基础功能服务类
 * 
 * @author O2LEE
 */
public class DocumentCommentInfoFactory extends AbstractFactory {

	DateOperation dateOperation = new DateOperation();
	
	public DocumentCommentInfoFactory(Business business) throws Exception {
		super(business);
	}

	public DocumentCommentInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, DocumentCommentInfo.class );
	}
	
	public DocumentCommentContent getContent( String id ) throws Exception {
		return this.entityManagerContainer().find(id, DocumentCommentContent.class );
	}

	public List<DocumentCommentInfo> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentCommentInfo> cq = cb.createQuery( DocumentCommentInfo.class );
		Root<DocumentCommentInfo> root = cq.from( DocumentCommentInfo.class );
		Predicate p = root.get( DocumentCommentInfo_.documentId ).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithDocument( String docId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommentInfo> root = cq.from( DocumentCommentInfo.class );
		Predicate p = cb.equal(root.get( DocumentCommentInfo_.documentId ), docId );
		cq.select( root.get( DocumentCommentInfo_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public Integer getMaxOrder(String documentId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery( Integer.class );
		Root<DocumentCommentInfo> root = cq.from( DocumentCommentInfo.class );
		Predicate p = cb.equal(root.get( DocumentCommentInfo_.documentId ), documentId );
		cq.select( cb.max( root.get( DocumentCommentInfo_.orderNumber )));
		Integer max = em.createQuery(cq.where(p)).getSingleResult();
		return max == null ? 0 : max;
	}
	
	public List<String> listWithPerson( String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommentInfo> root = cq.from( DocumentCommentInfo.class );
		Predicate p = cb.equal(root.get( DocumentCommentInfo_.creatorName ), personName );
		cq.select( root.get( DocumentCommentInfo_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithPersonAndDoc( String personName, String docId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommentInfo> root = cq.from( DocumentCommentInfo.class );
		Predicate p = cb.equal(root.get( DocumentCommentInfo_.creatorName ), personName );
		p = cb.and( p, cb.equal( root.get( DocumentCommentInfo_.documentId ), docId ));
		cq.select( root.get( DocumentCommentInfo_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据条件查询符合条件的评论信息数量
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<DocumentCommentInfo> root = cq.from(DocumentCommentInfo.class);		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( DocumentCommentInfo_.class, cb, null, root, queryFilter );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 根据条件查询符合条件的评论信息ID
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<DocumentCommentInfo> listWithFilter( Integer maxCount, String orderField, String orderType, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentCommentInfo> cq = cb.createQuery(DocumentCommentInfo.class);
		Root<DocumentCommentInfo> root = cq.from(DocumentCommentInfo.class);		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( DocumentCommentInfo_.class, cb, null, root, queryFilter );
		Order orderWithField = CriteriaBuilderTools.getOrder(cb, root, DocumentCommentInfo_.class, orderField, orderType);
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 * 根据条件查询符合条件的评论信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param maxCount
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<DocumentCommentInfo> listWithFilter( Integer maxCount, Object sequenceFieldValue, String orderField, String orderType, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommentInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentCommentInfo> cq = cb.createQuery(DocumentCommentInfo.class);
		Root<DocumentCommentInfo> root = cq.from(DocumentCommentInfo.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( DocumentCommentInfo_.class, cb, null, root, queryFilter );
		
		if( sequenceFieldValue != null && StringUtils.isNotEmpty( sequenceFieldValue.toString() )) {
			Predicate p_seq = cb.isNotNull( root.get( DocumentCommentInfo_.sequence ) );
			if( "desc".equalsIgnoreCase( orderType )){
				p_seq = cb.and( p_seq, cb.lessThan( root.get( DocumentCommentInfo_.sequence ), sequenceFieldValue.toString() ));
			}else{
				p_seq = cb.and( p_seq, cb.greaterThan( root.get( DocumentCommentInfo_.sequence ), sequenceFieldValue.toString() ));
			}
			p = cb.and( p, p_seq);
		}		
		
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, DocumentCommentInfo_.class, orderField, orderType );
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}	
}