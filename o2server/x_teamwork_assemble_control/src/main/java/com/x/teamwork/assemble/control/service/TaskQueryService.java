package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.jaxrs.task.BaseAction.TaskListChange;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskExtField;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskListRele;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

/**
 * 对工作任务信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskQueryService {

	private TaskService taskService = new TaskService();
	private TaskListService taskListService = new TaskListService();
	private TaskGroupService taskGroupService = new TaskGroupService();
	private ReviewService reviewService = new ReviewService();
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
			return emc.find(id, Task.class );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public TaskDetail getDetail(String id) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, TaskDetail.class );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据任务ID查询扩展属性信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TaskExtField getExtField(String id) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, TaskExtField.class );
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
	
	public Long countWithFilter( EffectivePerson effectivePerson, QueryFilter queryFilter ) throws Exception {
		String personName = effectivePerson.getDistinguishedName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskService.countWithFilter(emc, personName, queryFilter);
		} catch (Exception e) {
			throw e;
		}
	}

	public Long countWithTaskListId(String taskListId) throws Exception {
		if ( StringUtils.isEmpty( taskListId )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskService.countWithTaskListId( emc, taskListId );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据TaskListID查询任务列表，如果taskListId，则查询所有未分类的工作列表
	 * @param projectId
	 * @param taskListId
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<Task> listTaskWithTaskListId( String projectId, String taskListId, String personName ) throws Exception {
		if ( StringUtils.isEmpty( taskListId )) {
			return null;
		}
		
		Task task = null;
		TaskList taskList = null;
		List<String> taskIds = null;
		List<String> taskListIds = null;
		List<String> taskIds_forTaskList = null;
		List<TaskGroup> taskGroupList = null;
		List<TaskListRele> taskListReles = null;
		List<Task> resultList = new ArrayList<>();
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskList = emc.find( taskListId, TaskList.class );
			if( taskList != null ) {
				//查询该TaskList下所有的任务列表
				taskIds = taskListService.listTaskIdsWithTaskListId( emc, taskListId );
				//查询这些任务在指定工作任务列表里的关联，按关联的排序号查询任务信息列表
				taskListReles = taskListService.listReleWithTaskAndListId(emc, taskIds, taskListId );				
				if( ListTools.isNotEmpty( taskListReles )) {
					for( TaskListRele rele : taskListReles ) {
						task = emc.find( rele.getTaskId(), Task.class );
						task.setOrder( rele.getOrder() );
						resultList.add( task );
					}
				}
				return resultList;
			}else {
				//查询所有未归类的任务列表
				List<String> taskIds_all = new ArrayList<>();
				//查询在指定项目里所有可见的工作任务列表
				List<String> taskIds_all_tmp = reviewService.listTaskIdsWithPerson(emc, personName, projectId );
				if( taskIds_all_tmp == null ) {
					taskIds_all_tmp = new ArrayList<>();
				}
				for( String str : taskIds_all_tmp ) {
					taskIds_all.add( str );
				}
				//查询默认的TaskGroup
				taskGroupList = taskGroupService.listGroupByPersonAndProject( emc, personName, projectId);
				if( ListTools.isNotEmpty( taskGroupList )) {
					//查询该用户所有的TaskList的ID列表
					taskListIds = taskListService.listTaskListIdsWithGroup( emc, taskGroupList.get(0).getId(), personName );
					if( ListTools.isNotEmpty( taskListIds )) {
						//看看这些TaskList所关联的所有的TaskId列表
						taskIds_forTaskList = taskListService.listTaskIdsWithTaskListIds( emc, taskListIds );
						if( taskIds_forTaskList == null ) {
							taskIds_forTaskList = new ArrayList<>();
						}
						taskIds_all.removeAll( taskIds_forTaskList );
					}
				}
				return new Business(emc).taskFactory().list(taskIds_all );
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据TaskListID查询任务列表，如果taskListId，则查询所有未分类的工作列表
	 * @param projectId
	 * @param taskListId
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<Task> listMyTaskWithTaskListId( String projectId, String taskListId, String personName ) throws Exception {
		if ( StringUtils.isEmpty( taskListId )) {
			return null;
		}
		
		Task task = null;
		TaskList taskList = null;
		List<String> taskIds = null;
		List<String> taskListIds = null;
		List<String> taskIds_forTaskList = null;
		List<TaskGroup> taskGroupList = null;
		List<TaskListRele> taskListReles = null;
		List<Task> resultList = new ArrayList<>();
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			taskList = emc.find( taskListId, TaskList.class );
			if( taskList != null ) {				
				//查询该TaskList下所有的任务列表
				taskIds = taskListService.listTaskIdsWithTaskListId( emc, taskListId );
				//查询这些任务在指定工作任务列表里的关联，按关联的排序号查询任务信息列表
				taskListReles = taskListService.listReleWithTaskAndListId(emc, taskIds, taskListId );				
				if( ListTools.isNotEmpty( taskListReles )) {
					for( TaskListRele rele : taskListReles ) {
						task = emc.find( rele.getTaskId(), Task.class );
						//只查询自己负责的任务
						if( personName.equalsIgnoreCase( task.getExecutor() )) {
							task.setOrder( rele.getOrder() );
							resultList.add( task );
						}
					}
				}
			}else {
				//查询所有未归类的任务列表
				List<String> taskIds_all = new ArrayList<>();
				//查询在指定项目里所有可见的工作任务列表
				List<String> taskIds_all_tmp = reviewService.listTaskIdsWithPerson(emc, personName, projectId );
				if( taskIds_all_tmp == null ) {
					taskIds_all_tmp = new ArrayList<>();
				}
				for( String str : taskIds_all_tmp ) {
					taskIds_all.add( str );
				}
				//查询默认的TaskGroup
				taskGroupList = taskGroupService.listGroupByPersonAndProject( emc, personName, projectId);
				if( ListTools.isNotEmpty( taskGroupList )) {
					//查询该用户所有的TaskList的ID列表
					taskListIds = taskListService.listTaskListIdsWithGroup( emc, taskGroupList.get(0).getId(), personName );
					if( ListTools.isNotEmpty( taskListIds )) {
						//看看这些TaskList所关联的所有的TaskId列表
						taskIds_forTaskList = taskListService.listTaskIdsWithTaskListIds( emc, taskListIds );
						if( taskIds_forTaskList == null ) {
							taskIds_forTaskList = new ArrayList<>();
						}
						taskIds_all.removeAll( taskIds_forTaskList );
					}
				}
				List<Task> taskListTmp = new Business(emc).taskFactory().list(taskIds_all );
				if( ListTools.isNotEmpty( taskListTmp )) {
					for( Task _task : taskListTmp ) {
						//只查询自己负责的任务
						if( personName.equalsIgnoreCase( _task.getExecutor() )) {
							resultList.add( _task );
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return resultList;
	}
	
	/**
	 * 在人员的可见范围之类，根据指定的工作任务ID，查询子任务列表
	 * @param project
	 * @param taskId
	 * @param effectivePerson
	 * @return
	 * @throws Exception 
	 */
	public List<Task> listTaskWithParentId(String taskId, EffectivePerson effectivePerson) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			return null;
		}
		if ( effectivePerson == null ) {
			return null;
		}
		List<Task> taskList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//查询用户在该项目里可见的所有项目列表
			List<Review> reviewList = reviewService.listTaskWithPersonAndParentId( emc, effectivePerson.getDistinguishedName(), taskId );
			if( ListTools.isNotEmpty( reviewList )) {
				taskList = taskService.convertToTask(reviewList);
			}
		} catch (Exception e) {
			throw e;
		}
		return taskList;
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
		List<Task> taskList = null;
		List<Task> result = new ArrayList<>();
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
			taskList = taskService.listWithFilter( emc, maxCount, orderField, orderType, effectivePerson.getDistinguishedName(), queryFilter );
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
		List<Task> taskList = null;
		List<Task> resultList = new ArrayList<>();
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
			taskList = taskService.listWithFilter( emc, maxCount, orderField, orderType, effectivePerson.getDistinguishedName(), queryFilter );
		} catch (Exception e) {
			throw e;
		}
		if( ListTools.isNotEmpty( taskList )) {
			int count = 0;
			if( lastTask != null ) {
				boolean add = false;
				//获取自lastTask之后的一页内容
				for( Task task : taskList ) {
					if( add ) {
						count ++;
						if( count <= pageSize ) {
							resultList.add( task );
						}
					}
					if( task.getId().equals( lastTask.getId() )) {
						add = true;
					}
				}
			}else {
				//只获取第一页内容
				for( Task task : taskList ) {
					count ++;
					if( count <= pageSize ) {
						resultList.add(task);
					}
				}
			}
		}		
		return resultList;
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

			if( task.getParticipantList().contains( personName )) {
				return true;
			}
			
			identityNames.retainAll( task.getParticipantList() );
			if( ListTools.isNotEmpty( identityNames )) {
				return true;
			}
			unitNames.retainAll( task.getParticipantList() );
			if( ListTools.isNotEmpty( unitNames )) {
				return true;
			}
			groupNames.retainAll( task.getParticipantList() );
			if( ListTools.isNotEmpty( groupNames )) {
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

	public List<Task> listUnReviewIds( int maxCount ) throws Exception {
		if ( maxCount == 0 ) {
			maxCount = 100;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskService.listUnReviewTaskIds( emc, 100 );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询指定任务的所有未完成的子任务信息列表
	 * @param taskId
	 * @return
	 * @throws Exception 
	 */
	public List<Task> allUnCompletedSubTasks(String taskId) throws Exception {
		if( StringUtils.isEmpty( taskId ) ) { 
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskService.allUnCompletedSubTasks( emc, taskId );
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer countWithTaskGroupId( String taskGroupId, EffectivePerson effectivePerson ) throws Exception {
		if( StringUtils.isEmpty( taskGroupId ) ) { 
			return 0;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskGroup taskGroup = emc.find( taskGroupId, TaskGroup.class );
			if( taskGroup != null ) {
				List<String> taskIds = taskService.listTaskIdsWithPermissionInProject(emc, 999, taskGroup.getProject(), effectivePerson.getDistinguishedName() );
				if( ListTools.isNotEmpty( taskIds )) {
					return taskIds.size();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	public List<Task> listTaskWithProjectAndPerson(String project, EffectivePerson effectivePerson) throws Exception {
		if( StringUtils.isEmpty( project ) ) { 
			return null;
		}
		if( effectivePerson == null ) { 
			return null;
		}
		Business business = null;
		List<String> taskIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			taskIds = business.reviewFactory().listTaskIdsWithPersonAndProject( effectivePerson.getDistinguishedName(), project);
			if( ListTools.isNotEmpty( taskIds )) {
				return emc.list( Task.class, taskIds );
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	public List<String> listAllTaskIdsWithProject( String project ) throws Exception {
		if( StringUtils.isEmpty( project ) ) { 
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskService.listAllTaskIdsWithProject( emc, project );			
		} catch (Exception e) {
			throw e;
		}
	}

	public String getValueFromTaskExtField( TaskExtField taskExtField, String extFieldName) {
		if( StringUtils.isEmpty( extFieldName )) {
			return "";
		}
		if( taskExtField == null ) {
			return "";
		}
		if( StringUtils.equals( extFieldName, "memoString_1" ) ) {
			return taskExtField.getMemoString_1();
		}else if( StringUtils.equals( extFieldName, "memoString_2" ) ) {
			return taskExtField.getMemoString_2();
		}else if( StringUtils.equals( extFieldName, "memoString_3" ) ) {
			return taskExtField.getMemoString_3();
		}else if( StringUtils.equals( extFieldName, "memoString_4" ) ) {
			return taskExtField.getMemoString_4();
		}else if( StringUtils.equals( extFieldName, "memoString_5" ) ) {
			return taskExtField.getMemoString_5();
		}else if( StringUtils.equals( extFieldName, "memoString_6" ) ) {
			return taskExtField.getMemoString_6();
		}else if( StringUtils.equals( extFieldName, "memoString_7" ) ) {
			return taskExtField.getMemoString_7();
		}else if( StringUtils.equals( extFieldName, "memoString_8" ) ) {
			return taskExtField.getMemoString_8();
		}else if( StringUtils.equals( extFieldName, "memoString_1_lob" ) ) {
			return taskExtField.getMemoString_1_lob();
		}else if( StringUtils.equals( extFieldName, "memoString_2_lob" ) ) {
			return taskExtField.getMemoString_2_lob();
		}else if( StringUtils.equals( extFieldName, "memoString_3_lob" ) ) {
			return taskExtField.getMemoString_3_lob();
		}else if( StringUtils.equals( extFieldName, "memoString_4_lob" ) ) {
			return taskExtField.getMemoString_4_lob();
		}
		return "";
	}	
}
