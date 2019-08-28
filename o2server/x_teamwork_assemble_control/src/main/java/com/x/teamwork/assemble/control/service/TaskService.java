package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskExtField;
import com.x.teamwork.core.entity.TaskGroupRele;
import com.x.teamwork.core.entity.TaskListRele;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.TaskTagRele;
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
	
	public List<Task> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids )) {
			return null;
		}
		Business business = new Business( emc );
		return business.taskFactory().list(ids);
	}
	/**
	 * 根据工作任务组ID和用户名称，查询用户可见的所有工作任务ID列表
	 * @param emc
	 * @param maxCount
	 * @param project
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listTaskIdsWithPermissionInProject( EntityManagerContainer emc, int maxCount, String project, String personName) throws Exception {
		if( StringUtils.isEmpty( project )) {
			return null;
		}
		if( StringUtils.isEmpty( personName )) {
			return null;
		}
		Business business = new Business( emc );
		return business.reviewFactory().listTaskIdsWithPersonAndProject( project, personName, maxCount);
	}
	
	protected Long countWithFilter(EntityManagerContainer emc, String personName, QueryFilter queryFilter) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().countWithFilter( personName, queryFilter);
	}
	
	public Long countWithTaskListId(EntityManagerContainer emc, String taskListId) throws Exception {
		Business business = new Business( emc );
		return business.taskListFactory().countTaskWithTaskListId( taskListId );
	}
	
	/**
	 * 根据条件查询符合条件的工作任务信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	protected List<Task> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, String personName, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		List<Review> reviewList = business.reviewFactory().listWithFilter( maxCount, orderField, orderType, personName, queryFilter);		
		return convertToTask( reviewList );
	}

	public List<Task> convertToTask(List<Review> reviewList) {
		List<Task> taskList = new ArrayList<>();
		if( ListTools.isNotEmpty( reviewList )) {
			for( Review review : reviewList ) {
				taskList.add( convertToTask(review) );
			}
		}
		return taskList;
	}

	public Task convertToTask( Review review ) {
		Task task = new Task();
		
		task.setId( review.getTaskId() );
		task.setName( review.getName() );
		task.setParent( review.getParent() );
		task.setProject( review.getProject() );
		task.setProjectName( review.getProjectName() );
		task.setPriority( review.getPriority() );
		task.setProgress( review.getProgress() );
		task.setRemindRelevance( review.getRemindRelevance());
		
		task.setClaimed( review.getClaimed() );
		task.setWorkStatus( review.getWorkStatus() );
		task.setOvertime( review.getOvertime() );
		task.setArchive( review.getArchive() );
		task.setCompleted( review.getCompleted() );
		task.setDeleted( review.getDeleted() );		
		
		task.setStartTime( review.getStartTime() );
		task.setEndTime( review.getEndTime() );
		
		task.setCreatorPerson( review.getCreatorPerson() );
		task.setExecutor( review.getExecutor() );
		task.setExecutorIdentity( review.getExecutorIdentity() );
		task.setExecutorUnit( review.getExecutorUnit() );
		
		task.setCreateTime( review.getCreateTime() );
		task.setSequence( review.getTaskSequence() );
		task.setOrder( review.getOrder() );		
		task.setUpdateTime( review.getUpdateTime() );		
		return task;
	}

	/**
	 * 向数据库持久化工作任务信息
	 * @param emc
	 * @param taskExtField 
	 * @param taskDetail 
	 * @param task
	 * @return
	 * @throws Exception 
	 */
	protected Task save( EntityManagerContainer emc, Task object, TaskDetail detail, TaskExtField extField ) throws Exception {
		Task task = null;
		TaskDetail taskDetail = null;
		TaskExtField taskExtField = null;
		Project project = null;
		
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( Task.createId() );
		}
		project = emc.find( object.getProject(), Project.class );
		task = emc.find( object.getId(), Task.class );
		taskDetail = emc.find( object.getId(), TaskDetail.class );
		taskExtField = emc.find( object.getId(), TaskExtField.class );
		
		emc.beginTransaction( Task.class );
		emc.beginTransaction( TaskDetail.class );
		emc.beginTransaction( TaskExtField.class );
		
		if( project  == null ) {
			throw new Exception("project not exsits!ID=" + object.getProject() );
		}
		
		//处理task的保存
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
		
		//处理taskDetail的保存		
		if( taskDetail == null ){ // 保存一个新的对象
			taskDetail = new TaskDetail();
			detail.copyTo( taskDetail );
			taskDetail.setId( object.getId() );
			taskDetail.setProject( project.getId() );
			emc.persist( taskDetail, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			detail.copyTo( taskDetail, JpaObject.FieldsUnmodify  );
			taskDetail.setId( object.getId() );
			taskDetail.setProject( project.getId() );
			emc.check( taskDetail, CheckPersistType.all );	
		}
		
		//处理taskExtField的保存		
		if( taskExtField == null ){ // 保存一个新的对象
			taskExtField = new TaskExtField();
			extField.copyTo( taskExtField );
			taskExtField.setId( object.getId() );
			taskExtField.setProject( project.getId() );
			emc.persist( taskExtField, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			extField.copyTo( taskExtField, JpaObject.FieldsUnmodify  );
			taskExtField.setId( object.getId() );
			taskExtField.setProject( project.getId() );
			emc.check( taskExtField, CheckPersistType.all );	
		}
		emc.commit();
		return task;
	}
	
	/**
	 * 根据工作任务标识删除工作任务信息（物理删除）
	 * @param emc
	 * @param flag 主要是ID
	 * @throws Exception 
	 */
	protected void remove( EntityManagerContainer emc, String flag ) throws Exception {
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
						emc.remove( review, CheckRemoveType.all );
					}
				}
			}
		}
		if( task != null ) {
			emc.remove( task , CheckRemoveType.all );
		}
		if( taskDetail != null ) {
			emc.remove( taskDetail , CheckRemoveType.all );
		}
	}

	public List<Task> listUnReviewTaskIds(EntityManagerContainer emc, int maxCount) throws Exception {
		Business business = new Business( emc );
		return business.taskFactory().listUnReviewTask(maxCount);
	}

	/**
	 * 更新工作任务标签，如果有新的标签，则添加到个人的项目标签集里
	 * @param emc
	 * @param taskId
	 * @param tags
	 * @param add_tags
	 * @param remove_tags
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public List<String> updateTag(EntityManagerContainer emc, String taskId, List<String> tags, List<String> add_tags, List<String> remove_tags, EffectivePerson effectivePerson ) throws Exception {
		Business business = new Business( emc );
		List<String> currentTags = new ArrayList<>();
		List<TaskTag> tagList = null;
		Task task = emc.find( taskId, Task.class );
		
		if( task != null ) {			
			//查询用户在该项目下所有的Tag列表
			tagList = business.taskTagFactory().listWithProjectAndPerson( task.getProject(), effectivePerson.getDistinguishedName() );
			if( ListTools.isNotEmpty( tagList )) {
				for( TaskTag tag : tagList ) {
					currentTags.add( tag.getTag() );
				}
			}
			
			//如果有新的标签，将新的标签添加到Tag表里
			if( ListTools.isNotEmpty( tags )) {
				emc.beginTransaction( TaskTag.class );
				for( String tag : tags ) {
					if( !currentTags.contains( tag )) {
						TaskTag taskTag = new TaskTag();
						taskTag.setOwner( effectivePerson.getDistinguishedName() );
						taskTag.setProject( task.getProject() );
						taskTag.setTag( tag );
						emc.persist( taskTag, CheckPersistType.all );
					}
				}
				emc.commit();
			}
			
			//修改用户对该任务的标签关联信息
			if( ListTools.isNotEmpty( add_tags )) {
				for( String tagName : add_tags ) {
					List<String> tagIds = business.taskTagFactory().listTagIdsWithTagNameAndProjectAndPerson(tagName, task.getProject(), effectivePerson.getDistinguishedName() );
					if( ListTools.isNotEmpty( tagIds )) {
						//根据tagId, taskId, person查询关联是否已经存在了
						List<String> tagReleIds = business.taskTagFactory().listTagReleIdsWithTagIdAndTaskAndPerson( tagIds.get(0), taskId, effectivePerson.getDistinguishedName() );
						if( ListTools.isEmpty( tagReleIds )) {
							//新增tag关联
							TaskTagRele taskTagRele = new TaskTagRele();
							taskTagRele.setId( TaskTagRele.createId() );
							taskTagRele.setOrder( 0 );
							taskTagRele.setOwner( effectivePerson.getDistinguishedName() );
							taskTagRele.setProject( task.getProject() );
							taskTagRele.setTaskId(taskId);
							taskTagRele.setTagId( tagIds.get(0) );
							emc.beginTransaction( TaskTagRele.class );
							emc.persist( taskTagRele, CheckPersistType.all );
							emc.commit();
						}
					}
				}
			}
			
			if( ListTools.isNotEmpty( remove_tags )) {
				for( String tagName : remove_tags ) {
					List<String> tagIds = business.taskTagFactory().listTagIdsWithTagNameAndProjectAndPerson( tagName, task.getProject(), effectivePerson.getDistinguishedName() );
					if( ListTools.isNotEmpty( tagIds )) {
						//根据tagId, taskId, person查询关联是否已经存在
						List<String> tagReleIds = business.taskTagFactory().listTagReleIdsWithTagIdAndTaskAndPerson( tagIds.get(0), taskId, effectivePerson.getDistinguishedName() );
						if( ListTools.isNotEmpty( tagReleIds )) {
							for( String releId : tagReleIds ) {
								TaskTagRele taskTagRele  = emc.find( releId, TaskTagRele.class );
								if( taskTagRele != null ) {
									emc.beginTransaction( TaskTagRele.class );
									emc.remove( taskTagRele, CheckRemoveType.all );
									emc.commit();
								}
							}
						}
					}
				}
			}
		}
		return tags;
	}

	public List<Task> allUnCompletedSubTasks(EntityManagerContainer emc, String taskId) throws Exception {
		Business business = new Business( emc );
		return business.taskFactory().allUnCompletedSubTasks(taskId);
	}

	public List<String> listAllTaskIdsWithProject(EntityManagerContainer emc, String project) throws Exception {
		Business business = new Business( emc );
		return business.taskFactory().listAllTaskIdsWithProject( project );
	}
}

