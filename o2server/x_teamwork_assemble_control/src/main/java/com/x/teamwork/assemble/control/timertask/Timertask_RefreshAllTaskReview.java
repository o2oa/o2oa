package com.x.teamwork.assemble.control.timertask;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.core.entity.Task;

/**
 * 定时代理: 定期将所有的工作任务的Review重新核对一次，定期做修复
 *
 */
public class Timertask_RefreshAllTaskReview implements Job {

	private BatchOperationPersistService batchOperationPersistService = new BatchOperationPersistService();
	private ProjectQueryService projectQueryService = new ProjectQueryService();
	private TaskQueryService taskQueryService = new TaskQueryService();
	private static Logger logger = LoggerFactory.getLogger( Timertask_RefreshAllTaskReview.class );

	@Override
	public void execute( JobExecutionContext arg0 ) throws JobExecutionException {
		if( ThisApplication.queueBatchOperation.isEmpty() ) {//如果队列里有消息需要处理，先处理队列
			logger.info("Timertask_BatchOperationTask ->try to check all task in database......");
			//如果队列里已经没有任务了，那么检查一下是否还有未revieiw的工作任务，添加到队列刷新工作作息的Review
			
			Task task = null;
			List<String> projectIds = null;
			List<String> taskIds = null;
			try {
				projectIds = projectQueryService.listAllProjectIds();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if( ListTools.isNotEmpty( projectIds )) {
				for( String projectId : projectIds ) {
					try {
						taskIds = taskQueryService.listAllTaskIdsWithProject( projectId );
						if( ListTools.isNotEmpty( taskIds )) {						
							for( String taskId : taskIds ) {
								task = taskQueryService.get( taskId );
								if( task != null ) {
									logger.info("Timertask_RefreshAllTaskReview addOperation->refresh permission for task:" +  task.getName());
									batchOperationPersistService.addOperation( 
											BatchOperationProcessService.OPT_OBJ_TASK, 
											BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "refresh permission for task:" +  task.getName());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
		}
		logger.info("Timertask_BatchOperationTask -> batch operations timer task excute completed.");
	}
}