package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.TaskView;

/**
 * 对工作任务视图信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskViewQueryService {

	private TaskViewService taskViewService = new TaskViewService();
	
	/**
	 * 根据工作任务视图的标识查询工作任务视图信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskView get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskViewService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *  根据用户和项目ID查询工作任务视图
	 * @param effectivePerson.getDistinguishedName()
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<TaskView> listViewWithPersonAndProject( EffectivePerson effectivePerson, String project ) throws Exception {
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		if ( StringUtils.isEmpty( project )) {
			throw new Exception("project is empty.");
		}
		List<TaskView> taskViewList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskViewList = taskViewService.listViewWithPersonAndProject(emc, effectivePerson.getDistinguishedName(), project);
			//如果用户在该项目中没有工作任务组，则需要创建一个默认的工作任务组			
			if( ListTools.isEmpty( taskViewList )) {
				taskViewList = taskViewService.createDefaultTaskViewForPerson( emc, effectivePerson.getDistinguishedName(), project );
			}
		} catch (Exception e) {
			throw e;
		}
		return taskViewList;
	}
}
