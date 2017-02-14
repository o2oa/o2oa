package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppCatagoryAdmin;

public class AppCatagoryAdminService {

	public List<String> listAppCatagoryIdByCatagoryId(EntityManagerContainer emc, String catagoryId) throws Exception {
		if( catagoryId == null || catagoryId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listAppCatagoryIdByCatagoryId( catagoryId );
	}

	public List<AppCatagoryAdmin> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().list( ids );
	}

	public List<String> listAppCatagoryIdByAppId(EntityManagerContainer emc, String appId) throws Exception {
		if( appId == null || appId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listAppCatagoryIdByAppId( appId );
	}

	public List<String> listAppCatagoryIdByUser(EntityManagerContainer emc, String person) throws Exception {
		if( person == null || person.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listAppCatagoryIdByUser( person );
	}

	public List<String> listAppCatagoryObjectIdByUser(EntityManagerContainer emc, String person, String objectType ) throws Exception {
		if( person == null || person.isEmpty() ){
			return null;
		}
		if( objectType == null || objectType.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listAppCatagoryObjectIdByUser( person, objectType );
	}

	public List<AppCatagoryAdmin> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listAll();
	}

	public AppCatagoryAdmin id(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().get( id );
	}

	public AppCatagoryAdmin save( EntityManagerContainer emc, AppCatagoryAdmin appCatagoryAdmin ) throws Exception {
		if( appCatagoryAdmin == null ){
			throw new Exception( "appCatagoryAdmin is null!" );
		}
		AppCatagoryAdmin appCatagoryAdmin_old = null;
		appCatagoryAdmin_old = emc.find( appCatagoryAdmin.getId(), AppCatagoryAdmin.class );
		emc.beginTransaction( AppCatagoryAdmin.class );
		if( appCatagoryAdmin_old != null ){//update
			appCatagoryAdmin.copyTo( appCatagoryAdmin_old );
			emc.check( appCatagoryAdmin_old, CheckPersistType.all );
		}else{
			emc.persist( appCatagoryAdmin, CheckPersistType.all );
		}
		emc.commit();
		return appCatagoryAdmin;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AppCatagoryAdmin appCatagoryAdmin = emc.find( id, AppCatagoryAdmin.class );
		if( appCatagoryAdmin != null ){
			emc.beginTransaction(AppCatagoryAdmin.class);
			emc.remove(appCatagoryAdmin, CheckRemoveType.all);
			emc.commit();
		}else{
			throw new Exception("appCatagoryAdmin is not exists!");
		}
	}

	public List<String> listAppInfoIdsByAdminName( EntityManagerContainer emc, String name) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listAppInfoIdsByAdminName( name );
	}
	
	public List<String> listCatagoryInfoIdsByAdminName( EntityManagerContainer emc, String name) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = new Business( emc );
		return business.getAppCatagoryAdminFactory().listCatagoryInfoIdsByAdminName( name );
	}

	public List<String> listCatagoryInfoIdsByAdminName(EntityManagerContainer emc, String name, String appId ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		if( appId == null || appId.isEmpty() ){
			throw new Exception( "appId is null!" );
		}
		List<String> myCatagoryIds = null;
		Business business = new Business( emc );
		myCatagoryIds = business.getAppCatagoryAdminFactory().listCatagoryInfoIdsByAdminName( name );
		return business.getCatagoryInfoFactory().listMyCatagoryWithAppId( myCatagoryIds, appId );
	}

}
