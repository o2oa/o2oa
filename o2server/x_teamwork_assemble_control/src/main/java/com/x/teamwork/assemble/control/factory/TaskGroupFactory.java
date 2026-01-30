package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskGroup_;


public class TaskGroupFactory extends AbstractFactory {

	public TaskGroupFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的TaskGroup实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskGroup get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, TaskGroup.class );
	}
	
	/**
	 * 列示指定Id的TaskGroup实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<TaskGroup> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<TaskGroup>();
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskGroup> cq = cb.createQuery(TaskGroup.class);
		Root<TaskGroup> root = cq.from(TaskGroup.class);
		Predicate p = root.get(TaskGroup_.id).in(ids);
		cq.orderBy( cb.desc( root.get( TaskGroup_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据用户列示TaskGroup实体信息列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<String> listByPerson( String person ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskGroup> root = cq.from(TaskGroup.class);
		Predicate p = cb.equal( root.get(TaskGroup_.owner), person );
		cq.select( root.get(TaskGroup_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据用户列示TaskGroup实体信息列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<TaskGroup> listGroupByPerson( String person ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskGroup> cq = cb.createQuery(TaskGroup.class);
		Root<TaskGroup> root = cq.from(TaskGroup.class);
		Predicate p = cb.equal( root.get(TaskGroup_.owner), person );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据用户和项目列示TaskGroup实体信息ID列表
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<String> listByPersonAndProject( String person, String project ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		if( StringUtils.isEmpty( project ) ){
			throw new Exception("project can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskGroup> root = cq.from(TaskGroup.class);
		Predicate p = cb.equal( root.get(TaskGroup_.owner), person );
		p = cb.and( p, cb.equal( root.get(TaskGroup_.project), project ) );
		cq.select( root.get(TaskGroup_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据用户和项目列示TaskGroup实体信息列表
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<TaskGroup> listGroupByPersonAndProject( String person, String project ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		if( StringUtils.isEmpty( project ) ){
			throw new Exception("project can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskGroup> cq = cb.createQuery(TaskGroup.class);
		Root<TaskGroup> root = cq.from(TaskGroup.class);
		Predicate p = cb.equal( root.get(TaskGroup_.owner), person );
		p = cb.and( p, cb.equal( root.get(TaskGroup_.project), project ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
