package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.appinfo.WrapInAppInfo;
import com.x.cms.core.entity.AppInfo;

public class AppInfoServiceAdv {

	private AppInfoService appInfoService = new AppInfoService();
	private UserManagerService userManagerService = new UserManagerService();
	private AppCategoryAdminService appCategoryAdminService = new AppCategoryAdminService();
	private AppCategoryPermissionService appCategoryPermissionService = new AppCategoryPermissionService();
	
	/**
	 * 根据人员姓名，获取人员可以访问的所有AppInfo信息列表
	 * 1、所有未设置访问权限的（全员可以访问的AppInfo）
	 * 2、该用户自己为管理员的所有AppInfo
	 * 3、该用户以及用户所在的部门，公司有权限访问的所有AppInfo
	 * 
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listViewableAppInfoByUserPermission( String name ) throws Exception {
		List<String> viewAbleAppInfoIds = new ArrayList<>();
		List<String> appInfoIds = null;
		List<String> departmentNames = null;
		List<String> companyNames = null;
		List<String> groupNames = null;
		//1、所有未设置访问权限的（全员可以访问的AppInfo）
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appInfoService.listNoPermissionAppInfoIds( emc );
			if( appInfoIds != null && !appInfoIds.isEmpty() ){
				for( String id : appInfoIds ){
					if( !viewAbleAppInfoIds.contains( id )){
						viewAbleAppInfoIds.add( id );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		//2、该用户自己为管理员的所有AppInfo
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appCategoryAdminService.listAppInfoIdsByAdminName( emc, name );
			if( appInfoIds != null && !appInfoIds.isEmpty() ){
				for( String id : appInfoIds ){
					if( !viewAbleAppInfoIds.contains( id )){
						viewAbleAppInfoIds.add( id );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		//3、该用户自己以及用户所在的部门，公司有权限访问的所有AppInfo
		departmentNames = userManagerService.listDepartmentNameByEmployeeName( name );
		companyNames = userManagerService.listCompanyNameByEmployeeName( name );
		groupNames = userManagerService.listGroupNamesByPersonName( name );
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			appInfoIds = appCategoryPermissionService.listAppInfoIdsByPermission( emc, name, departmentNames, companyNames, groupNames, null );
			if( appInfoIds != null && !appInfoIds.isEmpty() ){
				for( String id : appInfoIds ){
					if( !viewAbleAppInfoIds.contains( id )){
						viewAbleAppInfoIds.add( id );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return viewAbleAppInfoIds;
	}
	
	
	/**
	 * 查询所有未设置发布权限的栏目ID列表，栏目没设置也没有分类设置发布权限
	 * （全员可以发布的AppInfo）
	 * 未设置发布权限的栏目，意味着所有人都要在该栏目下任何分类中发布信息
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listNoPubilshPermissionAppIds() throws Exception {
		List<String> appInfoIds = null;
		//1、所有未设置访问权限的（全员可以访问的AppInfo）
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appInfoService.listNonPublishPermissionAppInfoIds( emc );
		} catch ( Exception e ) {
			throw e;
		}
		return appInfoIds;
	}
	
	/**
	 * TODO:查询所有未设置阅读权限的栏目ID列表，栏目没设置也没有分类设置阅读权限 （全员可以阅读的AppInfo）
	 * 未设置阅读权限的栏目，意味着所有人都要在该栏目下任何分类中阅读信息
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listNoPermissionAppInfoIds() throws Exception {
		List<String> appInfoIds = null;
		//1、所有未设置访问权限的（全员可以访问的AppInfo）
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appInfoService.listNoPermissionAppInfoIds( emc );
		} catch ( Exception e ) {
			throw e;
		}
		return appInfoIds;
	}
	
	/**
	 * TODO:查询所有未设置阅读权限的栏目ID列表<br/>
	 * 所有未设置访问权限的栏目下，所有未设置可见范围的分类，全员可以访问
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listNoViewPermissionOnlyAppIds() throws Exception {
		List<String> appInfoIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appInfoService.listNoViewPermissionOnlyAppInfoIds( emc );
		} catch ( Exception e ) {
			throw e;
		}
		return appInfoIds;
	}
	
	/**
	 * TODO:查询所有未设置发布权限的栏目ID列表
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listNoPublishPermissionOnlyAppIds() throws Exception {
		List<String> appInfoIds = null;
		//1、所有未设置访问权限的（全员可以访问的AppInfo）
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appInfoService.listNoPublishPermissionOnlyAppInfoIds( emc );
		} catch ( Exception e ) {
			throw e;
		}
		return appInfoIds;
	}
	
	/**
	 * TODO:获取自己可以管理的栏目ID列表
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableAppIds( String name ) throws Exception {
		List<String> appInfoIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appCategoryAdminService.listAppInfoIdsByAdminName( emc, name );
		} catch ( Exception e ) {
			throw e;
		}
		return appInfoIds;
	}
	
	/**
	 * TODO:获取自己可以发布的栏目ID列表（下面所有的分类均可以发布）
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableAppIds( String name, List<String> departmentNames, List<String> companyNames, List<String> groupNames ) throws Exception {
		List<String> appInfoIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appCategoryPermissionService.listAppInfoIdsByPermission(emc, name, departmentNames, companyNames, groupNames, "PUBLISH");
		} catch ( Exception e ) {
			throw e;
		}
		return appInfoIds;
	}
	
	/**
	 * TODO:获取自己可以阅读的栏目ID列表（下面所有的分类均可以阅读）
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableAppIds( String name, List<String> departmentNames, List<String> companyNames, List<String> groupNames ) throws Exception {
		List<String> appInfoIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appCategoryPermissionService.listAppInfoIdsByPermission( emc, name, departmentNames, companyNames, groupNames, null );
		} catch ( Exception e ) {
			throw e;
		}
		return appInfoIds;
	}
	
	/**
	 * TODO:获取自己可以发布的栏目ID列表（下面所有的分类均可以发布）
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableCategoryIds( String name, List<String> departmentNames, List<String> companyNames, List<String> groupNames, String appId ) throws Exception {
		List<String> categoryIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryIds = appCategoryPermissionService.listCategoryIdsByPermission( emc, name, departmentNames, companyNames, groupNames, appId, "PUBLISH" );
		} catch ( Exception e ) {
			throw e;
		}
		return categoryIds;
	}
	
	/**
	 * TODO:获取自己可见或者可以发布文档的分类ID列表
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableCategoryIds( String name, List<String> departmentNames, List<String> companyNames, List<String> groupNames, String appId ) throws Exception {
		List<String> categoryIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryIds = appCategoryPermissionService.listCategoryIdsByPermission( emc, name, departmentNames, companyNames, groupNames, appId, null );
		} catch ( Exception e ) {
			throw e;
		}
		return categoryIds;
	}

	public AppInfo get(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public boolean appInfoEditAvailable( HttpServletRequest request, EffectivePerson currentPerson, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.appInfoEditAvailable( request, currentPerson, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long countCategoryByAppId(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.countCategoryByAppId( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id, EffectivePerson currentPerson ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			appInfoService.delete( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AppInfo> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listAllIds() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAllIds( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AppInfo> list(List<String> app_ids) throws Exception {
		if( app_ids == null || app_ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.list( emc, app_ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAdminPermissionAppInfoByUser( String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAdminPermissionAppInfoByUser( emc, name );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppInfo save( WrapInAppInfo wrapIn, EffectivePerson currentPerson ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn is null.");
		}
		List<String> ids = null;
		AppInfo appInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfo = appInfoService.save( emc, wrapIn );
			//检查一下该应用栏目是否存在管理者，如果不存在，则将当前登录者作为应用栏目的管理者
			ids = appCategoryAdminService.listAppCategoryIdByCondition( emc, "APPINFO", appInfo.getId(), currentPerson.getName() );
			if( ids == null || ids.isEmpty()  ){
				appCategoryAdminService.addNewAdminForAppInfo( emc, appInfo, currentPerson.getName() );
			}
		} catch ( Exception e ) {
			throw e;
		}
		return appInfo;
	}

	public List<String> listByAppName( String appName ) throws Exception {
		if( appName == null || appName.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listByAppName( emc, appName );
		} catch ( Exception e ) {
			throw e;
		}
	}


	public List<String> getWithAlias(String appAlias) throws Exception {
		if( appAlias == null || appAlias.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listByAppAlias( emc, appAlias );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
