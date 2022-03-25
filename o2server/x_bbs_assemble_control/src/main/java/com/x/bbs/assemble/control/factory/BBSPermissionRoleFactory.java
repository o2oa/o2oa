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
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSPermissionRole;
import com.x.bbs.entity.BBSPermissionRole_;

/**
 * 类   名：BBSPermissionRoleFactory<br/>
 * 实体类：BBSPermissionRole<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-08-10 17:17:26
**/
public class BBSPermissionRoleFactory extends AbstractFactory {

	public BBSPermissionRoleFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSPermissionRole实体信息对象" )
	public BBSPermissionRole get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSPermissionRole.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的BBSPermissionRole实体信息列表" )
	public List<BBSPermissionRole> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSPermissionRole>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSPermissionRole.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionRole> cq = cb.createQuery(BBSPermissionRole.class);
		Root<BBSPermissionRole> root = cq.from(BBSPermissionRole.class);
		Predicate p = root.get(BBSPermissionRole_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示全部的BBSPermissionRole实体信息列表" )
	public List<BBSPermissionRole> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSPermissionRole.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionRole> cq = cb.createQuery(BBSPermissionRole.class);
		return em.createQuery( cq ).setMaxResults( 1000 ).getResultList();
	}

	//@MethodDescribe( "根据角色编码列示该角色包含的所有BBSPermissionRole实体信息ID列表" )
	public List<String> listPermissionByRoleCode(String roleCode) throws Exception {
		if( roleCode == null || roleCode.isEmpty() ){
			throw new Exception("roleCode is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = cb.equal( root.get( BBSPermissionRole_.roleCode ), roleCode );
		cq.select( root.get( BBSPermissionRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "根据角色编码和权限编码判断角色和权限的绑定信息对象是否存在" )
	public boolean exsistPermissionRole( String roleCode, String permissionCode ) throws Exception {
		if( roleCode == null || roleCode.isEmpty() ){
			throw new Exception("roleCode is null!");
		}
		if( permissionCode == null || permissionCode.isEmpty() ){
			throw new Exception("permissionCode is null!");
		}
		List<BBSPermissionRole> roleInfoList = null;
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionRole> cq = cb.createQuery( BBSPermissionRole.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = cb.equal( root.get( BBSPermissionRole_.roleCode ), roleCode );
		p = cb.and( p, cb.equal( root.get( BBSPermissionRole_.permissionCode ), permissionCode ));
		roleInfoList = em.createQuery( cq.where(p) ).getResultList();
		if( ListTools.isNotEmpty(roleInfoList) ){
			return true;
		}else{
			return false;
		}
	}
	
	//@MethodDescribe( "根据角色编码和权限编码查询角色和权限的绑定信息对象" )
	public BBSPermissionRole getByRoleAndPermission(String roleCode, String permissionCode) throws Exception {
		if( roleCode == null || roleCode.isEmpty() ){
			throw new Exception("roleCode is null!");
		}
		if( permissionCode == null || permissionCode.isEmpty() ){
			throw new Exception("permissionCode is null!");
		}
		List<BBSPermissionRole> roleInfoList = null;
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionRole> cq = cb.createQuery( BBSPermissionRole.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = cb.equal( root.get( BBSPermissionRole_.roleCode ), roleCode );
		p = cb.and( p, cb.equal( root.get( BBSPermissionRole_.permissionCode ), permissionCode ));
		roleInfoList = em.createQuery( cq.where(p) ).getResultList();
		if( ListTools.isNotEmpty(roleInfoList) ){
			return roleInfoList.get( 0 );
		}else{
			return null;
		}
	}
		
	//@MethodDescribe( "根据角色ID列表列示权限ID信息列表" )
	public List<String> listPermissionCodesByRoleCodes(List<String> roleCodes) throws Exception {
		if( roleCodes == null || roleCodes.isEmpty() ){
			throw new Exception("roleCodes is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = root.get( BBSPermissionRole_.roleCode ).in( roleCodes );
		cq.select( root.get( BBSPermissionRole_.permissionCode ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "根据权限编码列示BBSPermissionRole实体信息列表" )
	public List<BBSPermissionRole> listByPermissionCode(String permissionCode) throws Exception {
		if( permissionCode == null || permissionCode.isEmpty() ){
			throw new Exception("permissionCode is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionRole> cq = cb.createQuery( BBSPermissionRole.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = cb.equal( root.get( BBSPermissionRole_.permissionCode ), permissionCode );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	//@MethodDescribe( "根据指定的权限编码列表查询权限信息对象列表" )
	public List<BBSPermissionRole> listPermissionByCodes( List<String> permissionCodes ) throws Exception {
		if( permissionCodes == null || permissionCodes.isEmpty() ){
			throw new Exception( "permissionCodes is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSPermissionRole> cq = cb.createQuery( BBSPermissionRole.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = root.get( BBSPermissionRole_.permissionCode ).in( permissionCodes );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listByForumId(String forumId) throws Exception {
		if( StringUtils.isEmpty( forumId) ){
			throw new Exception( "forumId is empty!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = cb.equal( root.get( BBSPermissionRole_.forumId ), forumId );
		cq.select( root.get( BBSPermissionRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据版块信息ID查询所有的权限角色关联信息
	 * @param sectionId
	 * @param  queryMainSectionId 是否查询主版块ID
	 * @return
	 * @throws Exception 
	 */
	public List<String> listBySectionId( String sectionId, Boolean queryMainSectionId ) throws Exception {
		if( StringUtils.isEmpty( sectionId) ){
			throw new Exception( "sectionId is empty!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSPermissionRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSPermissionRole> root = cq.from( BBSPermissionRole.class );
		Predicate p = cb.equal( root.get( BBSPermissionRole_.sectionId ), sectionId );
		if( queryMainSectionId ) {
			p = cb.or( p, cb.equal( root.get( BBSPermissionRole_.mainSectionId ), sectionId ) );
		}
		cq.select( root.get( BBSPermissionRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}	
}
