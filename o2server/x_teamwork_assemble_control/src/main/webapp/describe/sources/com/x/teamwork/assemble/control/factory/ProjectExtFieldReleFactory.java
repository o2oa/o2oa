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
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.ProjectExtFieldRele_;


public class ProjectExtFieldReleFactory extends AbstractFactory {

	public ProjectExtFieldReleFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ProjectExtFieldRele实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectExtFieldRele get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ProjectExtFieldRele.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的ProjectExtFieldRele实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<ProjectExtFieldRele> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<ProjectExtFieldRele>();
		}
		EntityManager em = this.entityManagerContainer().get(ProjectExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectExtFieldRele> cq = cb.createQuery(ProjectExtFieldRele.class);
		Root<ProjectExtFieldRele> root = cq.from(ProjectExtFieldRele.class);
		Predicate p = root.get(ProjectExtFieldRele_.id).in(ids);
		cq.orderBy( cb.desc( root.get( ProjectExtFieldRele_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据项目ID列示项目扩展属性ID信息列表
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<ProjectExtFieldRele> listFieldReleObjByProject( String project ) throws Exception {
		if( StringUtils.isEmpty( project ) ){
			throw new Exception("project can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectExtFieldRele> cq = cb.createQuery(ProjectExtFieldRele.class);
		Root<ProjectExtFieldRele> root = cq.from(ProjectExtFieldRele.class);
		Predicate p = cb.equal( root.get(ProjectExtFieldRele_.projectId), project );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据项目ID列示项目扩展属性ID信息列表
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<String> listFieldReleIdsByProject( String project ) throws Exception {
		if( StringUtils.isEmpty( project ) ){
			throw new Exception("project can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProjectExtFieldRele> root = cq.from(ProjectExtFieldRele.class);
		Predicate p = cb.equal( root.get(ProjectExtFieldRele_.projectId), project );
		cq.select( root.get(ProjectExtFieldRele_.projectId) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据扩展属性名以及项目ID获取一组关联信息
	 * @param fieldName
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<ProjectExtFieldRele> listWithFieldNameAndProject( String fieldName, String project ) throws Exception {
		if( StringUtils.isEmpty( fieldName ) ){
			throw new Exception("fieldName can not be empty!");
		}
		if( StringUtils.isEmpty( project ) ){
			throw new Exception("project can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(ProjectExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectExtFieldRele> cq = cb.createQuery(ProjectExtFieldRele.class);
		Root<ProjectExtFieldRele> root = cq.from(ProjectExtFieldRele.class);
		Predicate p = cb.equal( root.get(ProjectExtFieldRele_.extFieldName), fieldName );
		p = cb.and( p,  cb.equal( root.get(ProjectExtFieldRele_.projectId), project ));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
