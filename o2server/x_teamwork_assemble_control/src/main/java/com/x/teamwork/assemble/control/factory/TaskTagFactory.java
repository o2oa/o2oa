package com.x.teamwork.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.TaskTagRele;
import com.x.teamwork.core.entity.TaskTagRele_;
import com.x.teamwork.core.entity.TaskTag_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;


public class TaskTagFactory extends AbstractFactory {

	public TaskTagFactory( Business business ) throws Exception {
		super(business);
	}

	public List<String> listTagIdsWithTask(String taskId, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskTagRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskTagRele> root = cq.from(TaskTagRele.class);
		Predicate p = cb.equal( root.get( TaskTagRele_.taskId ), taskId );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTagRele_.owner ), person ) );
		cq.select( root.get(TaskTagRele_.tagId) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据标签名称，项目ID，人员标识查询该标签在用户的项目标签中的标签ID，理论上只有一条记录
	 * @param tagName
	 * @param project
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listTagIdsWithTagNameAndProjectAndPerson(String tagName, String project, String personName) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskTagRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskTag> root = cq.from(TaskTag.class);
		Predicate p = cb.equal( root.get( TaskTag_.tag ), tagName );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTag_.owner ), personName ) );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTag_.project ), project ) );
		cq.select( root.get(TaskTag_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据标签ID、任务ID和人员名称查询标签与任务的关联信息
	 * @param tagId
	 * @param taskId
	 * @param personName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listTagReleIdsWithTagIdAndTaskAndPerson(String tagId, String taskId, String personName) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskTagRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskTagRele> root = cq.from(TaskTagRele.class);
		Predicate p = cb.equal( root.get( TaskTagRele_.taskId ), taskId );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTagRele_.tagId ), tagId ) );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTagRele_.owner ), personName ) );
		cq.select( root.get(TaskTagRele_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<TaskTag> listWithProjectAndPerson( String project, String person ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskTag.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskTag> cq = cb.createQuery(TaskTag.class);
		Root<TaskTag> root = cq.from(TaskTag.class);
		Predicate p = cb.equal( root.get( TaskTag_.project ), project );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTag_.owner ), person ) );
		cq.orderBy( cb.asc( root.get( TaskTag_.createTime ) )  );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<TaskTagRele> listReleWithProjectAndPerson( String projectId, String person ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskTagRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskTagRele> cq = cb.createQuery(TaskTagRele.class);
		Root<TaskTagRele> root = cq.from(TaskTagRele.class);
		Predicate p = cb.equal( root.get( TaskTagRele_.project ), projectId );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTagRele_.owner ), person ) );
		cq.orderBy( cb.asc( root.get( TaskTagRele_.createTime ) )  );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<TaskTagRele> listReleWithTaskAndPerson( String taskId, String person ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskTagRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskTagRele> cq = cb.createQuery(TaskTagRele.class);
		Root<TaskTagRele> root = cq.from(TaskTagRele.class);
		Predicate p = cb.equal( root.get( TaskTagRele_.taskId ), taskId );
		p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( TaskTagRele_.owner ), person ) );
		cq.orderBy( cb.asc( root.get( TaskTagRele_.createTime ) )  );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
}
