package com.x.cms.assemble.control;

import java.util.concurrent.ConcurrentHashMap;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.cms.assemble.control.queue.DataImportStatus;
import com.x.cms.assemble.control.queue.QueueBatchOperation;
import com.x.cms.assemble.control.queue.QueueDataRowImport;
import com.x.cms.assemble.control.queue.QueueDocumentDelete;
import com.x.cms.assemble.control.queue.QueueDocumentUpdate;
import com.x.cms.assemble.control.queue.QueueDocumentViewCountUpdate;
import com.x.cms.assemble.control.queue.QueueSendDocumentNotify;
import com.x.cms.assemble.control.timertask.Timertask_BatchOperationTask;
import com.x.cms.assemble.control.timertask.Timertask_CheckDocumentReviewStatus;
import com.x.cms.assemble.control.timertask.Timertask_InitOperationRunning;
import com.x.cms.assemble.control.timertask.Timertask_LogRecordCheckTask;
import com.x.cms.assemble.control.timertask.Timertask_RefreshAllDocumentReviews;

public class ThisApplication {

	protected static Context context;

	public static final String ROLE_CMSManager = "CMSManager@CMSManagerSystemRole@R";
	public static final String ROLE_Manager = "Manager@ManagerSystemRole@R";
	public static QueueDataRowImport queueDataRowImport;
	public static QueueDocumentDelete queueDocumentDelete;
	public static QueueDocumentUpdate queueDocumentUpdate;
	public static QueueDocumentViewCountUpdate queueDocumentViewCountUpdate;
	public static QueueBatchOperation queueBatchOperation;
	public static QueueSendDocumentNotify queueSendDocumentNotify;
	private static ConcurrentHashMap<String, DataImportStatus> importStatus = new ConcurrentHashMap<>();

	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		CacheManager.init(context.clazz().getSimpleName());
		LoggerFactory.setLevel(Config.logLevel().x_cms_assemble_control());
		// 执行数据库中的批处理操作
		queueBatchOperation = new QueueBatchOperation();
		// Document删除时也需要检查一下热点图片里的数据是否已经删除掉了
		queueDocumentDelete = new QueueDocumentDelete();
		// 文档批量导入时数据存储过程
		queueDataRowImport = new QueueDataRowImport();
		// Document变更标题时也需要更新一下热点图片里的数据
		queueDocumentUpdate = new QueueDocumentUpdate();
		// Document被访问时，需要将总的访问量更新到item的document中，便于视图使用，在队列里异步修改
		queueDocumentViewCountUpdate = new QueueDocumentViewCountUpdate();
		// Document发布时，向所有阅读者推送通知
		queueSendDocumentNotify = new QueueSendDocumentNotify();

		MessageConnector.start(context());

		context().startQueue(queueBatchOperation);
		context().startQueue(queueDocumentDelete);
		context().startQueue(queueDataRowImport);
		context().startQueue(queueDocumentUpdate);
		context().startQueue(queueDocumentViewCountUpdate);
		context().startQueue(queueSendDocumentNotify);

		// 每天凌晨2点执行一次
		context.schedule(Timertask_LogRecordCheckTask.class, "0 0 2 * * ?");
		context.schedule(Timertask_BatchOperationTask.class, "0 */5 * * * ?");

		// 每天凌晨2点，计算所有的文档的权限信息
		context.schedule(Timertask_RefreshAllDocumentReviews.class, "0 0 2 * * ?");

		context.scheduleLocal(Timertask_CheckDocumentReviewStatus.class, 1200);
		context.scheduleLocal(Timertask_InitOperationRunning.class, 150);
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
			queueBatchOperation.stop();
			queueDocumentDelete.stop();
			queueDataRowImport.stop();
			queueDocumentUpdate.stop();
			queueDocumentViewCountUpdate.stop();
			queueSendDocumentNotify.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ConcurrentHashMap<String, DataImportStatus> listImportStatus() {
		return importStatus;
	}

	public static DataImportStatus getDataImportStatus(String batchName) {
		if (importStatus.get(batchName) == null) {
			DataImportStatus dataImportStatus = new DataImportStatus();
			dataImportStatus.setBatchName(batchName);
			importStatus.put(batchName, dataImportStatus);
		}
		return importStatus.get(batchName);
	}
}
