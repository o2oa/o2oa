package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppCatagoryAdmin;
import com.x.cms.core.entity.AppCatagoryAdmin_;

/**
 * 应用分类管理员配置管理表基础功能服务类
 * @author liyi
 */
public class AppCatagoryAdminFactory extends AbstractFactory {

	public AppCatagoryAdminFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * @param id
	 * @return AppCatagoryAdmin
	 * @throws Exception
	 */
	@MethodDescribe("获取指定Id的AppCatagoryAdmin应用分类管理员配置信息对象")
	public AppCatagoryAdmin get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, AppCatagoryAdmin.class, ExceptionWhen.none );
	}
	
	/**
	 * @return List：String
	 * @throws Exception
	 */
	@MethodDescribe("列示全部的AppCatagoryAdmin应用分类管理员配置信息ID列表")
	public List<AppCatagoryAdmin> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCatagoryAdmin> cq = cb.createQuery(AppCatagoryAdmin.class);
		Root<AppCatagoryAdmin> root = cq.from(AppCatagoryAdmin.class);
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 
	 * @param ids 需要查询的ID列表
	 * @return List：AppCatagoryAdmin
	 * @throws Exception
	 */
	@MethodDescribe("列示指定Id的AppCatagoryAdmin应用分类管理员配置信息列表")
	public List<AppCatagoryAdmin> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCatagoryAdmin> cq = cb.createQuery( AppCatagoryAdmin.class );
		Root<AppCatagoryAdmin> root = cq.from( AppCatagoryAdmin.class );
		Predicate p = root.get( AppCatagoryAdmin_.id ).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	
	
	/**
	 * 列示指定目录相关的所有管理员配置信息ID列表
	 * @param person 用户UID
	 * @return List：String (AppCatagoryAdmin_.id)
	 * @throws Exception
	 */
	@MethodDescribe("列示指定目录相关的所有管理员配置信息ID列表")
	public List<String> listAppCatagoryIdByCatagoryId( String catagoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCatagoryAdmin> root = cq.from( AppCatagoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCatagoryAdmin_.objectId ), catagoryId );
		p = cb.and( p, cb.equal(root.get( AppCatagoryAdmin_.objectType ), "CATAGORY" ) );
		
		cq.select( root.get( AppCatagoryAdmin_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定应用栏目相关的所有管理员配置信息ID列表
	 * @param person 用户UID
	 * @return List：String (AppCatagoryAdmin_.id)
	 * @throws Exception
	 */
	@MethodDescribe("列示指定应用栏目相关的所有管理员配置信息ID列表")
	public List<String> listAppCatagoryIdByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);		
		Root<AppCatagoryAdmin> root = cq.from( AppCatagoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCatagoryAdmin_.objectId ), appId );
		p = cb.and( p, cb.equal(root.get( AppCatagoryAdmin_.objectType ), "APPINFO" ) );		
		cq.select( root.get( AppCatagoryAdmin_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定应用栏目相关的所有管理员配置信息ID列表
	 * @param person 用户UID
	 * @return List：String (AppCatagoryAdmin_.id)
	 * @throws Exception
	 */
	@MethodDescribe("列示指定应用栏目相关的所有管理员配置信息ID列表")
	public List<String> listAppCatagoryIdByUser( String uid ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);		
		Root<AppCatagoryAdmin> root = cq.from( AppCatagoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCatagoryAdmin_.adminUid ), uid );		
		cq.select( root.get( AppCatagoryAdmin_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定用户可以管理的全部的对象信息ID列表
	 * @param person 用户UID
	 * @param objectType 对象类别 （ ALL | APP | CATAGORY ）
	 * @return List：String (AppCatagoryAdmin_.objectId)
	 * @throws Exception
	 */
	@MethodDescribe("列示指定用户可以管理的全部的对象信息ID列表")
	public List<String> listAppCatagoryObjectIdByUser( String person, String objectType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCatagoryAdmin> root = cq.from( AppCatagoryAdmin.class );
		Predicate p = null;
		
		//如果需要查询指定用户的权限
		if(person != null && !person.isEmpty()){
			p = cb.equal(root.get( AppCatagoryAdmin_.adminUid ), person );
		}
		//如果需要查询指定类别的权限	
		if( p != null ){//说明前面有用户的查询条件
			if( !"ALL".equalsIgnoreCase( objectType )){
				//查询指定用户所有的指定类别的配置信息
				p = cb.and(p, cb.equal(root.get( AppCatagoryAdmin_.objectType ), objectType ) );
			}
		}else{
			//没有用户查询条件
			if( !"ALL".equalsIgnoreCase( objectType )){
				//查询指定用户所有的指定类别的配置信息
				p = cb.equal(root.get( AppCatagoryAdmin_.objectType ), objectType );
			}
		}
		if( p != null ){
			return em.createQuery( cq.where( p ) ).getResultList();
		}else{
			return em.createQuery( cq ).getResultList();
		}
	}
	
	/**
	 * 判断用户是否是指定的应用或者分类的管理员
	 * @param person 用户UID
	 * @param objectType 对象类别 （ ALL | APP | CATAGORY ）
	 * @param objectId
	 * @return List：String (AppCatagoryAdmin_.objectId)
	 * @throws Exception
	 */
	@MethodDescribe("列示指定用户可以管理的全部的对象信息ID列表")
	public boolean isAdmin( String person, String objectType, String objectId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCatagoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCatagoryAdmin> root = cq.from( AppCatagoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCatagoryAdmin_.adminUid ), person );
		
		if( objectType == null || person == null || objectId == null ){
			return false;
		}else{
			//查询指定用户所有的指定类别的配置信息
			p = cb.and( p, 
					cb.equal(root.get( AppCatagoryAdmin_.objectType ), objectType ),
					cb.equal(root.get( AppCatagoryAdmin_.objectId ), objectId ),
					cb.equal(root.get( AppCatagoryAdmin_.adminLevel ), "ADMIN" )
			);			
		}
		cq.select( root.get( AppCatagoryAdmin_.id ) );
		List<String> ids = em.createQuery( cq.where(p) ).getResultList();
	
		if( ids == null || ids.size() == 0 ){
			return false;
		}else{
			return true;
		}
	}
	
	public List<String> listAppInfoIdsByAdminName( String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppCatagoryAdmin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryAdmin> root = cq.from(AppCatagoryAdmin.class);
		Predicate p = cb.equal( root.get(AppCatagoryAdmin_.adminName ), personName );
		p = cb.and( p, cb.equal( root.get(AppCatagoryAdmin_.objectType ), "APPINFO" ) );
		cq.select(root.get(AppCatagoryAdmin_.objectId ));
		return em.createQuery(cq).getResultList();
	}
	
	public List<String> listCatagoryInfoIdsByAdminName( String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppCatagoryAdmin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCatagoryAdmin> root = cq.from(AppCatagoryAdmin.class);
		Predicate p = cb.equal( root.get(AppCatagoryAdmin_.adminName ), personName );
		p = cb.and( p, cb.equal( root.get(AppCatagoryAdmin_.objectType ), "CATAGORY" ) );
		cq.select(root.get(AppCatagoryAdmin_.objectId ));
		return em.createQuery(cq).getResultList();
	}
}