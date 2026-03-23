package com.x.teamwork.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;
import com.x.teamwork.assemble.control.queue.QueueBatchOperation;
import com.x.teamwork.assemble.control.service.SystemConfigPersistService;
import com.x.teamwork.assemble.control.schedule.BatchOperationTask;
import com.x.teamwork.assemble.control.schedule.CheckAllTaskOverTimeJob;

public class ThisApplication {

	protected static Context context;
	public static QueueBatchOperation queueBatchOperation;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			//执行消息操作
			queueBatchOperation = new QueueBatchOperation();

			MessageConnector.start(context());

			context().startQueue( queueBatchOperation );

			new SystemConfigPersistService().initSystemConfig();

			//每隔5分钟检查是否有未完成的批处理工作需要完成的task需要核对权限信息
			context.schedule( BatchOperationTask.class, "0 */5 * * * ?" );

			//每天凌晨把所有项目的所有工作任务的权限和review信息核对一次
			//context.schedule( Timertask_RefreshAllTaskReview.class, "0 0 2 * * ?" );

			//判断工作任务是否已经超时
			context.schedule( CheckAllTaskOverTimeJob.class, "0 30 1,12 * * ?" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
