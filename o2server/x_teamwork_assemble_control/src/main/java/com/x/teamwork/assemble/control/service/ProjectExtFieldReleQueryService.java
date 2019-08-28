package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.tools.FieldInfo;
import com.x.teamwork.core.entity.tools.ProjectExtField;


/**
 * 对项目扩展属性信息查询的服务
 */
public class ProjectExtFieldReleQueryService {

	private ProjectExtFieldReleService projectExtFieldReleService = new ProjectExtFieldReleService();
	

	public List<ProjectExtFieldRele> list(List<String> ids ) throws Exception {
		if ( ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.list( emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * 根据项目扩展属性关联信息的标识查询项目扩展属性关联信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectExtFieldRele get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据项目ID查询项目关联的所有扩展属性关联信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<ProjectExtFieldRele> listReleWithProject( String projectId ) throws Exception {
		if (StringUtils.isEmpty(projectId)) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.listReleWithProject(emc, projectId);
		} catch (Exception e) {
			throw e;
		}
	}

	public String getExtFieldDisplayName(String projectId, String extFieldName) throws Exception {
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.getExtFieldDisplayName(emc, projectId, extFieldName);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public ProjectExtFieldRele getExtFieldRele(String projectId, String extFieldName) throws Exception {
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return projectExtFieldReleService.getExtFieldRele(emc, projectId, extFieldName);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 在可用的扩展属性里选择一个未占用的属性名
	 * @param projectId
	 * @param displayType
	 * @return
	 * @throws Exception 
	 */
	public String getNextUseableExtFieldName( String projectId, String displayType ) throws Exception {
		List<ProjectExtFieldRele>  reles = listReleWithProject( projectId );
		 List<FieldInfo> fieldInfos = ProjectExtField.listAllExtField();
		 List<String> fieldNames = new ArrayList<>();
		 for( ProjectExtFieldRele projectExtFieldRele : reles  ) {
			 fieldNames.add( projectExtFieldRele.getExtFieldName() );
		 }
		for( FieldInfo fieldInfo : fieldInfos  ) {
			if( "RICHTEXT".equalsIgnoreCase(displayType)) {
				if( fieldInfo.getFieldName().indexOf("lob") > 0 && !fieldNames.contains( fieldInfo.getFieldName() )) {
					return fieldInfo.getFieldName();
				}
			}else {
				if( fieldInfo.getFieldName().indexOf("lob") < 0 && !fieldNames.contains( fieldInfo.getFieldName() )) {
					return fieldInfo.getFieldName();
				}
			}
		}
		return null;
	}
}
