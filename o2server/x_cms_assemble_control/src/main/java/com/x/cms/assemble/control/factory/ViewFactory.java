package com.x.cms.assemble.control.factory;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewCategory_;
import com.x.cms.core.entity.element.View_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * 视图配置管理基础功能服务类
 *
 * @author O2LEE
 */
public class ViewFactory extends AbstractFactory {

	public ViewFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的View信息对象
	 * @param id
	 * @return View
	 * @throws Exception
	 */
	public View get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, View.class );
	}

	public View flag( String flag ) throws Exception {
		return this.entityManagerContainer().flag( flag, View.class );
	}

	/**
	 * 列示全部的View信息ID列表
	 * @return List：String
	 * @throws Exception
	 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 列示指定分类的所有视图配置信息ID列表
	 * @param categoryId 指定的文档ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listByCategoryId( String categoryId ) throws Exception {
		if( StringUtils.isEmpty(categoryId) ){
			throw new Exception("内容管理listByCategoryId方法不接受categoryId为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select( root.get(ViewCategory_.viewId ));
		Predicate p = cb.equal(root.get( ViewCategory_.categoryId ), categoryId );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 列示指定应用ID的所有视图配置信息ID列表
	 * @param id 指定的文档ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listByAppId( String id ) throws Exception {
		if( StringUtils.isEmpty(id) ){
			throw new Exception("内容管理listByAppId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		Predicate p = cb.equal(root.get( View_.appId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 列示指定表单ID的所有视图配置信息ID列表
	 * @param id 指定的文档ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listByFormId( String id ) throws Exception {
		if( StringUtils.isEmpty(id) ){
			throw new Exception("内容管理listByFormId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		Predicate p = cb.equal(root.get( View_.formId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}

}
