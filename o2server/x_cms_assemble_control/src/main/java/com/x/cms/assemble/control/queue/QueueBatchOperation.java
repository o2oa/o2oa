package com.x.cms.assemble.control.queue;

import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.core.entity.CmsBatchOperation;

/**
 * 批处理操作执行
 */
public class QueueBatchOperation extends AbstractQueue<CmsBatchOperation> {
	
	private CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
	private CmsBatchOperationProcessService cmsBatchOperationProcessService = new CmsBatchOperationProcessService();
	
	public void execute( CmsBatchOperation operation ) throws Exception {
		try {
			cmsBatchOperationProcessService.process( operation );
		}catch( Exception e) {
			e.printStackTrace();
			try {
				cmsBatchOperationPersistService.addErrorTime( operation, 1 );
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}
}
