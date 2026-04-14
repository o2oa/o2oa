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
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectGroup_;


public class ProjectGroupFactory extends AbstractFactory {

	public ProjectGroupFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ProjectGroup实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectGroup get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ProjectGroup.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的ProjectGroup实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<ProjectGroup> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<ProjectGroup>();
		}
		EntityManager em = this.entityManagerContainer().get(ProjectGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectGroup> cq = cb.createQuery(ProjectGroup.class);
		Root<ProjectGroup> root = cq.from(ProjectGroup.class);
		Predicate p = root.get(ProjectGroup_.id).in(ids);
		cq.orderBy( cb.desc( root.get( ProjectGroup_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
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
	 * 根据用户列示ProjectGroup实体信息列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<ProjectGroup> listGroupByPerson( String person ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectGroup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectGroup> cq = cb.createQuery(ProjectGroup.class);
		Root<ProjectGroup> root = cq.from(ProjectGroup.class);
		Predicate p = cb.equal( root.get(ProjectGroup_.owner), person );
		return em.createQuery(cq.where(p)).getResultList();
	}	
}
