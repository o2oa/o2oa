package com.x.teamwork.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.teamwork.core.entity.BatchOperation;

/**
 * 对批处理操作信息进行管理的服务类，利用Service完成事务控制
 * 
 */
public class BatchOperationQueryService {

	private BatchOperationService cmsBatchOperationService = new BatchOperationService();
	
	public List<BatchOperation> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<BatchOperation> list( Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<BatchOperation> listNotRun( Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.listNotRun( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
