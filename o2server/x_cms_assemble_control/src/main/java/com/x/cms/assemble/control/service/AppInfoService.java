package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class AppInfoService {

	public List<String> listAllIds(EntityManagerContainer emc, String documentType ) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAllIds(documentType);
	}
	
	public List<AppInfo> listAll(EntityManagerContainer emc, String appType, String documentType) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAll( appType, documentType);
	}
	
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		List<String> ids = null;
		List<String> feildIds = null;
		List<String> dictIds = null;
		List<String> viewReleIds = null;		
		AppInfo appInfo = null;
		Business business = new Business( emc );
		
		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( AppDict.class );
		emc.beginTransaction( AppDictItem.class );
		emc.beginTransaction( View.class );
		emc.beginTransaction( ViewCategory.class );
		emc.beginTransaction( ViewFieldConfig.class );
		
		//还有栏目下的所有列表视图，以及所有的列表视图列信息
		ids = business.getViewFactory().listByAppId( id );
		if( ListTools.isNotEmpty( ids ) ){
			View view = null;
			ViewFieldConfig viewFieldConfig = null;
			ViewCategory viewCategory = null;
			for( String del_id : ids ){
				view = emc.find( del_id, View.class );
				feildIds = business.getViewFieldConfigFactory().listByViewId( del_id );
				viewReleIds = business.getViewCategoryFactory().listByViewId( del_id );
				//删除列表配置的列
				if( ListTools.isNotEmpty( feildIds ) ){
					for( String feild_id : feildIds ){
						viewFieldConfig = emc.find( feild_id, ViewFieldConfig.class );
						if( viewFieldConfig != null ) {
							emc.remove( viewFieldConfig, CheckRemoveType.all  );
						}
					}
				}
				//删除列表与分类的关联信息
				if( ListTools.isNotEmpty( viewReleIds ) ){
					for( String rele_id : viewReleIds ){
						viewCategory = emc.find( rele_id, ViewCategory.class );
						if( viewCategory != null ) {
							emc.remove( viewCategory, CheckRemoveType.all  );
						}
					}
				}
				//删除列表信息
				if( view != null ) {
					emc.remove( view, CheckRemoveType.all  );
				}
			}
		}
		
		//还有栏目下的所有数据字典以及数据字典配置的列信息
		ids = business.getAppDictFactory().listWithAppInfo( id );
		if( ListTools.isNotEmpty( ids ) ){
			AppDict appDict = null;
			AppDictItem appDictItem = null;
			for (String del_id : ids) {
				appDict = emc.find(del_id, AppDict.class);
				dictIds = business.getAppDictItemFactory().listWithAppDict( del_id );
				if( ListTools.isNotEmpty( dictIds ) ){
					for ( String dict_id : dictIds ) {
						appDictItem = emc.find( dict_id, AppDictItem.class );
						if( appDictItem != null ) {
							emc.remove( appDictItem, CheckRemoveType.all  );	
						}
					}
				}
				if( appDict != null ) {
					emc.remove(appDict, CheckRemoveType.all );
				}
				
			}
		}
	
		//删除栏目信息
		appInfo = emc.find( id, AppInfo.class );
		if( appInfo != null ){
			emc.remove( appInfo, CheckRemoveType.all );
		}
		emc.commit();
	}
	
	public AppInfo get(EntityManagerContainer emc, String id) throws Exception {
		if( StringUtils.isEmpty( id )) {
			return null;
		}
		return emc.find( id, AppInfo.class );
	}
	
	public AppInfo getWithFlag(EntityManagerContainer emc, String flag) throws Exception {
		if( StringUtils.isEmpty( flag )) {
			return null;
		}
		return emc.flag( flag, AppInfo.class );
	}

	public Long countCategoryByAppId(EntityManagerContainer emc, String id, String documentType ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().countByAppId( id, documentType );
	}
	
	public AppInfo save( EntityManagerContainer emc, AppInfo wrapIn ) throws Exception {
		AppInfo appInfo = null;
		if( wrapIn.getId() == null ){
			wrapIn.setId( AppInfo.createId() );
		}
		appInfo = emc.find( wrapIn.getId(), AppInfo.class );		
		if( appInfo == null ){//新增一个栏目信息
			appInfo = new AppInfo();
			wrapIn.copyTo( appInfo );
			if( StringUtils.isNotEmpty( wrapIn.getId() ) ){
				appInfo.setId( wrapIn.getId() );
			}
			emc.beginTransaction( AppInfo.class );
			if( StringUtils.isEmpty( appInfo.getAppAlias() )) {
				appInfo.setAppAlias( appInfo.getAppName() );
			}
			if( StringUtils.isEmpty( appInfo.getAppType() )) {
				appInfo.setAppType( "未分类" );
			}
			emc.persist( appInfo, CheckPersistType.all);
			emc.commit();
		}else{
			wrapIn.copyTo(appInfo, JpaObject.FieldsUnmodify );
			appInfo.setAppIcon( appInfo.getAppIcon() );
			if( StringUtils.isEmpty( appInfo.getAppAlias() )) {
				appInfo.setAppAlias( appInfo.getAppName() );
			}
			emc.beginTransaction( AppInfo.class );
			if( StringUtils.isEmpty( appInfo.getAppType() )) {
				appInfo.setAppType( "未分类" );
			}
			emc.check( appInfo, CheckPersistType.all );	
			emc.commit();
		}		
		return appInfo;
	}

	public List<String> listByAppName( EntityManagerContainer emc, String appName) throws Exception {
		if( StringUtils.isEmpty(appName ) ){
			throw new Exception( "appName is null!" );
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().listByAppName( appName );
	}
	public List<String> listByAppAlias(EntityManagerContainer emc, String appAlias) throws Exception {
		if( StringUtils.isEmpty(appAlias ) ){
			throw new Exception( "appAlias is null!" );
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().listByAppAlias( appAlias );
	}
	public List<AppInfo> listAppInfoByAppAliases(EntityManagerContainer emc, List<String> appAliases) throws Exception {
		if( ListTools.isEmpty( appAliases )){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAppInfoByAppAlias( appAliases );
	}

	public List<String> listAllAppType(EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAllAppType();
	}

//	public Long countAppInfoWithAppType(EntityManagerContainer emc, String type) throws Exception {
//		Business business = new Business( emc );
//		return business.getAppInfoFactory().countAppInfoWithAppType( type );
//	}
//
//	public Long countAppInfoWithOutAppType(EntityManagerContainer emc ) throws Exception {
//		Business business = new Business( emc );
//		return business.getAppInfoFactory().countAppInfoWithOutAppType( );
//	}

	public List<String> listAppIdsWithAppType(EntityManagerContainer emc, String type) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAppIdsWithAppType(type);
	}

	public List<String> listAppIdsWithOutAppType(EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAppIdsWithOutAppType();
	}
	
}
