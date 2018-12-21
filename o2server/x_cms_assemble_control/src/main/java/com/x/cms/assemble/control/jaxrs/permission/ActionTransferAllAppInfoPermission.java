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
import com.x.cms.core.entity.AppInfo;

/**
 * 将所有的AppCategoryPermission对象转为新的AppInfo和CategoryInfo的对象
 * @author O2LEE
 *
 */
public class ActionTransferAllAppInfoPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionTransferAllAppInfoPermission.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> allAppInfoIds = null;
		Boolean check = true;

		//查询所有的栏目信息ID列表
		if( check ){
			try {
				allAppInfoIds = appInfoServiceAdv.listAllIds("全部");
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "系统查询所有的栏目ID列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( allAppInfoIds )) {
				List<String> appInfo_managerInfoIds = null;
				List<AppCategoryAdmin> appInfo_appCategoryAdminList = null;
				List<String> appInfo_permissionInfoIds = null;
				List<AppCategoryPermission> appInfo_appCategoryPermissionList = null;
				AppInfo appInfo = null;
				
				for( String appId : allAppInfoIds ) {
					//查询栏目信息
					try {
						appInfo = appInfoServiceAdv.get( appId );
						if( appInfo == null ){
							continue;
						}
					} catch (Exception e) {
						logger.error( e, effectivePerson, request, null);
					}
					
					System.out.println(">>>>>正在处理栏目信息：" + appInfo.getAppAlias() );
					
					//查询栏目所有的管理员信息
					try {
						appInfo_managerInfoIds = appCategoryAdminServiceAdv.listAppCategoryIdByAppId(appId);
						if( ListTools.isNotEmpty( appInfo_managerInfoIds )) {
							appInfo_appCategoryAdminList = appCategoryAdminServiceAdv.list( appInfo_managerInfoIds );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppCategoryAdminProcess( e, "根据栏目ID查询栏目所有的管理员信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					
					//查询栏目所有的发布和可见范围信息
					try {
						appInfo_permissionInfoIds = appCategoryPermissionServiceAdv.listPermissionByAppInfo(appId, null );
						if( ListTools.isNotEmpty( appInfo_permissionInfoIds )) {
							appInfo_appCategoryPermissionList = appCategoryPermissionServiceAdv.list( appInfo_permissionInfoIds );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppCategoryAdminProcess( e, "根据栏目ID查询栏目所有的管理员信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					
					//组织好一个appInfo的权限信息，直接更新到数据库
					List<String> viewablePersonList = new ArrayList<>();
					List<String> viewableUnitList = new ArrayList<>();
					List<String> viewableGroupList = new ArrayList<>();					
					List<String> publishablePersonList = new ArrayList<>();					
					List<String> publishableUnitList = new ArrayList<>();
					List<String> publishableGroupList = new ArrayList<>();					
					List<String> manageablePersonList = new ArrayList<>();
					
					if(ListTools.isNotEmpty( appInfo_appCategoryAdminList )) {
						for( AppCategoryAdmin appCategoryAdmin : appInfo_appCategoryAdminList) {
							manageablePersonList.add( appCategoryAdmin.getAdminUid());
						}
					}
					
					if(ListTools.isNotEmpty( appInfo_appCategoryPermissionList )) {
						for( AppCategoryPermission appCategoryPermission  : appInfo_appCategoryPermissionList) {
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
					
					appInfo.setManageablePersonList(manageablePersonList);
					appInfo.setPublishablePersonList(publishablePersonList);
					appInfo.setPublishableUnitList(publishableUnitList);
					appInfo.setPublishableGroupList(publishableGroupList);
					appInfo.setViewablePersonList(viewablePersonList);
					appInfo.setViewableUnitList(viewableUnitList);
					appInfo.setViewableGroupList(viewableGroupList);				
					
					try {
						appInfoServiceAdv.updateAllPermission( appInfo );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppCategoryAdminProcess( e, "根据栏目ID查询栏目所有的管理员信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					
				}
			}
			ApplicationCache.notify( AppInfo.class );
			
			Wo wo = new Wo();
			wo.setId( "" );
			result.setData( wo );
			System.out.println(">>>>>栏目信息权限数据结构全部处理完成。" );
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}