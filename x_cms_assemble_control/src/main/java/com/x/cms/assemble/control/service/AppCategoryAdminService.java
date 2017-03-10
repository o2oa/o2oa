package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.WrapInAppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

public class AppCategoryAdminService {

	public List<String> listAppCategoryIdByCategoryId(EntityManagerContainer emc, String categoryId) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAppCategoryIdByCategoryId( categoryId );
	}

	public List<AppCategoryAdmin> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().list( ids );
	}

	public List<String> listAppCategoryIdByAppId( EntityManagerContainer emc, String appId ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAppCategoryIdByAppId( appId );
	}
	
	public List<String> listAppCategoryIdByCondition( EntityManagerContainer emc, String objectType, String objectId, String personName ) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAppCategoryIdByCondition( objectType, objectId, personName );
	}

	public List<String> listAppCategoryIdByUser(EntityManagerContainer emc, String person) throws Exception {
		if( person == null || person.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAppCategoryIdByUser( person );
	}

	public List<String> listAppCategoryObjectIdByUser(EntityManagerContainer emc, String person, String objectType ) throws Exception {
		if( person == null || person.isEmpty() ){
			return null;
		}
		if( objectType == null || objectType.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAppCategoryObjectIdByUser( person, objectType );
	}

	public List<AppCategoryAdmin> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAll();
	}

	public AppCategoryAdmin id(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().get( id );
	}

	public AppCategoryAdmin save( EntityManagerContainer emc, WrapInAppCategoryAdmin wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		AppCategoryAdmin appCategoryAdmin = null;
		
		appCategoryAdmin = emc.find( wrapIn.getId(), AppCategoryAdmin.class );
		emc.beginTransaction( AppCategoryAdmin.class );
		if( appCategoryAdmin != null ){
			WrapTools.appCategoryAdmin_wrapin_copier.copy( wrapIn, appCategoryAdmin );
			emc.check( appCategoryAdmin, CheckPersistType.all );
		}else{
			appCategoryAdmin = new AppCategoryAdmin();
			WrapTools.appCategoryAdmin_wrapin_copier.copy( wrapIn, appCategoryAdmin );
			emc.persist( appCategoryAdmin, CheckPersistType.all );
		}
		emc.commit();
		return appCategoryAdmin;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AppCategoryAdmin appCategoryAdmin = emc.find( id, AppCategoryAdmin.class );
		if( appCategoryAdmin != null ){
			emc.beginTransaction( AppCategoryAdmin.class );
			emc.remove(appCategoryAdmin, CheckRemoveType.all);
			emc.commit();
		}else{
			throw new Exception("appCategoryAdmin is not exists!");
		}
	}

	public List<String> listAppInfoIdsByAdminName( EntityManagerContainer emc, String name) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listAppInfoIdsByAdminName( name );
	}
	
	public List<String> listCategoryInfoIdsByAdminName( EntityManagerContainer emc, String name) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listCategoryInfoIdsByAdminName( name );
	}

	public List<String> listCategoryInfoIdsByAdminName(EntityManagerContainer emc, String name, String appId ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		if( appId == null || appId.isEmpty() ){
			throw new Exception( "appId is null!" );
		}
		List<String> myCategoryIds = null;
		Business business = new Business( emc );
		myCategoryIds = business.getAppCategoryAdminFactory().listCategoryInfoIdsByAdminName( name );
		return business.getCategoryInfoFactory().listMyCategoryWithAppId( myCategoryIds, appId );
	}

	public void addNewAdminForAppInfo( EntityManagerContainer emc, AppInfo appInfo, String personName ) throws Exception {
		if( appInfo == null ){
			throw new Exception( "appInfo is null!" );
		}		
		List<String> myCategoryIds = null;
		AppCategoryAdmin admin = null;
		Business business = new Business( emc );
		myCategoryIds = business.getAppCategoryAdminFactory().listAppCategoryIdByAppId( appInfo.getId(), personName );
		if( myCategoryIds == null || myCategoryIds.isEmpty() ){
			admin = new AppCategoryAdmin();
			admin.setAdminLevel( "ADMIN" );
			admin.setAdminName( personName );
			admin.setAdminUid( personName );
			admin.setCreatorUid( personName );
			admin.setDescription( "应用栏目创建者，默认设定为管理员" );
			admin.setObjectId( appInfo.getId() );
			admin.setObjectType( "APPINFO" );
			emc.beginTransaction(AppCategoryAdmin.class);
			emc.persist( admin, CheckPersistType.all);
			emc.commit();
		}
	}

	public void addNewAdminForCategoryInfo(EntityManagerContainer emc, CategoryInfo categoryInfo, String personName ) throws Exception {
		if( categoryInfo == null ){
			throw new Exception( "categoryInfo is null!" );
		}		
		List<String> myCategoryIds = null;
		AppCategoryAdmin admin = null;
		Business business = new Business( emc );
		myCategoryIds = business.getAppCategoryAdminFactory().listAppCategoryIdByAppId( categoryInfo.getId(), personName );
		if( myCategoryIds == null || myCategoryIds.isEmpty() ){
			admin = new AppCategoryAdmin();
			admin.setAdminLevel( "ADMIN" );
			admin.setAdminName( personName );
			admin.setAdminUid( personName );
			admin.setCreatorUid( personName );
			admin.setDescription( "分类创建者，默认设定为管理员" );
			admin.setObjectId( categoryInfo.getId() );
			admin.setObjectType( "CATEGORY" );
			emc.beginTransaction(AppCategoryAdmin.class);
			emc.persist( admin, CheckPersistType.all);
			emc.commit();
		}
	}

	public List<String> listManageableCategoryIds(EntityManagerContainer emc, String name) throws Exception {
		Business business = new Business( emc );
		return business.getAppCategoryAdminFactory().listCategoryInfoIdsByAdminName( name );
	}
}
