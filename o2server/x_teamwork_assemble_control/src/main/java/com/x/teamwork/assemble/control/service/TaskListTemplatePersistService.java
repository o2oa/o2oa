package com.x.teamwork.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.assemble.common.date.DateOperation;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectTemplate;
import com.x.teamwork.core.entity.TaskListTemplate;

/**
 * 对项目信息查询的服务
 * 
 * @author O2LEE
 */
public class TaskListTemplatePersistService {

	private TaskListTemplateService taskListTemplateService = new TaskListTemplateService();
	private UserManagerService userManagerService = new UserManagerService();
	
	public void delete( String flag, EffectivePerson currentPerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		Boolean hasDeletePermission = false;
		Business business = null;
		try (EntityManagerContainer bc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(bc);
		}
		if( business.isManager(currentPerson) ) {
			hasDeletePermission = true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskListTemplate taskListTemplate = taskListTemplateService.get(emc, flag);
			//管理员可以删除，创建者可以删除
			if( !hasDeletePermission ) {
				//看看是不是项目创建者
				if( taskListTemplate.getOwner().equalsIgnoreCase( currentPerson.getDistinguishedName() )) {
					hasDeletePermission = true;
				}
			}
			if( !hasDeletePermission ) {
				throw new Exception("taskListTemplate delete permission denied.");
			}else {
				taskListTemplateService.delete( emc, flag );
			}			
		} catch (Exception e) {
			throw e;
		}
	}

	public TaskListTemplate save( TaskListTemplate taskListTemplate, EffectivePerson effectivePerson ) throws Exception {
		if ( taskListTemplate == null) {
			throw new Exception("taskListTemplate is null.");
		}
		if( StringUtils.isEmpty( taskListTemplate.getName() )) {
			taskListTemplate.setName("无标题项目("+ DateOperation.getNowDateTime() +")");
		}	
		if( taskListTemplate.getName().length() > 70 ) {
			taskListTemplate.setName( taskListTemplate.getName().substring(0, 70) + "..." );
		}
		if( StringUtils.isEmpty( taskListTemplate.getOwner()) ) {
			taskListTemplate.setOwner(effectivePerson.getDistinguishedName());
		}

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			taskListTemplate = taskListTemplateService.save( emc, taskListTemplate );			
		} catch (Exception e) {
			throw e;
		}
		return taskListTemplate;
	}
	
	/**
	 * 保存或者更新项目的图标信息
	 * @param projectId
	 * @param icon
	 * @throws Exception
	 */
	public void saveProjectIcon( String projectId, String icon ) throws Exception {
		if ( StringUtils.isEmpty( projectId )) {
			throw new Exception("projectId is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Project project = emc.find( projectId, Project.class );
			if( project == null ) {
				throw new Exception("Project not exists.id:" + projectId );
			}else {
				emc.beginTransaction( Project.class );
				if( StringUtils.isEmpty( icon )) {
					project.setIcon( null );
				}else {
					project.setIcon(icon);
				}
				emc.check( project, CheckPersistType.all );	
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询用户是否拥有创建项目的权限
	 * 根据配置的权限来确定项目创建权限
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public boolean checkPermissionForPersist( EffectivePerson effectivePerson, String project_creator_config ) throws Exception {
		//根据配置为全员可以创建项目
		if( "ALL".equalsIgnoreCase( project_creator_config )) {
			return true;
		}
		//系统管理员可以创建项目
		if( effectivePerson.isManager() ) {
			return true;
		}
		//工作任务系统管理员可以创建项目
		if( userManagerService.isHasPlatformRole( effectivePerson.getDistinguishedName(), "TeamWorkManager" )) {
			return true;
		}
		return false;
	}	
}
