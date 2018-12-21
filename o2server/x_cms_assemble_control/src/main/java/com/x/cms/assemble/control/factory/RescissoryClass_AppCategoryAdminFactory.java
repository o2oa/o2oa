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
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryAdmin_;

/**
 * 应用分类管理员配置管理表基础功能服务类
 * 
 * @author O2LEE
 */
public class RescissoryClass_AppCategoryAdminFactory extends AbstractFactory {

	public RescissoryClass_AppCategoryAdminFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * @param id
	 * @return AppCategoryAdmin
	 * @throws Exception
	 */
	//@MethodDescribe("获取指定Id的AppCategoryAdmin应用分类管理员配置信息对象")
	public AppCategoryAdmin get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, AppCategoryAdmin.class, ExceptionWhen.none );
	}
	
	/**
	 * @return List：String
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	//@MethodDescribe("列示全部的AppCategoryAdmin应用分类管理员配置信息ID列表")
	public List<AppCategoryAdmin> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCategoryAdmin> cq = cb.createQuery(AppCategoryAdmin.class);
		Root<AppCategoryAdmin> root = cq.from(AppCategoryAdmin.class);
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 
	 * @param ids 需要查询的ID列表
	 * @return List：AppCategoryAdmin
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定Id的AppCategoryAdmin应用分类管理员配置信息列表")
	public List<AppCategoryAdmin> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppCategoryAdmin> cq = cb.createQuery( AppCategoryAdmin.class );
		Root<AppCategoryAdmin> root = cq.from( AppCategoryAdmin.class );
		Predicate p = root.get( AppCategoryAdmin_.id ).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	
	
	/**
	 * 列示指定目录相关的所有管理员配置信息ID列表
	 * @param person 用户UID
	 * @return List：String (AppCategoryAdmin_.id)
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定目录相关的所有管理员配置信息ID列表")
	public List<String> listAppCategoryIdByCategoryId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCategoryAdmin> root = cq.from( AppCategoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCategoryAdmin_.objectId ), categoryId );
		p = cb.and( p, cb.equal(root.get( AppCategoryAdmin_.objectType ), "CATEGORY" ) );
		
		cq.select( root.get( AppCategoryAdmin_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定应用栏目相关的所有管理员配置信息ID列表
	 * @param person 用户UID
	 * @return List：String (AppCategoryAdmin_.id)
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定应用栏目相关的所有管理员配置信息ID列表")
	public List<String> listAppCategoryIdByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);		
		Root<AppCategoryAdmin> root = cq.from( AppCategoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCategoryAdmin_.objectId ), appId );
		p = cb.and( p, cb.equal(root.get( AppCategoryAdmin_.objectType ), "APPINFO" ) );		
		cq.select( root.get( AppCategoryAdmin_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定应用栏目相关的所有管理员配置信息ID列表
	 * @param person 用户UID
	 * @return List：String (AppCategoryAdmin_.id)
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定应用栏目相关的所有管理员配置信息ID列表")
	public List<String> listAppCategoryIdByAppId( String appId, String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);		
		Root<AppCategoryAdmin> root = cq.from( AppCategoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCategoryAdmin_.objectId ), appId );
		p = cb.and( p, cb.equal(root.get( AppCategoryAdmin_.objectType ), "APPINFO" ) );
		p = cb.and( p, cb.equal(root.get( AppCategoryAdmin_.adminName ), personName ) );	
		cq.select( root.get( AppCategoryAdmin_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定应用栏目相关的所有管理员配置信息ID列表
	 * @param person 用户UID
	 * @return List：String (AppCategoryAdmin_.id)
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定应用栏目相关的所有管理员配置信息ID列表")
	public List<String> listAppCategoryIdByUser( String uid ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);		
		Root<AppCategoryAdmin> root = cq.from( AppCategoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCategoryAdmin_.adminUid ), uid );		
		cq.select( root.get( AppCategoryAdmin_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 列示指定用户可以管理的全部的对象信息ID列表
	 * @param person 用户UID
	 * @param objectType 对象类别 （ ALL | APP | CATEGORY ）
	 * @return List：String (AppCategoryAdmin_.objectId)
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定用户可以管理的全部的对象信息ID列表")
	public List<String> listAppCategoryIdByAdminName( String person, String objectType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCategoryAdmin> root = cq.from( AppCategoryAdmin.class );
		Predicate p = null;
		
		//如果需要查询指定用户的权限
		if( person != null && !person.isEmpty() ){
			p = cb.equal(root.get( AppCategoryAdmin_.adminUid ), person );
		}
		//如果需要查询指定类别的权限	
		if( p != null ){//说明前面有用户的查询条件
			if( !"ALL".equalsIgnoreCase( objectType )){
				//查询指定用户所有的指定类别的配置信息
				p = cb.and(p, cb.equal(root.get( AppCategoryAdmin_.objectType ), objectType ) );
			}
		}else{
			//没有用户查询条件
			if( !"ALL".equalsIgnoreCase( objectType )){
				//查询指定用户所有的指定类别的配置信息
				p = cb.equal(root.get( AppCategoryAdmin_.objectType ), objectType );
			}
		}
		cq.select( root.get( AppCategoryAdmin_.id ) );
		
		if( p != null ){
			return em.createQuery( cq.where( p ) ).getResultList();
		}else{
			return em.createQuery( cq ).getResultList();
		}
	}
	
	/**
	 * 判断用户是否是指定的应用或者分类的管理员
	 * @param person 用户UID
	 * @param objectType 对象类别 （ ALL | APP | CATEGORY ）
	 * @param objectId
	 * @return List：String (AppCategoryAdmin_.objectId)
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定用户可以管理的全部的对象信息ID列表")
	public boolean isAdmin( String person, String objectType, String objectId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		
		Root<AppCategoryAdmin> root = cq.from( AppCategoryAdmin.class );
		Predicate p = cb.equal(root.get( AppCategoryAdmin_.adminUid ), person );
		
		if( objectType == null || person == null || objectId == null ){
			return false;
		}else{
			//查询指定用户所有的指定类别的配置信息
			p = cb.and( p, 
					cb.equal(root.get( AppCategoryAdmin_.objectType ), objectType ),
					cb.equal(root.get( AppCategoryAdmin_.objectId ), objectId ),
					cb.equal(root.get( AppCategoryAdmin_.adminLevel ), "ADMIN" )
			);			
		}
		cq.select( root.get( AppCategoryAdmin_.id ) );
		List<String> ids = em.createQuery( cq.where(p) ).getResultList();
	
		if( ids == null || ids.size() == 0 ){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 根据人员姓名获取用户可以管理的分类目ID列表
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listCategoryInfoIdsByAdminName( String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppCategoryAdmin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCategoryAdmin> root = cq.from(AppCategoryAdmin.class);
		Predicate p = cb.isNotNull(  root.get(AppCategoryAdmin_.objectId ) );
		p = cb.and( p, cb.equal( root.get(AppCategoryAdmin_.adminName ), personName ) );
		p = cb.and( p, cb.equal( root.get(AppCategoryAdmin_.objectType ), "CATEGORY" ) );
		cq.select(root.get(AppCategoryAdmin_.objectId ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAppCategoryIdByCondition( String objectType, String objectId, String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppCategoryAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppCategoryAdmin> root = cq.from(AppCategoryAdmin.class);
		Predicate p = cb.isNotNull(  root.get(AppCategoryAdmin_.objectId ) );
		
		if( objectType != null && !objectType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(AppCategoryAdmin_.objectType ), objectType ) );
		}
		if( personName != null && !personName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(AppCategoryAdmin_.adminName ), personName ) );
		}
		if( objectId != null && !objectId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(AppCategoryAdmin_.objectId ), objectId ) );
		}
		cq.select(root.get(AppCategoryAdmin_.objectId ));
		return em.createQuery(cq.where(p)).getResultList();
	}

}