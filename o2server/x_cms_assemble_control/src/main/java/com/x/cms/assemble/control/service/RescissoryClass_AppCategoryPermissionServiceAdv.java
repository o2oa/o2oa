package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;

/**
 * 对栏目分类的使用权限信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class RescissoryClass_AppCategoryPermissionServiceAdv {

	private UserManagerService userManagerService = new UserManagerService();
	private CategoryInfoService categoryInfoService = new CategoryInfoService();
	private RescissoryClass_AppCategoryAdminService appCategoryAdminService = new RescissoryClass_AppCategoryAdminService();
	private RescissoryClass_AppCategoryPermissionService appCategoryPermissionService = new RescissoryClass_AppCategoryPermissionService();

	public AppCategoryPermission get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryPermissionService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AppCategoryPermission> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryPermissionService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppCategoryPermission save( AppCategoryPermission wrapIn, EffectivePerson currentPerson ) throws Exception {
		AppCategoryPermission appCategoryPermission = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCategoryPermission = appCategoryPermissionService.save( emc, wrapIn );
			return appCategoryPermission;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( AppCategoryPermission appCategoryPermission, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCategoryPermissionService.delete( emc, appCategoryPermission.getId() );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listPermissionByAppInfo( String appId, String permission ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryPermissionService.listPermissionByAppInfo( emc, appId, permission );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listPermissionByCategory(String categoryId, String permission) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryPermissionService.listPermissionByCategory( emc, categoryId, permission );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCategoryIdByCondition( String objectType, String objectId, String distinguishedName, String permission ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryPermissionService.listAppCategoryIdByCondition(emc, objectType, objectId, distinguishedName, permission);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listAppCategoryIdByPermission( String objectType, String objectId, String distinguishedName, String permission ) throws Exception {
		List<String> unitNames = null;
		List<String> groupNames = null;
		
		if( distinguishedName != null && !distinguishedName.isEmpty() ){
			unitNames = userManagerService.listUnitNamesWithPerson( distinguishedName );
			groupNames = userManagerService.listGroupNamesByPerson( distinguishedName );
		}		
		
		if( "CATEGORY".equals( objectType )){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				return appCategoryPermissionService.listCategoryIdsByPermission( emc, distinguishedName, unitNames, groupNames, objectId, permission );
			} catch ( Exception e ) {
				throw e;
			}
		}else{
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				return appCategoryPermissionService.listAppInfoIdsByPermission( emc, distinguishedName, unitNames, groupNames, objectId, permission );
			} catch ( Exception e ) {
				throw e;
			}
		}
	}

	/**
	 * 查询用户可以看到的所有栏目ID列表
	 * 权限：可管理、可见、可发布
	 * @param distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> getViewableAppInfoIds( String distinguishedName ) throws Exception {
		List<String> queryObjectNames = userManagerService.getPersonPermissionCodes( distinguishedName );
		return getViewableAppInfoIds( distinguishedName, queryObjectNames );
	}
	/**
	 * 查询用户可以看到的所有栏目ID列表
	 * 权限：可管理、可见、可发布
	 * @param distinguishedName
	 * @param queryObjectNames
	 * @return
	 * @throws Exception
	 */
	public List<String> getViewableAppInfoIds( String distinguishedName, List<String> queryObjectNames ) throws Exception {
		List<String> viewable_appInfo_ids = new ArrayList<>();
		List<AppCategoryAdmin> appCategoryAdminList = null;
		List<String> ids = null;
		//查询有管理权限的栏目ID列表，可管理，可发布，可见	
		//1、可管理的所有APPINFO（CMS_APPCATEGORY_ADMIN表）
		ids = listAppCategoryIdByAdminName( distinguishedName, "APPINFO" );
		if( ids != null && !ids.isEmpty() ) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				appCategoryAdminList = appCategoryAdminService.list(emc, ids);
			} catch ( Exception e ) {
				throw e;
			}
			if( appCategoryAdminList != null ) {
				for( AppCategoryAdmin appCategoryAdmin : appCategoryAdminList ) {
					if( !viewable_appInfo_ids.contains( appCategoryAdmin.getObjectId() )) {
						viewable_appInfo_ids.add( appCategoryAdmin.getObjectId() );
					}
				}
			}
		}
		
		//2、可发布、可见的所有APPINFO（CMS_APPCATEGORY_PERMISSION表）
		ids = listAppCategoryIdByPermission( queryObjectNames, null, "APPINFO", null );
		if( ids != null && !ids.isEmpty() ) {
			for( String id : ids ) {
				if( !viewable_appInfo_ids.contains( id )) {
					viewable_appInfo_ids.add( id );
				}
			}
		}
		return viewable_appInfo_ids;
	}
	
	/**
	 * 直接单独从权限表中查询用户有权限访问的分类，不涉及栏目的权限以及栏目的嵌套分类
	 * @param distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> getViewableCategoriesWithOnlyPermissionRecord( String distinguishedName ) throws Exception {
		List<String> queryObjectNames = userManagerService.getPersonPermissionCodes( distinguishedName );
		return getViewableCategoriesWithOnlyPermissionRecord( distinguishedName, queryObjectNames );
	}
	/**
	 * 直接单独从权限表中查询用户有权限访问的分类，不涉及栏目的权限以及栏目的嵌套分类
	 * @param distinguishedName
	 * @param queryObjectNames
	 * @return
	 * @throws Exception
	 */
	public List<String> getViewableCategoriesWithOnlyPermissionRecord( String distinguishedName, List<String> queryObjectNames ) throws Exception {
		List<String> viewable_category_ids = new ArrayList<>();
		List<AppCategoryAdmin> appCategoryAdminList = null;
		List<String> ids = null;
		
		//查询有管理权限的栏目ID列表，可管理，可发布，可见	
		//1、可管理的所有APPINFO（CMS_APPCATEGORY_ADMIN表）
		ids = listAppCategoryIdByAdminName( distinguishedName, "CATEGORY" );
		if( ids != null && !ids.isEmpty() ) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				appCategoryAdminList = appCategoryAdminService.list(emc, ids);
			} catch ( Exception e ) {
				throw e;
			}
			if( appCategoryAdminList != null ) {
				for( AppCategoryAdmin appCategoryAdmin : appCategoryAdminList ) {
					if( !viewable_category_ids.contains( appCategoryAdmin.getObjectId() )) {
						viewable_category_ids.add( appCategoryAdmin.getObjectId() );
					}
				}
			}
		}
		//2、可发布、可见的所有APPINFO（CMS_APPCATEGORY_PERMISSION表）
		ids = listAppCategoryIdByPermission( queryObjectNames, null, "CATEGORY", null );
		if( ids != null && ids.isEmpty() ) {
			for( String id : ids ) {
				if( !viewable_category_ids.contains( id )) {
					viewable_category_ids.add( id );
				}
			}
		}
		return viewable_category_ids;
	}
	
	/**
	 * 查询用户可以看到的所有分类(嵌套可见的栏目的所有下级分类)
	 * 权限：可管理、可见、可发布
	 * @param distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> getViewableCatagoriesWithPerson( String distinguishedName ) throws Exception {
		List<String> queryObjectNames = null;
		List<String> appInfo_ids = null;
		List<String> category_ids = null;
		List<String> viewableCatagories = new ArrayList<>();
		
		//选查询个人涉及的所有组织角色以及群组编码
		queryObjectNames = userManagerService.getPersonPermissionCodes( distinguishedName );

		//查询有管理权限的栏目ID列表，可管理，可发布，可见	
		appInfo_ids = getViewableAppInfoIds( distinguishedName, queryObjectNames );
		
		//可见栏目下的所有分类都应该可见，加入到可见分类列表中
		if( appInfo_ids != null && !appInfo_ids.isEmpty() ){
			for( String appId : appInfo_ids ){
				category_ids = categoryInfoService.listByAppId( appId );
				if( category_ids != null && !category_ids.isEmpty() ){
					for( String categoryId : category_ids ){
						if( !viewableCatagories.contains( categoryId )){
							viewableCatagories.add( categoryId );
						}
					}
				}
			}
		}
		
		//再查询单独配置的可管理，可见，可发布的分类（栏目不一定可见，可管理，可发布）
		category_ids = getViewableCategoriesWithOnlyPermissionRecord( distinguishedName, queryObjectNames );
		if( category_ids != null && !category_ids.isEmpty() ){
			for( String categoryId : category_ids ){
				if( !viewableCatagories.contains( categoryId )){
					viewableCatagories.add( categoryId );
				}
			}
		}
		
		return viewableCatagories;
	}
	
	private List<String> listAppCategoryIdByPermission(List<String> queryObjectNames, String queryObjectType, String objectType, String permission) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return appCategoryPermissionService.listAppInfoIdsByPermission( emc, queryObjectNames, queryObjectType, objectType, permission );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCategoryIdByAdminName( String person, String objectType ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return appCategoryAdminService.listAppCategoryIdByAdminName( emc, person, objectType );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
