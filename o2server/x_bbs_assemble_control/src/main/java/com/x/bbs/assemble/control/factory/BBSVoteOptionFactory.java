package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;
import com.x.bbs.entity.BBSVoteOptionGroup_;
import com.x.bbs.entity.BBSVoteOption_;

/**
 * 类   名：BBSVoteOptionFactory<br/>
 * 实体类：BBSVoteOption<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class BBSVoteOptionFactory extends AbstractFactory {

	public BBSVoteOptionFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSVoteOption实体信息对象" )
	public BBSVoteOption get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSVoteOption.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的BBSVoteOption实体信息列表" )
	public List<BBSVoteOption> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSVoteOption>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSVoteOption.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteOption> cq = cb.createQuery(BBSVoteOption.class);
		Root<BBSVoteOption> root = cq.from(BBSVoteOption.class);
		Predicate p = root.get(BBSVoteOption_.id).in(ids);
		cq.orderBy( cb.desc( root.get( BBSVoteOption_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据主题信息ID查询所有投票选择项信息ID列表" )
	public List<String> listVoteOptionIdsBySubject( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteOption.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSVoteOption> root = cq.from( BBSVoteOption.class );
		Predicate p = cb.equal( root.get( BBSVoteOption_.subjectId ), subjectId );
		cq.select( root.get( BBSVoteOption_.id ) );
		return em.createQuery( cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据主题信息ID查询所有投票选择项信息列表" )
	public List<BBSVoteOption> listVoteOptionBySubject( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteOption.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteOption> cq = cb.createQuery( BBSVoteOption.class);
		Root<BBSVoteOption> root = cq.from( BBSVoteOption.class );
		Predicate p = cb.equal( root.get( BBSVoteOption_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据主题信息ID查询所有投票选择项分组信息列表" )
	public List<BBSVoteOptionGroup> listVoteOptionGroupBySubject( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteOptionGroup.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteOptionGroup> cq = cb.createQuery( BBSVoteOptionGroup.class);
		Root<BBSVoteOptionGroup> root = cq.from( BBSVoteOptionGroup.class );
		Predicate p = cb.equal( root.get( BBSVoteOptionGroup_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<BBSVoteOption> listVoteOptionByGroupId(String groupId) throws Exception {
		if( groupId == null || groupId.isEmpty() ){
			throw new Exception( "groupId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteOption.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteOption> cq = cb.createQuery( BBSVoteOption.class);
		Root<BBSVoteOption> root = cq.from( BBSVoteOption.class );
		Predicate p = cb.equal( root.get( BBSVoteOption_.optionGroupId ), groupId );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
