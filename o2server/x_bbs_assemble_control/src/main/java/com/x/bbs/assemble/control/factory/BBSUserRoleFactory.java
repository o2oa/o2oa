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
import com.x.bbs.entity.BBSUserRole;
import com.x.bbs.entity.BBSUserRole_;

/**
 * 类   名：BBSUserRoleFactory<br/>
 * 实体类：BBSUserRole<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-08-10 17:17:26
**/
public class BBSUserRoleFactory extends AbstractFactory {

	public BBSUserRoleFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSUserRole实体信息对象" )
	public BBSUserRole get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSUserRole.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的BBSUserRole实体信息列表" )
	public List<BBSUserRole> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSUserRole>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSUserRole.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserRole> cq = cb.createQuery(BBSUserRole.class);
		Root<BBSUserRole> root = cq.from(BBSUserRole.class);
		Predicate p = root.get(BBSUserRole_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示全部的BBSUserRole实体信息列表" )
	public List<BBSUserRole> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSUserRole.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserRole> cq = cb.createQuery(BBSUserRole.class);
		return em.createQuery( cq ).setMaxResults( 5000 ).getResultList();
	}

	//@MethodDescribe( "根据绑定对象唯一标识以及绑定对象类别查询所有绑定的角色ID列表" )
	public List<String> listRoleIdsByObjectUniqueId( String uniqueId, String objectType ) throws Exception {
		if( uniqueId == null || uniqueId.isEmpty() ){
			throw new Exception("uniqueId is null!");
		}
		if( objectType == null || objectType.isEmpty() ){
			throw new Exception("objectType is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.uniqueId ), uniqueId );
		p = cb.and( p, cb.equal( root.get( BBSUserRole_.objectType ), objectType ));
		cq.select( root.get( BBSUserRole_.roleId ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	//@MethodDescribe( "根据绑定对象唯一标识以及绑定对象类别查询所有绑定的角色编码列表" )
	public List<String> listRoleCodeByObjectUniqueId( String uniqueId, String objectType ) throws Exception {
		if( uniqueId == null || uniqueId.isEmpty() ){
			throw new Exception("uniqueId is null!");
		}
		if( objectType == null || objectType.isEmpty() ){
			throw new Exception("objectType is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.uniqueId ), uniqueId );
		p = cb.and( p, cb.equal( root.get( BBSUserRole_.objectType ), objectType ));
		cq.select( root.get( BBSUserRole_.roleCode ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "根据角色编码查询所有的人员角色绑定信息对象ID列表" )
	public List<String> listIdsByRoleCode( String roleCode ) throws Exception {
		if( roleCode == null || roleCode.isEmpty() ){
			throw new Exception("roleCode is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.roleCode ), roleCode );
		cq.select( root.get( BBSUserRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "根据指定的绑定对象唯一标识列表查询在该对象上绑定的所有角色ID列表" )
	public List<String> listRoleIdsByObjectUnique(List<String> objectUniqueIds) throws Exception {
		if( objectUniqueIds == null || objectUniqueIds.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = root.get( BBSUserRole_.uniqueId ).in( objectUniqueIds );
		cq.select( root.get( BBSUserRole_.roleId ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	//@MethodDescribe( "根据指定的绑定对象唯一标识列表查询在该对象上绑定的所有角色编码列表" )
	public List<String> listRoleCodesByObjectUnique( List<String> objectUniqueIds ) throws Exception {
		if( objectUniqueIds == null || objectUniqueIds.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = root.get( BBSUserRole_.uniqueId ).in( objectUniqueIds );
		cq.select( root.get( BBSUserRole_.roleCode ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listByForumId(String forumId) throws Exception {
		if( StringUtils.isEmpty( forumId ) ){
			throw new Exception("forumId is empty!!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.forumId ), forumId );
		cq.select( root.get( BBSUserRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据版块信息ID查询所有的用户角色关联信息
	 * @param sectionId
	 * @param queryMainSectionId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listBySectionId(String sectionId, Boolean queryMainSectionId) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception("sectionId is empty!!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.sectionId ), sectionId );
		if( queryMainSectionId ) {
			p = cb.or( p, cb.equal( root.get( BBSUserRole_.mainSectionId ), sectionId ) );
		}
		cq.select( root.get( BBSUserRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
}
