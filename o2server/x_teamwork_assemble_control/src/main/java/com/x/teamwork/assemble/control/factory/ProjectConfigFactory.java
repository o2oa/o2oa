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
import com.x.teamwork.core.entity.Priority;
import com.x.teamwork.core.entity.Priority_;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectConfig;
import com.x.teamwork.core.entity.ProjectConfig_;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectGroup_;
import com.x.teamwork.core.entity.Project_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


public class ProjectConfigFactory extends AbstractFactory {

	public ProjectConfigFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的ProjectGroup实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ProjectConfig.class, ExceptionWhen.none );
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
	
	/**
	 * 根据条件查询所有符合条件的项目配置信息ID，项目配置信息不会很多 ，所以直接查询出来
	 * @param maxCount
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllProjectConfigIds( Integer maxCount, String personName,  QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( ProjectConfig.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProjectConfig> root = cq.from(ProjectConfig.class);
		Predicate p_permission = null;
		
		
		/*if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}*/
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( ProjectConfig_.class, cb, p_permission, root, queryFilter );
		cq.distinct(true).select( root.get(ProjectConfig_.id) );
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 *  根据条件查询符合条件的项目信息ID
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
	public List<ProjectConfig> listWithFilter( Integer maxCount, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( ProjectConfig.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectConfig> cq = cb.createQuery(ProjectConfig.class);
		Root<ProjectConfig> root = cq.from(ProjectConfig.class);
		Predicate p_permission = null;
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( ProjectConfig_.class, cb, p_permission, root, queryFilter );

		Order orderWithField = CriteriaBuilderTools.getOrder(cb, root, ProjectConfig_.class, orderField, orderType);
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param maxCount
	 * @param sequenceFieldValue
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
	public List<ProjectConfig> listWithFilter( Integer maxCount, Object sequenceFieldValue, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( ProjectConfig.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectConfig> cq = cb.createQuery(ProjectConfig.class);
		Root<ProjectConfig> root = cq.from(ProjectConfig.class);
		Predicate p_permission = null;
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( ProjectConfig_.class, cb, p_permission, root, queryFilter );
		
		if( sequenceFieldValue != null && StringUtils.isNotEmpty( sequenceFieldValue.toString() )) {
			Predicate p_seq = cb.isNotNull( root.get( Dynamic_.sequence ) );
			if( "desc".equalsIgnoreCase( orderType )){
				p_seq = cb.and( p_seq, cb.lessThan( root.get( ProjectConfig_.sequence ), sequenceFieldValue.toString() ));
			}else{
				p_seq = cb.and( p_seq, cb.greaterThan( root.get( ProjectConfig_.sequence ), sequenceFieldValue.toString() ));
			}
			p = cb.and( p, p_seq);
		}		
		
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, ProjectConfig_.class, orderField, orderType );
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		System.out.println(">>>SQL：" + em.createQuery(cq.where(p)).setMaxResults( maxCount).toString() );
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 * 获取指定name的优先级实体信息对象
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<ProjectConfig> getProjectConfigByProject( String name ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ProjectConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectConfig> cq = cb.createQuery(ProjectConfig.class);
		Root<ProjectConfig> root = cq.from(ProjectConfig.class);
		Predicate p = cb.equal( root.get(ProjectConfig_.project), name );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
