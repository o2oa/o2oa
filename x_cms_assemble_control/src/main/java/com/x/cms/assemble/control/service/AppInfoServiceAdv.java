package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;

public class AppInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( AppInfoServiceAdv.class );
	
	private LogService logService = new LogService();
	private AppInfoService appInfoService = new AppInfoService();
	private UserManagerService userManagerService = new UserManagerService();
	private AppCatagoryAdminService appCatagoryAdminService = new AppCatagoryAdminService();
	private AppCatagoryPermissionService appCatagoryPermissionService = new AppCatagoryPermissionService();
	
	/**
	 * 根据人员姓名，获取人员可以访问的所有AppInfo信息列表
	 * 1、所有未设置访问权限的（全员可以访问的AppInfo）
	 * 2、该用户自己为管理员的所有AppInfo
	 * 3、该用户以及用户所在的部门，公司有权限访问的所有AppInfo
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAppInfoByUserPermission( String name ) throws Exception {
		List<String> viewAbleAppInfoIds = new ArrayList<>();
		List<String> appInfoIds = null;
		List<String> departmentNames = null;
		List<String> companyNames = null;
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
			appInfoIds = appCatagoryAdminService.listAppInfoIdsByAdminName( emc, name );
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
		//3、该用户以及用户所在的部门，公司有权限访问的所有AppInfo
		departmentNames = userManagerService.listDepartmentNameByEmployeeName( name );
		companyNames = userManagerService.listCompanyNameByEmployeeName( name );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoIds = appCatagoryPermissionService.listAppInfoIdsByPermission( emc, name, departmentNames, companyNames );
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

	public boolean appInfoDeleteAvailable(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.appInfoDeleteAvailable( emc, id );
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
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功删除一个应用信息", id, "", "", "", "APP", "删除");
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

	public List<String> listAdminPermissionAppInfoByUser(String name) throws Exception {
		if( name == null || name.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAdminPermissionAppInfoByUser( emc, name );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppInfo save( AppInfo appInfo, EffectivePerson currentPerson ) throws Exception {
		if( appInfo == null ){
			throw new Exception("appInfo is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfo = appInfoService.save( emc, appInfo );
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功创建一个应用信息", appInfo.getId(), "", "", "", "APP", "新增");
		} catch ( Exception e ) {
			throw e;
		}
		return appInfo;
	}

}
