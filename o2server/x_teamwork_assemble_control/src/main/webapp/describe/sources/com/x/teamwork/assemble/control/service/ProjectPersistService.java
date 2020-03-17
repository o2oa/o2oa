package com.x.teamwork.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.common.date.DateOperation;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectDetail;

/**
 * 对项目信息查询的服务
 * 
 * @author O2LEE
 */
public class ProjectPersistService {

	private ProjectService projectService = new ProjectService();
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
			Project project = projectService.get(emc, flag);
			//管理员可以删除，创建者可以删除
			if( !hasDeletePermission ) {
				//看看是不是项目创建者
				if( project.getCreatorPerson().equalsIgnoreCase( currentPerson.getDistinguishedName() )) {
					hasDeletePermission = true;
				}
			}
			if( !hasDeletePermission ) {
				throw new Exception("project delete permission denied.");
			}else {
				projectService.delete( emc, flag );
			}			
		} catch (Exception e) {
			throw e;
		}
	}

	public Project save( Project project, ProjectDetail projectDetail, EffectivePerson effectivePerson ) throws Exception {
		if ( project == null) {
			throw new Exception("project is null.");
		}
		if( StringUtils.isEmpty( project.getTitle() )) {
			project.setTitle("无标题项目("+ DateOperation.getNowDateTime() +")");
		}	
		if( StringUtils.isEmpty( project.getExecutor() ) ) {
			project.setExecutor( effectivePerson.getDistinguishedName() );
		}
		if( StringUtils.isEmpty( project.getCreatorPerson() ) ) {
			project.setCreatorPerson( effectivePerson.getDistinguishedName() );
		}
		if( ListTools.isEmpty( project.getManageablePersonList()) ) {
			project.addManageablePerson( effectivePerson.getDistinguishedName());
		}
		if( project.getTitle().length() > 70 ) {
			project.setTitle( project.getTitle().substring(0, 70) + "..." );
		}
		if( StringUtils.isEmpty( project.getType()) ) {
			project.setType("普通项目");
		}
		
		if( project.getGroupCount() == null  ) {
			project.setGroupCount( 0 );
		}
		project.addParticipantPerson( effectivePerson.getDistinguishedName() );

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			project = projectService.save( emc, project, projectDetail );			
		} catch (Exception e) {
			throw e;
		}
		return project;
	}
	
	/**
	 * 项目标星
	 * @param id
	 * @param effectivePerson
	 * @return
	 * @throws Exception 
	 */
	public void star( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Project project = emc.find( id, Project.class );
			if( project == null ) {
				throw new Exception("Project not exists.id:" + id );
			}else {
				emc.beginTransaction( Project.class );
				project.addStarPerson( effectivePerson.getDistinguishedName() );
				emc.check( project, CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 取消项目标星
	 * @param flag
	 * @param effectivePerson
	 * @return
	 * @throws Exception 
	 */
	public void unStar(String flag, EffectivePerson effectivePerson) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Project project = emc.find( flag, Project.class );
			if( project == null ) {
				throw new Exception("Project not exists.id:" + flag );
			}else {
				emc.beginTransaction( Project.class );
				project.removeStarPerson( effectivePerson.getDistinguishedName() );
				emc.check( project, CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
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
