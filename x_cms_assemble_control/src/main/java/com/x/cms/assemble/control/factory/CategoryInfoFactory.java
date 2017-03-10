package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CategoryInfo_;

/**
 * 分类信息基础功能服务类
 * @author liyi
 */
public class CategoryInfoFactory extends AbstractFactory {

	public CategoryInfoFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的CategoryInfo分类信息对象")
	public CategoryInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, CategoryInfo.class );
	}
	
	@MethodDescribe("列示全部的CategoryInfo分类信息列表")
	public List<CategoryInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery( CategoryInfo.class );
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的CategoryInfo分类信息列表")
	public List<CategoryInfo> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery( CategoryInfo.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = root.get( CategoryInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe("根据应用ID列示所有的CategoryInfo分类信息列表")
	public List<String> listByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.appId ), appId );
		cq.select(root.get( CategoryInfo_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
	}
	
	@MethodDescribe("根据应用ID列示所有的CategoryInfo分类信息数量")
	public Long countByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = cb.equal( root.get(CategoryInfo_.appId), appId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	@MethodDescribe("根据分类ID列示所有下级CategoryInfo分类信息列表")
	public List<String> listByParentId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.parentId ), categoryId );
		cq.select(root.get( CategoryInfo_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
	}

	@MethodDescribe("对分类信息进行模糊查询，并且返回信息列表.")
	public List<String> listLike(String keyStr) throws Exception {
		String str = keyStr.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from( CategoryInfo.class);
		Predicate p = cb.like(root.get( CategoryInfo_.categoryName ), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get( CategoryInfo_.categoryAlias ), str + "%", '\\'));
		cq.select(root.get( CategoryInfo_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}
	
	public List<String> listNoPermissionCategoryInfoIds( List<String> permissionedCategoryInfoIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		if( permissionedCategoryInfoIds != null && !permissionedCategoryInfoIds.isEmpty() ){
			Predicate p = cb.not( root.get(CategoryInfo_.id).in( permissionedCategoryInfoIds ));
			return em.createQuery(cq.where( p )).getResultList();
		}
		return em.createQuery(cq).getResultList();
	}

	public List<String> listNoPermissionCategoryInfoIds( List<String> permissionedCategoryInfoIds, String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = cb.equal( root.get( CategoryInfo_.appId ), appId );
		if( permissionedCategoryInfoIds != null && !permissionedCategoryInfoIds.isEmpty() ){
			p = cb.and( p, cb.not( root.get( CategoryInfo_.id ).in( permissionedCategoryInfoIds )) );
		}
		return em.createQuery(cq.where( p )).getResultList();
	}

	public List<String> listMyCategoryWithAppId( List<String> myCategoryIds, String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = cb.equal( root.get( CategoryInfo_.appId ), appId );
		if( myCategoryIds != null && !myCategoryIds.isEmpty() ){
			p = cb.and( p, root.get( CategoryInfo_.id ).in( myCategoryIds ) );
		}
		return em.createQuery(cq.where( p )).getResultList();
	}
}