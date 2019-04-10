package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;
import com.x.query.core.entity.Item;

public class AppInfoService {

	public List<String> listAllIds(EntityManagerContainer emc, String documentType ) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAllIds(documentType);
	}
	
	public List<AppInfo> listAll(EntityManagerContainer emc, String documentType) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAll(documentType);
	}
	
	@SuppressWarnings("unchecked")
	public void delete( EntityManagerContainer emc, String id, String documentType, Integer maxCount ) throws Exception {
		List<String> ids = null;
		List<String> feildIds = null;
		List<String> categoryIds = null;
		CategoryInfo categoryInfo = null;
		View view = null;
		ViewFieldConfig viewFieldConfig = null;
		ViewCategory viewCategory = null;
		Document document = null;
		AppDict appDict = null;
		AppDictItem appDictItem = null;
		AppInfo appInfo = null;
		List<Item> dataItems = null;
		Business business = new Business( emc );
		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( AppDict.class );
		emc.beginTransaction( AppDictItem.class );
		emc.beginTransaction( CategoryInfo.class );
		emc.beginTransaction( Document.class );
		emc.beginTransaction( Item.class );
		emc.beginTransaction( View.class );
		emc.beginTransaction( ViewCategory.class );
		emc.beginTransaction( ViewFieldConfig.class );
		
		appInfo = emc.find( id, AppInfo.class );
		categoryIds = business.getCategoryInfoFactory().listByAppId( id );

		if( categoryIds != null && !categoryIds.isEmpty() ){
			for( String categoryId : categoryIds ){
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
		ids = business.getDocumentFactory().listByAppId( id, documentType, maxCount );
		if (ids != null && !ids.isEmpty()) {
			for (String del_id : ids) {
				document = emc.find(del_id, Document.class);
				if( document != null ){
					//删除与该文档有关的所有数据信息
					dataItems = business.itemFactory().listWithDocmentWithPath( del_id );
					if ((!dataItems.isEmpty())) {
						emc.beginTransaction(Item.class);
						for ( Item o : dataItems ) {
							emc.remove(o);
						}
					}
					//检查是否需要删除热点图片
					try {
						ThisApplication.queueDocumentDelete.send( document.getId() );
					} catch ( Exception e1 ) {
						e1.printStackTrace();
					}
					emc.remove( document, CheckRemoveType.all  );
				}
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
		List<String> cataogry_ids = null;
		List<String> document_ids = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		
		Business business = new Business(emc);
		appInfo = emc.find( wrapIn.getId(), AppInfo.class );
		emc.beginTransaction( AppInfo.class );
		if( appInfo == null ){
			appInfo = new AppInfo();
			wrapIn.copyTo( appInfo );
			if( wrapIn.getId() != null && wrapIn.getId().isEmpty() ){
				appInfo.setId( wrapIn.getId() );
			}
			appInfo.setAppAlias( appInfo.getAppName() );
			emc.persist( appInfo, CheckPersistType.all);
		}else{
			if( !wrapIn.getAppName().equals(appInfo.getAppName() )){
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
						docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
						if( docCount > 0 ) {
							totalWhileCount = (int) (docCount/queryMaxCount) + 1;
							if( totalWhileCount > 0 ) {
								while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {
									document_ids = business.getDocumentFactory().listByCategoryId( categoryId, queryMaxCount );
									if( document_ids != null && !document_ids.isEmpty() ){
										for( String docId : document_ids ){
											try {
												document = emc.find( docId, Document.class );
												document.setAppName( categoryInfo.getAppName() );
												document.setCategoryAlias( categoryInfo.getCategoryAlias() );
												if( document.getHasIndexPic() == null ){
													document.setHasIndexPic( false );
												}
												emc.check( document, CheckPersistType.all );
											}catch( Exception e ) {
												e.printStackTrace();
											}
										}
									}
								}
							}
						}
					}
				}
			}
			wrapIn.copyTo(appInfo, JpaObject.FieldsUnmodify );
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
	public List<String> listByAppAlias(EntityManagerContainer emc, String appAlias) throws Exception {
		if( appAlias == null || appAlias.isEmpty() ){
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
	
}
