package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.TaskTagRele;

/**
 * 对项目标签信息查询的服务
 * 
 * @author O2LEE
 */
class TaskTagService {

	/**
	 * 根据项目的id查询项目的信息
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	protected TaskTag get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, TaskTag.class );
	}	

	public List<String> listTagIdsWithTask(EntityManagerContainer emc, String taskId, String person) throws Exception {
		Business business = new Business( emc );
		return business.taskTagFactory().listTagIdsWithTask(taskId, person);
	}
	
	/**
	 * 向数据库持久化动态信息
	 * @param emc
	 * @param taskTag
	 * @return
	 * @throws Exception 
	 */
	protected TaskTag save( EntityManagerContainer emc, TaskTag object ) throws Exception {
		TaskTag taskTag = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( TaskTag.createId() );
		}
		taskTag = emc.find( object.getId(), TaskTag.class );
		emc.beginTransaction( TaskTag.class );
		if( taskTag == null ){ // 保存一个新的对象
			taskTag = new TaskTag();
			object.copyTo( taskTag );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				taskTag.setId( object.getId() );
			}
			emc.persist( taskTag, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			object.copyTo( taskTag, JpaObject.FieldsUnmodify  );
			emc.check( taskTag, CheckPersistType.all );	
		}
		emc.commit();
		return taskTag;
	}

	/**
	 * 根据项目标签删除项目信息
	 * @param emc
	 * @param ID
	 * @throws Exception 
	 */
	protected void delete(EntityManagerContainer emc, String id) throws Exception {
		TaskTag taskTag = emc.find( id, TaskTag.class );
		if( taskTag != null ) {
			emc.beginTransaction( TaskTag.class );
			emc.remove( taskTag , CheckRemoveType.all );
			emc.commit();
		}
	}
	
	/**
	 * 添加任务标签
	 * @param emc
	 * @param task
	 * @param taskTag
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public TaskTagRele addTagRele(EntityManagerContainer emc, Task task, TaskTag taskTag, String personName ) throws Exception {
		Business business = new Business(emc);
		List<String>  ids = business.taskTagFactory().listTagReleIdsWithTagIdAndTaskAndPerson( taskTag.getId(), task.getId(), personName);
		if( ListTools.isEmpty( ids )) {
			//添加一个关联信息
			TaskTagRele taskTagRele = new TaskTagRele();
			taskTagRele.setId( TaskTagRele.createId() );
			taskTagRele.setOwner(personName);
			taskTagRele.setOrder( 0 );
			taskTagRele.setProject( task.getProject() );
			taskTagRele.setTagId( taskTag.getId() );
			taskTagRele.setTaskId( task.getId() );			
			emc.beginTransaction( TaskTagRele.class );
			emc.persist( taskTagRele, CheckPersistType.all );
			emc.commit();
			return taskTagRele;
		}
		return null;
	}
	
	/**
	 * 删除任务标签关联
	 * @param emc
	 * @param taskId
	 * @param taskTagId
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> removeTagRele(EntityManagerContainer emc, String taskId, String taskTagId, String personName ) throws Exception {
		Business business = new Business(emc);
		List<String>  ids = business.taskTagFactory().listTagReleIdsWithTagIdAndTaskAndPerson( taskTagId, taskId, personName);
		List<String> delIds = new ArrayList<>();;
		if( ListTools.isNotEmpty( ids )) {
			List<TaskTagRele>  reles = emc.list( TaskTagRele.class, ids);
			if( ListTools.isNotEmpty( reles )) {
				emc.beginTransaction( TaskTagRele.class );
				for( TaskTagRele rele :  reles ) {
					delIds.add( rele.getId() );
					emc.remove( rele, CheckRemoveType.all );
				}
				emc.commit();
			}
		}
		return delIds;
	}

	protected List<TaskTag> listWithProjectAndPerson( EntityManagerContainer emc, String project, String person ) throws Exception {
		Business business = new Business( emc );
		return business.taskTagFactory().listWithProjectAndPerson(project, person);
	}
	
	protected List<TaskTagRele> listReleWithProjectAndPerson( EntityManagerContainer emc, String project, String person ) throws Exception {
		Business business = new Business( emc );
		return business.taskTagFactory().listReleWithProjectAndPerson(project, person);
	}
	
	public List<TaskTagRele> listReleWithTaskAndPerson(EntityManagerContainer emc, String taskId, String person) throws Exception {
		Business business = new Business( emc );
		return business.taskTagFactory().listReleWithTaskAndPerson( taskId, person);
	}

	public List<String> listTagIdsWithContent(EntityManagerContainer emc, String tagName, String project, String personName ) throws Exception {
		Business business = new Business( emc );
		return business.taskTagFactory().listTagIdsWithTagNameAndProjectAndPerson( tagName, project, personName);
	}

	public List<String> listTaskIdsWithReleTagIds(EntityManagerContainer emc, List<String> tagIds) throws Exception {
		Business business = new Business( emc );
		return business.taskTagFactory().listTaskIdsWithReleTagIds( tagIds );
	}
}
