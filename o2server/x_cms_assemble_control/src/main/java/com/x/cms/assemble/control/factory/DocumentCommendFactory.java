package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

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

	public List<String> listWithPerson( String personName, Integer maxCount, String type) throws Exception {
		if( maxCount == null) {
			maxCount = 0;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.commendPerson), personName );
		if(StringUtils.isNoneBlank(type)){
			p = cb.and( p, cb.equal(root.get( DocumentCommend_.type), type ) );
		}
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		if(maxCount<1){
			return em.createQuery(cq).getResultList();
		}else {
			return em.createQuery(cq).setMaxResults(maxCount).getResultList();
		}
	}

	public List<String> listByDocument( String docId, Integer maxCount, String type) throws Exception {
		if( maxCount == null) {
			maxCount = 0;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.documentId), docId );
		if(StringUtils.isNoneBlank(type)){
			p = cb.and( p, cb.equal(root.get( DocumentCommend_.type), type ) );
		}
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		if(maxCount<1){
			return em.createQuery(cq).getResultList();
		}else {
			return em.createQuery(cq).setMaxResults(maxCount).getResultList();
		}
	}

	public List<String> listByComment( String commentId, Integer maxCount) throws Exception {
		if( maxCount == null) {
			maxCount = 0;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.commentId), commentId );
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		if(maxCount<1){
			return em.createQuery(cq).getResultList();
		}else {
			return em.createQuery(cq).setMaxResults(maxCount).getResultList();
		}
	}

	public List<String> listByDocAndPerson( String docId, String personName, Integer maxCount, String type) throws Exception {
		if( maxCount == null) {
			maxCount = 0;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.documentId), docId );
		p = cb.and( p, cb.equal(root.get( DocumentCommend_.commendPerson), personName ) );
		if(StringUtils.isNoneBlank(type)){
			p = cb.and( p, cb.equal(root.get( DocumentCommend_.type), type ) );
		}
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		if(maxCount<1){
			return em.createQuery(cq).getResultList();
		}else {
			return em.createQuery(cq).setMaxResults(maxCount).getResultList();
		}
	}

	public List<String> listByCommentAndPerson( String commentId, String personName, Integer maxCount) throws Exception {
		if( maxCount == null) {
			maxCount = 0;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.commentId), commentId );
		p = cb.and( p, cb.equal(root.get( DocumentCommend_.commendPerson), personName ) );
		cq.select( root.get( DocumentCommend_.id) ).where(p);
		if(maxCount<1){
			return em.createQuery(cq).getResultList();
		}else {
			return em.createQuery(cq).setMaxResults(maxCount).getResultList();
		}
	}

	public Long countByDocAndType( String docId, String type) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentCommend.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<DocumentCommend> root = cq.from( DocumentCommend.class );
		Predicate p = cb.equal(root.get( DocumentCommend_.documentId), docId );
		if(StringUtils.isNoneBlank(type)){
			p = cb.and( p, cb.equal(root.get( DocumentCommend_.type), type ) );
		}
		cq.select( cb.count(root) ).where(p);
		Long count = em.createQuery(cq).getSingleResult();
		return count == null ? 0L : count;
	}
}
