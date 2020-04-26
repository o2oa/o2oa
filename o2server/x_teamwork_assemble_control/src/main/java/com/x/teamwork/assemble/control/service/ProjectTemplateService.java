package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectDetail;
import com.x.teamwork.core.entity.ProjectTemplate;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskGroupRele;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskListRele;
import com.x.teamwork.core.entity.TaskListTemplate;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

class ProjectTemplateService {

	/**
	 * 根据项目的标识查询项目的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected ProjectTemplate get(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.projectTemplateFactory().get( flag );
	}
	
	protected ProjectDetail getDetail(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().getDetail( id );
	}
	
	/**
	 * 根据过滤条件查询符合要求的项目信息列表
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param group  项目分组
	 * @param title
	 * @return
	 * @throws Exception
	 */
	protected List<Project> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listWithFilter(maxCount, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param emc
	 * @param maxCount
	 * @param sequnce
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param group   项目分组
	 * @param title
	 * @return
	 * @throws Exception
	 */
	protected List<Project> listWithFilter( EntityManagerContainer emc, Integer maxCount, String sequnce, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listWithFilter(maxCount, sequnce, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
	}

	/**
	 * 向数据库持久化项目信息
	 * @param emc
	 * @param projectDetail 
	 * @param project
	 * @return
	 * @throws Exception 
	 */
	protected ProjectTemplate save( EntityManagerContainer emc, ProjectTemplate object) throws Exception {
		ProjectTemplate projectTemplate = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( Project.createId() );
		}
		
		projectTemplate = emc.find( object.getId(), ProjectTemplate.class );
		
		emc.beginTransaction( Project.class );
		emc.beginTransaction( ProjectDetail.class );
		
		if( projectTemplate == null ){ // 保存一个新的对象
			projectTemplate = new ProjectTemplate();
			object.copyTo( projectTemplate );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				projectTemplate.setId( object.getId() );
			}
			emc.persist( projectTemplate, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( projectTemplate.getOwner() )) {
				object.setOwner( projectTemplate.getOwner() );
			}
			object.copyTo( projectTemplate, JpaObject.FieldsUnmodify  );
			emc.check( projectTemplate, CheckPersistType.all );	
		}
		emc.commit();
		return projectTemplate;
	}

	/**
	 * 根据项目模板标识删除项目模板信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete(EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		ProjectTemplate projectTemplate = emc.find( id, ProjectTemplate.class );
		if( projectTemplate != null ) {
			emc.beginTransaction( TaskListTemplate.class );
			emc.beginTransaction( ProjectTemplate.class );
			if( projectTemplate != null ) {
				//emc.remove( projectTemplate , CheckRemoveType.all );
				//改为软删除
				projectTemplate.setDeleted(true);
				emc.check( projectTemplate , CheckPersistType.all );
			}
			//还需要删除所有的TaskListTemplate
			List<TaskListTemplate> TaskListTemplates = business.taskListTemplateFactory().list(projectTemplate.getId());
			if( ListTools.isNotEmpty(TaskListTemplates)) {
				for( TaskListTemplate TaskListTemplate : TaskListTemplates ) {
					//emc.remove( task , CheckRemoveType.all );
					TaskListTemplate.setDeleted(true);
					emc.check( TaskListTemplate , CheckPersistType.all );
				}
			}
			emc.commit();
		}
	}
	/**
	 * 根据工作任务标识删除工作任务信息（物理删除）
	 * @param emc
	 * @param flag 主要是ID
	 * @throws Exception 
	 */
	public void remove( EntityManagerContainer emc, String flag ) throws Exception {
		emc.beginTransaction( Task.class );
		emc.beginTransaction( Review.class );
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
		
		Task task = emc.find( id, Task.class );
		TaskDetail taskDetail = emc.find( id, TaskDetail.class );
		List<Review> reviewList = null;
		List<List<String>> reviewIdBatchs = null;
		List<String> reviewIds = business.reviewFactory().listReviewByTask( id, 9999 );
		if( ListTools.isNotEmpty( reviewIds )) {
			reviewIdBatchs = ListTools.batch( reviewIds, 1000 );
		}
		if( ListTools.isNotEmpty( reviewIdBatchs )) {
			for( List<String> batch : reviewIdBatchs ) {
				reviewList = emc.list( Review.class, batch );
				if( ListTools.isNotEmpty( reviewList )) {
					for( Review review : reviewList ) {
						//emc.remove( review, CheckRemoveType.all );
						//改为软删除
						review.setDeleted(true);
						emc.check( review, CheckPersistType.all );
					}
				}
			}
		}
		if( task != null ) {
			//emc.remove( task , CheckRemoveType.all );
			//改为软删除
			task.setDeleted(true);
			emc.check( task, CheckPersistType.all );	
		}
		if( taskDetail != null ) {
			emc.remove( taskDetail , CheckRemoveType.all );
		}
	}
	
	/**
	 * 创建模板的工作任务列表
	 * @param emc
	 * @param person
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public void createDefaultTaskListForProjectTemplate( EntityManagerContainer emc, String person, ProjectTemplate template) throws Exception {		
		TaskListTemplate taskList = null;
		List<String> taskListsTemplate = template.getTaskList();
		
		if( !ListTools.isEmpty( taskListsTemplate )) {
			emc.beginTransaction( TaskList.class );
			for( String taskListTemplate: taskListsTemplate ) {
				taskList = composeTaskListObject( template.getId(), taskListTemplate, 1, "SYSTEM", person, "" );
				emc.persist( taskList, CheckPersistType.all );
			}
			
			emc.commit();
		}
	}
	
	private TaskListTemplate  composeTaskListObject( String projectTemplateId, String listName, int orderNum, String creatorName, String owner, String memo ) {
		TaskListTemplate taskTemplateList = new TaskListTemplate();		
		taskTemplateList.setId( TaskListTemplate.createId() );
		taskTemplateList.setName( listName );
		taskTemplateList.setProjectTemplate( projectTemplateId );
		taskTemplateList.setMemo( memo );
		
		taskTemplateList.setOrder( orderNum );
		taskTemplateList.setCreatorPerson( creatorName );
		taskTemplateList.setOwner( owner );
		taskTemplateList.setDeleted(false);
		return taskTemplateList;
	}

	/**
	 * 根据条件查询项目ID列表，最大查询2000条
	 * @param emc
	 * @param maxCount
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllViewableProjectIds(EntityManagerContainer emc, int maxCount, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listAllViewableProjectIds(maxCount, personName, identityNames, unitNames, groupNames, queryFilter);
	}

	public List<String> listAllProjectIds(EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listAllProjectIds();
	}	
}
