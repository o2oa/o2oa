package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectDetail;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

class ProjectService {

	/**
	 * 根据项目的标识查询项目的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected Project get(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().get( flag );
	}
	
	protected ProjectDetail getDetail(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().getDetail( id );
	}
	
//	/**
//	 * 根据过滤条件查询符合要求的项目信息数量
//	 * @param emc
//	 * @param personName
//	 * @param identityNames
//	 * @param unitNames
//	 * @param groupNames
//	 * @param group 项目分组
//	 * @param title
//	 * @return
//	 * @throws Exception
//	 */
//	protected Long countWithFilter( EntityManagerContainer emc, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
//		Business business = new Business( emc );	
//		return business.projectFactory().countWithFilter( personName, identityNames, unitNames, groupNames, queryFilter );
//	}
	
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
	protected List<Project> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listWithFilter(maxCount, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
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
	protected List<Project> listWithFilter( EntityManagerContainer emc, Integer maxCount, String sequnce, String orderField, String orderType, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listWithFilter(maxCount, sequnce, orderField, orderType, personName, identityNames, unitNames, groupNames, queryFilter);
	}

	/**
	 * 向数据库持久化项目信息
	 * @param emc
	 * @param projectDetail 
	 * @param project
	 * @return
	 * @throws Exception 
	 */
	protected Project save( EntityManagerContainer emc, Project object, ProjectDetail detail ) throws Exception {
		Project project = null;
		ProjectDetail projectDetail = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( Project.createId() );
		}
		
		project = emc.find( object.getId(), Project.class );
		projectDetail = emc.find( object.getId(), ProjectDetail.class );
		
		emc.beginTransaction( Project.class );
		emc.beginTransaction( ProjectDetail.class );
		
		if( project == null ){ // 保存一个新的对象
			project = new Project();
			object.copyTo( project );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				project.setId( object.getId() );
			}
			emc.persist( project, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( project.getCreatorPerson() )) {
				object.setCreatorPerson( project.getCreatorPerson() );
			}
			object.copyTo( project, JpaObject.FieldsUnmodify  );
			emc.check( project, CheckPersistType.all );	
		}
		
		if( projectDetail == null ){ // 保存一个新的对象
			projectDetail = new ProjectDetail();
			detail.copyTo( projectDetail );
			projectDetail.setId( object.getId() );
			emc.persist( projectDetail, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			detail.copyTo( projectDetail, JpaObject.FieldsUnmodify  );
			projectDetail.setId( object.getId() );
			emc.check( projectDetail, CheckPersistType.all );	
		}
		emc.commit();
		return project;
	}

	/**
	 * 根据项目标识删除项目信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		Project project = emc.find( id, Project.class );
		ProjectDetail projectDetail = emc.find( id, ProjectDetail.class );
		if( project != null ) {
			//这里要先递归删除所有的任务信息
			emc.beginTransaction( Task.class );
			emc.beginTransaction( Project.class );
			emc.beginTransaction( ProjectDetail.class );
			emc.beginTransaction( ProjectExtFieldRele.class );
			if( project != null ) {
				emc.remove( project , CheckRemoveType.all );
			}
			if( projectDetail != null ) {
				emc.remove( projectDetail , CheckRemoveType.all );
			}
			//还需要删除所有的Task
			List<String> ids = business.taskFactory().listByProject( id );
			List<Task> tasks = business.taskFactory().list(ids);
			if( ListTools.isNotEmpty(tasks)) {
				for( Task task : tasks ) {
					emc.remove( task , CheckRemoveType.all );
				}
			}
			//还需要删除所有的ProjectExtFieldRele
			List<ProjectExtFieldRele> releList = business.projectExtFieldReleFactory().listFieldReleObjByProject( id );
			if( ListTools.isNotEmpty(releList)) {
				for( ProjectExtFieldRele rele : releList ) {
					emc.remove( rele , CheckRemoveType.all );
				}
			}
			emc.commit();
		}
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
	public List<String> listAllViewableProjectIds(EntityManagerContainer emc, int maxCount, String personName, List<String> identityNames, List<String> unitNames, List<String> groupNames, QueryFilter queryFilter) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listAllViewableProjectIds(maxCount, personName, identityNames, unitNames, groupNames, queryFilter);
	}

	public List<String> listAllProjectIds(EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.projectFactory().listAllProjectIds();
	}	
}
