package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.core.entity.AppCatagoryPermission;
import com.x.cms.core.entity.CatagoryInfo;

public class AppCatagoryPermissionServiceAdv {

	private LogService logService = new LogService();
	private CatagoryInfoService catagoryInfoService = new CatagoryInfoService();
	private AppCatagoryPermissionService appCatagoryPermissionService = new AppCatagoryPermissionService();
	
	public List<AppCatagoryPermission> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryPermissionService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AppCatagoryPermission> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryPermissionService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppCatagoryPermission get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryPermissionService.id( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppCatagoryPermission save( AppCatagoryPermission appCatagoryPermission, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCatagoryPermission = appCatagoryPermissionService.save( emc, appCatagoryPermission );
			if( currentPerson != null ){
				if( "APP".equalsIgnoreCase( appCatagoryPermission.getObjectType() )){
					logService.log( emc, currentPerson.getName(), "成功新增应用的访问权限件信息", appCatagoryPermission.getObjectId(), "", "", "", "PERMISSION", "新增" );
				}else if( "CATAGORY".equalsIgnoreCase( appCatagoryPermission.getObjectType() )){
					CatagoryInfo catagoryInfo = catagoryInfoService.get( emc, appCatagoryPermission.getObjectId() );
					if( catagoryInfo != null ){
						logService.log( emc, currentPerson.getName(), "成功新增分类的访问权限件信息", catagoryInfo.getAppId(), appCatagoryPermission.getObjectId(), "", "", "PERMISSION", "新增" );
					}				
				}
			}
			return appCatagoryPermission;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( AppCatagoryPermission appCatagoryPermission, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCatagoryPermissionService.delete( emc, appCatagoryPermission.getId() );
			if( currentPerson != null ){
				if( "APP".equalsIgnoreCase( appCatagoryPermission.getObjectType() )){
					logService.log( emc, currentPerson.getName(), "成功删除应用的访问权限件信息", appCatagoryPermission.getObjectId(), "", "", "", "PERMISSION", "删除" );
				}else if( "CATAGORY".equalsIgnoreCase( appCatagoryPermission.getObjectType() )){
					CatagoryInfo catagoryInfo = catagoryInfoService.get( emc, appCatagoryPermission.getObjectId() );
					if( catagoryInfo != null ){
						logService.log( emc, currentPerson.getName(), "成功删除分类的访问权限件信息", catagoryInfo.getAppId(), appCatagoryPermission.getObjectId(), "", "", "PERMISSION", "删除" );
					}				
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCatagoryPermissionByUser(String person, String objectType) throws Exception {
		if( objectType == null || objectType.isEmpty() ){
			throw new Exception("object type is null!");
		}
		if( person == null || person.isEmpty() ){
			throw new Exception("person type is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryPermissionService.listAppCatagoryPermissionByUser( emc, person, objectType );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppInfoByUserPermission(String person) throws Exception {
		if( person == null || person.isEmpty() ){
			throw new Exception("person type is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryPermissionService.listAppInfoByUserPermission( emc, person );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listCatagoryInfoByUserPermission( EffectivePerson currentPerson, List<String> appCatagoryIds) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryPermissionService.listCatagoryIdsByPermission( emc, currentPerson, appCatagoryIds );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listPermissionByAppInfo( String appId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryPermissionService.listPermissionByAppInfo( emc, appId );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
