package com.x.teamwork.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

public class ProjectExtFieldRelePersistService {

	private ProjectExtFieldReleService projectExtFieldReleService = new ProjectExtFieldReleService();
	
	/**
	 * 删除项目扩展属性信息
	 * @param flag
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			projectExtFieldReleService.delete( emc, id );		
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存项目扩展属性关联信息
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public ProjectExtFieldRele save( ProjectExtFieldRele object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		if( StringUtils.isEmpty( object.getProjectId() )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( object.getExtFieldName() )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( object.getDisplayName() )  ){
			throw new Exception("displayName can not empty for save field rele.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			object = projectExtFieldReleService.save( emc, object );
		} catch (Exception e) {
			throw e;
		}
		return object;
	}
	
	/**
	 * 保存项目扩展属性关联信息
	 * @param projectId
	 * @param extFieldName
	 * @param displayName
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public ProjectExtFieldRele save( String projectId, String extFieldName, String displayName, EffectivePerson effectivePerson ) throws Exception {
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( displayName )  ){
			throw new Exception("displayName can not empty for save field rele.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.save(emc, projectId, extFieldName, displayName);
		} catch (Exception e) {
			throw e;
		}
	}
}
