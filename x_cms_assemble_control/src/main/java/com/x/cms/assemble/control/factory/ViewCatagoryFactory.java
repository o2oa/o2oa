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
import com.x.cms.core.entity.element.ViewCatagory;
import com.x.cms.core.entity.element.ViewCatagory_;

/**
 * 视图配置管理基础功能服务类
 * 
 * @author liyi
 */
public class ViewCatagoryFactory extends AbstractFactory {

	public ViewCatagoryFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ViewCatagory信息对象
	 * @param id
	 * @return ViewCatagory
	 * @throws Exception
	 */
	@MethodDescribe("获取指定Id的ViewCatagory信息对象")
	public ViewCatagory get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ViewCatagory.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示全部的ViewCatagory信息ID列表
	 * @return List：String
	 * @throws Exception
	 */
	@MethodDescribe("列示全部的ViewCatagory文件附件信息ID列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( ViewCatagory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCatagory> root = cq.from( ViewCatagory.class );
		cq.select(root.get(ViewCatagory_.id));
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 列示指定Id的ViewCatagory信息ID列表
	 * @param ids 需要查询的ID列表
	 * @return List：ViewCatagory
	 * @throws Exception
	 */
	@MethodDescribe("列示指定Id的ViewCatagory文件附件信息ID列表")
	public List<ViewCatagory> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( ViewCatagory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ViewCatagory> cq = cb.createQuery( ViewCatagory.class );
		Root<ViewCatagory> root = cq.from( ViewCatagory.class );
		Predicate p = root.get(ViewCatagory_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定分类的所有视图配置信息ID列表
	 * @param id 指定的分类ID
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe("列示指定分类的所有视图分类关联配置信息ID列表")
	public List<String> listByCatagoryId( String id ) throws Exception {		
		if( id == null || id.isEmpty() ){
			throw new Exception("内容管理listViewByCatagoryId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCatagory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCatagory> root = cq.from( ViewCatagory.class );
		cq.select(root.get(ViewCatagory_.id));
		Predicate p = cb.equal(root.get( ViewCatagory_.catagoryId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定视图在哪些分类中使用
	 * @param id 指定的视图ID
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe("列示指定视图的所有视图分类关联信息")
	public List<String> listByViewId( String id ) throws Exception {		
		if( id == null || id.isEmpty() ){
			throw new Exception("内容管理listCatagoryByViewId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCatagory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCatagory> root = cq.from( ViewCatagory.class );
		cq.select(root.get(ViewCatagory_.id));
		Predicate p = cb.equal(root.get( ViewCatagory_.viewId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
}