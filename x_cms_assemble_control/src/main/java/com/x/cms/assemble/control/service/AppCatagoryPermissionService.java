package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppCatagoryPermission;

public class AppCatagoryPermissionService {

	public List<AppCatagoryPermission> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().list( ids );
	}

	public List<AppCatagoryPermission> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().listAll();
	}

	public AppCatagoryPermission id(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().get( id );
	}

	public AppCatagoryPermission save( EntityManagerContainer emc, AppCatagoryPermission appCatagoryPermission ) throws Exception {
		if( appCatagoryPermission == null ){
			throw new Exception( "appCatagoryPermission is null!" );
		}
		AppCatagoryPermission appCatagoryPermission_old = null;
		appCatagoryPermission_old = emc.find( appCatagoryPermission.getId(), AppCatagoryPermission.class );
		if( appCatagoryPermission_old != null ){//update
			appCatagoryPermission.copyTo( appCatagoryPermission_old );
			emc.beginTransaction( AppCatagoryPermission.class );
			emc.check( appCatagoryPermission_old, CheckPersistType.all );
			emc.commit();
		}else{
			emc.beginTransaction( AppCatagoryPermission.class );
			emc.persist( appCatagoryPermission, CheckPersistType.all );
			emc.commit();
		}
		return appCatagoryPermission;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AppCatagoryPermission appCatagoryPermission = emc.find( id, AppCatagoryPermission.class );
		if( appCatagoryPermission != null ){
			emc.beginTransaction(AppCatagoryPermission.class);
			emc.remove(appCatagoryPermission, CheckRemoveType.all);
			emc.commit();
		}else{
			throw new Exception("appCatagoryPermission is not exists!");
		}
	}

	public List<String> listAppCatagoryPermissionByUser(EntityManagerContainer emc, String person, String objectType) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().listAppCatagoryPermissionByUser( person, objectType );
	}

	public List<String> listAppInfoByUserPermission(EntityManagerContainer emc, String person) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().listAppInfoByUserPermission( person );
	}
	
	public List<String> listCatagoryIdsByPermission(EntityManagerContainer emc, EffectivePerson currentPerson, List<String> appCatagoryIds) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().listCatagoryInfoByUserPermission( currentPerson, appCatagoryIds );
	}

	public List<String> listPermissionByAppInfo(EntityManagerContainer emc, String appId) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().listPermissionByAppInfo( appId );
	}

	public List<String> listAppInfoIdsByPermission( EntityManagerContainer emc, String name, List<String> departmentNames, List<String> companyNames ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCatagoryPermissionFactory().listAppInfoIdsByPermission( name, departmentNames, companyNames );
	}

	public List<String> listCatagoryIdsByPermission(EntityManagerContainer emc, String name, List<String> departmentNames, List<String> companyNames, String appId) throws Exception {
		Business business = new Business( emc );
		List<String> catagoryIds = business.getCatagoryInfoFactory().listByAppId( appId );
		return business.getAppCatagoryPermissionFactory().listCatagoryIdsByPermission( name, departmentNames, companyNames, catagoryIds );
	}

}
