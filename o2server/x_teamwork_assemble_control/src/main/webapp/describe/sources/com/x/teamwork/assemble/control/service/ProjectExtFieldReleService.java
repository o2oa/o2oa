package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

/**
 * 对项目扩展属性查询信息的服务
 * 
 * @author O2LEE
 */
class ProjectExtFieldReleService {

	protected List<ProjectExtFieldRele> list(EntityManagerContainer emc, List<String> groupIds) throws Exception {
		Business business = new Business( emc );
		return business.projectExtFieldReleFactory().list(groupIds);
	}
	
	/**
	 * 根据项目扩展属性ID查询项目扩展属性的信息
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	protected ProjectExtFieldRele get( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.projectExtFieldReleFactory().get( id );
	}

	/**
	 * 根据项目ID查询项目关联的所有扩展属性关联信息
	 * @param emc
	 * @param projectId
	 * @return
	 * @throws Exception 
	 */
	protected List<ProjectExtFieldRele> listReleWithProject(EntityManagerContainer emc, String projectId ) throws Exception {
		Business business = new Business( emc );
		return business.projectExtFieldReleFactory().listFieldReleObjByProject( projectId );
	}
	
	/**
	 * 根据项目扩展属性ID删除项目扩展属性信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		ProjectExtFieldRele projectExtFieldRele = emc.find( id, ProjectExtFieldRele.class );
		emc.beginTransaction( ProjectExtFieldRele.class );
		if( projectExtFieldRele != null ) {
			emc.remove( projectExtFieldRele , CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 向数据库持久化项目扩展属性信息
	 * @param emc
	 * @param projectExtFieldRele
	 * @return
	 * @throws Exception 
	 */
	protected ProjectExtFieldRele save( EntityManagerContainer emc, ProjectExtFieldRele object ) throws Exception {
		if( StringUtils.isEmpty( object.getProjectId() )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( object.getExtFieldName() )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( object.getDisplayName() )  ){
			throw new Exception("displayName can not empty for save field rele.");
		}
		Business business = new Business( emc );
		ProjectExtFieldRele projectExtFieldRele = null;
		List<ProjectExtFieldRele> projectExtFieldReleList =  business.projectExtFieldReleFactory().listWithFieldNameAndProject( object.getExtFieldName(), object.getProjectId() );
		if( ListTools.isNotEmpty( projectExtFieldReleList )) {
			projectExtFieldRele = projectExtFieldReleList.get( 0 );
			object.copyTo( projectExtFieldRele );
			emc.beginTransaction( ProjectExtFieldRele.class );
			emc.check( projectExtFieldRele, CheckPersistType.all);
			emc.commit();
			return projectExtFieldRele;
		}else {
			if( StringUtils.isEmpty( object.getId())) {
				object.setId( ProjectExtFieldRele.createId() );
			}
			emc.beginTransaction( ProjectExtFieldRele.class );
			emc.persist( object, CheckPersistType.all);
			emc.commit();
			return object;
		}
	}
	
	/**
	 * 向数据库持久化项目扩展属性信息
	 * @param emc
	 * @param projectExtFieldRele
	 * @return
	 * @throws Exception 
	 */
	protected ProjectExtFieldRele save( EntityManagerContainer emc, String projectId, String extFieldName,  String displayName ) throws Exception {
		ProjectExtFieldRele projectExtFieldRele = null;
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( displayName )  ){
			throw new Exception("displayName can not empty for save field rele.");
		}
		projectExtFieldRele = new ProjectExtFieldRele();
		projectExtFieldRele.setProjectId(projectId);
		projectExtFieldRele.setExtFieldName(extFieldName);
		projectExtFieldRele.setDisplayName(displayName);	
		return  save( emc, projectExtFieldRele );
	}

	public String getExtFieldDisplayName(EntityManagerContainer emc, String projectId, String extFieldName) throws Exception {
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		Business business = new Business( emc );
		List<ProjectExtFieldRele> fieldReles = business.projectExtFieldReleFactory().listWithFieldNameAndProject( extFieldName, projectId );
		if(ListTools.isNotEmpty(fieldReles )) {
			return fieldReles.get(0).getDisplayName();
		}
		return null;
	}
	
	public ProjectExtFieldRele getExtFieldRele(EntityManagerContainer emc, String projectId, String extFieldName) throws Exception {
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		Business business = new Business( emc );
		List<ProjectExtFieldRele> fieldReles = business.projectExtFieldReleFactory().listWithFieldNameAndProject( extFieldName, projectId );
		if(ListTools.isNotEmpty(fieldReles )) {
			return fieldReles.get(0);
		}
		return null;
	}	
}
