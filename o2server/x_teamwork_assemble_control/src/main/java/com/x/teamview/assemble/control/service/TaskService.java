package com.x.teamview.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskGroupRele;
import com.x.teamwork.core.entity.TaskListRele;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

class TaskService {

	/**
	 * 根据工作任务的标识查询工作任务的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected Task get(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.taskFactory().get( flag );
	}
	
	/**
	 * 根据权限查询工作任务信息列表
	 * @param emc
	 * @param maxCount
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	protected List<Task> listWithPermission( EntityManagerContainer emc, Integer maxCount, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames ) throws Exception {
		Business business = new Business( emc );	
		return business.taskFactory().listWithPermission(maxCount, personName, identityNames, unitNames, groupNames );
	}
	
	/**
	 * 根据过滤条件查询符合要求的工作任务信息数量
	 * @param emc
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	protected Long countWithFilter( EntityManagerContainer emc, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.taskFactory().countWithFilter( personName, identityNames, unitNames, groupNames, queryFilter );
	}
	
	/**
	 * 根据过滤条件查询符合要求的工作任务信息列表
	 * @param emc
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
	protected List<Task> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );	
		return business.taskFactory().listWithFilter(maxCount, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
	}
	
	/**
	 * 根据条件查询符合条件的工作任务信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param emc
	 * @param maxCount
	 * @param sequnce
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
	protected List<Task> listWithFilter( EntityManagerContainer emc, Integer maxCount, String sequnce, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.taskFactory().listWithFilter(maxCount, sequnce, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
	}

	/**
	 * 向数据库持久化工作任务信息
	 * @param emc
	 * @param taskDetail 
	 * @param task
	 * @return
	 * @throws Exception 
	 */
	protected Task save( EntityManagerContainer emc, Task object, TaskDetail detail ) throws Exception {
		Task task = null;
		TaskDetail taskDetail = null;
		Project project = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( Task.createId() );
		}
		project = emc.find( object.getProject(), Project.class );
		task = emc.find( object.getId(), Task.class );
		taskDetail = emc.find( object.getId(), TaskDetail.class );
		
		emc.beginTransaction( Project.class );
		emc.beginTransaction( Task.class );
		emc.beginTransaction( TaskDetail.class );
		
		object.setProject( project.getId() );
		object.setProjectName( project.getTitle() );
		
		if( task == null ){ // 保存一个新的对象
			task = new Task();
			object.copyTo( task );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				task.setId( object.getId() );
			}			
			emc.persist( task, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( task.getCreatorPerson() )) {
				object.setCreatorPerson( task.getCreatorPerson() );
			}
			object.copyTo( task, JpaObject.FieldsUnmodify  );
			emc.check( task, CheckPersistType.all );	
		}
		
		if( taskDetail == null ){ // 保存一个新的对象
			taskDetail = new TaskDetail();
			detail.copyTo( taskDetail );
			detail.setId( object.getId() );
			emc.persist( task, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			detail.copyTo( taskDetail, JpaObject.FieldsUnmodify  );
			detail.setId( object.getId() );
			emc.check( task, CheckPersistType.all );	
		}
		
		project.addParticipantPerson( task.getCreatorPerson() );
		project.addParticipantPerson( task.getExecutor() );
		project.addParticipantPerson( task.getParticipantPersonList() );
		project.addParticipantIdentity( task.getParticipantIdentityList() );
		project.addParticipantUnit(task.getParticipantUnitList() );
		project.addParticipantGroup( task.getParticipantGroupList() );
		emc.commit();
		
		return task;
	}

	/**
	 * 根据工作任务标识删除工作任务信息（逻辑删除，加上delete标识）
	 * @param emc
	 * @param flag 主要是ID
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String flag ) throws Exception {
		emc.beginTransaction( Task.class );
		deleteTaskWithChildren( emc, flag);		
		emc.commit();
	}
	
	/**
	 * 根据工作任务标识删除工作任务信息（物理删除）
	 * @param emc
	 * @param flag 主要是ID
	 * @throws Exception 
	 */
	protected void remove( EntityManagerContainer emc, String flag ) throws Exception {
		emc.beginTransaction( Task.class );
		emc.beginTransaction( TaskDetail.class );
		emc.beginTransaction( TaskListRele.class );
		emc.beginTransaction( TaskGroupRele.class );
		removeTaskWithChildren( emc, flag);		
		emc.commit();
	}
	
	/**
	 * 根据工作任务标识删除工作任务信息( 物理删除 )
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	private void removeTaskWithChildren( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		Task task = emc.find( id, Task.class );
		TaskDetail taskDetail = emc.find( id, TaskDetail.class );
		
		//还需要递归删除所有的下级Task
		List<String> childrenIds = business.taskFactory().listByParent( id );
		if( ListTools.isNotEmpty( childrenIds )) {
			for( String _id : childrenIds ) {
				removeTaskWithChildren( emc, _id );
			}
		}
		
		//任务列表中的关联信息
		List<TaskListRele> listReles = business.taskListFactory().listReleWithTask(  id );
		if( ListTools.isNotEmpty( listReles )) {
			for( TaskListRele taskListRele : listReles ) {
				emc.remove( taskListRele , CheckRemoveType.all );
			}
		}
		
		//删除任务组关联信息
		List<TaskGroupRele> groupReles = business.taskGroupReleFactory().listTaskReleWithTask( id );
		if( ListTools.isNotEmpty( groupReles )) {
			for( TaskGroupRele taskGroupRele : groupReles ) {
				emc.remove( taskGroupRele , CheckRemoveType.all );
			}
		}
		
		if( task != null ) {
			emc.remove( task , CheckRemoveType.all );
		}
		if( taskDetail != null ) {
			emc.remove( taskDetail , CheckRemoveType.all );
		}
	}
	
	/**
	 * 根据工作任务标识删除工作任务信息（逻辑删除，加上delete标识）
	 * @param emc
	 * @param flag 主要是ID
	 * @throws Exception 
	 */
	private void deleteTaskWithChildren( EntityManagerContainer emc, String flag ) throws Exception {
		Business business = new Business( emc );
		Task task = emc.find( flag, Task.class );
		
		//还需要递归删除所有的下级Task
		List<String> childrenIds = business.taskFactory().listByParent( flag );
		if( ListTools.isNotEmpty( childrenIds )) {
			for( String id : childrenIds ) {
				deleteTaskWithChildren( emc, id );
			}
		}
		if( task != null ) {
			task.setDeleted( true );
		}
		emc.check( task , CheckPersistType.all );
	}
}
