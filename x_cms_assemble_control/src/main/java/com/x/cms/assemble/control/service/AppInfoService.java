package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.appinfo.WrapInAppInfo;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class AppInfoService {

	public List<AppInfo> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().list( ids );
	}
	public List<String> listAllIds(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAllIds();
	}
	public List<AppInfo> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAll();
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		List<String> ids = null;
		List<String> feildIds = null;
		List<String> categoryIds = null;
		List<String>  document_permission_ids = null;
		AppCategoryAdmin admin = null;
		AppCategoryPermission permission = null;
		CategoryInfo categoryInfo = null;
		View view = null;
		ViewFieldConfig viewFieldConfig = null;
		ViewCategory viewCategory = null;
		Document document = null;
		DocumentPermission documentPermission = null;
		AppDict appDict = null;
		AppDictItem appDictItem = null;
		AppInfo appInfo = null;
		List<DataItem> dataItems = null;
		DataLobItem lob = null;
		Business business = new Business( emc );
		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( AppDict.class );
		emc.beginTransaction( AppDictItem.class );
		emc.beginTransaction( CategoryInfo.class );
		emc.beginTransaction( AppCategoryPermission.class );
		emc.beginTransaction( AppCategoryAdmin.class );
		emc.beginTransaction( Document.class );
		emc.beginTransaction( DataItem.class );
		emc.beginTransaction( DataLobItem.class );
		emc.beginTransaction( DocumentPermission.class );
		emc.beginTransaction( View.class );
		emc.beginTransaction( ViewCategory.class );
		emc.beginTransaction( ViewFieldConfig.class );
		
		appInfo = emc.find( id, AppInfo.class );
		categoryIds = business.getCategoryInfoFactory().listByAppId( id );
		
		//需要删除所有的应用管理员信息和应用权限 信息
		ids = business.getAppCategoryAdminFactory().listAppCategoryIdByAppId(id);
		if (ids != null && !ids.isEmpty()) {
			for (String del_id : ids) {
				admin = emc.find(del_id, AppCategoryAdmin.class);
				emc.remove(admin, CheckRemoveType.all);

			}
		}

		if( categoryIds != null && !categoryIds.isEmpty() ){
			for( String categoryId : categoryIds ){
				//需要删除该栏目下所有的分类信息，以及所有分类所管理员和权限信息
				ids = business.getAppCategoryAdminFactory().listAppCategoryIdByCategoryId(categoryId);
				if( ids != null && !ids.isEmpty()  ){
					for( String del_id : ids ){
						admin = emc.find( del_id, AppCategoryAdmin.class );
						emc.remove( admin, CheckRemoveType.all  );
					}
				}
				ids = business.getAppCategoryPermissionFactory().listPermissionByCataogry( categoryId, null );
				if( ids != null && !ids.isEmpty()  ){
					for( String del_id : ids ){
						permission = emc.find( del_id, AppCategoryPermission.class );
						emc.remove( permission, CheckRemoveType.all  );
					}
				}
				//还有分类的分类视图关系需要删除
				ids = business.getViewCategoryFactory().listByCategoryId( categoryId );
				if( ids != null && !ids.isEmpty()  ){
					for( String del_id : ids ){
						viewCategory = emc.find( del_id, ViewCategory.class );
						emc.remove( viewCategory, CheckRemoveType.all  );
					}
				}
				
				categoryInfo = emc.find(categoryId, CategoryInfo.class );
				emc.remove( categoryInfo, CheckRemoveType.all  );
			}
		}
		
		//还有文档以及文档权限需要删除
		ids = business.getDocumentFactory().listByAppId(id);
		if (ids != null && !ids.isEmpty()) {
			for (String del_id : ids) {
				document_permission_ids = business.documentPermissionFactory().listIdsByDocumentId(del_id);
				if ( document_permission_ids != null && !document_permission_ids.isEmpty() ) {
					for ( String permissionId : document_permission_ids ) {
						documentPermission = emc.find(permissionId, DocumentPermission.class);
						emc.remove( documentPermission, CheckRemoveType.all  );
					}
				}
				document = emc.find(del_id, Document.class);
				if( document != null ){
					//删除与该文档有关的所有数据信息
					dataItems = business.getDataItemFactory().listWithDocIdWithPath( del_id );
					if ((!dataItems.isEmpty())) {
						emc.beginTransaction(DataItem.class);
						emc.beginTransaction(DataLobItem.class);
						for ( DataItem o : dataItems ) {
							if (o.isLobItem()) {
								lob = emc.find(o.getLobItem(), DataLobItem.class);
								if (null != lob) {
									emc.remove(lob);
								}
							}
							emc.remove(o);
						}
					}
					emc.remove(document, CheckRemoveType.all );
				}
			}
		}
				
		ids = business.getAppCategoryPermissionFactory().listPermissionByAppInfo( id, null );
		if( ids != null && !ids.isEmpty()  ){
			for( String del_id : ids ){
				permission = emc.find( del_id, AppCategoryPermission.class );
				emc.remove( permission, CheckRemoveType.all  );
			}
		}
		//还有栏目下的所有列表视图，以及所有的列表视图列信息
		ids = business.getViewFactory().listByAppId(id);
		if( ids != null && !ids.isEmpty()  ){
			for( String del_id : ids ){
				feildIds = business.getViewFieldConfigFactory().listByViewId(del_id);
				if( feildIds != null && !feildIds.isEmpty()  ){
					for( String feild_id : feildIds ){
						viewFieldConfig = emc.find( feild_id, ViewFieldConfig.class );
						emc.remove( viewFieldConfig, CheckRemoveType.all  );
					}
				}
				view = emc.find( del_id, View.class );
				emc.remove( view, CheckRemoveType.all  );
			}
		}
		//还有栏目下的所有数据字典以及数据字典配置的列信息
		ids = business.getAppDictFactory().listWithAppInfo(id);
		if (ids != null && !ids.isEmpty()) {
			for (String del_id : ids) {
				feildIds = business.getAppDictItemFactory().listWithAppDict( del_id );
				if (feildIds != null && !feildIds.isEmpty()) {
					for (String feild_id : feildIds) {
						appDictItem = emc.find(feild_id, AppDictItem.class);
						emc.remove(appDictItem, CheckRemoveType.all );
					}
				}
				appDict = emc.find(del_id, AppDict.class);
				emc.remove(appDict, CheckRemoveType.all );
			}
		}
	
		if( appInfo != null ){
			emc.remove( appInfo, CheckRemoveType.all );
		}
		
		emc.commit();
	}

	/**
	 * 查询所有未设置可见权限的AppInfo的ID列表
	 * 先查询出所有有可见权限设置的AppInfo,再查询未设置权限的AppInfo
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public List<String> listNoViewPermissionAppInfoIds( EntityManagerContainer emc) throws Exception {
		List<String> ids = null;
		Business business = new Business( emc );
		ids = business.getAppCategoryPermissionFactory().listAllAppInfoIds( null, "VIEW" );		
		return business.getAppInfoFactory().listNoPermissionAppInfoIds( ids );
	}
	
	/**
	 * 查询所有未设置发布权限的AppInfo的ID列表
	 * 先查询出所有有发布权限设置的AppInfo,再查询未设置权限的AppInfo
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public List<String> listNonPublishPermissionAppInfoIds( EntityManagerContainer emc) throws Exception {
		List<String> ids = null;
		Business business = new Business( emc );
		ids = business.getAppCategoryPermissionFactory().listAllAppInfoIds( null, "PUBLISH" );
		return business.getAppInfoFactory().listNoPermissionAppInfoIds( ids );
	}	

	public AppInfo get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, AppInfo.class );
	}

	public Long countCategoryByAppId(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().countByAppId( id );
	}

	public List<String> listAdminPermissionAppInfoByUser( EntityManagerContainer emc, String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAppInfoIdsByAdminName( name );
	}

	public AppInfo save( EntityManagerContainer emc, WrapInAppInfo wrapIn ) throws Exception {
		AppInfo appInfo = null;
		if( wrapIn.getId() == null ){
			wrapIn.setId( AppInfo.createId() );
		}
		List<String> cataogry_ids = null;
		List<String> document_ids = null;
		List<String> permission_ids = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		DocumentPermission documentPermission = null;
		Business business = new Business(emc);
		appInfo = emc.find( wrapIn.getId(), AppInfo.class );
		emc.beginTransaction( AppInfo.class );
		if( appInfo == null ){
			appInfo = new AppInfo();
			WrapTools.appInfo_wrapin_copier.copy( wrapIn, appInfo );
			if( wrapIn.getId() != null && wrapIn.getId().isEmpty() ){
				appInfo.setId( wrapIn.getId() );
			}
			appInfo.setAppAlias( appInfo.getAppName() );
			emc.persist( appInfo, CheckPersistType.all);
		}else{
			if( !wrapIn.getAppName().equals(appInfo.getAppName() )){
				emc.beginTransaction( DocumentPermission.class );
				emc.beginTransaction( CategoryInfo.class );
				emc.beginTransaction( Document.class );
				//用户修改了栏目名称，需要对分类信息中的相关信息以及所有文档中的相关信息进行调整
				//1、调所有分类信息中的应用名称和分类别名
				cataogry_ids = business.getCategoryInfoFactory().listByAppId( appInfo.getId() );
				if( cataogry_ids != null && !cataogry_ids.isEmpty() ){
					for( String categoryId : cataogry_ids ){
						categoryInfo = emc.find( categoryId, CategoryInfo.class );
						categoryInfo.setAppName( wrapIn.getAppName() );
						categoryInfo.setCategoryAlias( wrapIn.getAppName() + "-" + categoryInfo.getCategoryName() );
						emc.check( categoryInfo, CheckPersistType.all );
						
						//对该目录下所有的文档的栏目名称和分类别名进行调整
						document_ids = business.getDocumentFactory().listByCategoryId( categoryId );
						if( document_ids != null && !document_ids.isEmpty() ){
							for( String docId : document_ids ){
								document = emc.find( docId, Document.class );
								document.setAppName( categoryInfo.getAppName() );
								document.setCategoryAlias( categoryInfo.getCategoryAlias() );
								emc.check( document, CheckPersistType.all );
								
								//对该文档所有的阅读权限信息中的栏目名称和分类别名进行调整 
								permission_ids = business.documentPermissionFactory().listIdsByDocumentId( docId );
								if( permission_ids != null && !permission_ids.isEmpty() ){
									for( String permissionId : permission_ids ){
										documentPermission = emc.find( permissionId, DocumentPermission.class );
										documentPermission.setAppName( document.getAppName() );
										documentPermission.setCategoryAlias( document.getCategoryAlias() );
										emc.check( documentPermission, CheckPersistType.all );
									}
								}
							}
						}
					}
				}
			}
			
			WrapTools.appInfo_wrapin_copier.copy( wrapIn, appInfo );
			appInfo.setAppIcon( appInfo.getAppIcon() );
			appInfo.setAppAlias( appInfo.getAppName() );
			emc.check( appInfo, CheckPersistType.all );	
		}
		emc.commit();
		return appInfo;
	}
	public List<String> listByAppName( EntityManagerContainer emc, String appName) throws Exception {
		if( appName == null || appName.isEmpty() ){
			throw new Exception( "appName is null!" );
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().listByAppName( appName );
	}
}
