package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSectionInfo_;

/**
 * 类   名：BBSSectionInfoFactory<br/>
 * 实体类：BBSSectionInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class BBSSectionInfoFactory extends AbstractFactory {

	public BBSSectionInfoFactory( Business business ) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSSectionInfo实体信息对象" )
	public BBSSectionInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSectionInfo.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的BBSSectionInfo实体信息列表" )
	public List<BBSSectionInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSSectionInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = root.get(BBSSectionInfo_.id).in(ids);
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示全部的BBSSectionInfo实体信息列表" )
	public List<BBSSectionInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery( cq ).getResultList();
	}

	//@MethodDescribe( "根据论坛ID查询所有的主版块信息列表" )
	public List<BBSSectionInfo> listMainSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" ));
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据主版块ID查询所有的子版块信息列表" )
	public List<BBSSectionInfo> listSubSectionByMainSectionId( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据主版块ID查询所有的子版块信息Id列表" )
	public List<String> listSubSectionIdsByMainSectionId( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据论坛ID表查该论坛所包含的主版块数量" )
	public Long countMainSectionByForumId( String forumId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSectionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSectionInfo> root = cq.from( BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" ));
		cq.select( cb.count( root ) );		
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	//@MethodDescribe( "根据论坛ID表查该论坛所包含的主版块和子版块数量总和" )
	public Long countAllSectionByForumId( String forumId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSectionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSectionInfo> root = cq.from( BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		cq.select( cb.count( root ) );		
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	//@MethodDescribe( "根据版块ID查询该版块所包含的子版块数量" )
	public Long countSubSectionByMainSectionId(String sectionId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSectionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSectionInfo> root = cq.from( BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		cq.select( cb.count( root ) );		
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据论坛ID查询该论坛所包含的所有主版块以及子版块信息列表" )
	public List<BBSSectionInfo> listAllSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据论坛ID查询该论坛所包含的所有主版块以及子版块信息列表" )
	public List<String> listAllSectionIdsByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据论坛ID以及用户可访问的版块ID查询用户可访问的所有的主版块信息列表" )
	public List<BBSSectionInfo> viewMainSectionByForumId( String forumId, List<String> viewableSectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" ));	
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		Predicate or = cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" );
		if( ListTools.isNotEmpty( viewableSectionIds ) ){
			or = cb.or(p, root.get( BBSSectionInfo_.id ).in( viewableSectionIds ) );
		}
		p = cb.and( p, or );
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据版块ID以及用户可访问的版块ID列表查询所有用户可访问的子版块信息列表" )
	public List<BBSSectionInfo> viewSubSectionByMainSectionId( String sectionId, List<String> viewableSectionIds ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		Predicate or = cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" );
		if( ListTools.isNotEmpty( viewableSectionIds ) ){
			or = cb.or(p, root.get( BBSSectionInfo_.id ).in( viewableSectionIds ) );
		}
		p = cb.and( p, or );
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据论坛ID列表查询指定论坛范围内所有公开访问的主版块列表" )
	public List<String> listVisibleToAllUserMainSectionIds( List<String> forumIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and(p, root.get( BBSSectionInfo_.forumId ).in( forumIds ));
		}
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据论坛ID列表，主版块ID列表查询指定论坛和主版块范围内所有公开访问的子版块列表" )
	public List<String> listVisibleToAllUserSectionIds( List<String> forumIds, List<String> mainSectionIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and(p, root.get( BBSSectionInfo_.forumId ).in( forumIds ));
		}
		if( ListTools.isNotEmpty( mainSectionIds ) ){
			p = cb.and(p, root.get( BBSSectionInfo_.mainSectionId ).in( mainSectionIds ) );
		}
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据论坛ID列表查询指定论坛范围内所有公开访问的主版块和子版块信息列表" )
	public List<String> viewSectionByForumId( List<String> viewforumIds, Boolean publicStatus ) throws Exception {
		if( viewforumIds  == null || viewforumIds.isEmpty() ){
			throw new Exception( "viewforumIds is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = root.get( BBSSectionInfo_.forumId ).in( viewforumIds );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		if( publicStatus != null && publicStatus ){
			p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" ));
		}
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId);
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}	
}
