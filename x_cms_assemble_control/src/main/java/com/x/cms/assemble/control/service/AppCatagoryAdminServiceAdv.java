package com.x.cms.assemble.control.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.core.entity.AppCatagoryAdmin;
import com.x.cms.core.entity.CatagoryInfo;

public class AppCatagoryAdminServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( AppCatagoryAdminServiceAdv.class );
	
	private LogService logService = new LogService();
	private CatagoryInfoService catagoryInfoService = new CatagoryInfoService();
	private AppCatagoryAdminService appCatagoryAdminService = new AppCatagoryAdminService();
	
	public List<AppCatagoryAdmin> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryAdminService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listAppCatagoryIdByCatagoryId( String catagoryId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryAdminService.listAppCatagoryIdByCatagoryId( emc, catagoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AppCatagoryAdmin> list( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryAdminService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCatagoryIdByAppId( String appId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryAdminService.listAppCatagoryIdByAppId( emc, appId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCatagoryIdByUser( String person ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryAdminService.listAppCatagoryIdByUser( emc, person );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAppCatagoryObjectIdByUser(String person, String objectType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryAdminService.listAppCatagoryObjectIdByUser( emc, person, objectType );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppCatagoryAdmin get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appCatagoryAdminService.id( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AppCatagoryAdmin save( AppCatagoryAdmin appCatagoryAdmin, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCatagoryAdmin = appCatagoryAdminService.save( emc, appCatagoryAdmin );
			if( currentPerson != null ){
				if ( appCatagoryAdmin != null && "APP".equalsIgnoreCase( appCatagoryAdmin.getObjectType()) ) {
					logService.log( emc, currentPerson.getName(), "成功保存应用的管理员配置信息", appCatagoryAdmin.getObjectId(), "", "", "", "ADMINCONFIG", "新增" );
				} else if ( appCatagoryAdmin != null && "CATAGORY".equalsIgnoreCase(appCatagoryAdmin.getObjectType())) {
					CatagoryInfo catagoryInfo = catagoryInfoService.get( emc, appCatagoryAdmin.getObjectId() );
					if (catagoryInfo != null) {
						logService.log( emc, currentPerson.getName(), "成功保存分类的管理员配置信息", catagoryInfo.getAppId(), appCatagoryAdmin.getObjectId(), "", "", "ADMINCONFIG", "新增");
					}
				}
			}
			return appCatagoryAdmin;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( AppCatagoryAdmin appCatagoryAdmin, EffectivePerson currentPerson ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appCatagoryAdminService.delete( emc, appCatagoryAdmin.getId() );
			if( currentPerson != null ){
				if ( appCatagoryAdmin != null && "APP".equalsIgnoreCase( appCatagoryAdmin.getObjectType()) ) {
					logService.log( emc, currentPerson.getName(), "成功删除应用的管理员配置信息", appCatagoryAdmin.getObjectId(), "", "", "", "ADMINCONFIG", "删除" );
				} else if ( appCatagoryAdmin != null && "CATAGORY".equalsIgnoreCase(appCatagoryAdmin.getObjectType())) {
					CatagoryInfo catagoryInfo = catagoryInfoService.get( emc, appCatagoryAdmin.getObjectId() );
					if (catagoryInfo != null) {
						logService.log( emc, currentPerson.getName(), "成功删除分类的管理员配置信息", catagoryInfo.getAppId(), appCatagoryAdmin.getObjectId(), "", "", "ADMINCONFIG", "删除");
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

}
