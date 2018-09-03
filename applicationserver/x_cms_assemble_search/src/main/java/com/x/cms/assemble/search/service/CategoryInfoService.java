package com.x.cms.assemble.search.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.search.Business;
import com.x.cms.core.entity.CategoryInfo;

public class CategoryInfoService {

	public List<CategoryInfo> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.categoryInfoFactory().list( ids );
	}
	
	public CategoryInfo get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		return emc.find(id, CategoryInfo.class );
	}

	public List<String> listByAppId( EntityManagerContainer emc, String appId ) throws Exception {
		if( StringUtils.isEmpty(appId)){
			return null;
		}
		Business business = new Business( emc );
		return business.categoryInfoFactory().listByAppId( appId );
	}
	
	public List<String> listByAppId( String appId ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			return null;
		}
		Business business = new Business( EntityManagerContainerFactory.instance().create() );
		return business.categoryInfoFactory().listByAppId( appId );
	}
	
	public List<String> listByAppIds( EntityManagerContainer emc, List<String> appIds, String documentType, Boolean manager, Integer maxCount ) throws Exception {
		if( ListTools.isEmpty( appIds ) ){
			return listAllIds(emc);
		}
		Business business = new Business( emc );
		return business.categoryInfoFactory().listByAppIds( appIds, documentType, manager, maxCount );
	}

	public List<CategoryInfo> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.categoryInfoFactory().listAll();
	}
	
	public List<String> listAllIds(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.categoryInfoFactory().listAllIds();
	}
	
}
