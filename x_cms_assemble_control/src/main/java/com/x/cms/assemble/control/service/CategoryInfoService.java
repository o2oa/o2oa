package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.categoryinfo.WrapInCategoryInfo;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;
import com.x.cms.core.entity.element.ViewCategory;

public class CategoryInfoService {

	public List<CategoryInfo> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().list( ids );
	}
	
	public CategoryInfo get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		return emc.find(id, CategoryInfo.class );
	}

	public List<String> listByAppId( EntityManagerContainer emc, String appId ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listByAppId( appId );
	}

	public List<String> listNoViewPermissionCategoryInfoIds( EntityManagerContainer emc ) throws Exception {
		List<String> ids = null;
		Business business = new Business( emc );
		ids = business.getAppCategoryPermissionFactory().listAllCategoryInfoIds( "VIEW" );		
		return business.getCategoryInfoFactory().listNoPermissionCategoryInfoIds( ids );
	}

	public List<String> listNoViewPermissionCategoryIds(EntityManagerContainer emc, String appId, String permission ) throws Exception {
		List<String> ids = null;
		Business business = new Business( emc );
		ids = business.getAppCategoryPermissionFactory().listAllCategoryInfoIds( permission );		
		return business.getCategoryInfoFactory().listNoPermissionCategoryInfoIds( ids, appId );
	}

	public List<CategoryInfo> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listAll();
	}
	
	public CategoryInfo saveBaseInfo( EntityManagerContainer emc, CategoryInfo categoryInfo ) throws Exception {
		AppInfo appInfo = null;
		CategoryInfo categoryInfo_tmp = null;
		if( categoryInfo.getId() == null ){
			categoryInfo.setId( CategoryInfo.createId() );
		}
		appInfo = emc.find( categoryInfo.getAppId(), AppInfo.class );
		categoryInfo_tmp = emc.find( categoryInfo.getId(), CategoryInfo.class );
		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( CategoryInfo.class );
		if( categoryInfo_tmp == null ){
			categoryInfo.setAppName( appInfo.getAppName() );
			emc.persist( categoryInfo, CheckPersistType.all);
		}else{
			categoryInfo_tmp.setAppId( appInfo.getId() );
			categoryInfo_tmp.setAppName( appInfo.getAppName() );
			categoryInfo_tmp.setCategoryAlias( categoryInfo.getCategoryAlias() );
			categoryInfo_tmp.setCategoryMemo( categoryInfo.getCategoryMemo() );
			categoryInfo_tmp.setCategoryName( categoryInfo.getCategoryName() );
			categoryInfo_tmp.setFormId( categoryInfo.getFormId() );
			categoryInfo_tmp.setFormName( categoryInfo.getFormName() );
			categoryInfo_tmp.setParentId( categoryInfo.getParentId() );
			categoryInfo_tmp.setReadFormId( categoryInfo.getReadFormId() );
			categoryInfo_tmp.setReadFormName( categoryInfo.getReadFormName() );
			emc.check( categoryInfo_tmp, CheckPersistType.all );
		}
		if ( appInfo.getCategoryList() == null ){
			appInfo.setCategoryList( new ArrayList<String>());
		}
		if( !appInfo.getCategoryList().contains( categoryInfo.getId() ) ){
			appInfo.getCategoryList().add( categoryInfo.getId() );
		}
		emc.commit();
		return categoryInfo;
	}
	
	public CategoryInfo save( EntityManagerContainer emc, WrapInCategoryInfo wrap ) throws Exception {
		
		DocumentPermission documentPermission = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		AppInfo appInfo = null;
		
		List<String> document_ids = null;
		List<String> permission_ids = null;
		Business business = new Business(emc);
		
		if( wrap.getId() == null ){
			wrap.setId( CategoryInfo.createId() );
		}
		categoryInfo = emc.find( wrap.getId(), CategoryInfo.class );
		appInfo = emc.find( wrap.getAppId(), AppInfo.class );
		if( appInfo == null ){
			throw new Exception("应用栏目信息不存在！");
		}
		
		emc.beginTransaction( CategoryInfo.class );
		emc.beginTransaction( AppInfo.class );
		
		if( categoryInfo == null ){
			categoryInfo = new CategoryInfo();
			WrapTools.category_wrapin_copier.copy( wrap, categoryInfo );
			categoryInfo.setAppName( appInfo.getAppName() );
			categoryInfo.setCategoryAlias( categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
			emc.persist( categoryInfo, CheckPersistType.all);
		}else{
			if( !wrap.getCategoryName().equals( categoryInfo.getCategoryName() )){
				emc.beginTransaction( DocumentPermission.class );
				emc.beginTransaction( Document.class );
				
				//对该目录下所有的文档的栏目名称和分类别名进行调整
				document_ids = business.getDocumentFactory().listByCategoryId( wrap.getId() );
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
			
			WrapTools.category_wrapin_copier.copy( wrap, categoryInfo );
			categoryInfo.setAppName( appInfo.getAppName() );
			categoryInfo.setCategoryAlias( categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
			emc.check( categoryInfo, CheckPersistType.all );
		}
		if ( appInfo.getCategoryList() == null ){
			appInfo.setCategoryList( new ArrayList<String>());
		}
		if( !appInfo.getCategoryList().contains( categoryInfo.getId() ) ){
			appInfo.getCategoryList().add( categoryInfo.getId() );
		}
		emc.check( appInfo, CheckPersistType.all );
		emc.commit();
		return categoryInfo;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		List<String> ids = null;
		List<String> document_permission_ids = null;
		AppCategoryAdmin admin = null;
		AppCategoryPermission permission = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		DocumentPermission documentPermission = null;
		AppInfo appInfo = null;
		ViewCategory viewCategory = null;
		List<DataItem> dataItems = null;
		DataLobItem lob = null;
		Business business = new Business( emc );
		
		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( CategoryInfo.class );
		emc.beginTransaction( ViewCategory.class );
		emc.beginTransaction( AppCategoryPermission.class );
		emc.beginTransaction( AppCategoryAdmin.class );
		emc.beginTransaction( Document.class );
		emc.beginTransaction( DataItem.class );
		emc.beginTransaction( DataLobItem.class );
		emc.beginTransaction( DocumentPermission.class );
		
		categoryInfo = emc.find( id, CategoryInfo.class );	
		
		ids = business.getViewCategoryFactory().listByCategoryId(id);
		if( ids != null && !ids.isEmpty()  ){
			for( String del_id : ids ){
				viewCategory = emc.find( del_id, ViewCategory.class );
				emc.remove( viewCategory );
			}
		}
		
		//需要删除该栏目下所有的分类信息，以及所有分类所管理员和权限信息
		ids = business.getAppCategoryAdminFactory().listAppCategoryIdByCategoryId( id );
		if( ids != null && !ids.isEmpty()  ){
			for( String del_id : ids ){
				admin = emc.find( del_id, AppCategoryAdmin.class );
				emc.remove( admin );
			}
		}
		
		ids = business.getAppCategoryPermissionFactory().listPermissionByCataogry( id, null );
		if( ids != null && !ids.isEmpty()  ){
			for( String del_id : ids ){
				permission = emc.find( del_id, AppCategoryPermission.class );
				emc.remove( permission );
			}
		}
		
		if( categoryInfo != null ){
			appInfo = emc.find( categoryInfo.getFormId(), AppInfo.class );
			if( appInfo != null ){
				if( appInfo.getCategoryList() != null ){
					appInfo.getCategoryList().remove( id );
				}
				emc.check( appInfo, CheckPersistType.all );
			}
		}
		
		//还有文档以及文档权限需要删除
		ids = business.getDocumentFactory().listByCategoryId( id );
		if( ids != null && !ids.isEmpty()  ){
			for( String del_id : ids ){
				document_permission_ids = business.documentPermissionFactory().listIdsByDocumentId( del_id );
				if( document_permission_ids != null && !document_permission_ids.isEmpty() ){
					for( String permissionId : document_permission_ids ){
						documentPermission = emc.find( permissionId, DocumentPermission.class );
						emc.remove( documentPermission );
					}
				}
				document = emc.find( del_id, Document.class );
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
					emc.remove( document );
				}
			}
		}
		
		emc.remove( categoryInfo, CheckRemoveType.all );
		emc.commit();
	}
}
