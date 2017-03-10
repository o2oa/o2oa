package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.WrapInAppCategoryPermission;
import com.x.cms.core.entity.AppCategoryPermission;

public class AppCategoryPermissionServiceAdv {

	private AppCategoryPermissionService appCategoryPermissionService = new AppCategoryPermissionService();

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

	public AppCategoryPermission save( WrapInAppCategoryPermission wrapIn, EffectivePerson currentPerson ) throws Exception {
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

	public List<String> listAppCategoryIdByCondition( String objectType, String objectId, String personName, String permission ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryPermissionService.listAppCategoryIdByCondition(emc, objectType, objectId, personName, permission);
		} catch ( Exception e ) {
			throw e;
		}
	}

}
