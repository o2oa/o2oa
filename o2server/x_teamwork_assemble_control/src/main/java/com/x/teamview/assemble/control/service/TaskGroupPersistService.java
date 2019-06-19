package com.x.teamview.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskGroupRele;

public class TaskGroupPersistService {

	private TaskGroupService taskGroupService = new TaskGroupService();
	
	/**
	 * 删除工作任务组信息
	 * @param flag
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskGroupService.delete( emc, id );		
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存工作任务组信息
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskGroup save( TaskGroup object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			object.setCreatorPerson( effectivePerson.getDistinguishedName() );
			object.setOwner( effectivePerson.getDistinguishedName() );
			object = taskGroupService.save( emc, object );
		} catch (Exception e) {
			throw e;
		}
		return object;
	}
	
	/**
	 * 将工作任务添加到工作任务组中去
	 * @param taskId
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public TaskGroupRele addTaskToGroup( String taskId, String groupId ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("taskId is empty.");
		}
		if ( StringUtils.isEmpty( groupId )) {
			throw new Exception("groupId is empty.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskGroupService.addTaskToGroup(emc, taskId, groupId);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *  将工作任务从工作任务组中除去
	 * @param emc
	 * @param taskId
	 * @param groupId
	 * @throws Exception
	 */
	public void removeFromGroup( String taskId, String groupId ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("taskId is empty.");
		}
		if ( StringUtils.isEmpty( groupId )) {
			throw new Exception("groupId is empty.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskGroupService.removeFromGroup( emc, groupId, taskId );
		} catch (Exception e) {
			throw e;
		}
	}
}
