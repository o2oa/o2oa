package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.categoryinfo.WrapOutCategoryInfo;
import com.x.cms.assemble.control.service.AppCategoryAdminServiceAdv;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {

	protected Ehcache cache = ApplicationCache.instance().getCache(AppInfo.class);

	protected AppCategoryAdminServiceAdv appCategoryAdminServiceAdv = new AppCategoryAdminServiceAdv();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();

	/**
	 * TODO:查询所有的栏目信息列表，并且在栏目信息里包括所有的分类信息列表
	 * @return
	 * @throws Exception
	 */
	protected List<WrapOutAppInfo> getAllAppInfoWithCategory() throws Exception {
		List<AppInfo> appInfoList = null;
		List<WrapOutAppInfo> wraps = null;
		appInfoList = appInfoServiceAdv.listAll();
		if ( appInfoList != null && !appInfoList.isEmpty() ) {
			wraps = WrapTools.appInfo_wrapout_copier.copy(appInfoList);
			composeAllCategoriesInAppInfo( wraps );
			SortTools.desc( wraps, "appInfoSeq" );
		}
		return wraps;
	}
	
	/**
	 * TODO:查询指定的栏目信息列表，并且在栏目信息里包括所有的分类信息列表
	 * @return
	 * @throws Exception
	 */
	protected List<WrapOutAppInfo> getAppInfoWithCategory( List<String> ids ) throws Exception {
		List<AppInfo> appInfoList = null;
		List<WrapOutAppInfo> wraps = null;
		appInfoList = appInfoServiceAdv.list( ids );
		if ( appInfoList != null && !appInfoList.isEmpty() ) {
			wraps = WrapTools.appInfo_wrapout_copier.copy(appInfoList);
			composeAllCategoriesInAppInfo( wraps );
			SortTools.desc( wraps, "appInfoSeq" );
		}
		return wraps;
	}
	
	/**
	 * TODO:为指定的栏目列表装配所有的分类列表信息
	 * 
	 * @param wraps
	 * @return
	 * @throws Exception
	 */
	protected List<WrapOutAppInfo> composeAllCategoriesInAppInfo( List<WrapOutAppInfo> wraps ) throws Exception {
		List<String> category_ids = null;
		List<WrapOutCategoryInfo> wrapOutCatacoryList = null;
		List<CategoryInfo> catecoryList = null;
		if ( wraps != null && wraps.size() > 0 ) {
			for ( WrapOutAppInfo wrap_appInfo : wraps ) {
				category_ids = categoryInfoServiceAdv.listByAppId(wrap_appInfo.getId());
				catecoryList = categoryInfoServiceAdv.list(category_ids);
				if (catecoryList != null && !catecoryList.isEmpty()) {
					wrapOutCatacoryList = WrapTools.category_wrapout_copier.copy(catecoryList);
					wrap_appInfo.setWrapOutCategoryList(wrapOutCatacoryList);
				}
			}
		}
		return wraps;
	}

	/**
	 * TODO:当前登录者栏目分类可见列表查询<br/>
	 * 根据人员的权限获取可见的栏目信息列表，并且根据人员可见范围获取每个栏目中的可见分类信息列表<br/>
	 * 第一部分，查询可以全权访问的栏目信息（栏目下所有分类均可阅读）<br/>
	 * 1、全员可见的，栏目和分类均未设置可见权限的所有栏目信息<br/>
	 * 2、查询当前登录者有管理权限的所有栏目信息<br/>
	 * 3、查询当前登录者有发布权限的所有栏目信息<br/>
	 * <br/>
	 * 第二部分，查询当前登录者可阅读的所有分类ID<br/>
	 * 1、在所有没有设置可见范围的栏目中，所有没有设置可见范围的分类ID列表<br/>
	 * 2、查询当前登录者可以管理的分类ID<br/>
	 * 3、查询当前登录者可见或者可以发布文档的分类ID<br/>
	 * 组织第二部分这些分类信息到第一部分的栏目信息中，可能栏目信息在第一部分中不存在，也可能有重复的分类信息<br/>
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected List<WrapOutAppInfo> getViewAbleAppInfoByPermission( String name ) throws Exception {
		List<String> departmentNames = null;
		List<String> companyNames = null;
		List<String> groupNames = null;
		List<CategoryInfo> catecoryList = null;
		List<String> viewableAppInfoIds = new ArrayList<>();
		List<String> viewableCategoryIds = new ArrayList<>();
		List<WrapOutAppInfo> wraps = null;
		
		departmentNames = userManagerService.listDepartmentNameByEmployeeName( name );
		companyNames = userManagerService.listCompanyNameByEmployeeName( name );
		groupNames = userManagerService.listGroupNamesByPersonName( name );
		
		//第一部分，查询可以全权访问的栏目信息
		viewableAppInfoIds = getNoPermissionAppIdList( viewableAppInfoIds );		
		viewableAppInfoIds = getManageableAppIdList( name, viewableAppInfoIds );		
		viewableAppInfoIds = getViewableAppIdList( name, departmentNames, companyNames, groupNames, viewableAppInfoIds );
		
		//第二部分，查询阅读者设置为当前登录者的分类ID
		viewableCategoryIds = getNoViewPermissionCategoryIds( viewableCategoryIds );
		viewableCategoryIds = getManageableCategoryIds( name, viewableCategoryIds );
		viewableCategoryIds = getViewableCategoryIds( name, departmentNames, companyNames, groupNames, viewableCategoryIds );
				
		wraps = getAppInfoWithCategory( viewableAppInfoIds );
		if( wraps == null ){ wraps = new ArrayList<>(); }
		
		if( viewableCategoryIds != null && !viewableCategoryIds.isEmpty() ){
			catecoryList = categoryInfoServiceAdv.list( viewableCategoryIds );
		}
		return composeCategoriesIntoAppInfo(wraps, catecoryList );
	}

	
	
	/**
	  * TODO:当前登录者文档发布栏目分类列表查询<br/>
	  * 根据人员的权限获取可以发布文档的栏目信息列表，并且根据人员可见范围获取每个栏目中可以发布文档的分类信息列表<br/>
	  * 第一部分，查询可以全权访问的栏目信息
	  * 1、全员可见的，栏目和分类均未设置发布权限的所有栏目信息<br/>
	  * 2、查询当前登录者有管理权限的所有栏目信息<br/>
	  * 3、查询当前登录者有发布权限的所有栏目信息<br/>
	  * 
	  * 第二部分，查询发布者设置为当前登录者的分类ID
	  * 1、在所有没有设置发布范围的栏目中，所有没有设置发布范围的分类ID列表<br/>
	  * 2、查询当前登录者有权限管理的分类ID<br/>
	  * 3、查询当前登录者有权限发布文档的分类ID<br/>
	  * 组织第二部分这些分类信息到第一部分的栏目信息中，可能栏目信息在第一部分中不存在，也可能有重复的分类信息<br/>
	  * 
	  * @param name
	  * @return
	  * @throws Exception
	  */
	protected List<WrapOutAppInfo> getPublishAbleAppInfoByPermission(String name) throws Exception {
		List<String> departmentNames = null;
		List<String> companyNames = null;
		List<String> groupNames = null;
		List<CategoryInfo> catecoryList = null;
		List<String> publishableAppInfoIds = new ArrayList<>();
		List<String> publishableCategoryIds = new ArrayList<>();
		List<WrapOutAppInfo> wraps = null;

		departmentNames = userManagerService.listDepartmentNameByEmployeeName(name);
		companyNames = userManagerService.listCompanyNameByEmployeeName(name);
		groupNames = userManagerService.listGroupNamesByPersonName(name);

		// ===================================第一部分，查询栏目和分类均未设置过发布权限的栏目信息列表
		publishableAppInfoIds = getNoPubilshPermissionAppIds( publishableAppInfoIds );
		publishableAppInfoIds = getManageableAppIdList( name, publishableAppInfoIds);
		publishableAppInfoIds = getPublishableAppIdList(name, departmentNames, companyNames, groupNames, publishableAppInfoIds );
		
		// ======================================第二部分，查询发布者设置为当前登录者的分类ID
		publishableCategoryIds = getNoPublishPermissionCategoryIds( publishableCategoryIds );
		publishableCategoryIds = getManageableCategoryIds(name, publishableCategoryIds );
		publishableCategoryIds = getPublishableCategoryIds(name, departmentNames, companyNames, groupNames, publishableCategoryIds );
		
		wraps = getAppInfoWithCategory( publishableAppInfoIds );		
		if( wraps == null ){ wraps = new ArrayList<>(); }
		
		// 查询可以发布信息的分类信息列表
		if (publishableCategoryIds != null && !publishableCategoryIds.isEmpty()) {
			catecoryList = categoryInfoServiceAdv.list(publishableCategoryIds);
		}
		
		return composeCategoriesIntoAppInfo( wraps, catecoryList );
	}
	
	/**
	 * TODO:将查询到的分类结果装配到栏目信息列表里，如果栏目信息不存在，那么查询栏目信息并且添加到栏目列表里
	 * 
	 * @param wraps
	 * @param catecoryList
	 * @return
	 * @throws Exception
	 */
	protected List<WrapOutAppInfo> composeCategoriesIntoAppInfo( List<WrapOutAppInfo> wraps, List<CategoryInfo> catecoryList ) throws Exception {
		if( catecoryList == null || catecoryList.isEmpty() ){
			return wraps;
		}
		AppInfo appInfo = null;
		Boolean appExists = null;
		Boolean categoryExists = null;
		WrapOutAppInfo wrap_appinfo = null;
		WrapOutCategoryInfo wrap_category = null;
		for( CategoryInfo category : catecoryList ){
			appExists = false;
			for ( WrapOutAppInfo wrap_appInfo : wraps ) {
				if( wrap_appInfo.getId().equals( category.getAppId() )){
					appExists = true;//appExists = true, 找到了, 看看该栏目下分类是否已经存在了
					if( wrap_appInfo.getWrapOutCategoryList() != null && !wrap_appInfo.getWrapOutCategoryList().isEmpty() ){
						categoryExists = false;
						for( WrapOutCategoryInfo category_tmp : wrap_appInfo.getWrapOutCategoryList() ){
							if( category_tmp.getId().equals( category.getId() )){
								categoryExists = true;
							}
						}
						if( !categoryExists ){
							wrap_category = WrapTools.category_wrapout_copier.copy( category );
							wrap_category.setId( category.getId() );
							wrap_category.setAppId( category.getAppId() );
							wrap_appInfo.getWrapOutCategoryList().add( wrap_category );
						}
					}else{
						wrap_appInfo.setWrapOutCategoryList( new ArrayList<>() );
						wrap_category = WrapTools.category_wrapout_copier.copy( category );
						wrap_category.setId( category.getId() );
						wrap_category.setAppId( category.getAppId() );
						wrap_appInfo.getWrapOutCategoryList().add( wrap_category );
					}
					
				}
			}
			if( !appExists ){
				//在原来的wraps里没有该栏目，直接添加栏目和当前的category信息
				appInfo = appInfoServiceAdv.get( category.getAppId() );
				if( appInfo != null ){
					wrap_category = WrapTools.category_wrapout_copier.copy( category );
					wrap_appinfo = WrapTools.appInfo_wrapout_copier.copy( appInfo );
					if( wrap_category != null ){
						wrap_appinfo.setWrapOutCategoryList( new ArrayList<>() );
						wrap_appinfo.getWrapOutCategoryList().add( wrap_category );
						wraps.add( wrap_appinfo );
					}
				}
			}
		}
		return wraps;
	}
	
	/**
	 * TODO:查询没有设置任何权限的栏目信息,栏目下所有的分类都没有配置任何权限<br/>
	 * 所以该栏目下所有的分类默认所有人可以阅读信息
	 * 
	 * @param viewableAppInfoIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getNoPermissionAppIdList( List<String> viewableAppInfoIds ) throws Exception {
		if( viewableAppInfoIds == null ){
			viewableAppInfoIds = new ArrayList<>();
		}
		List<String> app_ids = appInfoServiceAdv.listNoPermissionAppInfoIds();
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				if( !viewableAppInfoIds.contains( id )){
					viewableAppInfoIds.add( id );
				}
			}
		}
		return viewableAppInfoIds;
	}
	
	/**
	 * TODO:查询没有设置任何发布权限的栏目信息（该栏目下所有的分类默认所有人可以发布信息）
	 * 
	 * @param publishableAppInfoIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getNoPubilshPermissionAppIds( List<String> publishableAppInfoIds ) throws Exception {
		if( publishableAppInfoIds == null ){
			publishableAppInfoIds = new ArrayList<>();
		}
		List<String> app_ids = appInfoServiceAdv.listNoPubilshPermissionAppIds();
		if (app_ids != null && !app_ids.isEmpty()) {
			for ( String id : app_ids ) {
				if (!publishableAppInfoIds.contains(id)) {
					publishableAppInfoIds.add(id);
				}
			}
		}
		return publishableAppInfoIds;
	}	
	
	/**
	 * TODO:查询登录者可以管理的所有栏目信息（这些栏目信息下所有的分类均可以由该登录者阅读信息）
	 * 
	 * @param personName
	 * @param viewableAppInfoIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getManageableAppIdList( String personName, List<String> viewableAppInfoIds ) throws Exception {
		if( viewableAppInfoIds == null ){
			viewableAppInfoIds = new ArrayList<>();
		}
		List<String> app_ids = appInfoServiceAdv.listManageableAppIds( personName );
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				if( !viewableAppInfoIds.contains( id )){
					viewableAppInfoIds.add( id );
				}
			}
		}
		return viewableAppInfoIds;
	}
	
	/**
	 * TODO:查询当前登录者可以管理的分类ID
	 * 
	 * @param personName
	 * @param viewableCategoryIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getManageableCategoryIds( String personName, List<String> viewableCategoryIds ) throws Exception {
		if( viewableCategoryIds == null ){
			viewableCategoryIds = new ArrayList<>();
		}
		List<String> category_ids = appCategoryAdminServiceAdv.listManageableCategoryIds( personName );
		if( category_ids != null && !category_ids.isEmpty() ){
			for( String id : category_ids ){
				if( !viewableCategoryIds.contains( id )){
					viewableCategoryIds.add( id );
				}
			}
		}
		return viewableCategoryIds;
	}
			
	/**
	 * TODO:根据权限查询登录者可见或者可发布的栏目信息，这些栏目信息下所有的分类均可以由该登录者阅读信息
	 * 
	 * @param personName
	 * @param departmentNames
	 * @param companyNames
	 * @param groupNames
	 * @param viewableAppInfoIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getViewableAppIdList( String personName, List<String> departmentNames, List<String> companyNames, List<String> groupNames, List<String> viewableAppInfoIds ) throws Exception {
		if( viewableAppInfoIds == null ){
			viewableAppInfoIds = new ArrayList<>();
		}
		List<String> app_ids = appInfoServiceAdv.listViewableAppIds( personName, departmentNames, companyNames, groupNames );
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				if( !viewableAppInfoIds.contains( id )){
					viewableAppInfoIds.add( id );
				}
			}
		}
		return viewableAppInfoIds;
	}
	
	/**
	 * TODO:根据权限查询登录者可见或者可发布的分类信息
	 * 
	 * @param personName
	 * @param departmentNames
	 * @param companyNames
	 * @param groupNames
	 * @param viewableCategoryIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getViewableCategoryIds( String personName, List<String> departmentNames, List<String> companyNames, List<String> groupNames, List<String> viewableCategoryIds ) throws Exception {
		if( viewableCategoryIds == null ){
			viewableCategoryIds = new ArrayList<>();
		}
		List<String> category_ids = appInfoServiceAdv.listViewableCategoryIds( personName, departmentNames, companyNames, groupNames, null );
		if( category_ids != null && !category_ids.isEmpty() ){
			for( String id : category_ids ){
				if( !viewableCategoryIds.contains( id )){
					viewableCategoryIds.add( id );
				}
			}
		}
		return viewableCategoryIds;
	}
		
	/**
	 * TODO:查询登录用户可以发布的栏目ID列表
	 * @param personName
	 * @param departmentNames
	 * @param companyNames
	 * @param groupNames
	 * @param publishableAppInfoIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getPublishableAppIdList( String personName, List<String> departmentNames, List<String> companyNames, List<String> groupNames, List<String> publishableAppInfoIds ) throws Exception {
		if( publishableAppInfoIds == null ){
			publishableAppInfoIds = new ArrayList<>();
		}
		List<String> app_ids = appInfoServiceAdv.listPublishableAppIds( personName, departmentNames, companyNames, groupNames);
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				if( !publishableAppInfoIds.contains( id )){
					publishableAppInfoIds.add( id );
				}
			}
		}
		return publishableAppInfoIds;
	}
	
	/**
	 * TODO:查询当前登录者有权限发布的所有分类ID列表
	 * @param personName
	 * @param departmentNames
	 * @param companyNames
	 * @param groupNames
	 * @param publishableCategoryIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getPublishableCategoryIds( String personName, List<String> departmentNames, List<String> companyNames, List<String> groupNames, List<String> publishableCategoryIds ) throws Exception {
		if( publishableCategoryIds == null ){
			publishableCategoryIds = new ArrayList<>();
		}
		List<String> category_ids = appInfoServiceAdv.listPublishableCategoryIds( personName, departmentNames, companyNames, groupNames, null );
		if (category_ids != null && !category_ids.isEmpty()) {
			for (String id : category_ids) {
				if (!publishableCategoryIds.contains(id)) {
					publishableCategoryIds.add(id);
				}
			}
		}
		return publishableCategoryIds;
	}
	
	/**
	 * TODO:栏目没有配置可见或者发布权限，这些栏目中所有没有配置可见或者发布权限的分类ID列表
	 * 
	 * @param viewableCategoryIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getNoViewPermissionCategoryIds( List<String> viewableCategoryIds ) throws Exception {
		if( viewableCategoryIds == null ){
			viewableCategoryIds = new ArrayList<>();
		}
		List<String> app_ids = appInfoServiceAdv.listNoViewPermissionOnlyAppIds();
		List<String> category_ids = null;
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				//查询这些栏目下没有配置查看权限的分类
				category_ids = categoryInfoServiceAdv.listNoViewPermissionCategoryIds( id );
				if( category_ids != null && !category_ids.isEmpty() ){
					for( String _id : category_ids ){
						if( !viewableCategoryIds.contains( _id )){
							viewableCategoryIds.add( _id );
						}
					}
				}
			}
		}
		return viewableCategoryIds;
	}
	
	/**
	 * TODO:栏目没有配置权限，这些栏目中所有没有配置权限的分类ID列表
	 * @param publishableCategoryIds
	 * @return
	 * @throws Exception
	 */
	protected List<String> getNoPublishPermissionCategoryIds( List<String> publishableCategoryIds ) throws Exception {
		if( publishableCategoryIds == null ){
			publishableCategoryIds = new ArrayList<>();
		}
		//栏目没有配置权限，这些栏目中所有没有配置权限的分类ID列表
		List<String> app_ids = appInfoServiceAdv.listNoPublishPermissionOnlyAppIds();
		List<String> category_ids = null;
		if (app_ids != null && !app_ids.isEmpty()) {
			for (String id : app_ids) {
				// 查询这些栏目下没有配置查看权限的分类
				category_ids = categoryInfoServiceAdv.listNoPublishPermissionCategoryIds(id);
				if (category_ids != null && !category_ids.isEmpty()) {
					for (String _id : category_ids) {
						if (!publishableCategoryIds.contains(_id)) {
							publishableCategoryIds.add(_id);
						}
					}
				}
			}
		}
		return publishableCategoryIds;
	}
			
}
