package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;

/**
 * 对工作任务列表信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskListQueryService {

	private TaskListService taskListService = new TaskListService();
	private ReviewService reviewService = new ReviewService();
	
	/**
	 * 根据工作任务列表的标识查询工作任务列表信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskList get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskListService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *  根据用户和项目ID查询工作任务列表
	 * @param person
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<TaskList> listWithProject( String person, String projectId ) throws Exception {
		if ( StringUtils.isEmpty( person )) {
			throw new Exception("person is empty.");
		}
		if ( StringUtils.isEmpty( projectId )) {
			throw new Exception("projectId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskListService.listWithProject(emc, person, projectId);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Long countTaskWithTaskListId( String personName, String taskListId, String taskGroupId ) throws Exception {
		if ( StringUtils.isEmpty( taskListId )) {
			throw new Exception("taskListId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskList taskList = emc.find( taskListId, TaskList.class );
			if( taskList == null ) {
				return taskListService.countTaskWithOutoTaskListInGroup(emc, personName, taskGroupId );
			}else {
				return taskListService.countTaskWithTaskListId(emc, taskListId );
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *  根据工作任务组ID查询工作任务列表集合
	 *  @param person
	 * @param taskGroupId
	 * @return
	 * @throws Exception
	 */
	public List<TaskList> listWithTaskGroup( String person, String taskGroupId ) throws Exception {
		if ( StringUtils.isEmpty( taskGroupId )) {
			throw new Exception("taskGroupId is empty.");
		}
		List<TaskList> result = new ArrayList<>();
		List<String> taskListIds = null;
		List<String> taskIds_all = new ArrayList<>();;
		List<String> taskIds_forTaskList = null;
		Boolean hasTaskWithNoList = false;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskGroup taskGroup = emc.find( taskGroupId, TaskGroup.class );
			List<TaskList> taskLists =  taskListService.listWithTaskGroup( emc, taskGroupId );
			if( ListTools.isEmpty( taskLists )) {
				//没有任何工作任务列表，需要新建一组默认的工作任务列表
				taskLists = taskListService.createDefaultTaskListForTaskGroup( emc, person, taskGroupId );
			}
			
			//判断用户在该项目里是否存在未归类的工作，如果存在，就需要临时产生一个未归类的TaskList
			if( taskGroup != null ) {
				//查询用户所有的工作ID列表
				List<String> taskIds_all_temp = reviewService.listTaskIdsWithPerson( emc, person, taskGroup.getProject() );
				taskIds_all.addAll( taskIds_all_temp );
			}
			if( ListTools.isNotEmpty( taskIds_all )) {
				//查询该用户所有的TaskList的ID列表
				taskListIds = taskListService.listTaskListIdsWithGroup( emc, taskGroupId, person );
				if( ListTools.isNotEmpty( taskListIds )) {
					//看看这些TaskList所关联的所有的TaskId列表
					taskIds_forTaskList = taskListService.listTaskIdsWithTaskListIds( emc, taskListIds );
					taskIds_all.removeAll( taskIds_forTaskList );
					if( ListTools.isNotEmpty( taskIds_all )) {
						//存在未分类的任务
						hasTaskWithNoList = true;
					}
				}else {
					//存在未分类的任务
					hasTaskWithNoList = true;
				}
			}			
			
			if( hasTaskWithNoList ) {
				TaskList taskList = getNoneList( taskGroup.getProject(),  taskGroupId, person );	
				result.add( taskList );
			}
			
			if( ListTools.isNotEmpty( taskLists )) {
				for( TaskList taskList : taskLists ) {
					result.add( taskList );
				}
			}
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public TaskList getNoneList(String projectId, String taskGroupId, String person ) {
		TaskList taskList = new TaskList();		
		taskList.setId( TaskList.createId() );
		taskList.setProject( projectId );
		taskList.setTaskGroup( taskGroupId );
		taskList.setMemo( "NoneList" );
		taskList.setName( "未归类任务" );
		taskList.setOrder( -1 );
		taskList.setCreatorPerson( "SYSTEM" );
		taskList.setOwner( person );
		return taskList;
	}
}
