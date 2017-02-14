package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppCatagoryPermission;
import com.x.cms.core.entity.AppCatagoryPermission_;

/**
 * 应用分类权限管理表基础功能服务类
 * @author liyi
 */
public class AppCatagoryPermissionFactory extends AbstractFactory {

	public AppCatagoryPermissionFactory( Business business ) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的AppCatagoryPermission应用分类权限配置信息对象")
	public AppCatagoryPermission get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, AppCatagoryPermission.class, ExceptionWhen.none );
	}
	
	@MethodDescribe("列示全部的AppCatagoryPermission应用分类权限配置信息列表")
	public List<AppCatagoryPermission> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCatagoryPermission> cq = cb.createQuery(AppCatagoryPermission.class);
		Root<AppCatagoryPermission> root = cq.from(AppCatagoryPermission.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的AppCatagoryPermission应用分类权限配置信息列表")
	public List<AppCatagoryPermission> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCatagoryPermission> cq = cb.createQuery( AppCatagoryPermission.class );
		Root<AppCatagoryPermission> root = cq.from( AppCatagoryPermission.class );
		Predicate p = root.get(AppCatagoryPermission_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 列示指定用户可以访问的全部的对象信息ID列表
	 * @param person 用户UID
	 * @param objectType 对象类别 （ ALL | APP | CATAGORY ）
	 * @return AppCatagoryPermission_.id
	 * @throws Exception
	 */
	@MethodDescribe("列示指定用户可以访问的全部的对象信息ID列表")
	public List<String> listAppCatagoryPermissionByUser( String person, String objectType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCatagoryPermission> root = cq.from( AppCatagoryPermission.class );
		Predicate p = null;
		if( objectType == null ){
			return new ArrayList<String>();
		}else if( "ALL".equals( objectType )){
			//查询指定用户所有的配置信息
			p = cb.and( cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "USER" ),
						cb.equal(root.get( AppCatagoryPermission_.usedObjectCode ), person )
				);
		}else{
			//查询指定用户所有的指定类别的配置信息
			p = cb.and( cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "USER" ),
						cb.equal(root.get( AppCatagoryPermission_.usedObjectCode ), person ),
						cb.equal(root.get( AppCatagoryPermission_.objectType ), objectType )
				);
		}
		cq.select( root.get( AppCatagoryPermission_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定应用栏目的所有权限配置信息
	 * @param appid 应用栏目ID
	 * @return AppCatagoryPermission_.id
	 * @throws Exception
	 */
	@MethodDescribe("列示指定用户可以访问的全部的对象信息ID列表")
	public List<String> listPermissionByAppInfo( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCatagoryPermission> root = cq.from( AppCatagoryPermission.class );
		Predicate p = cb.equal(root.get( AppCatagoryPermission_.objectType ), "APPINFO");
		p = cb.and(p, cb.equal(root.get( AppCatagoryPermission_.objectId ),appId) );
		cq.select( root.get( AppCatagoryPermission_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定用户可以访问的全部的AppInfo应用信息ID列表<br/>	
	 * Select objectid from AppCatagoryPermission WHERE OBJECTTYPE = 'APPINFO' AND ( USEDOBJECTCODE = '*' OR ( USEDOBJECTTYPE = 'USER' AND USEDOBJECTCODE = 'person' ) OR ( USEDOBJECTTYPE = 'ORGAN' and USEDOBJECTCODE IN 'organList' )) 
	 * 
	 * @param person
	 * @return AppCatagoryPermission_.objectId( AppId )
	 * @throws Exception
	 */
	@MethodDescribe("列示指定用户可以访问的全部的CatagoryInfo分类信息ID列表")
	public List<String> listAppInfoByUserPermission( String person ) throws Exception {
		
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryPermission> root = cq.from( AppCatagoryPermission.class );
		
		List<String> organList = null;
		//根据person查询组织列表
		organList = new ArrayList<String>();
		
		//OBJECTTYPE = 'APP'
		Predicate p = cb.equal(root.get( AppCatagoryPermission_.objectType ), "APPINFO");
		
		//( USEDOBJECTCODE = '*' OR ( USEDOBJECTTYPE = 'USER' AND USEDOBJECTCODE = 'person' ) OR ( USEDOBJECTTYPE = 'ORGAN' and USEDOBJECTCODE IN 'organList' )
		Predicate p_permission = cb.equal( root.get(AppCatagoryPermission_.usedObjectCode ), '*' );
		p_permission = cb.or( p_permission,
					   cb.and( cb.equal( root.get( AppCatagoryPermission_.usedObjectType ), "USER" ), cb.equal( root.get(AppCatagoryPermission_.usedObjectCode ), person )), 
					   cb.and( cb.equal( root.get( AppCatagoryPermission_.usedObjectType ), "ORGAN" ), cb.equal( root.get(AppCatagoryPermission_.usedObjectCode ), organList ))
		);
		
		p = cb.and( p, p_permission );
		
		cq.select(root.get(AppCatagoryPermission_.objectId)).where(p);
		
		return em.createQuery(cq).getResultList();	
	}
	
	/**
	 * 列示指定用户可以访问的全部的CatagoryInfo分类信息ID列表<br/>	
	 * Select id from AppCatagoryPermission WHERE OBJECTTYPE = 'CATAGORY' AND OBJECTID IN ('intersection_ids') AND ( USEDOBJECTCODE = '*' OR ( USEDOBJECTTYPE = 'USER' AND USEDOBJECTCODE = 'person' ) OR ( USEDOBJECTTYPE = 'ORGAN' and USEDOBJECTCODE IN 'organList' )) 
	 * 
	 * @param person
	 * @param intersection_ids 分类ID列表，如果此值不为空，则取交集 IN
	 * @return AppCatagoryPermission_.objectId( CatagoryId )
	 * @throws Exception
	 */
	@MethodDescribe("列示指定用户可以访问的全部的CatagoryInfo分类信息ID列表")
	public List<String> listCatagoryInfoByUserPermission( EffectivePerson person, List<String> intersection_ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryPermission> root = cq.from( AppCatagoryPermission.class );
		
		List<String> organList = null;
		//根据person查询组织列表
		organList = new ArrayList<String>();
		
		//OBJECTTYPE = 'CATAGORY'
		Predicate p = cb.equal(root.get( AppCatagoryPermission_.objectType ), "CATAGORY");
		
		//如果intersection_ids不为空，取交集。一般用来传递应用下的所有分类列表
		//AND OBJECTID IN ('intersection_ids')
		if( intersection_ids != null && intersection_ids.size() > 0 ){
			p = cb.and( p, root.get( AppCatagoryPermission_.usedObjectCode ).in( intersection_ids ) );
		}
		
		//( USEDOBJECTCODE = '*' OR ( USEDOBJECTTYPE = 'USER' AND USEDOBJECTCODE = 'person' ) OR ( USEDOBJECTTYPE = 'ORGAN' and USEDOBJECTCODE IN 'organList' )
		Predicate p_permission = cb.equal( root.get(AppCatagoryPermission_.usedObjectCode ), '*' );
		p_permission = cb.or( p_permission,
					   cb.and( cb.equal( root.get( AppCatagoryPermission_.usedObjectType ), "USER" ), cb.equal( root.get(AppCatagoryPermission_.usedObjectCode ), person.getName() )), 
					   cb.and( cb.equal( root.get( AppCatagoryPermission_.usedObjectType ), "ORGAN" ), cb.equal( root.get(AppCatagoryPermission_.usedObjectCode ), organList ))
		);
		
		p = cb.and( p, p_permission );
		
		cq.select(root.get(AppCatagoryPermission_.objectId)).where(p);
		
		return em.createQuery(cq).getResultList();	
	}

	public List<String> listAllAppInfoIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryPermission> root = cq.from(AppCatagoryPermission.class);
		Predicate p = cb.equal(root.get( AppCatagoryPermission_.objectType ), "APPINFO");
		cq.distinct(true).select( root.get( AppCatagoryPermission_.objectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listAllCatagoryInfoIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryPermission> root = cq.from(AppCatagoryPermission.class);
		Predicate p = cb.equal(root.get( AppCatagoryPermission_.objectType ), "CATAGORY");
		cq.distinct(true).select( root.get( AppCatagoryPermission_.objectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAppInfoIdsByPermission( String name, List<String> departmentNames, List<String> companyNames ) throws Exception {
		if( ( name == null || name.isEmpty() )
			&& ( departmentNames == null || departmentNames.isEmpty() ) 
			&& ( companyNames == null || companyNames.isEmpty() ) ){
			throw new Exception( "name, departmentNames and companyNames can not be all null, query needs one condition." );
		}
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryPermission> root = cq.from(AppCatagoryPermission.class);
		Predicate p = cb.equal(root.get( AppCatagoryPermission_.objectType ), "APPINFO");
		Predicate p_or  = null;
		Predicate p_or_user  = null;
		Predicate p_or_dept  = null;
		Predicate p_or_comp  = null;
		if( name != null && !name.isEmpty() ){
			p_or_user = cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "USER");
			p_or_user = cb.and( p_or_user, cb.equal(root.get( AppCatagoryPermission_.usedObjectName ), name ) );
			p_or = p_or_user;
		}
		if( departmentNames != null && !departmentNames.isEmpty() ){
			p_or_dept = cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "DEPARTMENT");
			p_or_dept = cb.and( p_or_dept, root.get( AppCatagoryPermission_.usedObjectName ).in( departmentNames ) );
			if( p_or == null ){
				p_or = p_or_dept;
			}else{
				p_or = cb.or( p_or, p_or_dept );
			}
		}
		if( companyNames != null && !companyNames.isEmpty() ){
			p_or_comp = cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "COMPANY");
			p_or_comp = cb.and( p_or_comp, root.get( AppCatagoryPermission_.usedObjectName ).in( companyNames ) );
			if( p_or == null ){
				p_or = p_or_comp;
			}else{
				p_or = cb.or( p_or, p_or_comp );
			}
		}
		p = cb.and( p, p_or );
		cq.distinct(true).select( root.get( AppCatagoryPermission_.objectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listCatagoryIdsByPermission( String name, List<String> departmentNames, List<String> companyNames, List<String> catagoryIds ) throws Exception {
		if( ( name == null || name.isEmpty() )
			&& ( departmentNames == null || departmentNames.isEmpty() ) 
			&& ( companyNames == null || companyNames.isEmpty() ) ){
			throw new Exception( "name, departmentNames and companyNames can not be all null, query needs one condition." );
		}
		EntityManager em = this.entityManagerContainer().get( AppCatagoryPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryPermission> root = cq.from(AppCatagoryPermission.class);
		Predicate p = cb.equal(root.get( AppCatagoryPermission_.objectType ), "CATAGORY");
		Predicate p_or  = null;
		Predicate p_or_user  = null;
		Predicate p_or_dept  = null;
		Predicate p_or_comp  = null;
		if( catagoryIds != null && !catagoryIds.isEmpty() ){
			p = cb.and( p, root.get( AppCatagoryPermission_.objectId ).in( catagoryIds ));
		}
		if( name != null && !name.isEmpty() ){
			p_or_user = cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "USER");
			p_or_user = cb.and( p_or_user, cb.equal(root.get( AppCatagoryPermission_.usedObjectName ), name ) );
			p_or = p_or_user;
		}
		if( departmentNames != null && !departmentNames.isEmpty() ){
			p_or_dept = cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "DEPARTMENT");
			p_or_dept = cb.and( p_or_dept, root.get( AppCatagoryPermission_.usedObjectName ).in( departmentNames ) );
			if( p_or == null ){
				p_or = p_or_dept;
			}else{
				p_or = cb.or( p_or, p_or_dept );
			}
		}
		if( companyNames != null && !companyNames.isEmpty() ){
			p_or_comp = cb.equal(root.get( AppCatagoryPermission_.usedObjectType ), "COMPANY");
			p_or_comp = cb.and( p_or_comp, root.get( AppCatagoryPermission_.usedObjectName ).in( companyNames ) );
			if( p_or == null ){
				p_or = p_or_comp;
			}else{
				p_or = cb.or( p_or, p_or_comp );
			}
		}
		p = cb.and( p, p_or );
		cq.distinct(true).select( root.get( AppCatagoryPermission_.objectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}	
}