package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSVoteRecord;
import com.x.bbs.entity.BBSVoteRecord_;

/**
 * 类   名：BBSVoteRecordFactory<br/>
 * 实体类：BBSVoteRecord<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class BBSVoteRecordFactory extends AbstractFactory {

	public BBSVoteRecordFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSVoteRecord实体信息对象" )
	public BBSVoteRecord get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSVoteRecord.class, ExceptionWhen.none );
	}
	
//	@MethodDescribe( "列示指定Id的BBSVoteRecord实体信息列表" )
	public List<BBSVoteRecord> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSVoteRecord>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSVoteRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteRecord> cq = cb.createQuery(BBSVoteRecord.class);
		Root<BBSVoteRecord> root = cq.from(BBSVoteRecord.class);
		Predicate p = root.get(BBSVoteRecord_.id).in(ids);
		cq.orderBy( cb.desc( root.get( BBSVoteRecord_.sequence ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据主题信息ID查询所有投票记录信息ID列表" )
	public List<String> listVoteRecordBySubjectIds( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		cq.select(root.get( BBSVoteRecord_.id));
		Predicate p = cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据主题信息ID查询所有投票记录信息ID列表" )
	public List<BBSVoteRecord> listVoteRecordBySubject( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteRecord> cq = cb.createQuery( BBSVoteRecord.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		Predicate p = cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Long getVoteCountByUserAndSubject(String personName, String subjectId) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		Predicate p = cb.equal( root.get( BBSVoteRecord_.votorName ), personName );
		p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId ) );
		cq.select( cb.count( root ));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getVoteCountByUserAndOption(String personName, String optionId) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		if( optionId == null || optionId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		Predicate p = cb.equal( root.get( BBSVoteRecord_.votorName ), personName );
		p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionId ), optionId ) );
		cq.select( cb.count( root ));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long countVoteRecordForSubject( String subjectId, String voteOptionId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class);
		Predicate p = cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId );
		if( StringUtils.isNotEmpty( voteOptionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionId ), voteOptionId ));
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<BBSVoteRecord> listVoteRecordForPage(String subjectId, String voteOptionId, Integer maxRecordCount) throws Exception {
		if( maxRecordCount == null ){
			throw new Exception( "maxRecordCount is null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSVoteRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteRecord> cq = cb.createQuery(BBSVoteRecord.class);
		Root<BBSVoteRecord> root = cq.from(BBSVoteRecord.class);
		Predicate p = cb.isNotNull( root.get( BBSVoteRecord_.id ) );
		p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionId ), voteOptionId ));
		cq.orderBy( cb.desc( root.get( BBSVoteRecord_.createTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}
}
