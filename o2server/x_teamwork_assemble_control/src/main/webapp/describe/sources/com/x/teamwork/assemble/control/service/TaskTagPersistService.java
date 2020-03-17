package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.TaskTagRele;

public class TaskTagPersistService {

	private TaskTagService taskTagService = new TaskTagService();
	
	/**
	 * 删除工作任务标签信息
	 * @param flag
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String flag, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskTagService.delete( emc, flag );	
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存工作任务标签信息
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskTag save( TaskTag  object,  EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		
		TaskTag result = null;
		object.setOwner( effectivePerson.getDistinguishedName() );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			result = taskTagService.save( emc, object );
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	public TaskTagRele addTagRele( Task task, TaskTag taskTag,  EffectivePerson effectivePerson ) throws Exception {
		if ( task == null) {
			throw new Exception("task is null.");
		}
		if ( taskTag == null ) {
			throw new Exception("taskTag is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			 return taskTagService.addTagRele( emc, task,  taskTag, effectivePerson.getDistinguishedName() );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> removeTagRele( String taskId, String taskTagId,  EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("task is null.");
		}
		if ( StringUtils.isEmpty( taskTagId )) {
			throw new Exception("taskTag is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			 return taskTagService.removeTagRele( emc, taskId,  taskTagId, effectivePerson.getDistinguishedName() );
		} catch (Exception e) {
			throw e;
		}
	}
}
