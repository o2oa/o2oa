package com.x.cms.assemble.control.timertask;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.CmsBatchOperationQueryService;
import com.x.cms.core.entity.CmsBatchOperation;

/**
 * 定时代理: 定期执行批处理，将批处理信息压入队列（如果队列是空的话）
 *
 */
public class Timertask_BatchOperationTask implements Job {

	private static Logger logger = LoggerFactory.getLogger( Timertask_BatchOperationTask.class );
	private CmsBatchOperationQueryService cmsBatchOperationQueryService = new CmsBatchOperationQueryService();

	@Override
	public void execute( JobExecutionContext arg0 ) throws JobExecutionException {
		if( ThisApplication.queueBatchOperation.isEmpty() ) {
			List<CmsBatchOperation> operations = null;		
			try {
				operations = cmsBatchOperationQueryService.listNotRun( 100 );
			} catch (Exception e) {
				logger.warn("Timertask_BatchOperationTask list operations got an exception.");
				logger.error(e);
			}
			
			if( ListTools.isNotEmpty( operations )) {
				for( CmsBatchOperation operation : operations ) {
					try {
						ThisApplication.queueBatchOperation.send( operation );
					} catch (Exception e) {
						logger.warn("Timertask_BatchOperationTask send operation to queue got an exception.");
						logger.error(e);
					}
				}
			}
		}
		logger.info("Timertask_BatchOperationTask excute batch operations completed.");
	}
}