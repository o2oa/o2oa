package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.CustomExtFieldRele;

/**
 * 对项目扩展属性查询信息的服务
 * 
 * @author O2LEE,O2LJ
 */
class CustomExtFieldReleService {

	protected List<CustomExtFieldRele> list(EntityManagerContainer emc, List<String> groupIds) throws Exception {
		Business business = new Business( emc );
		return business.customExtFieldReleFactory().list(groupIds);
	}
	
	/**
	 * 根据扩展属性ID查询项目扩展属性的信息
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	protected CustomExtFieldRele get( EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.customExtFieldReleFactory().get( id );
	}

	/**
	 * 根据项目或任务ID查询关联的所有扩展属性关联信息
	 * @param emc
	 * @param correlationId
	 * @return
	 * @throws Exception 
	 */
	protected List<CustomExtFieldRele> listReleWithCorrelation(EntityManagerContainer emc, String correlationId ) throws Exception {
		Business business = new Business( emc );
		if(StringUtils.isEmpty(correlationId)){
			return business.customExtFieldReleFactory().listAllFieldReleObj();
		}else{
			return business.customExtFieldReleFactory().listFieldReleObjByCorrelation( correlationId );
		}
	}
	
	/**
	 * 根据扩展属性类型查询扩展属性关联信息
	 * @param emc
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	protected List<CustomExtFieldRele> listReleWithType(EntityManagerContainer emc, String type ) throws Exception {
		Business business = new Business( emc );
		return business.customExtFieldReleFactory().listFieldReleObjByType( type );
	}
	
	/**
	 * 根据扩展属性ID删除项目扩展属性信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		CustomExtFieldRele customExtFieldRele = emc.find( id, CustomExtFieldRele.class );
		emc.beginTransaction( CustomExtFieldRele.class );
		if( customExtFieldRele != null ) {
			emc.remove( customExtFieldRele , CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 向数据库持久化扩展属性信息
	 * @param emc
	 * @param customExtFieldRele
	 * @return
	 * @throws Exception 
	 */
	protected CustomExtFieldRele save( EntityManagerContainer emc, CustomExtFieldRele object ) throws Exception {
		/*if( StringUtils.isEmpty( object.getCorrelationId() )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}*/
		if( StringUtils.isEmpty( object.getExtFieldName() )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( object.getDisplayName() )  ){
			throw new Exception("displayName can not empty for save field rele.");
		}
		Business business = new Business( emc );
		CustomExtFieldRele customExtFieldRele = null;
		List<CustomExtFieldRele> customExtFieldReleList =  business.customExtFieldReleFactory().listWithFieldNameAndCorrelation( object.getExtFieldName(), object.getCorrelationId() );
		if( ListTools.isNotEmpty( customExtFieldReleList )) {
			customExtFieldRele = customExtFieldReleList.get( 0 );
			object.copyTo( customExtFieldRele );
			emc.beginTransaction( CustomExtFieldRele.class );
			emc.check( customExtFieldRele, CheckPersistType.all);
			emc.commit();
			return customExtFieldRele;
		}else {
			if( StringUtils.isEmpty( object.getId())) {
				object.setId( CustomExtFieldRele.createId() );
			}
			emc.beginTransaction( CustomExtFieldRele.class );
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
	protected CustomExtFieldRele save( EntityManagerContainer emc, String projectId, String extFieldName,  String displayName ) throws Exception {
		CustomExtFieldRele projectExtFieldRele = null;
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( displayName )  ){
			throw new Exception("displayName can not empty for save field rele.");
		}
		projectExtFieldRele = new CustomExtFieldRele();
		projectExtFieldRele.setCorrelationId(projectId);
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
		List<CustomExtFieldRele> fieldReles = business.customExtFieldReleFactory().listWithFieldNameAndCorrelation( extFieldName, projectId );
		if(ListTools.isNotEmpty(fieldReles )) {
			return fieldReles.get(0).getDisplayName();
		}
		return null;
	}
	
	public CustomExtFieldRele getExtFieldRele(EntityManagerContainer emc, String projectId, String extFieldName) throws Exception {
		if( StringUtils.isEmpty( projectId )  ){
			throw new Exception("projectId can not empty for save field rele.");
		}
		if( StringUtils.isEmpty( extFieldName )  ){
			throw new Exception("extFieldName can not empty for save field rele.");
		}
		Business business = new Business( emc );
		List<CustomExtFieldRele> fieldReles = business.customExtFieldReleFactory().listWithFieldNameAndCorrelation( extFieldName, projectId );
		if(ListTools.isNotEmpty(fieldReles )) {
			return fieldReles.get(0);
		}
		return null;
	}	
}
