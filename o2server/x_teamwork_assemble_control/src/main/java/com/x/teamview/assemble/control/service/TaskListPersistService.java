package com.x.teamview.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskListRele;

/**
 * 对工作任务信息查询的服务
 */
public class TaskListPersistService {

	private TaskListService taskListService = new TaskListService();
	
	/**
	 * 根据工作任务列表标识删除工作任务列表信息（逻辑删除，加上delete标识）
	 * @param id
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskList taskList = taskListService.get(emc, id);
			if( taskList.getCreatorPerson().equals( effectivePerson.getDistinguishedName() ) 
					|| taskList.getOwner().equals( effectivePerson.getDistinguishedName()  )) {
				taskListService.delete( emc, id );
			}else {
				throw new Exception("taskList delete permission denied.");
			}		
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *  根据工作任务列表标识删除工作任务列表信息（物理删除，包括所有的关联信息）
	 * @param id
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void remove( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskList taskList = taskListService.get(emc, id);
			if( taskList.getCreatorPerson().equals( effectivePerson.getDistinguishedName() ) 
					|| taskList.getOwner().equals( effectivePerson.getDistinguishedName()  )) {
				taskListService.remove( emc, id );
			}else {
				throw new Exception("taskList delete permission denied.");
			}		
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存一个工作任务列表信息
	 * @param taskList
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskList save( TaskList taskList, EffectivePerson effectivePerson ) throws Exception {
		if ( taskList == null) {
			throw new Exception( "taskList is null." );
		}
		taskList.setCreatorPerson( effectivePerson.getDistinguishedName() );
		if( StringUtils.isEmpty( taskList.getOwner() )) {
			taskList.setOwner( effectivePerson.getDistinguishedName() );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			taskList = taskListService.save( emc, taskList );
		} catch (Exception e) {
			throw e;
		}
		return taskList;
	}
	
	/**
	 * 将一个工作任务添加至指定的列表
	 * @param taskId
	 * @param listId
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskListRele addTaskToList( String taskId, String listId, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("taskId is empty.");
		}
		if ( StringUtils.isEmpty( listId )) {
			throw new Exception("listId is empty.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business( emc );
			TaskList taskList = emc.find( listId, TaskList.class );
			Task task = emc.find( taskId, Task.class );
			
			if( taskList == null ||  task == null ) {
				return null;
			}
			TaskListRele taskListRele =null;
			List<TaskListRele> releList = business.taskListFactory().listReleWithTaskAndList( taskId, listId );
			Integer maxOrder = business.taskListFactory().maxOrder( listId );
			if( ListTools.isEmpty( releList ) ) {
				taskListRele = new TaskListRele();
				taskListRele.setProject( task.getProject() );
				taskListRele.setTaskId(taskId);
				taskListRele.setTaskListId( listId );
				if( maxOrder == null ) {
					taskListRele.setOrder( 1 );
				}else {
					taskListRele.setOrder( maxOrder + 1 );
				}
				emc.beginTransaction( TaskListRele.class );
				emc.persist(taskListRele, CheckPersistType.all );
				emc.commit();
			}else {
				taskListRele = releList.get( 0 );
			}
			return taskListRele;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 将一个工作任务从指定的列表中移除
	 * @param taskId
	 * @param listId
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void removeTaskFromList( String taskId, String listId, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("taskId is empty.");
		}
		if ( StringUtils.isEmpty( listId )) {
			throw new Exception("listId is empty.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business( emc );
			List<TaskListRele> releList = business.taskListFactory().listReleWithTaskAndList( taskId, listId );
			if( ListTools.isEmpty( releList ) ) {
				emc.beginTransaction( TaskListRele.class );
				for( TaskListRele rele : releList ) {
					emc.remove(rele, CheckRemoveType.all );
				}
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 重新指定列表中所有的任务（删除原来的关联，添加新的关联）
	 * @param taskIds
	 * @param listId
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public List<TaskListRele> refreshTaskListRele( List<String> taskIds, String listId, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( listId )) {
			throw new Exception("listId is empty.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business( emc );
			TaskList taskList = emc.find( listId, TaskList.class );
			List<Task> tasks = business.taskFactory().list(taskIds);			
			if( taskList == null ) {
				return null;
			}
			List<TaskListRele> taskListReles = new ArrayList<>();
			List<String> oldReleIds = business.taskListFactory().listReleWithListId(listId);
			List<TaskListRele> oldListRele = business.taskListFactory().listRele( oldReleIds );
			
			emc.beginTransaction( TaskListRele.class );
			
			if( ListTools.isEmpty( oldListRele ) ) {
				for( TaskListRele rele : oldListRele ) {
					emc.remove(rele, CheckRemoveType.all );
				}
			}
			if( ListTools.isEmpty( tasks ) ) {
				TaskListRele taskListRele = null;
				int i = 0;
				for( Task task : tasks ) {
					i++;
					taskListRele = new TaskListRele();
					taskListRele.setProject( task.getProject() );
					taskListRele.setTaskId( task.getId() );
					taskListRele.setTaskListId( listId );
					taskListRele.setOrder( i );
					emc.persist(taskListRele, CheckPersistType.all );
					taskListReles.add( taskListRele );
				}
			}
			emc.commit();
			return taskListReles;
		} catch (Exception e) {
			throw e;
		}
	}
}
