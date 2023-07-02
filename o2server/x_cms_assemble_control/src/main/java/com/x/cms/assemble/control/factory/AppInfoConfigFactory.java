package com.x.cms.assemble.control.factory;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfoConfig;

/**
 * 栏目配置支持信息基础功能服务类
 * 
 * @author O2LEE
 */
public class AppInfoConfigFactory extends AbstractFactory {

	public AppInfoConfigFactory(Business business) throws Exception {
		super(business);
	}

	public AppInfoConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, AppInfoConfig.class );
	}
	
	public String getContent( String id ) throws Exception {
		AppInfoConfig AppInfoConfig = this.entityManagerContainer().find( id, AppInfoConfig.class );
		if( AppInfoConfig != null ) {
			return AppInfoConfig.getConfig();
		}
		return null;
	}
}