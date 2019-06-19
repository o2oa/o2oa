package com.x.teamview.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.TaskGroupRele;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;


/**
 * 对工作任务组信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskGroupQueryService {

	private UserManagerService userManagerService = new UserManagerService();
	private TaskGroupService taskGroupService = new TaskGroupService();
	private TaskService taskService = new TaskService();

	public List<TaskGroup> list(List<String> groupIds) throws Exception {
		if ( ListTools.isEmpty( groupIds )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskGroupService.list( emc, groupIds );
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * 根据工作任务组的标识查询工作任务组信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskGroup get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskGroupService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据用户列示工作任务组信息列表
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public List<TaskGroup> listGroupByPersonAndProject( EffectivePerson effectivePerson, String project ) throws Exception {
		if ( effectivePerson == null ) {
			return new ArrayList<>();
		}
		if ( StringUtils.isEmpty( project ) ) {
			return new ArrayList<>();
		}
		List<TaskGroup> taskGroupList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			taskGroupList = taskGroupService.listGroupByPersonAndProject(emc, effectivePerson.getDistinguishedName(), project );
			//如果用户在该项目中没有工作任务组，则需要创建一个默认的工作任务组			
			if( ListTools.isEmpty( taskGroupList )) {
				taskGroupList = createDefaultTaskGroupForPerson( emc, effectivePerson, project );				
			}
		} catch (Exception e) {
			throw e;
		}
		return taskGroupList;
	}

	/**
	 * 为指定项目初始化一个工作任务组，并且将所有的工作任务加入到工作任务组中
	 * @param emc
	 * @param effectivePerson
	 * @param project
	 * @return
	 * @throws Exception
	 */
	private List<TaskGroup> createDefaultTaskGroupForPerson( EntityManagerContainer emc, EffectivePerson effectivePerson, String project ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		String personName = null;
		List<TaskGroup> taskGroupList = new ArrayList<>();
		personName = effectivePerson.getDistinguishedName();
		if( !effectivePerson.isManager() ) {
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			identityNames = userManagerService.listIdentitiesWithPerson( personName );
		}
		//查询用户在该项目中所有的任务列表，添加到默认的工作任务组里
		List<Task> taskList =  taskService.listWithPermission(emc, 999, personName, identityNames, unitNames, groupNames );
		TaskGroup group = taskGroupService.createDefaultTaskGroupForPerson( emc, personName, project, taskList );
		taskGroupList.add( group );
		return taskGroupList;
	}

	/**
	 * 根据工作任务组ID，查询工作任务组内所有的工作任务ID列表
	 * @param emc
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public List<String> listTaskIdByGroup(String group ) throws Exception {
		if (StringUtils.isEmpty(group)) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskGroupService.listTaskIdByGroup(emc, group);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listGroupIdsByTask( String taskId ) throws Exception {
		List<String> result = new ArrayList<>();
		if (StringUtils.isEmpty( taskId )) {
			return result;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<TaskGroupRele> reles = taskGroupService.listReleWithTask(emc, taskId);
			if( ListTools.isNotEmpty( reles )) {
				for( TaskGroupRele rele : reles ) {
					if( !result.contains( rele.getTaskGroupId() )) {
						result.add( rele.getTaskGroupId() );
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 判断是否存在分组和工作任务的关联
	 * @param taskId
	 * @param projectId
	 * @return
	 * @throws Exception 
	 */
	public boolean existsWithTaskAndGroup(String groupId, String taskId) throws Exception {
		if (StringUtils.isEmpty( groupId )) {
			return false;
		}
		if (StringUtils.isEmpty( taskId )) {
			return false;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<TaskGroupRele> reles = taskGroupService.listReleWithTaskAndGroup(emc, taskId, groupId );
			if( ListTools.isNotEmpty( reles )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

}
