package com.x.attendance.assemble.control.processor.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.base.core.project.gson.GsonPropertyObject;

public class StatusImportFileDetail extends GsonPropertyObject implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String fileId;
	private String filePath;
	private String checkStatus = "success";
	private long monitor_checkCount = 0;
	private String message = "";
	private Date startTime = null;
	private Date endTime = null;
	
	/**
	 * 该文件的导入数据是否正在处理
	 */
	private Boolean processing = false;
	private String currentProcessName = "NONE";
	
	private Boolean processing_validate = false;
	private long process_validate_count = 0;	
	private long process_validate_total = 0;
	
	/**
	 * 该文件的导入数据是否正在入库
	 */
	private Boolean processing_save = false;
	private long process_save_count = 0;	
	private long process_save_total = 0;
	
	private long rowCount = 0;	
	private long errorCount = 0;
	
	private List<String> personList;
	
	private List<EntityImportDataDetail> detailList;
	
	private List<EntityImportDataDetail> errorList;
	
	public synchronized List<String> addPersonList( String personName ){
		if( personList == null ){
			personList = new ArrayList<String>();
		}
		if( !personList.contains( personName )) {
			personList.add( personName );
		}
		return personList;
	}
	
	public synchronized List<EntityImportDataDetail> addDetailList( EntityImportDataDetail cacheImportRowDetail ){
		if( detailList == null ){
			detailList = new ArrayList<EntityImportDataDetail>();
		}
		detailList.add( cacheImportRowDetail );
		return detailList;
	}
	
	public synchronized List<EntityImportDataDetail> addErrorList( EntityImportDataDetail cacheImportRowDetail ){
		if( errorList == null ){
			errorList = new ArrayList<EntityImportDataDetail>();
		}
		errorList.add( cacheImportRowDetail );
		return errorList;
	}

	public synchronized void increaseMonitor_checkCount( long increaseCount ) {
		this.monitor_checkCount = this.monitor_checkCount + increaseCount;
	}
	
	public synchronized void increaseRowCount( long increaseCount ) {
		this.rowCount = this.rowCount + increaseCount;
	}
	
	public synchronized void increaseErrorCount( long increaseCount ) {
		this.errorCount = this.errorCount + increaseCount;
	}

	public synchronized void increaseProcess_validate_count( long increaseCount ) {
		this.process_validate_count = this.process_validate_count + increaseCount;
	}

	public synchronized void increaseProcess_validate_total(long increaseCount) {
		this.process_validate_total = this.process_validate_total + increaseCount;
	}

	public synchronized void increaseProcess_save_count(long increaseCount) {
		this.process_save_count = this.process_save_count + increaseCount;
	}

	public synchronized void increaseProcess_save_total( long increaseCount ) {
		this.process_save_total = this.process_save_total + increaseCount;
	}	
	
	public synchronized void sendStartTime( Date startTime) {
		if( this.startTime == null ) {
			this.startTime = startTime;
		}else {
			if( startTime != null && this.startTime.after( startTime )) {
				this.startTime = startTime;
			}
		}
	}

	public synchronized void sendEndTime( Date endTime) {
		if( this.endTime == null ) {
			this.endTime = endTime;
		}else {
			if( endTime != null && this.endTime.before( endTime )) {
				this.endTime = endTime;
			}
		}
	}
	
	public synchronized void setProcessing_save(Boolean processing_save) {
		this.processing_save = processing_save;
	}
	
	public synchronized void setProcessing(Boolean processing) {
		this.processing = processing;
	}

	public synchronized void setProcessing_validate(Boolean processing_validate) {
		this.processing_validate = processing_validate;
	}
	
	public synchronized void setCurrentProcessName(String currentProcessName) {
		this.currentProcessName = currentProcessName;
	}
	public synchronized void setProcess_validate_count(long process_validate_count) {
		this.process_validate_count = process_validate_count;
	}

	public synchronized void setProcess_validate_total(long process_validate_total) {
		this.process_validate_total = process_validate_total;
	}

	public synchronized void setProcess_save_count(long process_save_count) {
		this.process_save_count = process_save_count;
	}

	public synchronized void setProcess_save_total(long process_save_total) {
		this.process_save_total = process_save_total;
	}

	public synchronized void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public synchronized void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}	
	
	public synchronized void setMessage( String message ) {
		this.message = message;
	}
	
	public synchronized void setDetailList( List<EntityImportDataDetail> detailList ) {
		this.detailList = detailList;
	}
	
	public synchronized void setCheckStatus( String checkStatus ) {
		this.checkStatus = checkStatus;
	}
	
	public synchronized void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public synchronized void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public synchronized void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public synchronized void setMonitor_checkCount(Long monitor_checkCount) {
		this.monitor_checkCount = monitor_checkCount;
	}
	
	public synchronized void setErrorList(List<EntityImportDataDetail> errorList) {
		this.errorList = errorList;
	}
	
	public synchronized void setPersonList(List<String> personList) {
		this.personList = personList;
	}
	
	public synchronized void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public String getCheckStatus() {
		return checkStatus;
	}

	public long getRowCount() {
		return rowCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public List<EntityImportDataDetail> getDetailList() {
		return detailList;
	}

	public String getMessage() {
		return message;
	}

	public Boolean getProcessing() {
		return processing;
	}

	public Boolean getProcessing_validate() {
		return processing_validate;
	}

	public long getProcessing_validate_count() {
		return process_validate_count;
	}

	public long getProcessing_validate_total() {
		return process_validate_total;
	}

	public Boolean getProcessing_save() {
		return processing_save;
	}

	public long getProcess_save_count() {
		return process_save_count;
	}

	public long getProcess_save_total() {
		return process_save_total;
	}

	public String getCurrentProcessName() {
		return currentProcessName;
	}

	public long getProcess_validate_count() {
		return process_validate_count;
	}

	public long getProcess_validate_total() {
		return process_validate_total;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public Long getMonitor_checkCount() {
		return monitor_checkCount;
	}

	public List<EntityImportDataDetail> getErrorList() {
		return errorList;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setMonitor_checkCount(long monitor_checkCount) {
		this.monitor_checkCount = monitor_checkCount;
	}

	public List<String> getPersonList() {
		if( personList == null ) {
			personList = new ArrayList<>();
		}
		return personList;
	}	
}
