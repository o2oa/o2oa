package com.x.teamwork.assemble.control.factory;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.*;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


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

	public List<String> listNotArchiveProject() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Project.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p = cb.notEqual(root.get( Project_.workStatus), ProjectStatusEnum.ARCHIVED);
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
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}

		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(groupNames));
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
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}

		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(groupNames));
		}

		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, p_permission, root, queryFilter );

		Order orderWithField = CriteriaBuilderTools.getOrder(cb, root, Project_.class, orderField, orderType);
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	/**
	 *  根据条件查询符合条件的项目信息ID
	 * @param projectId
	 * @param deleted
	 * @return
	 * @throws Exception
	 */
	public List<Task> listAllTasks(String projectId, Boolean deleted) throws Exception {
		if( StringUtils.isEmpty( projectId ) ){
			return new ArrayList<Task>();
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = root.get(Task_.project).in(projectId);
		p = cb.and( p, cb.isFalse( root.get(Task_.deleted )));

		cq.orderBy( cb.desc( root.get( Task_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
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
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}

		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(groupNames));
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
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.isMember( personName, root.get( Project_.participantList )) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.creatorPerson ), personName ) );
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Project_.executor ), personName ) );
		}
		if( ListTools.isNotEmpty( identityNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(identityNames));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission,  root.get( Project_.participantList).in(groupNames));
		}

		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, p_permission, root, queryFilter );
		cq.distinct(true).select( root.get(Project_.id) );
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	/**
	 * 根据条件分页查询符合条件的文档信息列表
	 * @param personName
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @param adjustPage
	 * @param adjustPageSize
	 * @return
	 * @throws Exception
	 */
	public List<Project> listPagingWithCondition( String personName, String orderField, String orderType, QueryFilter queryFilter, Integer adjustPage,
												   Integer adjustPageSize) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Project.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Project> cq = cb.createQuery(Project.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.projectId), root.get(Project_.id)));
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}
		List<String> fields = ProjectWo.copier.getCopyFields();
		cq.select(root).where(p);

		List<Order> orders = new ArrayList<>();

		if(StringUtils.isNotBlank(orderField) && fields.contains(orderField)) {
			String defaultOrderBy = "asc";
			if( defaultOrderBy.equalsIgnoreCase( orderType )) {
				orders.add(cb.asc( root.get( orderField )));
			}else {
				orders.add(cb.desc( root.get( orderField )));
			}
		}
		if(orders.isEmpty()) {
			orders.add(cb.desc(root.get(JpaObject.sequence_FIELDNAME)));
		}
		cq.orderBy( orders );

		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	/**
	 * 根据条件统计文档数目
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithCondition( String personName, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Project.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.projectId), root.get(Project_.id)));
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}

		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	/**
	 * 根据条件列示符合条件的项目
	 * @param personName
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Project> listWithCondition( String personName, String orderField, String orderType, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Project.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Project> cq = cb.createQuery(Project.class);
		Root<Project> root = cq.from(Project.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Project_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.projectId), root.get(Project_.id)));
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}
		List<String> fields = ProjectWo.copier.getCopyFields();
		cq.select(root).where(p);

		List<Order> orders = new ArrayList<>();

		if(StringUtils.isNotBlank(orderField) && fields.contains(orderField)) {
			String defaultOrderBy = "asc";
			if( defaultOrderBy.equalsIgnoreCase( orderType )) {
				orders.add(cb.asc( root.get( orderField )));
			}else {
				orders.add(cb.desc( root.get( orderField )));
			}
		}
		if(orders.isEmpty()) {
			orders.add(cb.desc(root.get(JpaObject.sequence_FIELDNAME)));
		}
		cq.orderBy( orders );

		return em.createQuery(cq).getResultList();
	}

	public static class ProjectWo extends Project{

		static WrapCopier<Project, ProjectWo> copier = WrapCopierFactory.wo(Project.class, ProjectWo.class,
				JpaObject.singularAttributeField(Project.class, false, true), null);
	}


}
