package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CmsBatchOperation;

public class CmsBatchOperationService {

	public List<CmsBatchOperation> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.cmsBatchOperationFactory().list( ids );
	}
	
	public List<CmsBatchOperation> list( EntityManagerContainer emc, Integer maxCount ) throws Exception {
		if( maxCount == null ){
			maxCount = 10;
		}
		Business business = new Business( emc );
		return business.cmsBatchOperationFactory().list(maxCount);
	}

	public List<CmsBatchOperation> list( EntityManagerContainer emc, Integer maxCount, Integer minutesAgo) throws Exception {
		if( maxCount == null ){
			maxCount = 10;
		}
		Business business = new Business( emc );
		return business.cmsBatchOperationFactory().list(maxCount, minutesAgo);
	}
	
	public List<CmsBatchOperation> listNotRun( EntityManagerContainer emc, Integer maxCount ) throws Exception {
		if( maxCount == null ){
			maxCount = 10;
		}
		Business business = new Business( emc );
		return business.cmsBatchOperationFactory().listNotRun(maxCount);
	}
}
