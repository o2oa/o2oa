package com.x.cms.assemble.search.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.search.Business;
import com.x.cms.core.entity.Document;

public class DocumentInfoService {

	public List<Document> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentFactory().list( ids );
	}
	
	public List<String> listByCategoryId( EntityManagerContainer emc, String categoryId, String status ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentFactory().listByCategoryId( categoryId, status );
	}

	public Document get(EntityManagerContainer emc, String id) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentFactory().get( id );
	}
}
