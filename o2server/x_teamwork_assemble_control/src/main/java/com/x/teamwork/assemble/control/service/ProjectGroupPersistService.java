package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectGroupRele;

public class ProjectGroupPersistService {

	private ProjectGroupService projectGroupService = new ProjectGroupService();
	
	/**
	 * 删除项目组信息
	 * @param flag
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			projectGroupService.delete( emc, id );		
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存项目组信息
	 * @param object
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public ProjectGroup save( ProjectGroup object, EffectivePerson effectivePerson ) throws Exception {
		if ( object == null) {
			throw new Exception("object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			object.setCreatorPerson( effectivePerson.getDistinguishedName() );
			object.setOwner( effectivePerson.getDistinguishedName() );
			object = projectGroupService.save( emc, object );
		} catch (Exception e) {
			throw e;
		}
		return object;
	}
	
	/**
	 * 将项目添加到项目组中去
	 * @param projectId
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public ProjectGroupRele addToGroup( String projectId, String groupId ) throws Exception {
		if ( StringUtils.isEmpty( projectId )) {
			throw new Exception("projectId is empty.");
		}
		if ( StringUtils.isEmpty( groupId )) {
			throw new Exception("groupId is empty.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectGroupService.addToGroup(emc, projectId, groupId);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *  将项目从项目组中除去
	 * @param emc
	 * @param projectId
	 * @param groupId
	 * @throws Exception
	 */
	public void removeFromGroup( String projectId, String groupId ) throws Exception {
		if ( StringUtils.isEmpty( projectId )) {
			throw new Exception("projectId is empty.");
		}
		if ( StringUtils.isEmpty( groupId )) {
			throw new Exception("groupId is empty.");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			projectGroupService.removeFromGroup( emc, groupId, projectId );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 先删除项目所有已经关联的项目组，然后再将新的项目组与项目关联
	 * @param projectId
	 * @param groups
	 * @throws Exception 
	 */
	public void releProjectToGroup( String projectId, List<String> groups ) throws Exception {
		if ( StringUtils.isEmpty( projectId )) {
			return;
		}
		ProjectGroupRele projectGroupRele = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			//查询项目所有已经关联的项目组关联信息
			List<ProjectGroupRele> reles = projectGroupService.listReleWithProject( emc, projectId );
			
			emc.beginTransaction( ProjectGroupRele.class );			
			//删除项目所有已经关联的项目组关联信息
			if( ListTools.isNotEmpty( reles )) {
				for( ProjectGroupRele rele : reles ) {
					emc.remove( rele, CheckRemoveType.all );
				}
			}
			
			//将新的项目组与项目关联
			if( ListTools.isNotEmpty( groups )) {
				for( String group : groups ) {
					projectGroupRele = new ProjectGroupRele();
					projectGroupRele.setProjectId( projectId );
					projectGroupRele.setGroupId( group );
					emc.persist( projectGroupRele, CheckPersistType.all );
				}
			}
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}
}
