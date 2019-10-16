package com.x.teamwork.assemble.control.timertask;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.assemble.control.service.BatchOperationQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.core.entity.BatchOperation;
import com.x.teamwork.core.entity.Task;

/**
 * 定时代理: 定期执行批处理，将批处理信息压入队列（如果队列是空的话）
 *
 */
public class Timertask_BatchOperationTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger( Timertask_BatchOperationTask.class );
	private BatchOperationQueryService batchOperationQueryService = new BatchOperationQueryService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		if( ThisApplication.queueBatchOperation.isEmpty() ) {//如果队列里有消息需要处理，先处理队列
			List<BatchOperation> operations = null;		
			try {
				logger.info("Timertask_BatchOperationTask ->  query 2000 task batch operation in database......");
				////如果队列处理完了，就看看有没有批处理信息
				operations = batchOperationQueryService.list( 2000 );
			} catch (Exception e) {
				logger.warn("Timertask_BatchOperationTask -> list operations got an exception.");
				logger.error(e);
			}
			if( ListTools.isNotEmpty( operations )) {
				for( BatchOperation operation : operations ) {
					try {
						logger.info("Timertask_BatchOperationTask -> send operation to queue[queueBatchOperation]......");
						ThisApplication.queueBatchOperation.send( operation );
					} catch (Exception e) {
						logger.warn("Timertask_BatchOperationTask -> send operation to queue got an exception.");
						logger.error(e);
					}
				}
			}else {
				logger.info("Timertask_BatchOperationTask -> not fount any task batch operation, try to check unreview task in database......");
				//如果队列里已经没有任务了，那么检查一下是否还有未revieiw的工作任务，添加到队列刷新工作作息的Review
				BatchOperationPersistService batchOperationPersistService = new BatchOperationPersistService();
				TaskQueryService taskQueryService = new TaskQueryService();
				List<Task> tasks = null;
				try {
					tasks = taskQueryService.listUnReviewIds(1000);
					if( ListTools.isNotEmpty( tasks )) {						
						for( Task task : tasks ) {
							logger.info("Timertask_BatchOperationTask addOperation->refresh permission for task:" +  task.getName());
							batchOperationPersistService.addOperation( 
									BatchOperationProcessService.OPT_OBJ_TASK, 
									BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "refresh permission for task:" +  task.getName());
						}
					}else {
						logger.info("Timertask_BatchOperationTask -> not found any unreview task in database.");
						//也没有需要review的文档了，那么检查一下最近变更过的身份，组织，群组，人员等信息
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else {
			logger.info("Timertask_BatchOperationTask -> queueBatchOperation is processing, wait to next excute.");
		}
		logger.info("Timertask_BatchOperationTask -> batch operations timer task excute completed.");
	}
}