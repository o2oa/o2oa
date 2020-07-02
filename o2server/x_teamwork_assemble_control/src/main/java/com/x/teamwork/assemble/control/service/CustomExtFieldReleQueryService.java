package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.CustomExtFieldRele;
import com.x.teamwork.core.entity.tools.FieldInfo;
import com.x.teamwork.core.entity.tools.CustomExtField;


/**
 * 对项目扩展属性信息查询的服务
 */
public class CustomExtFieldReleQueryService {

	private CustomExtFieldReleService customExtFieldReleService = new CustomExtFieldReleService();
	

	public List<CustomExtFieldRele> list(List<String> ids ) throws Exception {
		if ( ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return customExtFieldReleService.list( emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * 根据扩展属性关联信息的标识查询项目扩展属性关联信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CustomExtFieldRele get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return customExtFieldReleService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据项目ID查询项目/任务关联的所有扩展属性关联信息
	 * @param correlationId
	 * @return
	 * @throws Exception
	 */
	public List<CustomExtFieldRele> listReleWithCorrelation( String correlationId ) throws Exception {
		/*if (StringUtils.isEmpty(correlationId)) {
			return new ArrayList<>();
		}*/
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return customExtFieldReleService.listReleWithCorrelation(emc, correlationId);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据扩展属性类型查询扩展属性关联信息
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<CustomExtFieldRele> listReleWithType( String type ) throws Exception {
		if (StringUtils.isEmpty(type)) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return customExtFieldReleService.listReleWithType(emc, type);
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
			return customExtFieldReleService.getExtFieldDisplayName(emc, projectId, extFieldName);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public CustomExtFieldRele getExtFieldRele(String projectId, String extFieldName) throws Exception {
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return customExtFieldReleService.getExtFieldRele(emc, projectId, extFieldName);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 在可用的扩展属性里选择一个未占用的属性名
	 * @param correlationId
	 * @param displayType
	 * @return
	 * @throws Exception 
	 */
	public String getNextUseableExtFieldName( String correlationId, String displayType ) throws Exception {
		List<CustomExtFieldRele>  reles = listReleWithCorrelation( correlationId );
		 List<FieldInfo> fieldInfos = CustomExtField.listAllExtField();
		 List<String> fieldNames = new ArrayList<>();
		 for( CustomExtFieldRele projectExtFieldRele : reles  ) {
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
