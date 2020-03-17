package com.x.teamwork.assemble.control.queue;

import com.x.base.core.project.queue.AbstractQueue;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.BatchOperation;

/**
 * 批处理操作执行
 */
public class QueueBatchOperation extends AbstractQueue<BatchOperation> {
	
	private BatchOperationPersistService cmsBatchOperationPersistService = new BatchOperationPersistService();
	private BatchOperationProcessService cmsBatchOperationProcessService = new BatchOperationProcessService();
	
	public void execute( BatchOperation operation ) throws Exception {
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
