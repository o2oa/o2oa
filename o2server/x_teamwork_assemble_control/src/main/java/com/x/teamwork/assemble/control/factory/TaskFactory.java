package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.*;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;


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
			return Collections.EMPTY_LIST;
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
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<String> listByProject( String projectId ) throws Exception {
		if( StringUtils.isEmpty( projectId ) ){
			return Collections.EMPTY_LIST;
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
	 * 根据类别列示Task实体列表
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<Task> listObjectByProject( String projectId ) throws Exception {
		if( StringUtils.isEmpty( projectId ) ){
			return Collections.EMPTY_LIST;
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get(Task_.project), projectId );
		cq.select( root );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 列示项目的第一层任务
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<Task> listSubTaskByProject(String projectId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get(Task_.project), projectId );
		p = cb.and(p, cb.equal( root.get(Task_.parent), Task.TOP_TASK ));
		cq.select( root );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 列示子任务
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List<Task> listSubTask(String parentTaskId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get(Task_.parent), parentTaskId );
		cq.select( root );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 列示子任务(是否包含已取消的任务)
	 * @param parentTaskId
	 * @param containCancel
	 * @return
	 * @throws Exception
	 */
	public List<Task> listSubTask(String parentTaskId, boolean containCancel) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get(Task_.parent), parentTaskId );
		if(!containCancel){
			p = cb.and(p, cb.notEqual( root.get(Task_.workStatus), ProjectStatusEnum.CANCELED.getValue() ));
		}
		cq.select( root );
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
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	public List<String> listByParent( String pid) throws Exception {
		if( StringUtils.isEmpty( pid ) ){
			return Collections.EMPTY_LIST;
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

	/**
	 * 根据条件查询符合条件的项目信息数量
	 * @param parentTaskId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public Long countWithParentAndProject(String parentTaskId, String projectId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal( root.get( Task_.project), projectId );
		p = cb.and(p, cb.equal(root.get( Task_.parent), parentTaskId));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<Task> allUnCompletedSubTasks(String taskId) throws Exception {
		if( StringUtils.isEmpty( taskId )  ){
			return Collections.EMPTY_LIST;
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(  root.get( Task_.parent ), taskId );
		p = cb.and( p, root.get( Task_.workStatus ).in(ProjectStatusEnum.PROCESSING.getValue(), ProjectStatusEnum.DELAY.getValue()));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Task> allUnCompletedTasks(String projectId) throws Exception {
		if( StringUtils.isEmpty( projectId )  ){
			return Collections.EMPTY_LIST;
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(  root.get( Task_.project ), projectId );
		p = cb.and( p, root.get( Task_.workStatus ).in(ProjectStatusEnum.PROCESSING.getValue(), ProjectStatusEnum.DELAY.getValue()));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Task> listExpireTasks(String projectId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(  root.get( Task_.project ), projectId );
		p = cb.and(p, cb.lessThan(root.get( Task_.endTime ), new Date()));
		p = cb.and( p, root.get( Task_.workStatus ).in(ProjectStatusEnum.PROCESSING.getValue(), ProjectStatusEnum.DELAY.getValue()));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAllTaskIdsWithProject(String project) throws Exception {
		if( StringUtils.isEmpty( project )  ){
			return Collections.EMPTY_LIST;
		}
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(  root.get( Task_.project ), project );
		cq.select( root.get(Task_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
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
	public List<Task> listPagingWithCondition(String personName, String orderField, String orderType, QueryFilter queryFilter, Integer adjustPage,
												 Integer adjustPageSize) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Task_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.targetId), root.get(Task_.id)));
			if(BooleanUtils.isTrue(queryFilter.getQueryManager())){
				p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.role), ProjectRoleEnum.MANAGER.getValue()));
			}
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}
		List<String> fields = TaskWo.copier.getCopyFields();
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections).where(p);

		List<Order> orders = new ArrayList<>();

		if(StringUtils.isNotBlank(orderField) && fields.contains(orderField)) {
			String defaultOrderBy = "asc";
			if( defaultOrderBy.equalsIgnoreCase( orderType )) {
				orders.add(cb.asc( root.get( orderField )));
			}else {
				orders.add(cb.desc( root.get( orderField )));
			}
		}
		if(orders.isEmpty() || (!JpaObject.sequence_FIELDNAME.equals(orderField) && !JpaObject.createTime_FIELDNAME.equals(orderField))) {
			orders.add(cb.desc(root.get(JpaObject.sequence_FIELDNAME)));
		}
		cq.orderBy( orders );

		List<Tuple> list = em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
		List<Task> documentList = new ArrayList<>();
		for (Tuple o : list){
			Task doc = new Task();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(doc, fields.get(i), o.get(selections.get(i)));
			}
			documentList.add(doc);
		}
		return documentList;
	}

	/**
	 * 根据条件统计文档数目
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithCondition( String personName, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Task_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.targetId), root.get(Task_.id)));
			if(BooleanUtils.isTrue(queryFilter.getQueryManager())){
				p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.role), ProjectRoleEnum.MANAGER.getValue()));
			}
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}

		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	/**
	 * 任务四象限条件查询
	 * @param effectivePerson
	 * @param projectId
	 * @param important
	 * @param urgency
	 * @param justExecutor
	 * @return
	 * @throws Exception
	 */
	public List<Task> listFourQuadrant(EffectivePerson effectivePerson, List<String> statusList, String projectId,
									   String important, String urgency, Integer count, Boolean justExecutor) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.conjunction();
		if(ListTools.isNotEmpty(statusList)){
			p = cb.and(root.get(Task_.workStatus).in(statusList));
		}
		if(StringUtils.isNotBlank(projectId) && !EMPTY_SYMBOL.equals(projectId)){
			p = cb.and(p, cb.equal(root.get(Task_.project), projectId));
		}
		if(StringUtils.isNotBlank(important) && !EMPTY_SYMBOL.equals(important)){
			p = cb.and(p, cb.equal(root.get(Task_.important), important));
		}
		if(StringUtils.isNotBlank(urgency) && !EMPTY_SYMBOL.equals(urgency)){
			p = cb.and(p, cb.equal(root.get(Task_.urgency), urgency));
		}
		if(BooleanUtils.isTrue(justExecutor)){
			p = cb.and(p, cb.equal(root.get(Task_.executor), effectivePerson.getDistinguishedName()));
		} else {
			EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
			CriteriaBuilder cb1 = em1.getCriteriaBuilder();
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), effectivePerson.getDistinguishedName());
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.targetId), root.get(Task_.id)));
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}
		List<String> fields = TaskWo.copier.getCopyFields();
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections).where(p);

		cq.orderBy( cb.desc(root.get(JpaObject.sequence_FIELDNAME)) );

		if(count==null || count<1){
			count = 10;
		}
		List<Tuple> list = em.createQuery(cq).setMaxResults(count).getResultList();
		List<Task> documentList = new ArrayList<>();
		for (Tuple o : list){
			Task doc = new Task();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(doc, fields.get(i), o.get(selections.get(i)));
			}
			documentList.add(doc);
		}
		return documentList;
	}

	/**
	 * 根据条件查询符合条件的文档信息列表
	 * @param personName
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Task> listWithCondition(String personName, String orderField, String orderType, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Task.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Task_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.targetId), root.get(Task_.id)));
			if(BooleanUtils.isTrue(queryFilter.getQueryManager())){
				p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.role), ProjectRoleEnum.MANAGER.getValue()));
			}
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}
		cq.select(root).where(p);

		List<Order> orders = new ArrayList<>();
		List<String> fields = TaskWo.copier.getCopyFields();
		if(StringUtils.isNotBlank(orderField) && fields.contains(orderField)) {
			String defaultOrderBy = "asc";
			if( defaultOrderBy.equalsIgnoreCase( orderType )) {
				orders.add(cb.asc( root.get( orderField )));
			}else {
				orders.add(cb.desc( root.get( orderField )));
			}
		}
		if(orders.isEmpty() || (!JpaObject.sequence_FIELDNAME.equals(orderField) && !JpaObject.createTime_FIELDNAME.equals(orderField))) {
			orders.add(cb.desc(root.get(JpaObject.sequence_FIELDNAME)));
		}
		cq.orderBy( orders );

		return em.createQuery(cq).getResultList();
	}

	public static class TaskWo extends Task{

		static WrapCopier<Task, TaskWo> copier = WrapCopierFactory.wo(Task.class, TaskWo.class,
				JpaObject.singularAttributeField(Task.class, true, true), null);
	}
}
