package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDict;

public class AppDictService {

	public List<AppDict> list(EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppDictFactory().list(ids);
	}

    public List<String> listIdsWithAppId( EntityManagerContainer emc, String appId ) throws Exception {
        if( appId == null || appId.isEmpty() ){
            throw new Exception("appId is null!");
        }
        Business business = new Business( emc );
        return business.getAppDictFactory().listWithAppInfo( appId );
    }
}
