package com.x.cms.assemble.control;

import java.util.concurrent.ConcurrentHashMap;

import com.x.base.core.project.Context;
import com.x.cms.assemble.control.queue.DataImportStatus;
import com.x.cms.assemble.control.queue.QueueBatchOperation;
import com.x.cms.assemble.control.queue.QueueDataRowImport;
import com.x.cms.assemble.control.queue.QueueDocumentDelete;
import com.x.cms.assemble.control.queue.QueueDocumentUpdate;
import com.x.cms.assemble.control.queue.QueueDocumentViewCountUpdate;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.timertask.Timertask_BatchOperationTask;
import com.x.cms.assemble.control.timertask.Timertask_LogRecordCheckTask;

public class ThisApplication {

	protected static Context context;
	
	public static final String ROLE_CMSManager = "CMSManager@CMSManagerSystemRole@R";
	public static QueueDataRowImport queueDataRowImport;
	public static QueueDocumentDelete queueDocumentDelete;
	public static QueueDocumentUpdate queueDocumentUpdate;
	public static QueueDocumentViewCountUpdate queueDocumentViewCountUpdate;
	public static QueueBatchOperation queueBatchOperation;
	private static CmsBatchOperationPersistService cmsBatchOperationPersistService;
	private static ConcurrentHashMap<String, DataImportStatus> importStatus = new ConcurrentHashMap<>();
	
	public static Context context() {
		return context;
	}
	
	public static void init() throws Exception {
		cmsBatchOperationPersistService = new CmsBatchOperationPersistService();	
		//执行数据库中的批处理操作
		queueBatchOperation = new QueueBatchOperation();
		//Document删除时也需要检查一下热点图片里的数据是否已经删除掉了
		queueDocumentDelete = new QueueDocumentDelete();
		//文档批量导入时数据存储过程
		queueDataRowImport = new QueueDataRowImport();
		//Document变更标题时也需要更新一下热点图片里的数据
		queueDocumentUpdate = new QueueDocumentUpdate();
		//Document被访问时，需要将总的访问量更新到item的document中，便于视图使用，在队列里异步修改
		queueDocumentViewCountUpdate = new QueueDocumentViewCountUpdate();
		
		context().startQueue( queueBatchOperation );
		context().startQueue( queueDocumentDelete );
		context().startQueue( queueDataRowImport );
		context().startQueue( queueDocumentUpdate );
		context().startQueue( queueDocumentViewCountUpdate );

		// 每天凌晨2点执行一次
		context.schedule( Timertask_LogRecordCheckTask.class, "0 0 2 * * ?" );
		context.schedule( Timertask_BatchOperationTask.class, "0 */5 * * * ?" );
		cmsBatchOperationPersistService.initOperationRunning();
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ConcurrentHashMap<String, DataImportStatus> listImportStatus(){
		return importStatus;
	}
	
	public static DataImportStatus getDataImportStatus( String batchName ) {
		if( importStatus.get( batchName ) == null ) {
			DataImportStatus dataImportStatus = new DataImportStatus();
			dataImportStatus.setBatchName(batchName);
			importStatus.put( batchName,  dataImportStatus );
		}
		return importStatus.get( batchName );
	}
}
