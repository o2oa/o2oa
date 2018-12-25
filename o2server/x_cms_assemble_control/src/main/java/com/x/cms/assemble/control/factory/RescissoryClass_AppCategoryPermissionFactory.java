package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppCategoryPermission_;

/**
 * 应用分类权限管理表基础功能服务类
 * 
 * @author O2LEE
 */
public class RescissoryClass_AppCategoryPermissionFactory extends AbstractFactory {

	public RescissoryClass_AppCategoryPermissionFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AppCategoryPermission应用分类权限配置信息对象")
	public AppCategoryPermission get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, AppCategoryPermission.class, ExceptionWhen.none );
	}
	
	@SuppressWarnings("unused")
	//@MethodDescribe("列示全部的AppCategoryPermission应用分类权限配置信息列表")
	public List<AppCategoryPermission> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCategoryPermission> cq = cb.createQuery(AppCategoryPermission.class);
		Root<AppCategoryPermission> root = cq.from(AppCategoryPermission.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AppCategoryPermission应用分类权限配置信息列表")
	public List<AppCategoryPermission> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCategoryPermission> cq = cb.createQuery( AppCategoryPermission.class );
		Root<AppCategoryPermission> root = cq.from( AppCategoryPermission.class );
		Predicate p = root.get(AppCategoryPermission_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定应用栏目的所有权限配置信息
	 * @param appId
	 * @param permission
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定应用栏目的所有权限配置信息")
	public List<String> listPermissionByAppInfo( String appId, String permission ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCategoryPermission> root = cq.from( AppCategoryPermission.class );
		Predicate p = cb.equal(root.get( AppCategoryPermission_.objectType ), "APPINFO");
		p = cb.and(p, cb.equal(root.get( AppCategoryPermission_.objectId ),appId) );
		if( permission != null && !permission.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.permission ),permission ) );
		}
		cq.select( root.get( AppCategoryPermission_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定分类的所有权限配置信息
	 * @param categoryId
	 * @param permission
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定分类的所有权限配置信息")
	public List<String> listPermissionByCataogry( String categoryId, String permission ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCategoryPermission> root = cq.from( AppCategoryPermission.class );
		Predicate p = cb.equal(root.get( AppCategoryPermission_.objectType ), "CATEGORY");
		p = cb.and(p, cb.equal(root.get( AppCategoryPermission_.objectId ),categoryId) );
		if( permission != null && !permission.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.permission ),permission ) );
		}
		cq.select( root.get( AppCategoryPermission_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据对象类别以及权限类别查询涉及权限的所有栏目ID列表
	 * 
	 * @param type
	 * @param permission
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("根据对象类别以及权限类别查询涉及权限的所有栏目ID列表")
	public List<String> listAllAppInfoIds( String type, String permission ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<AppCategoryPermission> root = cq.from( AppCategoryPermission.class );
		Predicate p = cb.isNotNull( root.get( AppCategoryPermission_.id ) );
		if( type != null && !type.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.objectType ), type ) );
		}
		if( permission != null && !permission.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.permission ), permission ) );
		}
		cq.distinct(true).select( root.get( AppCategoryPermission_.appId ) );
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据权限类别查询涉及权限的所有分类ID列表
	 * 
	 * @param permission
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("根据权限类别查询涉及权限的所有分类ID列表")
	public List<String> listAllCategoryInfoIds( String permission ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCategoryPermission> root = cq.from(AppCategoryPermission.class);
		Predicate p = cb.equal(root.get( AppCategoryPermission_.objectType ), "CATEGORY");
		if( permission != null && !permission.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.permission ),permission ) );
		}
		cq.distinct(true).select( root.get( AppCategoryPermission_.objectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据权限类别查询涉及权限的所有分类ID列表
	 * 
	 * @param permission
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("根据权限类别查询涉及权限的所有分类ID列表")
	public List<String> listAllCategoryInfoIds( String appId, String permission ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCategoryPermission> root = cq.from(AppCategoryPermission.class);
		Predicate p = cb.equal(root.get( AppCategoryPermission_.objectType ), "CATEGORY");
		if( appId != null && !appId.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.appId ),appId ) );
		}
		if( permission != null && !permission.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.permission ),permission ) );
		}
		cq.distinct(true).select( root.get( AppCategoryPermission_.objectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据用户姓名以及用户所在的顶层组织和组织列表以及权限类别查询该用户可以访问到的所有应用栏目ID列表
	 * @param name
	 * @param unitNames
	 * @param topUnitNames
	 * @param groupNames 
	 * @param permission
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("根据用户姓名以及用户所在的顶层组织和组织列表以及权限类别查询该用户可以访问到的所有应用栏目ID列表")
	public List<String> listAppInfoIdsByPermission( String name, List<String> unitNames, List<String> groupNames, String appId, String permission ) throws Exception {
		if( ( name == null || name.isEmpty() )
			&& ( unitNames == null || unitNames.isEmpty() ) 
		){
			throw new Exception( "name and unitNames can not be all null, query needs one condition." );
		}
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCategoryPermission> root = cq.from(AppCategoryPermission.class);
		Predicate p = cb.equal(root.get( AppCategoryPermission_.objectType ), "APPINFO");
		Predicate p_or  = null;
		Predicate p_or_user  = null;
		Predicate p_or_dept  = null;
		Predicate p_or_grop  = null;
		if( appId != null && !appId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( AppCategoryPermission_.appId ), appId) );
		}
		if( permission != null && !permission.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( AppCategoryPermission_.permission ), permission) );
		}
		if( name != null && !name.isEmpty() ){
			p_or_user = cb.equal(root.get( AppCategoryPermission_.usedObjectType ), "USER");
			p_or_user = cb.and( p_or_user, cb.equal(root.get( AppCategoryPermission_.usedObjectName ), name ) );
			p_or = p_or_user;
		}
		if( unitNames != null && !unitNames.isEmpty() ){
			p_or_dept = cb.equal(root.get( AppCategoryPermission_.usedObjectType ), "UNIT");
			p_or_dept = cb.and( p_or_dept, root.get( AppCategoryPermission_.usedObjectName ).in( unitNames ) );
			if( p_or == null ){
				p_or = p_or_dept;
			}else{
				p_or = cb.or( p_or, p_or_dept );
			}
		}
		if( groupNames != null && !groupNames.isEmpty() ){
			p_or_grop = cb.equal(root.get( AppCategoryPermission_.usedObjectType ), "GROUP");
			p_or_grop = cb.and( p_or_grop, root.get( AppCategoryPermission_.usedObjectName ).in( groupNames ) );
			if( p_or == null ){
				p_or = p_or_grop;
			}else{
				p_or = cb.or( p_or, p_or_grop );
			}
		}
		
		p = cb.and( p, p_or );
		cq.distinct(true).select( root.get( AppCategoryPermission_.objectId ) );
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据用户姓名以及用户所在的顶层组织和组织列表以及权限类别查询该用户可以访问到的所有分类ID列表
	 * @param name
	 * @param unitNames
	 * @param topUnitNames
	 * @param groupNames 
	 * @param appId
	 * @param permission
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("根据用户姓名以及用户所在的顶层组织和组织列表以及权限类别查询该用户可以访问到的所有分类ID列表")
	public List<String> listCategoryIdsByPermission( String name, List<String> unitNames, List<String> groupNames, String appId, String permission ) throws Exception {
		if( ( name == null || name.isEmpty() )
			&& ( unitNames == null || unitNames.isEmpty() ) 
		){
			throw new Exception( "name, unitNames can not be all null, query needs one condition." );
		}
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCategoryPermission> root = cq.from(AppCategoryPermission.class);
		Predicate p = cb.equal(root.get( AppCategoryPermission_.objectType ), "CATEGORY");
		Predicate p_or  = null;
		Predicate p_or_user  = null;
		Predicate p_or_dept  = null;
		Predicate p_or_grop  = null;
		if( appId != null && !appId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( AppCategoryPermission_.appId ), appId) );
		}
		if( permission != null && !permission.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( AppCategoryPermission_.permission ), permission) );
		}
		if( name != null && !name.isEmpty() ){
			p_or_user = cb.equal(root.get( AppCategoryPermission_.usedObjectType ), "USER");
			p_or_user = cb.and( p_or_user, cb.equal(root.get( AppCategoryPermission_.usedObjectName ), name ) );
			p_or = p_or_user;
		}
		if( unitNames != null && !unitNames.isEmpty() ){
			p_or_dept = cb.equal(root.get( AppCategoryPermission_.usedObjectType ), "UNIT");
			p_or_dept = cb.and( p_or_dept, root.get( AppCategoryPermission_.usedObjectName ).in( unitNames ) );
			if( p_or == null ){
				p_or = p_or_dept;
			}else{
				p_or = cb.or( p_or, p_or_dept );
			}
		}
		if( groupNames != null && !groupNames.isEmpty() ){
			p_or_grop = cb.equal(root.get( AppCategoryPermission_.usedObjectType ), "GROUP");
			p_or_grop = cb.and( p_or_grop, root.get( AppCategoryPermission_.usedObjectName ).in( groupNames ) );
			if( p_or == null ){
				p_or = p_or_grop;
			}else{
				p_or = cb.or( p_or, p_or_grop );
			}
		}
		
		p = cb.and( p, p_or );
		cq.distinct(true).select( root.get( AppCategoryPermission_.objectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAppCategoryIdByCondition(String objectType, String objectId, String personName, String permission) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<AppCategoryPermission> root = cq.from( AppCategoryPermission.class );
		Predicate p = cb.isNotNull( root.get( AppCategoryPermission_.id ) );
		if( objectType != null && !objectType.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.objectType ),objectType ) );
		}
		if( objectId != null && !objectId.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.objectId ),objectId ) );
		}
		if( personName != null && !personName.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.usedObjectName ),personName ) );
		}
		if( permission != null && !permission.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.permission ),permission ) );
		}
		cq.select( root.get( AppCategoryPermission_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listAppInfoIdsByPermission( List<String> queryObjectNames, String queryObjectType, String objectType, String permission) throws Exception {
		if( queryObjectNames == null || queryObjectNames.isEmpty() ){
			throw new Exception( "queryObjectNames is null." );
		}
		EntityManager em = this.entityManagerContainer().get( AppCategoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<AppCategoryPermission> root = cq.from( AppCategoryPermission.class );
		Predicate p = root.get( AppCategoryPermission_.usedObjectName ).in( queryObjectNames );
		if( queryObjectType != null && !queryObjectType.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.usedObjectType ),queryObjectType ) );
		}
		if( objectType != null && !objectType.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.objectType ),objectType ) );
		}
		if( permission != null && !permission.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( AppCategoryPermission_.permission ),permission ) );
		}
		cq.select( root.get( AppCategoryPermission_.objectId ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}	
}