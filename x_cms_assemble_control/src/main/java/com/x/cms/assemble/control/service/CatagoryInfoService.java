package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CatagoryInfo;

public class CatagoryInfoService {

	public List<CatagoryInfo> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getCatagoryInfoFactory().list( ids );
	}
	
	public CatagoryInfo get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		return emc.find(id, CatagoryInfo.class );
	}

	public List<String> listByAppId( EntityManagerContainer emc, String appId ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getCatagoryInfoFactory().listByAppId( appId );
	}

	public List<String> listNoPermissionCatagoryInfoIds( EntityManagerContainer emc) throws Exception {
		List<String> ids = null;
		Business business = new Business( emc );
		ids = business.getAppCatagoryPermissionFactory().listAllCatagoryInfoIds();		
		return business.getCatagoryInfoFactory().listNoPermissionCatagoryInfoIds( ids );
	}

	public List<String> listNoPermissionCatagoryIds(EntityManagerContainer emc, String appId) throws Exception {
		List<String> ids = null;
		Business business = new Business( emc );
		ids = business.getAppCatagoryPermissionFactory().listAllCatagoryInfoIds();		
		return business.getCatagoryInfoFactory().listNoPermissionCatagoryInfoIds( ids, appId );
	}

	public List<CatagoryInfo> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getCatagoryInfoFactory().listAll();
	}
	public CatagoryInfo saveBaseInfo( EntityManagerContainer emc, CatagoryInfo catagoryInfo ) throws Exception {
		CatagoryInfo catagoryInfo_tmp = null;
		if( catagoryInfo.getId() == null ){
			catagoryInfo.setId( CatagoryInfo.createId() );
		}
		catagoryInfo_tmp = emc.find( catagoryInfo.getId(), CatagoryInfo.class );
		emc.beginTransaction( CatagoryInfo.class );
		if( catagoryInfo_tmp == null ){
			emc.persist( catagoryInfo, CheckPersistType.all);
		}else{
			catagoryInfo_tmp.setAppId( catagoryInfo.getAppId() );
			catagoryInfo_tmp.setCatagoryAlias( catagoryInfo.getCatagoryAlias() );
			catagoryInfo_tmp.setCatagoryMemo( catagoryInfo.getCatagoryMemo() );
			catagoryInfo_tmp.setCatagoryName( catagoryInfo.getCatagoryName() );
			catagoryInfo_tmp.setFormId( catagoryInfo.getFormId() );
			catagoryInfo_tmp.setFormName( catagoryInfo.getFormName() );
			catagoryInfo_tmp.setParentId( catagoryInfo.getParentId() );
			catagoryInfo_tmp.setReadFormId( catagoryInfo.getReadFormId() );
			catagoryInfo_tmp.setReadFormName( catagoryInfo.getReadFormName() );
			emc.check( catagoryInfo_tmp, CheckPersistType.all );	
		}
		emc.commit();
		return catagoryInfo;
	}
	public CatagoryInfo save(EntityManagerContainer emc, CatagoryInfo catagoryInfo) throws Exception {
		CatagoryInfo catagoryInfo_tmp = null;
		if( catagoryInfo.getId() == null ){
			catagoryInfo.setId( CatagoryInfo.createId() );
		}
		catagoryInfo_tmp = emc.find( catagoryInfo.getId(), CatagoryInfo.class );
		emc.beginTransaction( CatagoryInfo.class );
		if( catagoryInfo_tmp == null ){
			emc.persist( catagoryInfo, CheckPersistType.all);
		}else{
			catagoryInfo.copyTo( catagoryInfo_tmp );
			emc.check( catagoryInfo_tmp, CheckPersistType.all );	
		}
		emc.commit();
		return catagoryInfo;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AppInfo appInfo = null;
		CatagoryInfo catagoryInfo = emc.find( id, CatagoryInfo.class );
		if( catagoryInfo != null ){
			appInfo = emc.find( catagoryInfo.getFormId(), AppInfo.class );
			emc.beginTransaction(CatagoryInfo.class);
			if( appInfo != null ){
				emc.beginTransaction( AppInfo.class );
				appInfo.getCatagoryList().remove( id );
				emc.check( appInfo, CheckPersistType.all );
			}
			emc.remove( catagoryInfo, CheckRemoveType.all );
			emc.commit();
		}else{
			throw new Exception("catagoryInfo is not exists!");
		}
	}
}
