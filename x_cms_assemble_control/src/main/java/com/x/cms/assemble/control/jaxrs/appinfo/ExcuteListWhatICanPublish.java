package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.categoryinfo.WrapOutCategoryInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Element;

public class ExcuteListWhatICanPublish extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWhatICanPublish.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutAppInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		List<WrapOutAppInfo> wraps = null;
		Boolean isXAdmin = false;
		Boolean check = true;
		
		try {
			isXAdmin = effectivePerson.isManager();
		} catch (Exception e) {
			check = false;
			Exception exception = new UserManagerCheckException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( "publish", effectivePerson.getName(), isXAdmin );
		Element element = cache.get( cacheKey );
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutAppInfo> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			if( check ){
				if ( isXAdmin ) { //如果用户管理系统管理，则获取所有的栏目和分类信息
					try{
						wraps = getAllAppInfoWithCategory();
					}catch( Exception e ){
						check = false;
						Exception exception = new CategoryInfoListViewableInPermissionException( e, effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					try{
						wraps = getPublishAbleAppInfoByPermission( effectivePerson.getName() );
					}catch( Exception e ){
						check = false;
						Exception exception = new CategoryInfoListViewableInPermissionException( e, effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			if( check ){
				cache.put(new Element( cacheKey, wraps ));
				result.setData(wraps);
			}
		}
		return result;
	}

	private List<WrapOutAppInfo> getAllAppInfoWithCategory() throws Exception {
		List<AppInfo> appInfoList = null;
		List<CategoryInfo> catacoryList = null;
		List<String> category_ids = null;
		List<WrapOutAppInfo> wraps = null;
		List<WrapOutCategoryInfo> wrapOutCatacoryList = null;
		
		appInfoList = appInfoServiceAdv.listAll();
		if( appInfoList != null && !appInfoList.isEmpty() ){
			wraps = WrapTools.appInfo_wrapout_copier.copy( appInfoList );
			SortTools.desc( wraps, "appInfoSeq");
		}
		
		if (wraps != null && wraps.size() > 0) {
			for ( WrapOutAppInfo wrap_appInfo : wraps ) {
				category_ids = categoryInfoServiceAdv.listByAppId( wrap_appInfo.getId() );
				catacoryList = categoryInfoServiceAdv.list( category_ids );
				if( catacoryList != null && !catacoryList.isEmpty() ){
					wrapOutCatacoryList = WrapTools.category_wrapout_copier.copy( catacoryList );
					wrap_appInfo.setWrapOutCategoryList( wrapOutCatacoryList );
				}
			}
		}
		
		return wraps;
	}

	private List<WrapOutAppInfo> getPublishAbleAppInfoByPermission( String name ) throws Exception {
		
		AppInfo appInfo = null;
		List<String> departmentNames = null;
		List<String> companyNames = null;
		List<AppInfo> appInfoList = null;
		List<CategoryInfo> catacoryList = null;
		List<String> app_ids = null;
		List<String> category_ids = null;
		List<String> publishableAppInfoIds = new ArrayList<>();
		List<String> publishableCategoryIds = new ArrayList<>();
		List<WrapOutAppInfo> wraps = null;
		List<WrapOutCategoryInfo> wrapOutCatacoryList = null;
		WrapOutAppInfo wrap_appinfo = null;
		WrapOutCategoryInfo wrap_category = null;
		Boolean appExists = false;
		Boolean categoryExists = false;
		
		departmentNames = userManagerService.listDepartmentNameByEmployeeName( name );
		companyNames = userManagerService.listCompanyNameByEmployeeName( name );

		//===================================第一部分，查询可以全权访问的栏目信息
		//查询没有设置任何发布者权限的栏目信息（该栏目下所有的分类默认所有人可以发布信息）
		app_ids = appInfoServiceAdv.listNoPubilshPermissionAppIds();
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				if( !publishableAppInfoIds.contains( id )){
					publishableAppInfoIds.add( id );
				}
			}
		}
		
		//查询登录者可以管理的所有栏目信息（这些栏目信息下所有的分类均可以由该登录者发布信息）
		app_ids = appInfoServiceAdv.listManageableAppIds( name );
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				if( !publishableAppInfoIds.contains( id )){
					publishableAppInfoIds.add( id );
				}
			}
		}
		
		//查询发布权限中有我的栏目信息（这些栏目信息下所有的分类均可以由该登录者发布信息）
		app_ids = appInfoServiceAdv.listPublishableAppIds( name, departmentNames, companyNames );
		if( app_ids != null && !app_ids.isEmpty() ){
			for( String id : app_ids ){
				if( !publishableAppInfoIds.contains( id )){
					publishableAppInfoIds.add( id );
				}
			}
		}
		
		//以上这些栏目中所有的分类信息均可以由当前登录者发布信息，所以查询所有的分类信息直接输出 
		if( publishableAppInfoIds != null && !publishableAppInfoIds.isEmpty() ){
			//以上是可以全部分类进行发布的栏目， 先全部转换为Wrap
			appInfoList = appInfoServiceAdv.list( publishableAppInfoIds );
			if( appInfoList != null && !appInfoList.isEmpty() ){
				wraps = WrapTools.appInfo_wrapout_copier.copy( appInfoList );
				//为可以全权访问的栏目添加所有分类信息
				if (wraps != null && wraps.size() > 0) {
					for ( WrapOutAppInfo wrap_appInfo : wraps ) {
						category_ids = categoryInfoServiceAdv.listByAppId( wrap_appInfo.getId() );
						catacoryList = categoryInfoServiceAdv.list( category_ids );
						if( catacoryList != null && !catacoryList.isEmpty() ){
							wrapOutCatacoryList = WrapTools.category_wrapout_copier.copy( catacoryList );
							wrap_appInfo.setWrapOutCategoryList( wrapOutCatacoryList );
						}
					}
				}
				SortTools.desc( wraps, "appInfoSeq");
			}
		}
		
		if( wraps == null ){
			wraps = new ArrayList<>();
		}
		
		//======================================第二部分，查询发布者设置为当前登录者的分类ID
		//查询当前登录者可以管理的分类ID
		category_ids = appCategoryAdminServiceAdv.listManageableCategoryIds( name );
		if( category_ids != null && !category_ids.isEmpty() ){
			for( String id : category_ids ){
				if( !publishableCategoryIds.contains( id )){
					publishableCategoryIds.add( id );
				}
			}
		}
		
		//再查询当前便当者可以发布的分类ID
		category_ids = appInfoServiceAdv.listPublishableCategoryIds( name, departmentNames, companyNames, null );
		if( category_ids != null && !category_ids.isEmpty() ){
			for( String id : category_ids ){
				if( !publishableCategoryIds.contains( id )){
					publishableCategoryIds.add( id );
				}
			}
		}
		
		//查询可以发布信息的分类信息列表
		if( publishableCategoryIds != null && !publishableCategoryIds.isEmpty() ){
			catacoryList = categoryInfoServiceAdv.list( publishableCategoryIds );
		}
		
		//将这些分类信息组织到之前的栏目列表里去
		if( catacoryList != null && !catacoryList.isEmpty() ){
			for( CategoryInfo category : catacoryList ){
				
				appExists = false;
				for ( WrapOutAppInfo wrap_appInfo : wraps ) {
					
					if( wrap_appInfo.getId().equals( category.getAppId() )){
						
						//appExists = true, 找到了, 看看该栏目下分类是否已经存在了
						appExists = true;
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
					
					//在原来的wraps里没有该栏目，直接添加栏目和当前的catagory信息
					appInfo = appInfoServiceAdv.get( category.getAppId() );
					
					if( appInfo != null ){

						wrap_category = WrapTools.category_wrapout_copier.copy( category );
						wrap_appinfo = WrapTools.appInfo_wrapout_copier.copy( appInfo );
						
						wrap_appinfo.setWrapOutCategoryList( new ArrayList<>() );
						wrap_appinfo.getWrapOutCategoryList().add( wrap_category );
						
						wraps.add( wrap_appinfo );
					}
				}
				
			}
		}
		
		return wraps;
	}
}