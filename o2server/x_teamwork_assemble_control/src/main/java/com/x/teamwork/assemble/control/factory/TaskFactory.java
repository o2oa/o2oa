package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.Task_;


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
		cq.orderBy( cb.asc( root.get( Task_.createTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据类别列示Task实体ID信息列表
	 * @param application
	 * @return
	 * @throws Exception
	 */
	public List<String> listByProject( String projectId ) throws Exception {
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
	 * 查询未review的工作任务信息
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<Task> listUnReviewTask(int maxCount) throws Exception {
		if( maxCount == 0  ){
			maxCount = 100;
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.or( cb.isNull(root.get(Task_.reviewed )), cb.isFalse( root.get(Task_.reviewed )));
		cq.orderBy( cb.asc( root.get( Task_.updateTime ) ) );
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
	 * 根据条件查询符合条件的项目信息数量
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public Long countWithProject( String projectId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get( Task_.project), projectId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<Task> allUnCompletedSubTasks(String taskId) throws Exception {
		if( StringUtils.isEmpty( taskId )  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(  root.get( Task_.parent ), taskId );
		p = cb.and( p, cb.isFalse( root.get(Task_.completed )));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAllTaskIdsWithProject(String project) throws Exception {
		if( StringUtils.isEmpty( project )  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(  root.get( Task_.project ), project );
		cq.select( root.get(Task_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
