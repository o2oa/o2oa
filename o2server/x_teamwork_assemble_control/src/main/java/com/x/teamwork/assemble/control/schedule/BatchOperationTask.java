package com.x.teamwork.assemble.control.schedule;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.assemble.control.service.BatchOperationQueryService;
import com.x.teamwork.core.entity.BatchOperation;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.List;

/**
 * 定时代理: 定期执行批处理，将批处理信息压入队列（如果队列是空的话）
 * @author sword
 */
public class BatchOperationTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger( BatchOperationTask.class );
	private BatchOperationQueryService batchOperationQueryService = new BatchOperationQueryService();
	private static final Long DIFFER = 1000 * 60 * 10L;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) {
		if( ThisApplication.queueBatchOperation.isEmpty() ) {
			try {
				List<BatchOperation> operations = batchOperationQueryService.list( 2000 );
				logger.info("Timertask_BatchOperationTask -> query {} task batch operation in database......", operations.size());
				if( ListTools.isNotEmpty( operations )) {
					for( BatchOperation operation : operations ) {
						try {
							if(((new Date()).getTime() - operation.getCreateTime().getTime()) > DIFFER) {
								ThisApplication.queueBatchOperation.send(operation);
							}
						} catch (Exception e) {
							logger.warn("Timertask_BatchOperationTask -> send operation to queue got an exception.");
							logger.error(e);
						}
					}
				}
			} catch (Exception e) {
				logger.warn("Timertask_BatchOperationTask -> list operations got an exception.");
				logger.error(e);
			}
		}
	}
}
