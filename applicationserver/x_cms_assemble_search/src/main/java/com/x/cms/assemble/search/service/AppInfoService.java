package com.x.cms.assemble.search.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.search.Business;
import com.x.cms.core.entity.AppInfo;

public class AppInfoService {

	public List<AppInfo> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.appInfoFactory().list( ids );
	}
	public List<String> listAllIds(EntityManagerContainer emc, String documentType ) throws Exception {
		Business business = new Business( emc );
		return business.appInfoFactory().listAllIds(documentType);
	}
	public List<AppInfo> listAll(EntityManagerContainer emc, String documentType) throws Exception {
		Business business = new Business( emc );
		return business.appInfoFactory().listAll(documentType);
	}

	public AppInfo get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, AppInfo.class );
	}
}
