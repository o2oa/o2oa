package com.x.teamview.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.common.date.DateOperation;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;

/**
 * 对工作任务信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskPersistService {

	private TaskService taskService = new TaskService();
	private UserManagerService userManagerService = new UserManagerService();
	
	public void delete( String flag, EffectivePerson currentPerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		Boolean hasDeletePermission = false;
		if( currentPerson.isManager() ) {
			hasDeletePermission = true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = taskService.get(emc, flag);
			//管理员可以删除，创建者可以删除
			if( !hasDeletePermission ) {
				//看看是不是工作任务创建者
				if( task.getCreatorPerson().equalsIgnoreCase( currentPerson.getDistinguishedName() )) {
					hasDeletePermission = true;
				}
			}
			if( !hasDeletePermission ) {
				throw new Exception("task delete permission denied.");
			}else {
				taskService.delete( emc, flag );
			}			
		} catch (Exception e) {
			throw e;
		}
	}

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
		if( StringUtils.isEmpty( task.getExecutorIdentity() ) ) {
			String identity = userManagerService.getIdentityWithPerson( task.getExecutor(), "min");
			task.setExecutorIdentity(identity);
		}
		if( StringUtils.isEmpty( task.getExecutorUnit() ) ) {
			String unitName = userManagerService.getUnitNameByIdentity( task.getExecutorIdentity() );
			task.setExecutorUnit(unitName);
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
		if( StringUtils.isEmpty( task.getPriority()) ) {
			task.setPriority("普通");
		}
		
		task.addParticipantPerson( effectivePerson.getDistinguishedName() );

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			task = taskService.save( emc, task, taskDetail );
		} catch (Exception e) {
			throw e;
		}
		return task;
	}

	/**
	 * 查询用户是否拥有创建工作任务的权限
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public boolean checkPermissionForPersist( EffectivePerson effectivePerson ) throws Exception {
		if( effectivePerson.isManager() ) {
			return true;
		}
		if( userManagerService.isHasPlatformRole( effectivePerson.getDistinguishedName(), "TeamWorkManager" )) {
			return true;
		}
		return false;
	}

	/**
	 * 将指定的参与者添加到工作任务中
	 * @param id
	 * @param participantPersons
	 * @param participantIdentitys
	 * @param participantUnits
	 * @param participantGroups
	 * @param effectivePerson
	 * @return
	 * @throws Exception 
	 */
	public Task addParticipants( String id, List<String> participantPersons, List<String> participantIdentitys,
			List<String> participantUnits, List<String> participantGroups, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		Task task = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.find( id,  Task.class );
			if( task != null ) {
				task.addParticipantPerson( participantPersons );
				task.addParticipantIdentity( participantIdentitys );
				task.addParticipantUnit( participantUnits );
				task.addParticipantGroup( participantGroups );
				
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
	 * 将指定的参与者从到工作任务中删除
	 * @param id
	 * @param participantPersons
	 * @param participantIdentitys
	 * @param participantUnits
	 * @param participantGroups
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Task removeParticipants(String id, List<String> participantPersons, List<String> participantIdentitys,
			List<String> participantUnits, List<String> participantGroups, EffectivePerson effectivePerson) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		Task task = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.find( id,  Task.class );
			if( task != null ) {
				task.removeParticipantPerson( participantPersons );
				task.removeParticipantIdentity( participantIdentitys );
				task.removeParticipantUnit( participantUnits );
				task.removeParticipantGroup( participantGroups );
				
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
}
