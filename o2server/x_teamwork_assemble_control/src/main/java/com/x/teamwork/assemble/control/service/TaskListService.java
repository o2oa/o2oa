package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskListRele;

class TaskListService {

	/**
	 * 根据工作任务列表的标识查询工作任务列表的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected TaskList get(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.taskListFactory().get( flag );
	}
	
	/**
	 * 根据用户和项目ID查询工作任务列表
	 * @param emc
	 * @param person
	 * @return
	 * @throws Exception
	 */
	protected List<TaskList> listWithProject( EntityManagerContainer emc,  String person, String projectId ) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().listWithPersonAndProject( person, projectId );
	}
	
	/**
	 * 根据工作任务组ID查询工作任务列表
	 * @param emc
	 * @param taskGroupId
	 * @return
	 * @throws Exception
	 */
	protected List<TaskList> listWithTaskGroup( EntityManagerContainer emc, String taskGroupId ) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().listWithTaskGroup( taskGroupId );
	}

	public List<String> listTaskIdsWithTaskListId( EntityManagerContainer emc, String taskListId) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().listTaskIdWithTaskListId(taskListId);
	}
	
	public List<String> listTaskIdsWithTaskListIds( EntityManagerContainer emc, List<String> taskListIds) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().listTaskIdWithTaskListIds(taskListIds);
	}
	
	public List<TaskListRele> listTaskListWithTask(EntityManagerContainer emc, String taskId, String taskGroupId ) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().listTaskListWithTask( taskId, taskGroupId );
	}
	
	public List<String> listTaskListIdsWithGroup(EntityManagerContainer emc, String taskGroupId, String person) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().listTaskListIdsWithGroup( taskGroupId, person );
	}	
	
	public List<TaskListRele> listReleWithTaskAndListId(EntityManagerContainer emc, List<String> taskIds, String taskListId) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().listReleWithTaskAndListId( taskIds, taskListId );
	}
	
	public Long countTaskWithTaskListId(EntityManagerContainer emc, String taskListId) throws Exception {
		Business business = new Business( emc );	
		return business.taskListFactory().countTaskWithTaskListId( taskListId );
	}
	
	public Long countTaskWithOutoTaskListInGroup(EntityManagerContainer emc, String personName, String taskGroupId) throws Exception {
		Business business = new Business(emc);
		TaskGroup taskGroup = emc.find( taskGroupId, TaskGroup.class );
		//查询所有未归类的任务列表
		List<String> taskIds_all = new ArrayList<>();
		List<String> taskIds_allViewable = null;
		List<String> taskIds_forGroup = null;
		List<String> taskListIds_forGroup = null;
		
		if( taskGroup == null ) {
			return 0L;
		}
		
		//查询在指定项目里所有可见的工作任务列表
		taskIds_allViewable = business.reviewFactory().listTaskIdsWithPersonAndProject( personName,  taskGroup.getProject() );
		if( ListTools.isNotEmpty( taskIds_allViewable ) ) {
			for( String str : taskIds_allViewable ) {
				//先全部添加到输出的工作任务ID里，然后将所有已经归过类的任务排除
				taskIds_all.add( str );
			}
		}
		
		//查询用户在该taskGroup里所有已经有关联的任务ID列表
		taskListIds_forGroup = business.taskListFactory().listTaskListIdsWithGroup( taskGroupId, personName );
		taskIds_forGroup = business.taskListFactory().listTaskIdsWithTaskGroupId( taskListIds_forGroup );
		if( ListTools.isNotEmpty( taskIds_forGroup )) {
			taskIds_all.removeAll( taskIds_forGroup );
		}
		
		return Long.parseLong( taskIds_all.size()+"" );
	}
	
	/**
	 * 向数据库持久化工作任务列表列表信息
	 * @param emc
	 * @param taskList
	 * @return
	 * @throws Exception 
	 */
	protected TaskList save( EntityManagerContainer emc, TaskList object ) throws Exception {
		TaskList taskList = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( TaskList.createId() );
		}
		
		taskList = emc.find( object.getId(), TaskList.class );
		
		emc.beginTransaction( TaskList.class );		
		if( taskList == null ){ // 保存一个新的对象
			TaskGroup taskGroup = emc.find( object.getTaskGroup(), TaskGroup.class );
			if( taskGroup == null ) {
				throw new Exception("TaskGroup not exists!ID:" + object.getTaskGroup() );
			}
			object.setProject( taskGroup.getProject() );
			
			taskList = new TaskList();
			object.copyTo( taskList );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				taskList.setId( object.getId() );
			}			
			emc.persist( taskList, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			TaskGroup taskGroup = emc.find( taskList.getTaskGroup(), TaskGroup.class );
			if( taskGroup == null ) {
				throw new Exception("TaskGroup not exists!ID:" + object.getTaskGroup() );
			}
			taskList.setMemo( object.getMemo() );
			if( StringUtils.isNotEmpty( object.getName() )) {
				taskList.setName( object.getName() );
			}
			if( object.getOrder() != null ) {
				taskList.setOrder( object.getOrder() );
			}			
			emc.check( taskList, CheckPersistType.all );	
		}
		emc.commit();
		
		return taskList;
	}
	
	/**
	 * 根据工作任务列表标识删除工作任务列表信息（逻辑删除，加上delete标识）
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		emc.beginTransaction( TaskList.class );
		TaskList taskList = emc.find( id, TaskList.class );
		if( taskList != null ) {
			taskList.setDeleted( true );
			emc.check( taskList , CheckPersistType.all );
		}
		emc.commit();
	}
	
	/**
	 * 根据工作任务列表标识删除工作任务列表信息（物理删除）
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void remove( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		emc.beginTransaction( TaskList.class );
		emc.beginTransaction( TaskListRele.class );
		TaskList taskList = emc.find( id, TaskList.class );
		//还需删除TaskList所有的关联信息
		List<TaskListRele> lists = business.taskListFactory().listReleWithListId( id );
		if( ListTools.isNotEmpty( lists )) {
			for( TaskListRele listRele: lists ) {
				emc.remove( listRele , CheckRemoveType.all );
			}
		}
		if( taskList != null ) {
			emc.remove( taskList , CheckRemoveType.all );
		}
		emc.commit();
	}
	
	/**
	 * 创建默认的工作任务列表
	 * @param emc
	 * @param person
	 * @param taskGroupId
	 * @return
	 * @throws Exception
	 */
	public List<TaskList> createDefaultTaskListForTaskGroup( EntityManagerContainer emc, String person, String taskGroupId) throws Exception {
		List<TaskList> taskLists = new ArrayList<>();		
		TaskGroup taskGroup = emc.find( taskGroupId, TaskGroup.class );
		
		TaskList taskList = null;
		if( taskGroup != null ) {
			emc.beginTransaction( TaskList.class );
			
			taskList = new TaskList();		
			taskList.setId( TaskList.createId() );
			taskList.setProject( taskGroup.getProject() );
			taskList.setTaskGroup( taskGroupId );
			taskList.setMemo( "" );
			taskList.setName( "任务列表1" );
			taskList.setOrder( 1 );
			taskList.setCreatorPerson( "SYSTEM" );
			taskList.setOwner( person );
			emc.persist( taskList, CheckPersistType.all );
			taskLists.add( taskList );
			
			taskList = new TaskList();		
			taskList.setId( TaskList.createId() );
			taskList.setProject( taskGroup.getProject() );
			taskList.setTaskGroup( taskGroupId );
			taskList.setMemo( "" );
			taskList.setName( "任务列表2" );
			taskList.setOrder( 2 );
			taskList.setCreatorPerson( "SYSTEM" );
			taskList.setOwner( person );
			emc.persist( taskList, CheckPersistType.all );
			taskLists.add( taskList );
			
			emc.commit();
		}
		return taskLists;
	}
}
