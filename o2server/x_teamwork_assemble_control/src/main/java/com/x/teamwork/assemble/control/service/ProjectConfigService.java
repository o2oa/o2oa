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
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectConfig;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectGroupRele;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

/**
 * 对项目组查询信息的服务
 * 
 * @author O2LEE
 */
class ProjectConfigService {

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
	protected ProjectConfig get( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.projectConfigFactory().get( id ); 
	}

	/**
	 * 根据优先级ID删除优先级信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		ProjectConfig projectConfig = emc.find( id, ProjectConfig.class );
		emc.beginTransaction( ProjectConfig.class );
		if( projectConfig != null ) {
			emc.remove( projectConfig , CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 向数据库持久化项目配置信息
	 * @param emc
	 * @param projectGroup
	 * @return
	 * @throws Exception 
	 */
	protected ProjectConfig save( EntityManagerContainer emc, ProjectConfig object ) throws Exception {
		ProjectConfig projectConfig = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( ProjectConfig.createId() );
		}
		projectConfig = emc.find( object.getId(), ProjectConfig.class );		
		emc.beginTransaction( ProjectConfig.class );		
		if( projectConfig == null ){ // 保存一个新的对象
			projectConfig = new ProjectConfig();
			object.copyTo( projectConfig );
			projectConfig.setId( object.getId() );
			emc.persist( projectConfig, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( projectConfig.getOwner() )) {
				object.setOwner( projectConfig.getOwner() );
			}
			object.copyTo( projectConfig, JpaObject.FieldsUnmodify  );
			emc.check( projectConfig, CheckPersistType.all );	
		}
		emc.commit();
		return projectConfig;
	}
	
	/**
	 * 根据条件查询项目ID列表，最大查询2000条
	 * @param emc
	 * @param maxCount
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllProjectConfigIds(EntityManagerContainer emc, int maxCount, String personName, QueryFilter queryFilter) throws Exception {
		Business business = new Business( emc );
		return business.projectConfigFactory().listAllProjectConfigIds(maxCount, personName,  queryFilter);
	}
	
	/**
	 * 根据project获取项目配置信息列表
	 * @param emc
	 * @param project
	 * @return
	 * @throws Exception
	 */
	protected List<ProjectConfig> getProjectConfigByProject( EntityManagerContainer emc, String project) throws Exception {
		Business business = new Business( emc );
		return business.projectConfigFactory().getProjectConfigByProject(project);
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
	
	/**
	 * 根据过滤条件查询符合要求的项目信息列表
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param group  项目分组
	 * @param title
	 * @return
	 * @throws Exception
	 */
	protected List<ProjectConfig> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.projectConfigFactory().listWithFilter(maxCount, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param emc
	 * @param maxCount
	 * @param sequnce
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param identityNames
	 * @param unitNames
	 * @param groupNames
	 * @param group   项目分组
	 * @param title
	 * @return
	 * @throws Exception
	 */
	protected List<ProjectConfig> listWithFilter( EntityManagerContainer emc, Integer maxCount, String sequnce, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.projectConfigFactory().listWithFilter(maxCount, sequnce, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
	}

	
}
