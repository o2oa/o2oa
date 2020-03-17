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
import com.x.cms.core.entity.element.ViewFieldConfig;
import com.x.cms.core.entity.element.ViewFieldConfig_;

/**
 * 视图配置管理基础功能服务类
 * 
 * @author O2LEE
 */
public class ViewFieldConfigFactory extends AbstractFactory {

	public ViewFieldConfigFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ViewFieldConfig信息对象
	 * @param id
	 * @return ViewFieldConfig
	 * @throws Exception
	 */
	//@MethodDescribe("获取指定Id的ViewFieldConfig信息对象")
	public ViewFieldConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ViewFieldConfig.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示全部的ViewFieldConfig信息ID列表
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("列示全部的ViewFieldConfig文件附件信息ID列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( ViewFieldConfig.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewFieldConfig> root = cq.from( ViewFieldConfig.class );
		cq.select(root.get(ViewFieldConfig_.id));
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 列示指定Id的ViewFieldConfig信息ID列表
	 * @param ids 需要查询的ID列表
	 * @return List：ViewFieldConfig
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定Id的ViewFieldConfig文件附件信息ID列表")
//	public List<ViewFieldConfig> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( ViewFieldConfig.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ViewFieldConfig> cq = cb.createQuery( ViewFieldConfig.class );
//		Root<ViewFieldConfig> root = cq.from( ViewFieldConfig.class );
//		Predicate p = root.get(ViewFieldConfig_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	
	/**
	 * 列示指定视图的所有展示列配置信息ID列表
	 * @param id 指定的视图ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定视图的所有展示列配置信息ID列表")
	public List<String> listByViewId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
			throw new Exception("内容管理listByViewId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewFieldConfig.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewFieldConfig> root = cq.from( ViewFieldConfig.class );
		cq.select(root.get(ViewFieldConfig_.id));
		Predicate p = cb.equal(root.get( ViewFieldConfig_.viewId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
}