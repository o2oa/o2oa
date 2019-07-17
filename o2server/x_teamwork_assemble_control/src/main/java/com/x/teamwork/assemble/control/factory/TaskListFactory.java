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
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskListRele;
import com.x.teamwork.core.entity.TaskListRele_;
import com.x.teamwork.core.entity.TaskList_;


public class TaskListFactory extends AbstractFactory {

	public TaskListFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的TaskList实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskList get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, TaskList.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的TaskList实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<TaskList> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<TaskList>();
		}
		EntityManager em = this.entityManagerContainer().get(TaskList.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskList> cq = cb.createQuery(TaskList.class);
		Root<TaskList> root = cq.from(TaskList.class);
		Predicate p = root.get(TaskList_.id).in(ids);
		cq.orderBy( cb.desc( root.get( TaskList_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定Id的TaskList实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<TaskListRele> listRele( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<TaskListRele>();
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskListRele> cq = cb.createQuery(TaskListRele.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = root.get(TaskListRele_.id).in(ids);
		cq.orderBy( cb.desc( root.get( TaskListRele_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据用户和项目ID查询工作任务列表
	 * @param person
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<TaskList> listWithPersonAndProject( String person, String projectId ) throws Exception {
		if( StringUtils.isEmpty( person ) ){
			throw new Exception("person can not be empty!");
		}
		if( StringUtils.isEmpty( projectId ) ){
			throw new Exception("projectId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskList.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskList> cq = cb.createQuery(TaskList.class);
		Root<TaskList> root = cq.from(TaskList.class);
		Predicate p = cb.equal( root.get(TaskList_.owner ), person );
		p = cb.and( p, cb.equal( root.get(TaskList_.project), projectId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据列表ID获取所有的工作任务关联ID列表
	 * @param listId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listReleIdsWithListId(String listId ) throws Exception {
		if( StringUtils.isEmpty( listId ) ){
			throw new Exception("listId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskListId), listId );
		cq.select( root.get(TaskListRele_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据列表ID获取所有的工作任务关联信息列表
	 * @param listId
	 * @return
	 * @throws Exception 
	 */
	public List<TaskListRele> listReleWithListId( String listId ) throws Exception {
		if( StringUtils.isEmpty( listId ) ){
			throw new Exception("listId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskListRele> cq = cb.createQuery(TaskListRele.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskListId), listId );
		cq.orderBy( cb.asc( root.get( TaskListRele_.order ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据任务ID和列表ID查询工作列表关联对象
	 * @param taskId
	 * @param listId
	 * @return
	 * @throws Exception 
	 */
	public List<TaskListRele> listReleWithTaskAndList(String taskId, String listId) throws Exception {
		if( StringUtils.isEmpty( taskId ) ){
			throw new Exception("taskId can not be empty!");
		}
		if( StringUtils.isEmpty( listId ) ){
			throw new Exception("listId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskListRele> cq = cb.createQuery(TaskListRele.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskId), taskId );
		p = cb.and( p, cb.equal( root.get(TaskListRele_.taskListId), listId ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据任务ID查询工作列表关联对象
	 * @param taskId
	 * @return
	 * @throws Exception 
	 */
	public List<TaskListRele> listReleWithTask(String taskId ) throws Exception {
		if( StringUtils.isEmpty( taskId ) ){
			throw new Exception("taskId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskListRele> cq = cb.createQuery(TaskListRele.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskId), taskId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据任务ID查询工作列表关联对象
	 * @param taskId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listGroupIdsWithTask( String taskId ) throws Exception {
		if( StringUtils.isEmpty( taskId ) ){
			throw new Exception("taskId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskId), taskId );
		cq.distinct(true).select( root.get(TaskListRele_.taskGroupId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Integer maxOrder(String listId) throws Exception {
		if( StringUtils.isEmpty( listId ) ){
			throw new Exception("listId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskListId), listId );		
		cq.select( cb.max( root.get( TaskListRele_.order )));
		Integer max = em.createQuery(cq.where(p)).getSingleResult();
		return max == null ? 0 : max;
	}

	public List<TaskList> listWithTaskGroup(String taskGroupId) throws Exception {
		if( StringUtils.isEmpty( taskGroupId ) ){
			throw new Exception("taskGroupId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskList.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskList> cq = cb.createQuery(TaskList.class);
		Root<TaskList> root = cq.from(TaskList.class);
		Predicate p = cb.equal( root.get(TaskList_.taskGroup ), taskGroupId );
		cq.orderBy( cb.asc( root.get( TaskList_.order ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listTaskListIdsWithTaskGroup(String taskGroupId) throws Exception {
		if( StringUtils.isEmpty( taskGroupId ) ){
			throw new Exception("taskGroupId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskGroupId ), taskGroupId );
		cq.select( root.get(TaskListRele_.taskListId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listTaskIdsWithTaskGroupId(String taskGroupId) throws Exception {
		if( StringUtils.isEmpty( taskGroupId ) ){
			throw new Exception("taskGroupId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( TaskListRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskGroupId ), taskGroupId );
		cq.distinct(true).select( root.get(TaskListRele_.taskId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listTaskIdsWithTaskGroupId( List<String> taskLists ) throws Exception {
		if( ListTools.isEmpty( taskLists ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( TaskListRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p =  root.get(TaskListRele_.taskListId ).in( taskLists );
		cq.distinct(true).select( root.get(TaskListRele_.taskId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public Long countTaskWithTaskListId(String taskListId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( TaskListRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskListId ), taskListId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	public List<String> listTaskIdWithTaskListId(String taskListId) throws Exception {
		if( StringUtils.isEmpty( taskListId ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( TaskListRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskListId ), taskListId );
		cq.select( root.get(TaskListRele_.taskId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listTaskIdWithTaskListIds( List<String> taskListIds ) throws Exception {
		if(ListTools.isEmpty( taskListIds ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( TaskListRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = root.get(TaskListRele_.taskListId ).in( taskListIds );
		cq.select( root.get(TaskListRele_.taskId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listTaskIdWithTaskListId( List<String> taskListId) throws Exception {
		if( ListTools.isEmpty( taskListId ) ){
			throw new Exception("taskListId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( TaskListRele.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = root.get(TaskListRele_.taskListId ).in( taskListId );
		cq.select( root.get(TaskListRele_.taskId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<TaskListRele> listReleWithTaskAndGroup( String taskId, String taskGroupId) throws Exception {
		if( StringUtils.isEmpty( taskGroupId ) ){
			throw new Exception("taskGroupId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskListRele> cq = cb.createQuery(TaskListRele.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskId ), taskId );
		p = cb.and( p, cb.equal( root.get(TaskListRele_.taskGroupId ) , taskGroupId));
		return em.createQuery(cq.where(p)).getResultList();
	}
	public List<String> listTaskListIdWithTaskAndGroup( String taskId, String taskGroupId ) throws Exception {
		if( StringUtils.isEmpty( taskGroupId ) ){
			throw new Exception("taskGroupId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskId ), taskId );
		p = cb.and( p, cb.equal( root.get(TaskListRele_.taskGroupId ) , taskGroupId));
		cq.select( root.get(TaskListRele_.taskListId) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<TaskListRele> listTaskListWithTask(String taskId, String taskGroupId ) throws Exception {
		if( StringUtils.isEmpty( taskId ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskListRele> cq = cb.createQuery(TaskListRele.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskId ), taskId );
		p = cb.and( p, cb.equal( root.get(TaskListRele_.taskGroupId ), taskGroupId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listTaskListIdsWithGroup(String taskGroupId, String person) throws Exception {
		if( StringUtils.isEmpty( taskGroupId ) ){
			return null;
		}
		if( StringUtils.isEmpty( person ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(TaskList.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskList> root = cq.from(TaskList.class);
		Predicate p = cb.equal( root.get( TaskList_.owner ), person );
		p = cb.and( p, cb.equal( root.get( TaskList_.taskGroup ), taskGroupId ) );
		cq.select( root.get(TaskList_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<TaskListRele> listReleWithTaskAndListId(List<String> taskIds, String taskListId) throws Exception {
		if( ListTools.isEmpty( taskIds ) ){
			return null;
		}
		if( StringUtils.isEmpty( taskListId ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(TaskListRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskListRele> cq = cb.createQuery(TaskListRele.class);
		Root<TaskListRele> root = cq.from(TaskListRele.class);
		Predicate p = cb.equal( root.get(TaskListRele_.taskListId ), taskListId );
		p = cb.and( p, root.get(TaskListRele_.taskId ).in( taskIds ) );
		cq.orderBy( cb.asc( root.get( TaskListRele_.order ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
