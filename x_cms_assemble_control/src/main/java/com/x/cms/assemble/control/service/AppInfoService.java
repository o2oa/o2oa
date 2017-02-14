package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;

public class AppInfoService {

	public List<AppInfo> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().list( ids );
	}
	public List<String> listAllIds(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAllIds();
	}
	public List<AppInfo> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAll();
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AppInfo appInfo = emc.find( id, AppInfo.class );
		if( appInfo != null ){
			emc.beginTransaction(AppInfo.class);
			emc.remove( appInfo, CheckRemoveType.all );
			emc.commit();
		}else{
			throw new Exception("appInfo is not exists!");
		}
	}

	/**
	 * 查询所有未设置权限的AppInfo的ID列表
	 * 先查询出所有有权限设置的AppInfo,再查询未设置权限的AppInfo
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	public List<String> listNoPermissionAppInfoIds( EntityManagerContainer emc) throws Exception {
		List<String> ids = null;
		Business business = new Business( emc );
		ids = business.getAppCatagoryPermissionFactory().listAllAppInfoIds();		
		return business.getAppInfoFactory().listNoPermissionAppInfoIds( ids );
	}

	public AppInfo get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, AppInfo.class );
	}

	public boolean appInfoDeleteAvailable(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		long count = business.getCatagoryInfoFactory().countByAppId( id );
		if( count > 0 ){
			return false;
		}
		return true;
	}

	public List<String> listAdminPermissionAppInfoByUser( EntityManagerContainer emc, String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listAppInfoIdsByAdminName( name );
	}

	public AppInfo save( EntityManagerContainer emc, AppInfo appInfo ) throws Exception {
		AppInfo appInfo_tmp = null;
		if( appInfo.getId() == null ){
			appInfo.setId( AppInfo.createId() );
		}
		appInfo_tmp = emc.find( appInfo.getId(), AppInfo.class );
		emc.beginTransaction( AppInfo.class );
		if( appInfo_tmp == null ){
			emc.persist( appInfo, CheckPersistType.all);
		}else{
			appInfo.copyTo( appInfo_tmp );
			emc.check( appInfo_tmp, CheckPersistType.all );	
		}
		emc.commit();
		return appInfo;
	}

	
	
}
