package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.core.entity.CmsBatchOperation;

/**
 * 对批处理操作信息进行管理的服务类，利用Service完成事务控制
 * 
 */
public class CmsBatchOperationQueryService {

	private CmsBatchOperationService cmsBatchOperationService = new CmsBatchOperationService();
	
	public List<CmsBatchOperation> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CmsBatchOperation> list( Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CmsBatchOperation> list( Integer maxCount, Integer minutesAgo) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, maxCount, minutesAgo);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<CmsBatchOperation> listNotRun( Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.listNotRun( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
