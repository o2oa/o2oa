package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.TaskTag;

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
	
	/**
	 * 根据项目和人员列示的项目标签信息
	 * @param emc
	 * @param project
	 * @param person
	 * @return
	 * @throws Exception
	 */
	protected List<TaskTag> listWithProjectAndPerson( EntityManagerContainer emc, String project, String person ) throws Exception {
		Business business = new Business( emc );
		return business.taskTagFactory().listWithProjectAndPerson(project, person);
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
}
