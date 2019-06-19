package com.x.teamview.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskListRele;
import com.x.teamwork.core.entity.TaskGroup;

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
			taskList = new TaskList();
			object.copyTo( taskList );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				taskList.setId( object.getId() );
			}			
			emc.persist( taskList, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( taskList.getCreatorPerson() )) {
				object.setCreatorPerson( taskList.getCreatorPerson() );
			}
			object.copyTo( taskList,  JpaObject.FieldsUnmodify  );
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
		List<String> listIds = business.taskListFactory().listReleWithListId( id );
		List<TaskListRele> lists = business.taskListFactory().listRele(listIds);
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
			taskList.setMemo( "系统任务列表" );
			taskList.setName( "未开始的任务" );
			taskList.setOrder( 1 );
			taskList.setCreatorPerson( "SYSTEM" );
			taskList.setOwner( person );
			taskList.setWorkStatus( "未开始" );
			taskList.setBindWorkStatus( true );
			emc.persist( taskList, CheckPersistType.all );
			taskLists.add( taskList );
			
			
			taskList = new TaskList();		
			taskList.setId( TaskList.createId() );
			taskList.setProject( taskGroup.getProject() );
			taskList.setTaskGroup( taskGroupId );
			taskList.setMemo( "系统任务列表" );
			taskList.setName( "进行中的任务" );
			taskList.setOrder( 2 );
			taskList.setCreatorPerson( "SYSTEM" );
			taskList.setOwner( person );
			taskList.setWorkStatus( "执行中" );
			taskList.setBindWorkStatus( true );
			emc.persist( taskList, CheckPersistType.all );
			taskLists.add( taskList );
			
			taskList = new TaskList();		
			taskList.setId( TaskList.createId() );
			taskList.setProject( taskGroup.getProject() );
			taskList.setTaskGroup( taskGroupId );
			taskList.setMemo( "系统任务列表" );
			taskList.setName( "已完成的任务" );
			taskList.setOrder( 3 );
			taskList.setCreatorPerson( "SYSTEM" );
			taskList.setOwner( person );
			taskList.setWorkStatus( "已完成" );
			taskList.setBindWorkStatus( true );
			emc.persist( taskList, CheckPersistType.all );
			taskLists.add( taskList );
			
			taskList = new TaskList();		
			taskList.setId( TaskList.createId() );
			taskList.setProject( taskGroup.getProject() );
			taskList.setTaskGroup( taskGroupId );
			taskList.setMemo( "系统任务列表" );
			taskList.setName( "已挂起的任务" );
			taskList.setOrder( 4 );
			taskList.setCreatorPerson( "SYSTEM" );
			taskList.setOwner( person );
			taskList.setWorkStatus( "已挂起" );
			taskList.setBindWorkStatus( true );
			emc.persist( taskList, CheckPersistType.all );
			taskLists.add( taskList );
			
			emc.commit();
		}
		return taskLists;
	}
}
