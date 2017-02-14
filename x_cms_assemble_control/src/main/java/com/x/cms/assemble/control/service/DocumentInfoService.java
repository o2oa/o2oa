package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;

public class DocumentInfoService {

	public List<Document> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().list( ids );
	}
	
	public List<String> listByCatagoryId( EntityManagerContainer emc, String catagoryId ) throws Exception {
		if( catagoryId == null || catagoryId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().listByCatagoryId( catagoryId );
	}

	public Document get(EntityManagerContainer emc, String id) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().get( id );
	}

	
	
}
