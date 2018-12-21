package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppCategoryPermission;

public class RescissoryClass_AppCategoryPermissionService {

	public AppCategoryPermission get(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().get( id );
	}
	
	public List<AppCategoryPermission> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().list( ids );
	}	

	public AppCategoryPermission save( EntityManagerContainer emc, AppCategoryPermission wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		AppCategoryPermission appCategoryPermission = null;
		appCategoryPermission = emc.find( wrapIn.getId(), AppCategoryPermission.class );
		if( appCategoryPermission != null ){
			wrapIn.copyTo( appCategoryPermission );
			emc.beginTransaction( AppCategoryPermission.class );
			emc.check( appCategoryPermission, CheckPersistType.all );
			emc.commit();
		}else{
			appCategoryPermission = new AppCategoryPermission();
			wrapIn.copyTo( appCategoryPermission, JpaObject.FieldsUnmodify );
			if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ) {
				appCategoryPermission.setId( wrapIn.getId() );
			}
			emc.beginTransaction( AppCategoryPermission.class );
			emc.persist( appCategoryPermission, CheckPersistType.all );
			emc.commit();
		}
		return appCategoryPermission;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AppCategoryPermission appCategoryPermission = emc.find( id, AppCategoryPermission.class );
		if( appCategoryPermission != null ){
			emc.beginTransaction( AppCategoryPermission.class );
			emc.remove(appCategoryPermission, CheckRemoveType.all);
			emc.commit();
		}else{
			throw new Exception("appCategoryPermission is not exists!");
		}
	}
	
	public List<String> listPermissionByAppInfo( EntityManagerContainer emc, String appId, String permission ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listPermissionByAppInfo( appId, permission );
	}
	
	public List<String> listPermissionByCategory(EntityManagerContainer emc, String categoryId, String permission) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listPermissionByCataogry( categoryId, permission );
	}
	
	/**
	 * 根据用户姓名以及用户所在的顶层组织和组织列表查询该用户可以访问到的所有应用栏目ID列表
	 * 不用判断可见和发布权限 ，发布的人员肯定可见，只需要distinct就行
	 * @param emc
	 * @param name
	 * @param unitNames
	 * @param topUnitNames
	 * @param groupNames 
	 * @return
	 * @throws Exception
	 */
	public List<String> listAppInfoIdsByPermission( EntityManagerContainer emc, String name, List<String> unitNames, List<String> groupNames, String permission ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listAppInfoIdsByPermission( name, unitNames, groupNames, null, permission );
	}
	
	public List<String> listAppInfoIdsByPermission( EntityManagerContainer emc, String name, List<String> unitNames, List<String> groupNames, String appId, String permission ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listAppInfoIdsByPermission( name, unitNames, groupNames, appId, permission );
	}

	public List<String> listAllAppInfoIds( EntityManagerContainer emc, String type, String permission ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listAllAppInfoIds( type, permission);
	}
	
	public List<String> listAppCategoryIdByCondition( EntityManagerContainer emc, String objectType, String objectId, String personName, String permission ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listAppCategoryIdByCondition( objectType, objectId, personName, permission );
	}
	
	public List<String> listCategoryIdsByPermission( EntityManagerContainer emc, String name, List<String> unitNames, List<String> groupNames, String appId, String permission ) throws Exception {
		Business business = new Business( emc );		
		return business.getAppCategoryPermissionFactory().listCategoryIdsByPermission( name, unitNames, groupNames, appId, permission );
	}
	
	public List<String> listAllCategoryIds( EntityManagerContainer emc, String permission ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listAllCategoryInfoIds( permission);
	}

	public List<String> listAppInfoIdsByPermission(EntityManagerContainer emc, List<String> queryObjectNames,
			String queryObjectType, String objectType, String permission) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryPermissionFactory().listAppInfoIdsByPermission( queryObjectNames, queryObjectType, objectType, permission );
	}
}
