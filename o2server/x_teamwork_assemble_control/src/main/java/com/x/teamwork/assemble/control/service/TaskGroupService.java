package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskGroupRele;

/**
 * 对工作任务组查询信息的服务
 * 
 * @author O2LEE
 */
class TaskGroupService {

	protected List<TaskGroup> list(EntityManagerContainer emc, List<String> groupIds) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupFactory().list(groupIds);
	}
	
	/**
	 * 根据工作任务组ID查询工作任务组的信息
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	protected TaskGroup get( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupFactory().get( id );
	}

	/**
	 * 根据工作任务组ID删除工作任务组信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		TaskGroup taskGroup = emc.find( id, TaskGroup.class );
		emc.beginTransaction( TaskGroup.class );
		emc.beginTransaction( TaskGroupRele.class );
		//先删除所有的关联
		List<String> ids = business.taskGroupReleFactory().listByGroup( id );
		if( ListTools.isNotEmpty( ids )) {
			List<TaskGroupRele> reles = business.taskGroupReleFactory().list( ids );
			for( TaskGroupRele rele : reles ) {
				emc.remove( rele , CheckRemoveType.all );
			}
		}
		if( taskGroup != null ) {
			emc.remove( taskGroup , CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 向数据库持久化工作任务组信息
	 * @param emc
	 * @param taskGroup
	 * @return
	 * @throws Exception 
	 */
	protected TaskGroup save( EntityManagerContainer emc, TaskGroup object ) throws Exception {
		TaskGroup taskGroup = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( TaskGroup.createId() );
		}
		taskGroup = emc.find( object.getId(), TaskGroup.class );		
		emc.beginTransaction( TaskGroup.class );		
		if( taskGroup == null ){ // 保存一个新的对象
			taskGroup = new TaskGroup();
			object.copyTo( taskGroup );
			taskGroup.setId( object.getId() );
			emc.persist( taskGroup, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( taskGroup.getCreatorPerson() )) {
				object.setCreatorPerson( taskGroup.getCreatorPerson() );
			}
			object.copyTo( taskGroup, JpaObject.FieldsUnmodify  );
			emc.check( taskGroup, CheckPersistType.all );	
		}
		emc.commit();
		return taskGroup;
	}
	
	/**
	 * 将工作任务添加到工作任务组中去
	 * @param emc
	 * @param taskId
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	protected TaskGroupRele addTaskToGroup( EntityManagerContainer emc, String taskId, String groupId ) throws Exception {
		List<TaskGroupRele> reles = null;
		Business business = new Business( emc );
		TaskGroupRele taskGroupRele = null;
		TaskGroup  taskGroup = emc.find( groupId, TaskGroup.class );
		if( taskGroup != null ){
			reles = business.taskGroupReleFactory().listWithGroupAndTask( groupId, taskId );
			if( ListTools.isEmpty( reles )) { 
				emc.beginTransaction( TaskGroup.class );		
				taskGroupRele = new TaskGroupRele();
				taskGroupRele.setId( TaskGroupRele.createId() );
				taskGroupRele.setProject( taskGroup.getProject() );
				taskGroupRele.setTaskGroupId( groupId );
				taskGroupRele.setTaskId( taskId );
				emc.persist( taskGroupRele, CheckPersistType.all );	
				emc.commit();
			}
		}
		return taskGroupRele;
	}
	
	/**
	 *  将工作任务从工作任务组中除去
	 * @param emc
	 * @param taskId
	 * @param groupId
	 * @throws Exception
	 */
	protected void removeFromGroup( EntityManagerContainer emc, String taskId, String groupId ) throws Exception {
		Business business = new Business( emc );
		List<TaskGroupRele> reles = business.taskGroupReleFactory().listWithGroupAndTask( groupId, taskId );
		if( ListTools.isNotEmpty( reles )) {
			emc.beginTransaction( TaskGroup.class );
			for( TaskGroupRele rele : reles ) {
				emc.remove( rele, CheckRemoveType.all );	
			}
			emc.commit();
		}
	}

	/**
	 * 根据用户列示所有的工作任务组信息列表
	 * @param emc
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception
	 */
	protected List<TaskGroup> listGroupByPersonAndProject( EntityManagerContainer emc, String person, String project ) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupFactory().listGroupByPersonAndProject(person, project);
	}
	
	/**
	 * 根据用户列示所有的工作任务组信息ID列表
	 * @param emc
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception
	 */
	protected List<String> listGroupIdsByPersonAndProject( EntityManagerContainer emc, String person, String project ) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupFactory().listByPersonAndProject(person, project);
	}

	/**
	 * 根据工作任务组ID，查询工作任务组内所有的工作任务ID列表
	 * @param emc
	 * @param group
	 * @return
	 * @throws Exception
	 */
	protected List<String> listTaskIdByGroup(EntityManagerContainer emc, String group) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupReleFactory().listTaskIdByGroup(group);
	}

	/**
	 * 根据工作任务ID查询工作任务所有的工作任务组关联信息对象列表
	 * @param emc
	 * @param taskId
	 * @return
	 * @throws Exception 
	 */
	protected List<TaskGroupRele> listReleWithTask(EntityManagerContainer emc, String taskId ) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupReleFactory().listTaskReleWithTask(taskId);
	}
	
	/**
	 * 根据项目ID查询工作任务所有的工作任务组关联信息对象列表
	 * @param emc
	 * @param projectId
	 * @return
	 * @throws Exception 
	 */
	protected List<TaskGroupRele> listReleWithProject(EntityManagerContainer emc, String projectId ) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupReleFactory().listTaskReleWithProject(projectId);
	}

	/**
	 * 判断是否存在分组和工作任务的关联
	 * @param emc
	 * @param taskId
	 * @param groupId
	 * @return
	 * @throws Exception 
	 */
	protected List<TaskGroupRele> listReleWithTaskAndGroup(EntityManagerContainer emc, String taskId, String groupId) throws Exception {
		Business business = new Business( emc );
		return business.taskGroupReleFactory().listWithGroupAndTask( groupId, taskId);
	}

	/**
	 * 为用户在当前项目创建一个默认的工作任务组
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception 
	 */
	protected TaskGroup createDefaultTaskGroupForPerson(EntityManagerContainer emc, String person, String project, List<Task> taskList ) throws Exception {
		TaskGroup taskGroup = new TaskGroup();
		taskGroup.setId( TaskGroup.createId() );
		taskGroup.setName( "所有工作" );
		taskGroup.setProject(project);
		taskGroup.setOwner( person );
		taskGroup.setCreatorPerson( "System" );		
		taskGroup.setOrder(0);
		taskGroup.setMemo( "默认工作任务组" );
		emc.beginTransaction( TaskGroup.class );
		emc.persist( taskGroup, CheckPersistType.all );
		emc.commit(); 
		
		if( ListTools.isNotEmpty( taskList )) {
			TaskGroupRele taskGroupRele = null;
			emc.beginTransaction( TaskGroup.class );
			emc.beginTransaction( TaskGroupRele.class );
			for( Task task : taskList ) {
				//将工作与工作任务组关联起来
				taskGroupRele = new TaskGroupRele();
				taskGroupRele.setId( TaskGroupRele.createId() );
				taskGroupRele.setProject( project );
				taskGroupRele.setTaskId( task.getId() );
				taskGroupRele.setTaskGroupId( taskGroup.getId() );
				taskGroupRele.setOrder(0);
				emc.persist( taskGroupRele, CheckPersistType.all );
				
				taskGroup.addTaskTotal(1);
				if( task.getCompleted() ) {
					taskGroup.addCompletedTotal(1);
				}
				if( task.getOvertime() ) {
					taskGroup.addOvertimeTotal(1);
				}
			}
			emc.beginTransaction( TaskGroup.class );
			emc.check( taskGroup, CheckPersistType.all );
			emc.commit(); 
		}
		return taskGroup;
	}
	
	/**
	 * 根据工作任务组ID，重新计划任务组中所有任务的计数
	 * @param taskGroupId
	 * @return
	 * @throws Exception 
	 */
	protected TaskGroup refreshTaskCountInTaskGroup( EntityManagerContainer emc, String person, String taskGroupId ) throws Exception {
		Business business = new Business( emc );
		TaskGroup taskGroup = emc.find( taskGroupId, TaskGroup.class );
		List<String> taskListIds = null;
		List<String> taskIds = null;
		if( taskGroup == null ) {
			throw new Exception("taskGroup not exists. ID=" + taskGroupId );
		}
		if( "所有工作".equals( taskGroup.getName()) && "System".equalsIgnoreCase( taskGroup.getCreatorPerson() )) {
			//工作的任务组，查询有权限看到的所有工作
			taskIds = business.reviewFactory().listTaskIdsWithPersonAndProject( taskGroup.getProject(), person, 2000 );
		}else {
			taskListIds = business.taskListFactory().listTaskListIdsWithTaskGroup(taskGroupId);
			taskIds = business.taskListFactory().listTaskIdWithTaskListId( taskListIds );
		}
		
		taskGroup.setTaskTotal( 0 );
		taskGroup.setCompletedTotal( 0 );
		taskGroup.setOvertimeTotal( 0 );
		if( ListTools.isNotEmpty(  taskIds )) {
			List<Task> taskList = business.taskFactory().list( taskIds );
			if( ListTools.isNotEmpty( taskList )) {
				for( Task task : taskList ) {
					taskGroup.addTaskTotal(1);
					if( task.getCompleted() ) {
						taskGroup.addCompletedTotal(1);
					}
					if( task.getOvertime() ) {
						taskGroup.addOvertimeTotal(1);
					}
				}
			}
		}
		emc.beginTransaction( TaskGroup.class );
		emc.check( taskGroup, CheckPersistType.all );
		emc.commit(); 
		return taskGroup;
	}
	
	/**
	 * 重新计算工作任务组关联的任务数量
	 * @param releTaskGroupIds
	 * @throws Exception 
	 */
	public void refreshTaskCountInTaskGroup(EntityManagerContainer emc, String person, List<String> releTaskGroupIds) throws Exception {
		if( ListTools.isNotEmpty( releTaskGroupIds )) {
			for( String releTaskGroupId :  releTaskGroupIds ) {
				refreshTaskCountInTaskGroup( emc, person, releTaskGroupId );
			}
		}
	}

	/**
	 * 根据工作任务ID，刷新该工作涉及到的所有的工作任务组的工作任务数量
	 * @param emc
	 * @param taskId
	 * @throws Exception
	 */
	public void refreshTaskCountInTaskGroupWithTaskId(EntityManagerContainer emc, String person, String taskId) throws Exception {
		Business business = new Business( emc );
		List<String> taskGroupIds = business.taskListFactory().listGroupIdsWithTask(taskId);
		refreshTaskCountInTaskGroup(emc, person, taskGroupIds );
	}
}
