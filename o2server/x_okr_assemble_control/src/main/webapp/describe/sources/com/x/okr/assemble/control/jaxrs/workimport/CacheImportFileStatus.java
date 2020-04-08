package com.x.okr.assemble.control.jaxrs.workimport;

import java.io.Serializable;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class CacheImportFileStatus extends GsonPropertyObject implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String fileId;
	
	private String checkStatus = "success";
	
	private String message = "";
	
	private long rowCount;
	
	private long errorCount;
	
	private List<CacheImportRowDetail> detailList;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}

	public List<CacheImportRowDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<CacheImportRowDetail> detailList) {
		this.detailList = detailList;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
