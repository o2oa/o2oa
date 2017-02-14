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
import com.x.cms.core.entity.CatagoryInfo;
import com.x.cms.core.entity.CatagoryInfo_;

/**
 * 分类信息基础功能服务类
 * @author liyi
 */
public class CatagoryInfoFactory extends AbstractFactory {

	public CatagoryInfoFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的CatagoryInfo分类信息对象")
	public CatagoryInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, CatagoryInfo.class );
	}
	
	@MethodDescribe("列示全部的CatagoryInfo分类信息列表")
	public List<CatagoryInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( CatagoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CatagoryInfo> cq = cb.createQuery( CatagoryInfo.class );
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的CatagoryInfo分类信息列表")
	public List<CatagoryInfo> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CatagoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CatagoryInfo> cq = cb.createQuery( CatagoryInfo.class );
		Root<CatagoryInfo> root = cq.from( CatagoryInfo.class );
		Predicate p = root.get( CatagoryInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe("根据应用ID列示所有的CatagoryInfo分类信息列表")
	public List<String> listByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CatagoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CatagoryInfo> root = cq.from( CatagoryInfo.class );
		Predicate p = cb.equal(root.get( CatagoryInfo_.appId ), appId );
		cq.select(root.get( CatagoryInfo_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
	}
	
	@MethodDescribe("根据应用ID列示所有的CatagoryInfo分类信息数量")
	public Long countByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CatagoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CatagoryInfo> root = cq.from(CatagoryInfo.class);
		Predicate p = cb.equal( root.get(CatagoryInfo_.appId), appId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	@MethodDescribe("根据分类ID列示所有下级CatagoryInfo分类信息列表")
	public List<String> listByParentId( String catagoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CatagoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CatagoryInfo> root = cq.from( CatagoryInfo.class );
		Predicate p = cb.equal(root.get( CatagoryInfo_.parentId ), catagoryId );
		cq.select(root.get( CatagoryInfo_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
	}

	@MethodDescribe("对分类信息进行模糊查询，并且返回信息列表.")
	public List<String> listLike(String keyStr) throws Exception {
		String str = keyStr.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get( CatagoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CatagoryInfo> root = cq.from( CatagoryInfo.class);
		Predicate p = cb.like(root.get( CatagoryInfo_.catagoryName ), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get( CatagoryInfo_.catagoryAlias ), str + "%", '\\'));
		cq.select(root.get( CatagoryInfo_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}
	
	public List<String> listNoPermissionCatagoryInfoIds( List<String> permissionedCatagoryInfoIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CatagoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CatagoryInfo> root = cq.from(CatagoryInfo.class);
		cq.select(root.get(CatagoryInfo_.id));
		if( permissionedCatagoryInfoIds != null && !permissionedCatagoryInfoIds.isEmpty() ){
			Predicate p = cb.not( root.get(CatagoryInfo_.id).in( permissionedCatagoryInfoIds ));
			return em.createQuery(cq.where( p )).getResultList();
		}
		return em.createQuery(cq).getResultList();
	}

	public List<String> listNoPermissionCatagoryInfoIds( List<String> permissionedCatagoryInfoIds, String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CatagoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CatagoryInfo> root = cq.from(CatagoryInfo.class);
		cq.select(root.get(CatagoryInfo_.id));
		Predicate p = cb.equal( root.get( CatagoryInfo_.appId ), appId );
		if( permissionedCatagoryInfoIds != null && !permissionedCatagoryInfoIds.isEmpty() ){
			p = cb.and( p, cb.not( root.get( CatagoryInfo_.id ).in( permissionedCatagoryInfoIds )) );
		}
		return em.createQuery(cq.where( p )).getResultList();
	}

	public List<String> listMyCatagoryWithAppId( List<String> myCatagoryIds, String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CatagoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CatagoryInfo> root = cq.from(CatagoryInfo.class);
		cq.select(root.get(CatagoryInfo_.id));
		Predicate p = cb.equal( root.get( CatagoryInfo_.appId ), appId );
		if( myCatagoryIds != null && !myCatagoryIds.isEmpty() ){
			p = cb.and( p, root.get( CatagoryInfo_.id ).in( myCatagoryIds ) );
		}
		return em.createQuery(cq.where( p )).getResultList();
	}
}