package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.FileInfo;

public class FileInfoService {

	public List<FileInfo> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getFileInfoFactory().list( ids );
	}

	public FileInfo get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getFileInfoFactory().get( id );
	}

	
	
}
