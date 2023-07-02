package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.control.Business;

public class ScriptService {

    public List<String> listIdsWithAppId( EntityManagerContainer emc, String appId ) throws Exception {
        if( StringUtils.isEmpty( appId ) ){
            throw new Exception("appId is null!");
        }
        Business business = new Business( emc );
        return business.getScriptFactory().listWithApp( appId );
    }
}
