package com.x.teamview.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.temwork.assemble.control.jaxrs.task.BaseAction.TaskListChange;

/**
 * 对工作任务信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskQueryService {

	private TaskService taskService = new TaskService();
	private UserManagerService userManagerService = new UserManagerService();
	
	/**
	 * 根据工作任务的标识查询工作任务信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Task get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据ID列表查询工作任务信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Task> list(List<String> ids) throws Exception {
		if (ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( Task.class,  ids );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据过滤条件查询符合要求的工作任务信息数量
	 * @param effectivePerson
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( EffectivePerson effectivePerson, QueryFilter queryFilter ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		String personName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( !effectivePerson.isManager() ) {
				personName = effectivePerson.getDistinguishedName();
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson(personName);
				identityNames = userManagerService.listIdentitiesWithPerson( personName );
			}
			return taskService.countWithFilter( emc, personName, identityNames, unitNames, groupNames, queryFilter );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *  根据过滤条件查询符合要求的工作任务信息列表
	 * @param effectivePerson
	 * @param pageSize
	 * @param pageNum
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Task> listWithFilter( EffectivePerson effectivePerson, Integer pageSize, Integer pageNum, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		List<Task> taskList = null;
		List<Task> result = new ArrayList<>();
		String personName = null;
		Integer maxCount = 20;
		Integer startNumber = 0;		
		
		if( pageNum == 0 ) { pageNum = 1; }
		if( pageSize == 0 ) { pageSize = 20; }
		maxCount = pageSize * pageNum;
		startNumber = pageSize * ( pageNum -1 );
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( !effectivePerson.isManager() ) {
				personName = effectivePerson.getDistinguishedName();
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson( personName );
				identityNames = userManagerService.listIdentitiesWithPerson( personName );
			}
			taskList = taskService.listWithFilter(emc, maxCount, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter );
			
			if( ListTools.isNotEmpty( taskList )) {
				for( int i = 0; i<taskList.size(); i++ ) {
					if( i >= startNumber ) {
						result.add( taskList.get( i ));
					}
				}
			}			
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 根据条件查询符合条件的工作任务信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param effectivePerson
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Task> listWithFilter( EffectivePerson effectivePerson, Integer pageSize, String lastId, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<Task> taskList = null;
		List<String> identityNames = null;
		String personName = null;
		Integer maxCount = 20;
		Task task = null;
		
		if( pageSize == 0 ) { pageSize = 20; }
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( !effectivePerson.isManager() ) {
				personName = effectivePerson.getDistinguishedName();
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson(personName);
				identityNames = userManagerService.listIdentitiesWithPerson(personName);
			}
			if( lastId != null ) {
				task = emc.find( lastId, Task.class );
			}
			if( task != null ) {
				taskList = taskService.listWithFilter(emc, maxCount, task.getSequence(), orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter );
			}else {
				taskList = taskService.listWithFilter(emc, maxCount, null, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter );
			}	
		} catch (Exception e) {
			throw e;
		}
		return taskList;
	}
	
	/**
	 * 判断用户是否为指定工作任务的管理员
	 * @param taskId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isTaskManager(String taskId, String distinguishedName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.find( taskId, Task.class );
			if( ListTools.isNotEmpty( task.getManageablePersonList() )){
				if( distinguishedName.equalsIgnoreCase( task.getCreatorPerson() )) {
					return true;
				}
				if( distinguishedName.equalsIgnoreCase( task.getExecutor() )) {
					return true;
				}
				if( task.getManageablePersonList().contains( distinguishedName )) {
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	/**
	 * 判断用户是工作任务参与者
	 * @param taskId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isTaskParticipant( String taskId, String personName ) throws Exception {		
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			identityNames = userManagerService.listIdentitiesWithPerson(personName);
			
			Task task = emc.find( taskId, Task.class );

			if( task.getParticipantPersonList().contains( personName )) {
				return true;
			}
			task.getParticipantIdentityList().retainAll( identityNames );
			if( ListTools.isNotEmpty( task.getParticipantIdentityList() )) {
				return true;
			}
			task.getParticipantUnitList().retainAll( unitNames );
			if( ListTools.isNotEmpty( task.getParticipantUnitList() )) {
				return true;
			}
			task.getParticipantGroupList().retainAll( groupNames );
			if( ListTools.isNotEmpty( task.getParticipantGroupList() )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	/**
	 * 判断用户是否拥有指定工作任务的访问权限
	 * @param appId
	 * @param distinguishedName
	 * @return
	 * @throws Exception 
	 */
	public Boolean isTaskViewer(String appId, String personName ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> identityNames = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			identityNames = userManagerService.listIdentitiesWithPerson(personName);
			
			Task task = emc.find( appId, Task.class );
			if( personName.equalsIgnoreCase( task.getCreatorPerson() )) {
				return true;
			}
			if( personName.equalsIgnoreCase( task.getExecutor() )) {
				return true;
			}	
			if( task.getManageablePersonList().contains( personName )) {
				return true;
			}				
			if( task.getParticipantPersonList().contains( personName )) {
				return true;
			}
			task.getParticipantIdentityList().retainAll( identityNames );
			if( ListTools.isNotEmpty( task.getParticipantIdentityList() )) {
				return true;
			}
			task.getParticipantUnitList().retainAll( unitNames );
			if( ListTools.isNotEmpty( task.getParticipantUnitList() )) {
				return true;
			}
			task.getParticipantGroupList().retainAll( groupNames );
			if( ListTools.isNotEmpty( task.getParticipantGroupList() )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	/**
	 * 根据任务的状态信息变化查询展现的列表变化
	 * 任务原来所在的列表是绑定状态的，才会根据状态来变化列表，否则就不变
	 * @param id
	 * @param sourceStatus
	 * @param targetStatus
	 * @return
	 */
	public TaskListChange getTaskListChange( String id, String sourceStatus, String targetStatus ) {
		TaskListChange change = null;
		String sourceListId = null;
		String targetListId = null;
		//根据任务ID、sourceStatus查询原来所在列表ID
		
		//根据任务ID、targetStatus查询目标列表ID
		
		//组织一个ViewChange
		if( StringUtils.isNotEmpty( sourceListId ) || StringUtils.isNotEmpty( targetListId ) ) {
			change = new TaskListChange();
			change.setSource(sourceListId);
			change.setTarget(targetListId);
			return change;
		}
		return null;		
	}
}
