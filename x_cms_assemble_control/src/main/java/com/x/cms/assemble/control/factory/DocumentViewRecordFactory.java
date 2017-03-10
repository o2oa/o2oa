package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentViewRecord;
import com.x.cms.core.entity.DocumentViewRecord_;

/**
 * 文档权限基础功能服务类
 * @author liyi
 */
public class DocumentViewRecordFactory extends AbstractFactory {

	public DocumentViewRecordFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe("列示指定Id的DocumentViewRecord信息列表")
	public List<DocumentViewRecord> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentViewRecord> cq = cb.createQuery( DocumentViewRecord.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = root.get( DocumentViewRecord_.id).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}

	@MethodDescribe("根据文档ID列示指定Id的DocumentViewRecord信息列表")
	public List<String> listByDocument( String docId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.documentId ), docId );
		cq.orderBy( cb.desc( root.get( DocumentViewRecord_.createTime ) ) );
		cq.select( root.get( DocumentViewRecord_.id ));
		return em.createQuery( cq.where(p) ).setMaxResults(50).getResultList();
	}
	
	@MethodDescribe("根据访问者姓名列示指定Id的DocumentViewRecord信息列表")
	public List<String> listByPerson( String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.viewerName ), personName );
		cq.orderBy( cb.desc( root.get( DocumentViewRecord_.createTime ) ) );
		cq.select( root.get( DocumentViewRecord_.id ));
		return em.createQuery( cq.where(p) ).setMaxResults(50).getResultList();
	}
	
	@MethodDescribe("根据访问者姓名和文档ID列示指定Id的DocumentViewRecord信息列表")
	public List<String> listByDocAndPerson(String documentId, String personName) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.viewerName ), personName );
		p = cb.and( p, cb.equal( root.get( DocumentViewRecord_.documentId ), documentId ) );
		cq.select( root.get( DocumentViewRecord_.id ));
		return em.createQuery( cq.where(p) ).getResultList();
	}
}