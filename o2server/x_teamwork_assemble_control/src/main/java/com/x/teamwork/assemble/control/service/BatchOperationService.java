package com.x.teamwork.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.BatchOperation;

public class BatchOperationService {

	public List<BatchOperation> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.batchOperationFactory().list( ids );
	}
	
	public List<BatchOperation> list( EntityManagerContainer emc, Integer maxCount ) throws Exception {
		if( maxCount == null ){
			maxCount = 10;
		}
		Business business = new Business( emc );
		return business.batchOperationFactory().list(maxCount);
	}
	
	public List<BatchOperation> listNotRun( EntityManagerContainer emc, Integer maxCount ) throws Exception {
		if( maxCount == null ){
			maxCount = 10;
		}
		Business business = new Business( emc );
		return business.batchOperationFactory().listNotRun(maxCount);
	}
}
