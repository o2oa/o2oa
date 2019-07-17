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
import com.x.teamwork.core.entity.TaskGroupRele;
import com.x.teamwork.core.entity.TaskGroupRele_;


public class TaskGroupReleFactory extends AbstractFactory {

	public TaskGroupReleFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的TaskGroupRele实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskGroupRele get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, TaskGroupRele.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的TaskGroupRele实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<TaskGroupRele> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<TaskGroupRele>();
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskGroupRele> cq = cb.createQuery(TaskGroupRele.class);
		Root<TaskGroupRele> root = cq.from(TaskGroupRele.class);
		Predicate p = root.get(TaskGroupRele_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据项目组ID列示项目ID信息列表
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public List<String> listTaskIdByGroup( String group ) throws Exception {
		if( StringUtils.isEmpty( group ) ){
			throw new Exception("group can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskGroupRele> root = cq.from(TaskGroupRele.class);
		Predicate p = cb.equal( root.get(TaskGroupRele_.taskGroupId), group );
		cq.select( root.get(TaskGroupRele_.taskId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据项目组ID列示关联信息ID列表
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public List<String> listByGroup( String group ) throws Exception {
		if( StringUtils.isEmpty( group ) ){
			throw new Exception("group can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskGroupRele> root = cq.from(TaskGroupRele.class);
		Predicate p = cb.equal( root.get(TaskGroupRele_.taskGroupId), group );
		cq.select( root.get(TaskGroupRele_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据项目组ID以及项目ID获取一个关联信息
	 * @param group
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<TaskGroupRele> listWithGroupAndTask( String group, String taskId ) throws Exception {
		if( StringUtils.isEmpty( group ) ){
			throw new Exception("group can not be empty!");
		}
		if( StringUtils.isEmpty( taskId ) ){
			throw new Exception("taskId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskGroupRele> cq = cb.createQuery(TaskGroupRele.class);
		Root<TaskGroupRele> root = cq.from(TaskGroupRele.class);
		Predicate p = cb.equal( root.get(TaskGroupRele_.taskGroupId), group );
		p = cb.and( p,  cb.equal( root.get(TaskGroupRele_.taskId), taskId ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<TaskGroupRele> listTaskReleWithProject(String projectId ) throws Exception {
		if( StringUtils.isEmpty( projectId ) ){
			throw new Exception("projectId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskGroupRele> cq = cb.createQuery(TaskGroupRele.class);
		Root<TaskGroupRele> root = cq.from(TaskGroupRele.class);
		Predicate p = cb.equal( root.get(TaskGroupRele_.project), projectId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<TaskGroupRele> listTaskReleWithTask(String taskId ) throws Exception {
		if( StringUtils.isEmpty( taskId ) ){
			throw new Exception("taskId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskGroupRele> cq = cb.createQuery(TaskGroupRele.class);
		Root<TaskGroupRele> root = cq.from(TaskGroupRele.class);
		Predicate p = cb.equal( root.get(TaskGroupRele_.taskId), taskId );
		return em.createQuery(cq.where(p)).getResultList();
	}	
}
