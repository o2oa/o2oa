package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.TaskView;
import com.x.teamwork.core.entity.TaskView_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


public class TaskViewFactory extends AbstractFactory {

	public TaskViewFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的TaskView实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskView get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, TaskView.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的TaskView实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<TaskView> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<TaskView>();
		}
		EntityManager em = this.entityManagerContainer().get(TaskView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskView> cq = cb.createQuery(TaskView.class);
		Root<TaskView> root = cq.from(TaskView.class);
		Predicate p = root.get(TaskView_.id).in(ids);
		cq.orderBy( cb.desc( root.get( TaskView_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据用户和项目ID查询工作任务列表
	 * @param person
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<TaskView> listWithPersonAndProject( String person, String projectId ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		if( StringUtils.isEmpty( projectId ) ){
			throw new Exception("projectId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskView> cq = cb.createQuery(TaskView.class);
		Root<TaskView> root = cq.from(TaskView.class);
		Predicate p = cb.equal( root.get(TaskView_.owner ), person );
		p = cb.and( p, cb.equal( root.get(TaskView_.project), projectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public Long countWithFilter( QueryFilter queryFilter) throws Exception {
		if( queryFilter == null  ) {
			return 0L;
		}
		EntityManager em = this.entityManagerContainer().get( TaskView.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskView> root = cq.from(TaskView.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( TaskView_.class, cb, null, root, queryFilter );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	public List<TaskView> listWithFilter( Integer maxCount, String orderField, String orderType, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskView.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskView> cq = cb.createQuery(TaskView.class);
		Root<TaskView> root = cq.from(TaskView.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( TaskView_.class, cb, null, root, queryFilter );
		
		//排序，添加排序列，默认使用sequence
		List<Order> orders = new ArrayList<>();
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, TaskView_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}
		
		if( !TaskView.sequence_FIELDNAME.equalsIgnoreCase( orderField )) {
			//如果是其他的列，很可能排序值不唯一，所以使用多一列排序列来确定每次查询的顺序
			orderWithField = CriteriaBuilderTools.getOrder( cb, root, TaskView_.class, TaskView.id_FIELDNAME, orderType );
			if( orderWithField != null ){
				orders.add( orderWithField );
			}
		}		
		if( ListTools.isNotEmpty(  orders )){
			cq.orderBy( orders );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
}
