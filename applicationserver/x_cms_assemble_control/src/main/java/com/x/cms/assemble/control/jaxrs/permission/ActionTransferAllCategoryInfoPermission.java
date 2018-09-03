package com.x.cms.assemble.control.jaxrs.permission;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.CategoryInfo;

/**
 * 将所有的AppCategoryPermission对象转为新的CategoryInfo和CategoryInfo的对象
 * @author O2LEE
 *
 */
public class ActionTransferAllCategoryInfoPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionTransferAllCategoryInfoPermission.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> allCategoryInfoIds = null;
		Boolean check = true;

		//查询所有的分类信息ID列表
		if( check ){
			try {
				allCategoryInfoIds = categoryInfoServiceAdv.listAllIds();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "系统查询所有的分类ID列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( allCategoryInfoIds )) {
				List<String> category_managerInfoIds = null;
				List<AppCategoryAdmin> category_appCategoryAdminList = null;
				List<String> category_permissionInfoIds = null;
				List<AppCategoryPermission> category_appCategoryPermissionList = null;
				CategoryInfo categoryInfo = null;
				
				for( String categoryId : allCategoryInfoIds ) {
					//查询分类信息
					try {
						categoryInfo = categoryInfoServiceAdv.get( categoryId );
						if( categoryInfo == null ){
							continue;
						}
					} catch (Exception e) {
						logger.error( e, effectivePerson, request, null);
					}
					
					System.out.println(">>>>>正在处理分类信息：" + categoryInfo.getCategoryAlias() );
					//查询分类所有的管理员信息
					try {
						category_managerInfoIds = appCategoryAdminServiceAdv.listAppCategoryIdByAppId(categoryId);
						if( ListTools.isNotEmpty( category_managerInfoIds )) {
							category_appCategoryAdminList = appCategoryAdminServiceAdv.list( category_managerInfoIds );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppCategoryAdminProcess( e, "根据分类ID查询分类所有的管理员信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					
					//查询分类所有的发布和可见范围信息
					try {
						category_permissionInfoIds = appCategoryPermissionServiceAdv.listPermissionByCategory(categoryId, null);
						if( ListTools.isNotEmpty( category_permissionInfoIds )) {
							category_appCategoryPermissionList = appCategoryPermissionServiceAdv.list( category_permissionInfoIds );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppCategoryAdminProcess( e, "根据分类ID查询分类所有的管理员信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					
					//组织好一个categoryInfo的权限信息，直接更新到数据库
					List<String> viewablePersonList = new ArrayList<>();
					List<String> viewableUnitList = new ArrayList<>();
					List<String> viewableGroupList = new ArrayList<>();					
					List<String> publishablePersonList = new ArrayList<>();					
					List<String> publishableUnitList = new ArrayList<>();
					List<String> publishableGroupList = new ArrayList<>();					
					List<String> manageablePersonList = new ArrayList<>();
					
					if(ListTools.isNotEmpty( category_appCategoryAdminList )) {
						for( AppCategoryAdmin appCategoryAdmin : category_appCategoryAdminList) {
							manageablePersonList.add( appCategoryAdmin.getAdminUid());
						}
					}
					
					if(ListTools.isNotEmpty( category_appCategoryPermissionList )) {
						for( AppCategoryPermission appCategoryPermission  : category_appCategoryPermissionList) {
							if( "PUBLISH".equals( appCategoryPermission.getPermission() )) {
								if( "USER".equals( appCategoryPermission.getUsedObjectType() )) {
									publishablePersonList.add( appCategoryPermission.getUsedObjectCode() );
								}else if( "UNIT".equals( appCategoryPermission.getUsedObjectType()  )) {
									publishableUnitList.add( appCategoryPermission.getUsedObjectCode() );
								}else if( "DEPARTMENT".equals( appCategoryPermission.getUsedObjectType()  )) {
									publishableUnitList.add( appCategoryPermission.getUsedObjectCode() );
								}else if( "GROUP".equals( appCategoryPermission.getUsedObjectType()  )) {
									publishableGroupList.add( appCategoryPermission.getUsedObjectCode() );
								}
							}else if( "VIEW".equals( appCategoryPermission.getPermission() )) {
								if( "USER".equals( appCategoryPermission.getUsedObjectType() )) {
									viewablePersonList.add( appCategoryPermission.getUsedObjectCode() );
								}else if( "UNIT".equals( appCategoryPermission.getUsedObjectType()  )) {
									viewableUnitList.add( appCategoryPermission.getUsedObjectCode() );
								}else if( "DEPARTMENT".equals( appCategoryPermission.getUsedObjectType()  )) {
									viewableUnitList.add( appCategoryPermission.getUsedObjectCode() );
								}else if( "GROUP".equals( appCategoryPermission.getUsedObjectType()  )) {
									viewableGroupList.add( appCategoryPermission.getUsedObjectCode() );
								}
							}
						}
					}
					
					categoryInfo.setManageableGroupList( new ArrayList<>() );
					categoryInfo.setManageableUnitList(  new ArrayList<>() );
					categoryInfo.setManageablePersonList(manageablePersonList);
					categoryInfo.setPublishablePersonList(publishablePersonList);
					categoryInfo.setPublishableUnitList(publishableUnitList);
					categoryInfo.setPublishableGroupList(publishableGroupList);
					categoryInfo.setViewablePersonList(viewablePersonList);
					categoryInfo.setViewableUnitList(viewableUnitList);
					categoryInfo.setViewableGroupList(viewableGroupList);
					
					try {
						categoryInfoServiceAdv.updateAllPermission( categoryInfo );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppCategoryAdminProcess( e, "根据分类ID查询分类所有的管理员信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					
				}
			}
			ApplicationCache.notify( CategoryInfo.class );
			
			Wo wo = new Wo();
			wo.setId( "" );
			result.setData( wo );
			System.out.println(">>>>>分类信息权限数据结构全部处理完成。" );
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}