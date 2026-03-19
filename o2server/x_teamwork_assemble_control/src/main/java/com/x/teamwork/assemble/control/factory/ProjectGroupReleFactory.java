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
import com.x.teamwork.core.entity.ProjectGroupRele;
import com.x.teamwork.core.entity.ProjectGroupRele_;


public class ProjectGroupReleFactory extends AbstractFactory {

	public ProjectGroupReleFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ProjectGroupRele实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectGroupRele get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ProjectGroupRele.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的ProjectGroupRele实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<ProjectGroupRele> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<ProjectGroupRele>();
		}
		EntityManager em = this.entityManagerContainer().get(ProjectGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectGroupRele> cq = cb.createQuery(ProjectGroupRele.class);
		Root<ProjectGroupRele> root = cq.from(ProjectGroupRele.class);
		Predicate p = root.get(ProjectGroupRele_.id).in(ids);
		cq.orderBy( cb.desc( root.get( ProjectGroupRele_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据项目组ID列示项目ID信息列表
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public List<String> listProjectIdByGroup( String group ) throws Exception {
		if( StringUtils.isEmpty( group ) ){
			throw new Exception("group can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProjectGroupRele> root = cq.from(ProjectGroupRele.class);
		Predicate p = cb.equal( root.get(ProjectGroupRele_.groupId), group );
		cq.select( root.get(ProjectGroupRele_.projectId) );
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
		EntityManager em = this.entityManagerContainer().get(ProjectGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProjectGroupRele> root = cq.from(ProjectGroupRele.class);
		Predicate p = cb.equal( root.get(ProjectGroupRele_.groupId), group );
		cq.select( root.get(ProjectGroupRele_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据项目组ID以及项目ID获取一个关联信息
	 * @param group
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<ProjectGroupRele> listWithGroupAndProject( String group, String project ) throws Exception {
		if( StringUtils.isEmpty( group ) ){
			throw new Exception("group can not be empty!");
		}
		if( StringUtils.isEmpty( project ) ){
			throw new Exception("project can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectGroupRele> cq = cb.createQuery(ProjectGroupRele.class);
		Root<ProjectGroupRele> root = cq.from(ProjectGroupRele.class);
		Predicate p = cb.equal( root.get(ProjectGroupRele_.groupId), group );
		p = cb.and( p,  cb.equal( root.get(ProjectGroupRele_.projectId), project ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<ProjectGroupRele> listReleWithProject(String projectId) throws Exception {
		if( StringUtils.isEmpty( projectId ) ){
			throw new Exception("projectId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectGroupRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectGroupRele> cq = cb.createQuery(ProjectGroupRele.class);
		Root<ProjectGroupRele> root = cq.from(ProjectGroupRele.class);
		Predicate p = cb.equal( root.get(ProjectGroupRele_.projectId), projectId );
		return em.createQuery(cq.where(p)).getResultList();
	}

}
