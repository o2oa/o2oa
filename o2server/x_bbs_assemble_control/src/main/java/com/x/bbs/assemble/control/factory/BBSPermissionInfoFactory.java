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
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSPermissionInfo_;

/**
 * 类   名：BBSPermissionInfoFactory<br/>
 * 实体类：BBSPermissionInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-08-10 17:17:26
**/
public class BBSPermissionInfoFactory extends AbstractFactory {

	public BBSPermissionInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSPermissionInfo实体信息对象" )
	public BBSPermissionInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSPermissionInfo.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的BBSPermissionInfo实体信息列表" )
	public List<BBSPermissionInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSPermissionInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionInfo> cq = cb.createQuery(BBSPermissionInfo.class);
		Root<BBSPermissionInfo> root = cq.from(BBSPermissionInfo.class);
		Predicate p = root.get(BBSPermissionInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示全部的BBSPermissionInfo实体信息列表" )
	public List<BBSPermissionInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionInfo> cq = cb.createQuery(BBSPermissionInfo.class);
		return em.createQuery( cq ).setMaxResults( 1000 ).getResultList();
	}

	//@MethodDescribe( "查询指定CODE的权限信息对象" )
	public BBSPermissionInfo getPermissionByCode( String permissionCode ) throws Exception {
		if( permissionCode == null || permissionCode.isEmpty() ){
			throw new Exception("permissionCode is null!");
		}
		List<BBSPermissionInfo> permissionInfoList = null;
		EntityManager em = this.entityManagerContainer().get(BBSPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionInfo> cq = cb.createQuery(BBSPermissionInfo.class);
		Root<BBSPermissionInfo> root = cq.from(BBSPermissionInfo.class);
		Predicate p = cb.equal( root.get(BBSPermissionInfo_.permissionCode), permissionCode );
		permissionInfoList = em.createQuery(cq.where(p)).getResultList();
		if( ListTools.isNotEmpty(permissionInfoList) ){
			return permissionInfoList.get(0);
		}else{
			return null;
		}
	}

	//@MethodDescribe( "根据论坛ID查询指定论坛所有相关的权限信息列表" )
	public List<String> listPermissionByForumId(String forumId) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception("forumId is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSPermissionInfo> root = cq.from( BBSPermissionInfo.class );
		Predicate p = cb.equal( root.get( BBSPermissionInfo_.forumId ), forumId );
		p = cb.and( p, cb.equal( root.get( BBSPermissionInfo_.permissionType ), "论坛权限" ));
		cq.select( root.get( BBSPermissionInfo_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 根据版块ID查询指定版块所有相关的权限信息列表
	 * @param sectionId
	 * @param queryMainSection 是否查询主版块ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listPermissionBySectionId( String sectionId, Boolean queryMainSection ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception("sectionId is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSPermissionInfo> root = cq.from( BBSPermissionInfo.class );
		Predicate p = cb.equal( root.get( BBSPermissionInfo_.sectionId ), sectionId );
		if( queryMainSection ) {
			p = cb.or( p, cb.equal( root.get( BBSPermissionInfo_.mainSectionId ), sectionId ) );
		}
		cq.select( root.get( BBSPermissionInfo_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "根据权限编码列表获取权限信息列表" )
	public List<BBSPermissionInfo> listByPermissionCodes( List<String> permissionCodes ) throws Exception {
		if( permissionCodes == null || permissionCodes.size() == 0 ){
			return new ArrayList<BBSPermissionInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionInfo> cq = cb.createQuery(BBSPermissionInfo.class);
		Root<BBSPermissionInfo> root = cq.from(BBSPermissionInfo.class);
		Predicate p = root.get(BBSPermissionInfo_.permissionCode).in( permissionCodes );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据主版块ID获取所有的权限信息列表" )
	public List<BBSPermissionInfo> listPermissionByMainSectionId( String mainSectionId ) throws Exception {
		if( mainSectionId == null || mainSectionId.isEmpty() ){
			throw new Exception("mainSectionId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionInfo> cq = cb.createQuery(BBSPermissionInfo.class);
		Root<BBSPermissionInfo> root = cq.from(BBSPermissionInfo.class);
		Predicate p = cb.equal( root.get(BBSPermissionInfo_.mainSectionId ), mainSectionId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listPermissionIdsByMainSectionId( String mainSectionId ) throws Exception {
		if( mainSectionId == null || mainSectionId.isEmpty() ){
			throw new Exception("mainSectionId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSPermissionInfo> root = cq.from(BBSPermissionInfo.class);
		Predicate p = cb.equal( root.get(BBSPermissionInfo_.mainSectionId ), mainSectionId );
		cq.select( root.get( BBSPermissionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
