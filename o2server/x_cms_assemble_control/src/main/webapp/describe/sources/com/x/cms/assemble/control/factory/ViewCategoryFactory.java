package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewCategory_;

/**
 * 视图配置管理基础功能服务类
 * 
 * @author O2LEE
 */
public class ViewCategoryFactory extends AbstractFactory {

	public ViewCategoryFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ViewCategory信息对象
	 * @param id
	 * @return ViewCategory
	 * @throws Exception
	 */
	//@MethodDescribe("获取指定Id的ViewCategory信息对象")
	public ViewCategory get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ViewCategory.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示全部的ViewCategory信息ID列表
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("列示全部的ViewCategory文件附件信息ID列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select(root.get(ViewCategory_.id));
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 列示指定Id的ViewCategory信息ID列表
	 * @param ids 需要查询的ID列表
	 * @return List：ViewCategory
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定Id的ViewCategory文件附件信息ID列表")
//	public List<ViewCategory> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ViewCategory> cq = cb.createQuery( ViewCategory.class );
//		Root<ViewCategory> root = cq.from( ViewCategory.class );
//		Predicate p = root.get(ViewCategory_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	
	/**
	 * 列示指定分类的所有视图配置信息ID列表
	 * @param id 指定的分类ID
	 * @return
	 * @throws Exception 
	 */
//	@MethodDescribe("列示指定分类的所有视图分类关联配置信息ID列表")
	public List<String> listByCategoryId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
			throw new Exception("内容管理listViewByCategoryId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select(root.get(ViewCategory_.viewId));
		Predicate p = cb.equal(root.get( ViewCategory_.categoryId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定视图在哪些分类中使用
	 * @param id 指定的视图ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定视图的所有视图分类关联信息")
	public List<String> listByViewId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
			throw new Exception("内容管理listCategoryByViewId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select(root.get(ViewCategory_.id));
		Predicate p = cb.equal(root.get( ViewCategory_.viewId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public ViewCategory getByViewAndCategory(String viewId, String categoryId) throws Exception {
		if( StringUtils.isEmpty(viewId) ){
			throw new Exception("viewId is null!");
		}
		if( StringUtils.isEmpty(categoryId) ){
			throw new Exception("categoryId is null!");
		}
		List<ViewCategory> list = null;
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ViewCategory> cq = cb.createQuery(ViewCategory.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		Predicate p = cb.equal(root.get( ViewCategory_.viewId ), viewId);
		p = cb.and( p, cb.equal(root.get( ViewCategory_.categoryId ), categoryId));
		list = em.createQuery(cq.where(p)).getResultList();
		if( list != null && !list.isEmpty() ){
			return list.get( 0 );
		}
		return null;
	}
}