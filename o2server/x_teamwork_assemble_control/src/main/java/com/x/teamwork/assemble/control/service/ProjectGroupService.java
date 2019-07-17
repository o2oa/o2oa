package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectGroupRele;

/**
 * 对项目组查询信息的服务
 * 
 * @author O2LEE
 */
class ProjectGroupService {

	protected List<ProjectGroup> list(EntityManagerContainer emc, List<String> groupIds) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupFactory().list(groupIds);
	}
	
	/**
	 * 根据项目组ID查询项目组的信息
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	protected ProjectGroup get( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupFactory().get( id );
	}

	/**
	 * 根据项目组ID删除项目组信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		ProjectGroup projectGroup = emc.find( id, ProjectGroup.class );
		emc.beginTransaction( ProjectGroup.class );
		emc.beginTransaction( ProjectGroupRele.class );
		//先删除所有的关联
		List<String> ids = business.projectGroupReleFactory().listByGroup( id );
		if( ListTools.isNotEmpty( ids )) {
			List<ProjectGroupRele> reles = business.projectGroupReleFactory().list( ids );
			for( ProjectGroupRele rele : reles ) {
				emc.remove( rele , CheckRemoveType.all );
			}
		}
		if( projectGroup != null ) {
			emc.remove( projectGroup , CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 向数据库持久化项目组信息
	 * @param emc
	 * @param projectGroup
	 * @return
	 * @throws Exception 
	 */
	protected ProjectGroup save( EntityManagerContainer emc, ProjectGroup object ) throws Exception {
		ProjectGroup projectGroup = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( ProjectGroup.createId() );
		}
		projectGroup = emc.find( object.getId(), ProjectGroup.class );		
		emc.beginTransaction( ProjectGroup.class );		
		if( projectGroup == null ){ // 保存一个新的对象
			projectGroup = new ProjectGroup();
			object.copyTo( projectGroup );
			projectGroup.setId( object.getId() );
			emc.persist( projectGroup, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( projectGroup.getCreatorPerson() )) {
				object.setCreatorPerson( projectGroup.getCreatorPerson() );
			}
			object.copyTo( projectGroup, JpaObject.FieldsUnmodify  );
			emc.check( projectGroup, CheckPersistType.all );	
		}
		emc.commit();
		return projectGroup;
	}
	
	/**
	 * 将项目添加到项目组中去
	 * @param emc
	 * @param projectId
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	protected ProjectGroupRele addToGroup( EntityManagerContainer emc, String projectId, String groupId ) throws Exception {
		List<ProjectGroupRele> reles = null;
		Business business = new Business( emc );
		ProjectGroupRele projectGroupRele = null;
		ProjectGroup  projectGroup = emc.find( groupId, ProjectGroup.class );		
		if( projectGroup != null ){
			reles = business.projectGroupReleFactory().listWithGroupAndProject(groupId, projectId);
			if( ListTools.isEmpty( reles )) {
				emc.beginTransaction( ProjectGroup.class );		
				projectGroupRele = new ProjectGroupRele();
				projectGroupRele.setId( ProjectGroupRele.createId() );
				projectGroupRele.setProjectId(projectId);
				projectGroupRele.setGroupId(groupId);
				emc.persist( projectGroupRele, CheckPersistType.all );	
				emc.commit();
			}
		}
		return projectGroupRele;
	}
	
	/**
	 *  将项目从项目组中除去
	 * @param emc
	 * @param projectId
	 * @param groupId
	 * @throws Exception
	 */
	protected void removeFromGroup( EntityManagerContainer emc, String projectId, String groupId ) throws Exception {
		Business business = new Business( emc );
		List<ProjectGroupRele> reles = business.projectGroupReleFactory().listWithGroupAndProject(groupId, projectId);
		if( ListTools.isNotEmpty( reles )) {
			emc.beginTransaction( ProjectGroup.class );
			for( ProjectGroupRele rele : reles ) {
				emc.remove( rele, CheckRemoveType.all );	
			}
			emc.commit();
		}
	}

	/**
	 * 根据用户列示所有的项目组信息列表
	 * @param emc
	 * @param person
	 * @return
	 * @throws Exception
	 */
	protected List<ProjectGroup> listGroupByPerson( EntityManagerContainer emc, String person) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupFactory().listGroupByPerson(person);
	}
	
	/**
	 * 根据用户列示所有的项目组信息ID列表
	 * @param emc
	 * @param person
	 * @return
	 * @throws Exception
	 */
	protected List<String> listGroupIdsByPerson( EntityManagerContainer emc, String person) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupFactory().listByPerson(person);
	}

	/**
	 * 根据项目组ID，查询项目组内所有的项目ID列表
	 * @param emc
	 * @param group
	 * @return
	 * @throws Exception
	 */
	protected List<String> listProjectIdByGroup(EntityManagerContainer emc, String group) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupReleFactory().listProjectIdByGroup(group);
	}

	/**
	 * 根据项目ID查询项目所有的项目组关联信息对象列表
	 * @param emc
	 * @param projectId
	 * @return
	 * @throws Exception 
	 */
	protected List<ProjectGroupRele> listReleWithProject(EntityManagerContainer emc, String projectId) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupReleFactory().listReleWithProject( projectId );
	}

	/**
	 * 判断是否存在分组和项目的关联
	 * @param emc
	 * @param projectId
	 * @param groupId
	 * @return
	 * @throws Exception 
	 */
	protected List<ProjectGroupRele> listReleWithProjectAndGroup(EntityManagerContainer emc, String projectId, String groupId) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupReleFactory().listWithGroupAndProject( groupId, projectId );
	}

	
}
