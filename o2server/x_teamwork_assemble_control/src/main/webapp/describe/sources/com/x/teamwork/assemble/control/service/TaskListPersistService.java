package com.x.teamwork.assemble.control.service;

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
	private TaskGroupService taskGroupService = new TaskGroupService();
	
	/**
	 *  根据工作任务列表标识删除工作任务列表信息（物理删除，包括所有的关联信息）
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
	 * @param listIds
	 * @param orderNumber
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskListRele addTaskToTaskListWithOrderNumber( String taskId, List<String> listIds, Integer orderNumber, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("taskId is empty.");
		}
		if ( ListTools.isNotEmpty( listIds )) {
			List<TaskList> taskLists = null;
			TaskListRele taskListRele =null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				if( ListTools.isNotEmpty( listIds )) {
					taskLists = emc.list( TaskList.class, listIds );
				}
				//逐个添加任务与列表的关联
				if( ListTools.isNotEmpty( taskLists )) {
					for( TaskList taskList : taskLists ) {
						addTaskToTaskListWithOrderNumber( taskId, taskList.getId(), orderNumber, effectivePerson );
					}
				}
				return taskListRele;
			} catch (Exception e) {
				throw e;
			}
		}
		return null;
	}
	
	/**
	 * 将一个工作任务添加至指定的列表
	 * @param taskId
	 * @param listIds
	 * @param behindTaskId
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public TaskListRele addTaskToTaskListWithBehindTask( String taskId, List<String> listIds, String behindTaskId, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("taskId is empty.");
		}
		if ( ListTools.isEmpty( listIds )) {
			throw new Exception("listIds is empty.");
		}
		List<TaskList> taskLists = null;
		TaskListRele taskListRele =null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			if( ListTools.isNotEmpty( listIds )) {
				taskLists = emc.list( TaskList.class, listIds );
			}
			//逐个添加任务与列表的关联
			if( ListTools.isNotEmpty( taskLists )) {
				for( TaskList taskList : taskLists ) {
					addTaskToTaskListWithBehindTask( taskId, taskList.getId(), behindTaskId, effectivePerson );
				}
			}
			return taskListRele;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 将一个工作任务添加至指定的列表
	 * @param taskId
	 * @param listId
	 * @param orderNumber
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public void addTaskToTaskListWithOrderNumber( String taskId, String listId, Integer orderNumber, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			return;
		}
		if ( StringUtils.isEmpty( listId )) {
			return;
		}
		TaskList taskList = null;
		String taskListId_old = null;
		String taskGroupId = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskList = emc.find( listId, TaskList.class );
			if( taskList != null ) {
				taskGroupId = taskList.getTaskGroup();				
				//查询工作任务在当前的taskList所在的group下的taskList关联，有可能未归类，可能为空的
				taskListId_old = getTaskListWithTaskAndGroup( emc, taskId, taskGroupId );						

				//判断一下，TaskList是否需要改变，也许只是调整在同一个列表中的位置而已
				 if( taskListId_old != null && taskListId_old.equalsIgnoreCase( listId )) {
					//不需要创建关联，只是调整序号
					orderTaskInTaskList( emc, taskId, listId, orderNumber );					
				}else {
					//需要更改列表，先删除原来的关联, 再创建新的关联
					//taskListId_old为空的情况不更改
					moveTaskFromOldListToNewList( emc, taskId, taskListId_old, listId );
					orderTaskInTaskList( emc, taskId, listId, orderNumber );
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 将一个工作任务添加至指定的列表
	 * @param taskId
	 * @param listId
	 * @param behindTaskId
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void addTaskToTaskListWithBehindTask( String taskId, String listId, String behindTaskId, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			return;
		}
		if ( StringUtils.isEmpty( listId )) {
			return;
		}
		TaskList taskList = null;
		String taskListId_old = null;
		String taskGroupId = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskList = emc.find( listId, TaskList.class );
			if( taskList != null ) {
				taskGroupId = taskList.getTaskGroup();				
				//查询工作任务在当前的taskList所在的group下的taskList关联，有可能未归类，可能为空的
				taskListId_old = getTaskListWithTaskAndGroup( emc, taskId, taskGroupId );						

				//判断一下，TaskList是否需要改变，也许只是调整在同一个列表中的位置而已
				 if( taskListId_old != null && taskListId_old.equalsIgnoreCase( listId )) {
					//不需要创建关联，只是调整序号
					orderTaskInTaskList( emc, taskId, listId, behindTaskId );					
				}else {
					//需要更改列表，先删除原来的关联, 再创建新的关联
					//taskListId_old为空的情况不更改
					moveTaskFromOldListToNewList( emc, taskId, taskListId_old, listId );
					orderTaskInTaskList( emc, taskId, listId, behindTaskId );
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 对工作任务列表里的任务的排序进行整理，处理任务插队的逻辑
	 * @param emc
	 * @param taskId
	 * @param listId
	 * @param newOrder
	 * @throws Exception 
	 */
	private void orderTaskInTaskList(EntityManagerContainer emc, String taskId, String listId, Integer newOrder) throws Exception {
		Business business = new Business( emc );
		//查询当前任务在当前列表里的关联信息
		List<TaskListRele> reles = business.taskListFactory().listReleWithTaskAndList( taskId, listId );
		Integer  maxOrder = business.taskListFactory().maxOrder( listId );
		if( ListTools.isNotEmpty( reles) && reles.get(0).getOrder() == newOrder ) {//排序号未改变
			return;
		}
		if( newOrder == null || newOrder == 0 || newOrder == -1 ) {			
			if( ListTools.isNotEmpty( reles) && maxOrder == reles.get(0).getOrder() ) {//但是本来就是最后一个，排序号不变
				return;
			}			
			//未设置排序号，放到最后一个就行了，不需要插入到列表中间，不改变其他任务的序号
			if( ListTools.isNotEmpty( reles )) {
				emc.beginTransaction( TaskListRele.class );
				for( TaskListRele rele : reles ) {
					rele.setOrder( maxOrder + 1 );
					emc.check( rele, CheckPersistType.all );
				}
				emc.commit();
			}
		}else {
			if( newOrder > maxOrder ) {//排序号最大只能是已有最大排序号 + 1 
				newOrder = maxOrder + 1;
			}
			TaskListRele taskListRele = null;
			Integer addOrder = 0;
			//按order排序，取出所有的关联信息
			reles = business.taskListFactory().listReleWithListId( listId );
			if( ListTools.isNotEmpty( reles )) {
				emc.beginTransaction( TaskListRele.class );
				for( int i=0; i<reles.size(); i++ ) {
					taskListRele = reles.get(i);
					taskListRele.setOrder( i + 1 + addOrder  );
					if( i == newOrder -1 ) {
						addOrder = 1;
					}
					if( taskListRele.getTaskId().equalsIgnoreCase( taskId )) {
						taskListRele.setOrder( newOrder );
					}
					emc.check( taskListRele, CheckPersistType.all );
				}
				emc.commit();
			}
		}
	}
	
	/**
	 * 对工作任务列表里的任务的排序进行整理，处理任务插队的逻辑
	 * @param emc
	 * @param taskId
	 * @param listId
	 * @param behindTaskId
	 * @throws Exception
	 */
	private void orderTaskInTaskList(EntityManagerContainer emc, String taskId, String listId, String behindTaskId) throws Exception {
		Business business = new Business( emc );
		//查询该任务在指定的任务列表当前的关联信息
		List<TaskListRele> reles = business.taskListFactory().listReleWithTaskAndList( taskId, listId );
		//查询指定的工作任务列表中所有的任务关联信息，以order排序
		List<TaskListRele> reles_allTask = business.taskListFactory().listReleWithListId( listId );
		//查询出当前工作任务的后一个工作任务关联信息，以order排序
		TaskListRele rele_behindTask = getBehindTaskRele( taskId, reles_allTask );	
		
		if( StringUtils.isNotEmpty( behindTaskId ) && rele_behindTask != null && rele_behindTask.getTaskId().equalsIgnoreCase( behindTaskId ) ) {//排序号未改变
			return;
		}
		
		if( StringUtils.isEmpty( behindTaskId ) ) {
			//未设置后序任务ID，放到最后一个，不需要插入到列表中间，不改变其他任务的序号
			Integer  maxOrder = business.taskListFactory().maxOrder( listId );
			if( ListTools.isNotEmpty( reles )) {
				emc.beginTransaction( TaskListRele.class );
				for( TaskListRele rele : reles ) {
					rele.setOrder( (maxOrder + 1) );
					emc.check( rele, CheckPersistType.all );
				}
				emc.commit();
			}
		}else {
			//不放到最后一个，插在中间
			TaskListRele taskListRele = null;
			TaskListRele current_taskListRele = null;
			Integer addOrder = 0;
			Integer insideOrderNumber = 0;			
			if( ListTools.isNotEmpty( reles_allTask )) {
				emc.beginTransaction( TaskListRele.class );
				for( int i=0; i<reles_allTask.size(); i++ ) {
					taskListRele = reles_allTask.get(i);
					//找到了后面的一个
					if( taskListRele.getTaskId().equalsIgnoreCase( behindTaskId )) {
						insideOrderNumber = i + 1;
						//把当前的这个关联排序向后移一位
						addOrder = 1;
					}					
					taskListRele.setOrder( ( i + 1 + addOrder)  );	
					//如果是当前指定的Task，先保存到一个新的变量里，最后再修改它的排序 号
					if( taskListRele.getTaskId().equalsIgnoreCase( taskId )) {
						current_taskListRele = taskListRele;
						continue;
					}
					emc.check( taskListRele, CheckPersistType.all );
				}
				emc.commit();
				
				emc.beginTransaction( TaskListRele.class );
				//最后把当前的这个任务设置上正确的排序号
				if( current_taskListRele != null ) {
					current_taskListRele.setOrder( insideOrderNumber );
					emc.check( taskListRele, CheckPersistType.all );
				}
				
				emc.commit();
			}
		}
	}

	/**
	 * 从集合查询指定Task关联位置后面的一条关联信息
	 * @param taskId
	 * @param reles_allTask
	 * @return
	 */
	private TaskListRele getBehindTaskRele(String taskId, List<TaskListRele> reles_allTask) {
		if( ListTools.isNotEmpty( reles_allTask )) {
			for( int i=0; i<reles_allTask.size(); i++ ) {
				if( reles_allTask.get(i).getTaskId().equalsIgnoreCase( taskId )) {
					if( i < (reles_allTask.size()-1) ) {
						//不是最后一个
						return reles_allTask.get(i+1);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 将任务从旧的列表里删除关联，再创建一个新的列表关联，事务控制
	 * @param emc
	 * @param taskId
	 * @param taskListId_old
	 * @param listId
	 * @throws Exception 
	 */
	private void moveTaskFromOldListToNewList( EntityManagerContainer emc, String taskId, String taskListId_old, String listId_new ) throws Exception {
		TaskListRele old_rele = null;
		TaskList newList = null;
		if( StringUtils.isNotEmpty( taskListId_old )) {
			old_rele = getReleWithTaskAndListId( emc, taskId, taskListId_old );
		}
		if( StringUtils.isNotEmpty( listId_new )) {
			newList = emc.find( listId_new, TaskList.class );
		}
		if( newList == null ) {
			throw new Exception("new list not exists. ID=" + listId_new );
		}
		
		TaskListRele taskListRele = new TaskListRele();
		taskListRele.setProject( newList.getProject() );
		taskListRele.setTaskId( taskId );
		taskListRele.setTaskGroupId( newList.getTaskGroup() );
		taskListRele.setTaskListId( listId_new );
		
		emc.beginTransaction( TaskListRele.class );
		if( old_rele  != null  ) {
			emc.remove( old_rele, CheckRemoveType.all  );
		}
		emc.persist( taskListRele, CheckPersistType.all );
		emc.commit();
	}

	private TaskListRele getReleWithTaskAndListId( EntityManagerContainer emc, String taskId, String taskListId_old) throws Exception {
		Business business = new Business(emc);
		TaskList taskList = null;
		TaskListRele taskListRele = null;
		List<TaskListRele> releList = business.taskListFactory().listReleWithTaskAndList(taskId, taskListId_old);
		if( ListTools.isNotEmpty( releList )) {
			for(TaskListRele rele :  releList ) {
				taskList = emc.find( rele.getTaskListId(), TaskList.class );
				if( taskList == null ) { //List不存在，这个关联本身也没用
					emc.beginTransaction( TaskListRele.class );
					emc.remove( rele, CheckRemoveType.all );
					emc.commit();
				}else {
					//List不为空，取第一个关联，其他多余的关联全部删除
					if( taskListRele == null ) {
						taskListRele = rele;
					}else {
						emc.beginTransaction( TaskListRele.class );
						emc.remove( rele, CheckRemoveType.all );
						emc.commit();
					}
				}
			}
		}		
		return taskListRele;
	}

	/**
	 * 获取任务在指定的工作任务组里所关联的列表ID
	 * 一个任务在一个Group里只允许 有一个List与之关联
	 * @param emc
	 * @param taskId
	 * @param taskGroupId
	 * @return
	 * @throws Exception
	 */
	private String getTaskListWithTaskAndGroup(EntityManagerContainer emc, String taskId, String taskGroupId) throws Exception {
		List<TaskListRele> taskReleList = new Business(emc).taskListFactory().listReleWithTaskAndGroup( taskId, taskGroupId );
		TaskList taskList = null;
		String taskListID = null;
		if( ListTools.isNotEmpty( taskReleList )) {
			for(TaskListRele rele :  taskReleList ) {
				taskList = emc.find( rele.getTaskListId(), TaskList.class );
				if( taskList == null ) { //List不存在，这个关联本身也没用
					emc.beginTransaction( TaskListRele.class );
					emc.remove( rele, CheckRemoveType.all );
					emc.commit();
				}else {
					//List不为空，取第一个关联，其他多余的关联全部删除
					if( StringUtils.isEmpty( taskListID ) ) {
						if( taskList != null ) {
							taskListID = taskList.getId();
						}
					}else {
						emc.beginTransaction( TaskListRele.class );
						emc.remove( rele, CheckRemoveType.all );
						emc.commit();
					}
				}
			}
		}
		return taskListID;
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
			TaskList taskList = emc.find( listId, TaskList.class );
			List<TaskListRele> releList = business.taskListFactory().listReleWithTaskAndList( taskId, listId );
			if( ListTools.isEmpty( releList ) ) {
				emc.beginTransaction( TaskListRele.class );
				for( TaskListRele rele : releList ) {
					emc.remove(rele, CheckRemoveType.all );
				}
				emc.commit();
			}
			if( taskList != null ) {
				taskGroupService.refreshTaskCountInTaskGroup(emc, effectivePerson.getDistinguishedName(), taskList.getTaskGroup() );
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
			List<TaskListRele> oldListRele = business.taskListFactory().listReleWithListId(listId);

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
