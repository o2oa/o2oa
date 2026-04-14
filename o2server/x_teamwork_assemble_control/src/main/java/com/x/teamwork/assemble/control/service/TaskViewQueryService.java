package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskView;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

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
	
	public Long countWithFilter( QueryFilter queryFilter ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskViewService.countWithFilter(emc, queryFilter);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据条件查询符合条件的工作任务视图信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param effectivePerson
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<TaskView> listWithFilter( EffectivePerson effectivePerson, Integer pageSize, String lastId, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<TaskView> taskViewList = null;
		List<TaskView> resultList = new ArrayList<>();
		Integer maxCount = 2000;
		Task lastTask = null;
		
		if( pageSize == 0 ) { pageSize = 20; }
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( StringUtils.isNotEmpty(lastId) && !"(0)".equals( lastId ) && !"null".equals( lastId )) {
				lastTask = emc.find( lastId, Task.class );
			}
			taskViewList = taskViewService.listWithFilter( emc, maxCount, orderField, orderType, queryFilter );
		} catch (Exception e) {
			throw e;
		}
		if( ListTools.isNotEmpty( taskViewList )) {
			int count = 0;
			if( lastTask != null ) {
				boolean add = false;
				//获取自lastTask之后的一页内容
				for( TaskView taskView : taskViewList ) {
					if( add ) {
						count ++;
						if( count <= pageSize ) {
							resultList.add( taskView );
						}
					}
					if( taskView.getId().equals( lastTask.getId() )) {
						add = true;
					}
				}
			}else {
				//只获取第一页内容
				for( TaskView taskView : taskViewList ) {
					count ++;
					if( count <= pageSize ) {
						resultList.add(taskView);
					}
				}
			}
		}		
		return resultList;
	}
}
