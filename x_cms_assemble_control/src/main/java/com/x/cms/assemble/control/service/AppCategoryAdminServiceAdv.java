package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.WrapInAppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryAdmin;

public class AppCategoryAdminServiceAdv {

	private AppCategoryAdminService appCategoryAdminService = new AppCategoryAdminService();
	
	public List<AppCategoryAdmin> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listAppCategoryIdByCategoryId( String categoryId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.listAppCategoryIdByCategoryId( emc, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listAppCategoryIdByCondition( String objectType, String objectId, String personName ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.listAppCategoryIdByCondition(emc, objectType, objectId, personName);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AppCategoryAdmin> list( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCategoryIdByAppId( String appId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.listAppCategoryIdByAppId( emc, appId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCategoryIdByUser( String person ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.listAppCategoryIdByUser( emc, person );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCategoryObjectIdByUser(String person, String objectType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.listAppCategoryObjectIdByUser( emc, person, objectType );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppCategoryAdmin get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.id( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppCategoryAdmin save( WrapInAppCategoryAdmin wrapIn, EffectivePerson currentPerson ) throws Exception {
		AppCategoryAdmin appCategoryAdmin = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCategoryAdmin = appCategoryAdminService.save( emc, wrapIn );
			return appCategoryAdmin;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( AppCategoryAdmin appCategoryAdmin, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCategoryAdminService.delete( emc, appCategoryAdmin.getId() );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listManageableCategoryIds(String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCategoryAdminService.listManageableCategoryIds( emc, name );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
