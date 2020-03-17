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
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectDetail;
import com.x.teamwork.core.entity.Project_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


public class ProjectFactory extends AbstractFactory {

	public ProjectFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的Project实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Project get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Project.class, ExceptionWhen.none );
	}
	
	public ProjectDetail getDetail(String id) throws Exception {
		return this.entityManagerContainer().find( id, ProjectDetail.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的Project实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Project> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<Project>();
		}
		EntityManager em = this.entityManagerContainer().get(Project.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> cq = cb.createQuery(Project.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p = root.get(Project_.id).in(ids);
		cq.orderBy( cb.desc( root.get( Project_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAllProjectIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Project.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Project> root = cq.from(Project.class);
		cq.select( root.get( Project_.id) );
		return em.createQuery(cq ).getResultList();
	}	
	
	/**
	 * 根据条件查询符合条件的项目信息数量
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Project.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p_permission = null;

		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}
		
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantGroupList).in(groupNames));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, p_permission, root, queryFilter );
		cq.select(cb.count(root)).where(p);
		Long count =  em.createQuery(cq).getSingleResult();
		return count;
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
	public List<Project> listWithFilter( Integer maxCount, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Project.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> cq = cb.createQuery(Project.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p_permission = null;
		
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}
		
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantGroupList).in(groupNames));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, p_permission, root, queryFilter );

		Order orderWithField = CriteriaBuilderTools.getOrder(cb, root, Project_.class, orderField, orderType);
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
	public List<Project> listWithFilter( Integer maxCount, Object sequenceFieldValue, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Project.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> cq = cb.createQuery(Project.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p_permission = null;
		
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}
		
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantGroupList).in(groupNames));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, p_permission, root, queryFilter );
		
		if( sequenceFieldValue != null && StringUtils.isNotEmpty( sequenceFieldValue.toString() )) {
			Predicate p_seq = cb.isNotNull( root.get( Dynamic_.sequence ) );
			if( "desc".equalsIgnoreCase( orderType )){
				p_seq = cb.and( p_seq, cb.lessThan( root.get( Project_.sequence ), sequenceFieldValue.toString() ));
			}else{
				p_seq = cb.and( p_seq, cb.greaterThan( root.get( Project_.sequence ), sequenceFieldValue.toString() ));
			}
			p = cb.and( p, p_seq);
		}		
		
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Project_.class, orderField, orderType );
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		System.out.println(">>>SQL：" + em.createQuery(cq.where(p)).setMaxResults( maxCount).toString() );
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 * 根据条件查询所有符合条件的项目信息ID，项目信息不会很多 ，所以直接查询出来
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
	public List<String> listAllViewableProjectIds( Integer maxCount, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Project.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p_permission = null;
		
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantPersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.manageablePersonList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantIdentityList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantGroupList).in(groupNames));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, p_permission, root, queryFilter );
		cq.distinct(true).select( root.get(Project_.id) );
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	
}
