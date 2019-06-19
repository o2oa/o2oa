package com.x.teamview.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.TaskList;

/**
 * 对工作任务列表信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskListQueryService {

	private TaskListService taskListService = new TaskListService();
	
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
	
	/**
	 *  根据项目ID查询工作任务列表
	 *  @param person
	 * @param taskGroupId
	 * @return
	 * @throws Exception
	 */
	public List<TaskList> listWithTaskGroup( String person, String taskGroupId ) throws Exception {
		if ( StringUtils.isEmpty( taskGroupId )) {
			throw new Exception("taskGroupId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<TaskList> taskLists =  taskListService.listWithTaskGroup( emc, taskGroupId );
			if( ListTools.isEmpty( taskLists )) {
				//没有任何工作任务列表，需要新建一组默认的工作任务列表
				taskLists = taskListService.createDefaultTaskListForTaskGroup( emc, person, taskGroupId );
			}
			return taskLists;
		} catch (Exception e) {
			throw e;
		}
	}
}
