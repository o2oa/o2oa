package com.x.cms.assemble.control;

import java.util.concurrent.ConcurrentHashMap;

import com.x.base.core.project.Context;
import com.x.cms.assemble.control.queue.DataImportStatus;
import com.x.cms.assemble.control.queue.QueueDataRowImport;
import com.x.cms.assemble.control.queue.QueueDocumentDelete;
import com.x.cms.assemble.control.queue.QueueDocumentUpdate;
import com.x.cms.assemble.control.timertask.Timertask_LogRecordCheckTask;

public class ThisApplication {

	protected static Context context;
	
	public static final String ROLE_CMSManager = "CMSManager@CMSManagerSystemRole@R";
	public static QueueDataRowImport queueDataRowImport;
	public static QueueDocumentDelete queueDocumentDelete;
	public static QueueDocumentUpdate queueDocumentUpdate;
	private static ConcurrentHashMap<String, DataImportStatus> importStatus = new ConcurrentHashMap<>();
	
	public static Context context() {
		return context;
	}
	
	public static void init() throws Exception {
		queueDocumentDelete = new QueueDocumentDelete();
		queueDataRowImport = new QueueDataRowImport();
		queueDocumentUpdate = new QueueDocumentUpdate();
		context().startQueue( queueDocumentDelete );
		context().startQueue( queueDataRowImport );
		context().startQueue( queueDocumentUpdate );
		// 每天凌晨2点执行一次
		context.schedule( Timertask_LogRecordCheckTask.class, "0 0 2 * * ?" );
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
