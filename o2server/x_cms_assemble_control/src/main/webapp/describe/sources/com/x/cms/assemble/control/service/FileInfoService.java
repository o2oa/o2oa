package com.x.cms.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.FileInfo;

public class FileInfoService {
	
	public FileInfo get( EntityManagerContainer emc, String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getFileInfoFactory().get( id );
	}
}
