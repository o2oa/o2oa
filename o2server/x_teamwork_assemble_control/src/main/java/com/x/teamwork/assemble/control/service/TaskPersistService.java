package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.common.date.DateOperation;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskStatuType;

/**
 * 对工作任务信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskPersistService {

	private TaskService taskService = new TaskService();
	private TaskGroupService taskGroupService = new TaskGroupService();
	private UserManagerService userManagerService = new UserManagerService();
	
	/**
	 * 删除任务信息
	 * @param flag
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String flag, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		Boolean hasDeletePermission = false;
		if( effectivePerson.isManager() ) {
			hasDeletePermission = true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = taskService.get(emc, flag);
			//管理员可以删除，创建者可以删除，工作任务创建者、管理者都可以删除
			if( !hasDeletePermission ) {
				if( task.getCreatorPerson().equalsIgnoreCase( effectivePerson.getDistinguishedName() )) {
					hasDeletePermission = true;
				}else if( ListTools.isNotEmpty( task.getManageablePersonList() ) && task.getManageablePersonList().contains(effectivePerson.getDistinguishedName() )) {
					hasDeletePermission = true;
				}
			}
			if( !hasDeletePermission ) {
				throw new Exception("task delete permission denied.");
			}else {
				taskService.remove( emc, flag );
				taskGroupService.refreshTaskCountInTaskGroupWithTaskId(emc, effectivePerson.getDistinguishedName(), task.getId() );
			}			
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存工作任务信息
	 * @param task
	 * @param taskDetail
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Task save( Task task, TaskDetail taskDetail, EffectivePerson effectivePerson ) throws Exception {
		if ( task == null) {
			throw new Exception( "task is null." );
		}
		if( StringUtils.isEmpty( task.getName() )) {
			task.setName("无标题工作任务("+ DateOperation.getNowDateTime() +")");
		}		
		if( StringUtils.isEmpty( task.getParent() ) ) {
			task.setParent("0");
		}
		
		if( StringUtils.isEmpty( task.getExecutor() ) ) {
			task.setExecutor( effectivePerson.getDistinguishedName() );
		}
		
		if( StringUtils.isEmpty( task.getPriority() ) ) {
			task.setPriority( "普通" );
		}
		
		if( StringUtils.isEmpty( task.getWorkStatus() ) ) {
			task.setWorkStatus( TaskStatuType.processing.name() );
		}
		
		//校验工作任务的状态变更
		if( TaskStatuType.completed.name().equalsIgnoreCase( task.getWorkStatus() )) {
			task.setCompleted( true );
			task.setArchive(  false );
		}else if( TaskStatuType.processing.name().equalsIgnoreCase( task.getWorkStatus() )) {
			task.setCompleted( false );
			task.setArchive(  false );
		}else if( TaskStatuType.archived.name().equalsIgnoreCase( task.getWorkStatus() )) {
			task.setCompleted( true  );
			task.setArchive(  true );
		}
		
		if( StringUtils.isEmpty( task.getExecutorIdentity() ) ) {
			//取第一个身份
			String identity = userManagerService.getIdentityWithPerson( task.getExecutor(), "min");
			if( StringUtils.isNotEmpty( identity)) {
				task.setExecutorIdentity(identity);
			}else {
				throw new Exception("executor  has no identity, please concat manager! person:" + task.getExecutor());
			}
		}
		
		if( StringUtils.isEmpty( task.getExecutorUnit() ) ) {
			String unitName = userManagerService.getUnitNameByIdentity( task.getExecutorIdentity() );
			if( StringUtils.isNotEmpty( unitName)) {
				task.setExecutorUnit(unitName);
			}else {
				throw new Exception("executor unit not exists with identity:" + task.getExecutorIdentity());
			}
		}
		
		if( StringUtils.isNotEmpty( task.getExecutor() ) && task.getExecutor().indexOf( "@I" ) > 0  ) {
			//选择的是身份
			String personName = userManagerService.getPersonNameWithIdentity( task.getExecutor() );
			task.setExecutor( personName );
		}
		
		if( StringUtils.isEmpty( task.getCreatorPerson() ) ) {
			task.setCreatorPerson( effectivePerson.getDistinguishedName() );
		}
		if( ListTools.isEmpty( task.getManageablePersonList()) ) {
			task.addManageablePerson( effectivePerson.getDistinguishedName());
		}
		if( task.getName().length() > 70 ) {
			task.setName( task.getName().substring(0, 70) + "..." );
		}
		
		
		if( task.getStartTime() == null  ) {
			task.setStartTime( new Date() );
		}
		if( task.getEndTime() == null  ) {
			task.setEndTime( new Date( ( task.getStartTime().getTime() + 30*60*1000) ) ); //30分钟之后
		}
		
		task.addParticipant( effectivePerson.getDistinguishedName() );

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			task = taskService.save( emc, task, taskDetail );
		} catch (Exception e) {
			throw e;
		}
		return task;
	}

	/**
	 * 更新工作任务的标签信息
	 * @param id
	 * @param new_tags
	 * @param add_tags
	 * @param remove_tags
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public List<String> updateTag(String id, List<String> new_tags, List<String> add_tags, List<String> remove_tags, EffectivePerson effectivePerson) throws Exception {
		if( StringUtils.isEmpty( id )) {
			throw new Exception("id can not empty in operation updateTag.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return taskService.updateTag( emc, id, new_tags, add_tags, remove_tags, effectivePerson );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 查询用户是否拥有创建工作任务的权限
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public boolean checkPermissionForPersist( Project project, EffectivePerson effectivePerson ) throws Exception {
		if( effectivePerson.isManager() ) {
			return true;
		}
		String personName = effectivePerson.getDistinguishedName();
		if( userManagerService.isHasPlatformRole( personName, "TeamWorkManager" )) {
			return true;
		}
		//项目的创建者，负责人以及参与者都应该可以在项目内项目任务
		if( project.getCreatorPerson().equalsIgnoreCase( personName )) {
			return true;
		}
		if( project.getExecutor().equalsIgnoreCase( personName )) {
			return true;
		}
		//查询用户所在的所有组织和群组
		if( ListTools.isNotEmpty( project.getParticipantPersonList() ) && project.getParticipantPersonList().contains( personName )) {
			return true;
		}
		
		List<String> groupNames = userManagerService.listGroupNamesByPerson(personName);
		List<String> unitNames = userManagerService.listUnitNamesWithPerson(personName);
		
		if( ListTools.isNotEmpty( unitNames )) {
			unitNames.retainAll( project.getParticipantUnitList() );
			if( ListTools.isNotEmpty( unitNames  )) {
				return true;
			} 
		}
		
		if( ListTools.isNotEmpty( groupNames )) {
			groupNames.retainAll( project.getParticipantGroupList() );
			if( ListTools.isNotEmpty( groupNames  )) {
				return true;
			} 
		}
		return false;
	}

	/**
	 * 添加工作任务的参与者
	 * @param id
	 * @param participants_source
	 * @return
	 * @throws Exception
	 */
	public Task addParticipants(String id, List<String> participants_source )
			throws Exception {
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id is empty.");
		}
		Task task = null;
		List<String> participants = new ArrayList<>();
		if( ListTools.isNotEmpty( participants_source )) {
			for( String participant : participants_source ) {
				if( !participants.contains( participant ) ) {
					participants.add( participant );
				}
			}
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			task = emc.find(id, Task.class);
			if ( task != null ) {
				task.setParticipantList( participants );
				emc.beginTransaction( Task.class);
				emc.check(task, CheckPersistType.all);
				emc.commit();
			} else {
				throw new Exception("task not exists.id=" + id);
			}
		} catch (Exception e) {
			throw e;
		}
		return task;
	}

	/**
	 * 向工作任务管理者中添加指定人员
	 * @param id
	 * @param managers
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Task addManager(String id, List<String> managers, EffectivePerson effectivePerson) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		Task task = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.find( id,  Task.class );
			if( task != null ) {
				task.addManageablePerson(managers);
				
				emc.beginTransaction( Task.class );
				emc.check( task , CheckPersistType.all );
				emc.commit();
			}else {
				throw new Exception("task not exists.id=" + id );
			}
		} catch (Exception e) {
			throw e;
		}
		return task;
	}

	/**
	 * 从工作任务管理者中移除指定人员
	 * @param id
	 * @param managers
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Task removeParticipants(String id, List<String> managers, EffectivePerson effectivePerson) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		Task task = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.find( id,  Task.class );
			if( task != null ) {
				task.removeManageablePerson(managers);
				
				emc.beginTransaction( Task.class );
				emc.check( task , CheckPersistType.all );
				emc.commit();
			}else {
				throw new Exception("task not exists.id=" + id );
			}
		} catch (Exception e) {
			throw e;
		}
		return task;
	}

	/**
	 * 对指定的工作任务进行归档，递归执行所有的子任务
	 * @param taskId
	 * @throws Exception
	 */
	public void archiveTask( String taskId ) throws Exception {
		if ( StringUtils.isEmpty( taskId )) {
			throw new Exception("taskId is empty.");
		}
		Task task = null;
		List<Review> subTaskReviews = null;
		List<String> subTaskReviewIds = null;
		List<String> subTaskIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			//先归档子任务
			subTaskIds = business.taskFactory().listByParent( taskId );
			if( ListTools.isNotEmpty( subTaskIds )) {
				for( String subTaskId : subTaskIds ) {
					archiveTask( subTaskId );
				}
			}
			task = emc.find( taskId,  Task.class );
			subTaskReviewIds = business.reviewFactory().listReviewByTask(taskId, 999);
			//Task
			if( task != null ) {
				task.setArchive( true );
				emc.beginTransaction( Task.class );
				emc.check( task , CheckPersistType.all );
				emc.commit();
			}
			//Review
			if( ListTools.isNotEmpty( subTaskReviewIds ) ) {
				subTaskReviews = emc.list( Review.class, subTaskReviewIds );
				if( ListTools.isNotEmpty(subTaskReviews)) {
					emc.beginTransaction( Task.class );
					for( Review review : subTaskReviews ) {
						review.setArchive( true );
						emc.check( review , CheckPersistType.all );
					}
					emc.commit();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
