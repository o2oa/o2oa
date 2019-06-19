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
import com.x.teamwork.core.entity.Dynamic_;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.Task_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


public class TaskFactory extends AbstractFactory {

	public TaskFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的Task实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Task get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Task.class, ExceptionWhen.none );
	}
	
	/**
	 * 获取指定Id的TaskDetail实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskDetail getDetail( String id ) throws Exception {
		return this.entityManagerContainer().find( id, TaskDetail.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的Task实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Task> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<Task>();
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = root.get(Task_.id).in(ids);
		cq.orderBy( cb.desc( root.get( Task_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据类别列示Task实体信息列表
	 * @param application
	 * @return
	 * @throws Exception
	 */
	public List<String> listByProject( String projectId) throws Exception {
		if( StringUtils.isEmpty( projectId ) ){
			throw new Exception("projectId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get(Task_.project), projectId );
		cq.select( root.get(Task_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据类别列示Task实体信息列表
	 * @param application
	 * @return
	 * @throws Exception
	 */
	public List<String> listByParent( String pid) throws Exception {
		if( StringUtils.isEmpty( pid ) ){
			throw new Exception("projectId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get(Task_.parent), pid );
		cq.select( root.get(Task_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据权限查询项目信息列表
	 * @param maxCount
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public List<Task> listWithPermission( Integer maxCount, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = null;		
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p = CriteriaBuilderTools.predicate_or( cb, p, cb.isMember( personName, root.get( Task_.participantPersonList )) );
			p = CriteriaBuilderTools.predicate_or( cb, p, cb.isMember( personName, root.get( Task_.manageablePersonList )) );
			p = CriteriaBuilderTools.predicate_or( cb, p, cb.equal( root.get( Task_.creatorPerson ), personName ) );
			p = CriteriaBuilderTools.predicate_or( cb, p, cb.equal( root.get( Task_.executor ), personName ) );
		}
		
		if( ListTools.isNotEmpty( identityNames )) {
			p = CriteriaBuilderTools.predicate_or( cb, p,  root.get( Task_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p = CriteriaBuilderTools.predicate_or( cb, p,  root.get( Task_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p = CriteriaBuilderTools.predicate_or( cb, p,  root.get( Task_.participantGroupList).in(groupNames));
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 * 根据条件查询符合条件的项目信息数量
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p_permission = null;
		
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Task_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Task_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Task_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Task_.executor ), personName ) );
		}
		
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantIdentityList).in(groupNames));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Task_.class, cb, p_permission, root, queryFilter );

		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Task> listWithFilter( Integer maxCount, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p_permission = null;
		
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Task_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Task_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Task_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Task_.executor ), personName ) );
		}
		
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantGroupList).in(groupNames));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Task_.class, cb, p_permission, root, queryFilter );

		Order orderWithField = CriteriaBuilderTools.getOrder(cb, root, Task_.class, orderField, orderType);
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param maxCount
	 * @param sequnce
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Task> listWithFilter( Integer maxCount, Object sequenceFieldValue, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p_permission = null;
		
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Task_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Task_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Task_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Task_.executor ), personName ) );
		}
		
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Task_.participantGroupList).in(groupNames));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Task_.class, cb, p_permission, root, queryFilter );
		
		if( sequenceFieldValue != null && StringUtils.isNotEmpty( sequenceFieldValue.toString() )) {
			Predicate p_seq = cb.isNotNull( root.get( Dynamic_.sequence ) );
			if( "desc".equalsIgnoreCase( orderType )){
				p_seq = cb.and( p_seq, cb.lessThan( root.get( Task_.sequence ), sequenceFieldValue.toString() ));
			}else{
				p_seq = cb.and( p_seq, cb.greaterThan( root.get( Task_.sequence ), sequenceFieldValue.toString() ));
			}
			p = cb.and( p, p_seq);
		}		
		
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Task_.class, orderField, orderType );
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
}
