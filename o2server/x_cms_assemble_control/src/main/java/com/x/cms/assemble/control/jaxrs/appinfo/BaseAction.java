package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.AppDictServiceAdv;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.PermissionOperateService;
import com.x.cms.assemble.control.service.PermissionQueryService;
import com.x.cms.assemble.control.service.ScriptServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.assemble.control.service.ViewServiceAdv;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache( AppInfo.class );

	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
    protected FormServiceAdv formServiceAdv = new FormServiceAdv();
    protected ViewServiceAdv viewServiceAdv = new ViewServiceAdv();
    protected ScriptServiceAdv scriptServiceAdv = new ScriptServiceAdv();
	protected AppDictServiceAdv appDictServiceAdv = new AppDictServiceAdv();
    protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
    protected DocumentQueryService documentServiceAdv = new DocumentQueryService();
	protected UserManagerService userManagerService = new UserManagerService();
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();
	protected PermissionOperateService permissionOperateService = new PermissionOperateService();
	
	/**
	 * 当前登录者访问栏目分类列表查询<br/>
	  * 1、根据人员的访问权限获取可以访问的栏目信息ID列表<br/>
	  * 2、根据人员的访问权限获取可以访问的分类信息ID列表<br/>
	  * 3、将栏目信息和分类信息查询出来组织在一起，如果只有分类，那么也要把栏目信息加上
	  * 4、如果栏目信息下没有分类，则删除栏目信息的输出
	 * @param personName
	 * @param isAnonymous
	 * @param inAppInfoIds
	 * @param appType
	 * @param documentType
	 * @param manager
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	protected List<Wo> listViewAbleAppInfoByPermission( String personName, Boolean isAnonymous, List<String> inAppInfoIds,  String appType, String documentType, 
			Boolean manager, Integer maxCount ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> viewableAppInfoIds = null;
		List<String> viewableCategoryIds = new ArrayList<>();
		if( manager ) {
			if( ListTools.isNotEmpty( inAppInfoIds )) {
				viewableAppInfoIds = inAppInfoIds; //可发布栏目就限制为inAppInfoIds
			}else {
				viewableAppInfoIds = appInfoServiceAdv.listAllIds(documentType); //所有栏目均可见
			}
			viewableCategoryIds = categoryInfoServiceAdv.listCategoryIdsWithAppIds( viewableAppInfoIds, documentType, manager, maxCount );
		}else {
			if( !isAnonymous ) {
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson( personName );
			}
			//查询用户可以访问到的栏目
			viewableAppInfoIds = permissionQueryService.listViewableAppIdByPerson(personName, isAnonymous, unitNames, groupNames, inAppInfoIds, null, documentType, maxCount );
			if( ListTools.isNotEmpty( viewableAppInfoIds )) {
				viewableAppInfoIds.add("NO_APPINFO");
			}
			
			//根据人员的发布权限获取可以发布文档的分类信息ID列表
			viewableCategoryIds = permissionQueryService.listViewableCategoryIdByPerson( personName, isAnonymous, unitNames, groupNames, viewableAppInfoIds, 
					null, null, documentType, maxCount, manager );
		}
		return composeCategoriesIntoAppInfo( viewableAppInfoIds, viewableCategoryIds, appType );
	}
	
	/**
	 * 当前登录者文档发布栏目分类列表查询<br/>
	  * 1、根据人员的发布权限获取可以发布文档的栏目信息ID列表<br/>
	  * 2、根据人员的发布权限获取可以发布文档的分类信息ID列表<br/>
	  * 3、将栏目信息和分类信息查询出来组织在一起，如果只有分类，那么也要把栏目信息加上
	  * 4、如果栏目信息下没有分类，则删除栏目信息的输出
	 * @param personName
	 * @param isAnonymous
	 * @param inAppInfoIds
	 * @param documentType
	 * @param appType
	 * @param manager
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	protected List<Wo> listPublishAbleAppInfoByPermission( String personName, Boolean isAnonymous, List<String> inAppInfoIds,  String documentType, String appType, Boolean manager, Integer maxCount ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> publishableAppInfoIds = null;
		List<String> publishableCategoryIds = new ArrayList<>();
		if( manager ) {
			if( ListTools.isNotEmpty( inAppInfoIds )) {
				publishableAppInfoIds = inAppInfoIds; //可发布栏目就限制为inAppInfoIds
			}else {				
				publishableAppInfoIds = appInfoServiceAdv.listAllIds(documentType); //所有栏目均可发布
			}
			publishableCategoryIds = categoryInfoServiceAdv.listCategoryIdsWithAppIds( publishableAppInfoIds, documentType, manager, maxCount );
		}else {
			if( !isAnonymous ) {
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson( personName );
			}
			//2、根据人员的发布权限获取可以发布文档的分类信息ID列表
			publishableCategoryIds = permissionQueryService.listPublishableCategoryIdByPerson(
					personName, isAnonymous, unitNames, groupNames, inAppInfoIds, null, null, documentType, maxCount, manager );
		}
//		if( ListTools.isNotEmpty(publishableCategoryIds  )) {
//			System.out.println(">>>>>>>>>publishableCategoryIds.size=" + publishableCategoryIds.size() );
//		}else {
//			System.out.println(">>>>>>>>>publishableCategoryIds is empty!"  );
//		}		
		return composeCategoriesIntoAppInfo( publishableAppInfoIds, publishableCategoryIds, appType );
	}
	
	/**
	 * 根据指定的栏目和分类ID，将分类组织到栏目信息中
	 * @param publishableAppInfoIds
	 * @param publishableCategoryIds
	 * @return
	 * @throws Exception 
	 */
	private List<Wo> composeCategoriesIntoAppInfo(List<String> appInfoIds, List<String> categoryInfoIds, String appType ) throws Exception {
		List<Wo> wraps = null;
		List<WoCategory> wrapCategories = null;
		Map<String, Wo> app_map = new HashMap<>();
		Map<String, WoCategory> category_map = new HashMap<>();
		List<AppInfo> appInfoList = appInfoServiceAdv.list(appInfoIds);
		List<CategoryInfo> categoryInfoList = categoryInfoServiceAdv.list( categoryInfoIds );
		if( ListTools.isNotEmpty( appInfoList )) {
			wraps = Wo.copier.copy(appInfoList);
			if( ListTools.isNotEmpty( wraps ) ) {
				for( Wo wo : wraps ) {
					app_map.put( wo.getId() , wo );
				}
			}
		}
		if( ListTools.isNotEmpty( categoryInfoList )) {
			wrapCategories = WoCategory.copier.copy(categoryInfoList);
			if( ListTools.isNotEmpty( wrapCategories ) ) {
				for( WoCategory woCategory : wrapCategories ) {
					category_map.put( woCategory.getId() , woCategory );
				}
			}
		}
		
		//循环category_map，将category装配到app_map里相应的app里
		Set<String> category_set = category_map.keySet();
		Iterator<String> category_iterator = category_set.iterator();
		String category_key = null;
		AppInfo appInfo  = null;
		WoCategory  woCategory  = null;
		Wo wo_app = null;
		while( category_iterator.hasNext() ) {
			category_key = category_iterator.next().toString();
			woCategory = category_map.get( category_key );
			wo_app = app_map.get( woCategory.getAppId() );
			if( wo_app == null ) {
				//栏目信息在map里不存在，将栏目信息查询出来，放到map里
				appInfo = appInfoServiceAdv.get( woCategory.getAppId() );
				if( appInfo != null ) {
					wo_app = Wo.copier.copy( appInfo );
				}
			}
			if( wo_app.getWrapOutCategoryList() == null ) {
				wo_app.setWrapOutCategoryList( new ArrayList<>() );
			}
			wo_app.getWrapOutCategoryList().add( woCategory );
			app_map.put( wo_app.getId(), wo_app );
			
		}
		//将app_map转移到List里返回
		if( wraps == null  ) {
			wraps = new ArrayList<>();
		}
		wraps.clear();
		Set<String> app_set = app_map.keySet();
		Iterator<String> app_iterator = app_set.iterator();
		String app_key = null;
		while( app_iterator.hasNext() ) {
			app_key = app_iterator.next().toString();
			wo_app = app_map.get( app_key );
			if( StringUtils.isEmpty( wo_app.getAppType() )) {
				wo_app.setAppType("未分类");
			}
			if( "全部".equalsIgnoreCase( appType )||  "all".equalsIgnoreCase( appType ) || (StringUtils.isNotEmpty(  wo_app.getAppType() ) && wo_app.getAppType().equalsIgnoreCase( appType ))) {
				wraps.add( wo_app );
			}
		}
		return wraps;
	}
		
	public static class Wo extends AppInfo {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();

		private List<WoCategory> wrapOutCategoryList = null;
		
		public List<WoCategory> getWrapOutCategoryList() {
			return wrapOutCategoryList;
		}
		public void setWrapOutCategoryList(List<WoCategory> wrapOutCategoryList) {
			this.wrapOutCategoryList = wrapOutCategoryList;
		}
		
		static WrapCopier<AppInfo, Wo> copier = WrapCopierFactory.wo( AppInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));
		
	}
	
	public static class WoCategory extends CategoryInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		static WrapCopier<CategoryInfo, WoCategory> copier = WrapCopierFactory.wo( CategoryInfo.class, WoCategory.class, null, ListTools.toList(JpaObject.FieldsInvisible));
		
	}
			
}
