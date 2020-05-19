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
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Priority;
import com.x.teamwork.core.entity.Priority_;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectGroup_;


public class PriorityFactory extends AbstractFactory {

	public PriorityFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ProjectGroup实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Priority get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Priority.class, ExceptionWhen.none );
	}
	
	/**
	 * 获取指定name的优先级实体信息对象
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<Priority> getByName( String name ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Priority.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Priority> cq = cb.createQuery(Priority.class);
		Root<Priority> root = cq.from(Priority.class);
		Predicate p = cb.equal( root.get(Priority_.priority), name );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定Id的ProjectGroup实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Priority> listPriority() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Priority.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Priority> cq = cb.createQuery(Priority.class);
		Root<Priority> root = cq.from(Priority.class);
		//Predicate p = cb.equal( root.get(Priority_.owner), person );
		cq.orderBy( cb.asc( root.get( Priority_.order ) ) );
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 根据用户列示ProjectGroup实体信息列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<String> listByPerson( String person ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProjectGroup> root = cq.from(ProjectGroup.class);
		Predicate p = cb.equal( root.get(ProjectGroup_.owner), person );
		cq.select( root.get(ProjectGroup_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据用户列示Priority实体信息列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<Priority> listPriorityByPerson( String person ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Priority.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Priority> cq = cb.createQuery(Priority.class);
		Root<Priority> root = cq.from(Priority.class);
		//Predicate p = cb.equal( root.get(Priority_.owner), person );
		cq.orderBy( cb.desc( root.get( Priority_.order ) ) );
		return em.createQuery(cq).getResultList();
	}	
}
