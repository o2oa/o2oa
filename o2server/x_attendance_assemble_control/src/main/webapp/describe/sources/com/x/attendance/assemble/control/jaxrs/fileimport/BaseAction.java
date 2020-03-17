package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected StatusImportFileDetail copyCacheImportFileStatus( StatusImportFileDetail source ) {
		
		StatusImportFileDetail target = new StatusImportFileDetail();
		target.setCheckStatus( source.getCheckStatus() );
		target.setCurrentProcessName( source.getCurrentProcessName() );
		target.setErrorCount( source.getErrorCount() );
		target.setFileId( source.getFileId() );
		target.setMessage( source.getMessage() );
		target.setProcess_save_count( source.getProcess_save_count() );
		target.setProcess_save_total( source.getProcess_save_total() );
		target.setProcess_validate_count( source.getProcess_validate_count() );
		target.setProcess_validate_total( source.getProcess_validate_total() );
		target.setProcessing( source.getProcessing() );
		target.setProcessing_save( source.getProcessing_save() );
		target.setProcessing_validate( source.getProcessing_validate() );
		target.setRowCount( source.getRowCount() );
		target.setDetailList( source.getDetailList() );
		target.setMonitor_checkCount( source.getMonitor_checkCount());
		target.setErrorCount( source.getErrorCount() );
		target.setFilePath( source.getFilePath() );
		
		return target;
	}
	
}
