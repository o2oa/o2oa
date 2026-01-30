package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Priority;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectGroupRele;

/**
 * 对项目组查询信息的服务
 * 
 * @author O2LEE
 */
class PriorityService {

	protected List<ProjectGroup> list(EntityManagerContainer emc, List<String> groupIds) throws Exception {
		Business business = new Business( emc );
		return business.projectGroupFactory().list(groupIds);
	}
	
	/**
	 * 根据优先级ID查询优先级的信息
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	protected Priority get( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.priorityFactory().get( id ); 
	}
	
	/**
	 * 根据优先级名称查询优先级的信息
	 * @param emc
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	protected List<Priority> getByName( EntityManagerContainer emc, String name ) throws Exception {
		Business business = new Business( emc );
		return business.priorityFactory().getByName( name ); 
	}

	/**
	 * 根据优先级ID删除优先级信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		Priority priority = emc.find( id, Priority.class );
		emc.beginTransaction( Priority.class );
		if( priority != null ) {
			emc.remove( priority , CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 向数据库持久化优先级信息
	 * @param emc
	 * @param projectGroup
	 * @return
	 * @throws Exception 
	 */
	protected Priority save( EntityManagerContainer emc, Priority object ) throws Exception {
		Priority priority = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( Priority.createId() );
		}
		priority = emc.find( object.getId(), Priority.class );		
		emc.beginTransaction( Priority.class );		
		if( priority == null ){ // 保存一个新的对象
			priority = new Priority();
			object.copyTo( priority );
			priority.setId( object.getId() );
			emc.persist( priority, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( priority.getOwner() )) {
				object.setOwner( priority.getOwner() );
			}
			object.copyTo( priority, JpaObject.FieldsUnmodify  );
			emc.check( priority, CheckPersistType.all );	
		}
		emc.commit();
		return priority;
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
	 * 根据用户列示所有的优先级信息列表
	 * @param emc
	 * @param person
	 * @return
	 * @throws Exception
	 */
	protected List<Priority> listPriority( EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.priorityFactory().listPriority();
	}
	
	/**
	 * 根据用户列示所有的优先级信息列表
	 * @param emc
	 * @param person
	 * @return
	 * @throws Exception
	 */
	protected List<Priority> listPriorityByPerson( EntityManagerContainer emc, String person) throws Exception {
		Business business = new Business( emc );
		return business.priorityFactory().listPriorityByPerson(person);
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
