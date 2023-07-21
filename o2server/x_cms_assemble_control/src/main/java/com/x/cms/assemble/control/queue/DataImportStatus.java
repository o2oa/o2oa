package com.x.cms.assemble.control.queue;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;

public class DataImportStatus {
	
	@FieldDescribe( "导入批次号." )
	private String batchName = null;
	
	@FieldDescribe( "数据总数." )
	private Integer dataTotal = 0;
	
	@FieldDescribe( "已处理总数." )
	private Integer processTotal = 0;
	
	@FieldDescribe( "处理成功总数." )
	private Integer successTotal = 0;
	
	@FieldDescribe( "处理失败总数." )
	private Integer errorTotal = 0;
	
	@FieldDescribe( "错误的数据列表." )
	private List<List<String>> errors = null;
	
	@FieldDescribe( "导入正确的文档ID列表." )
	private List<String> documentIds = new ArrayList<>();
	
	public DataImportStatus() {
		super();
	}
	
	public DataImportStatus(String batchName, Integer dataTotal, Integer processTotal, Integer successTotal,
			Integer errorTotal, List<List<String>> errors, List<String> documentIds) {
		super();
		this.batchName = batchName;
		this.dataTotal = dataTotal;
		this.processTotal = processTotal;
		this.successTotal = successTotal;
		this.errorTotal = errorTotal;
		this.errors = errors;
		this.documentIds = documentIds;
	}
	
	public String getBatchName() {
		return batchName;
	}
	public Integer getDataTotal() {
		return dataTotal;
	}
	public Integer getProcessTotal() {
		return processTotal;
	}
	public Integer getSuccessTotal() {
		return successTotal;
	}
	public Integer getErrorTotal() {
		return errorTotal;
	}
	public List<List<String>> getErrors() {
		return errors;
	}
	public List<String> getDocumentIds() {
		return documentIds;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public void setDataTotal(Integer dataTotal) {
		this.dataTotal = dataTotal;
	}
	public void setProcessTotal(Integer processTotal) {
		this.processTotal = processTotal;
	}
	public void setSuccessTotal(Integer successTotal) {
		this.successTotal = successTotal;
	}
	public void setErrorTotal(Integer errorTotal) {
		this.errorTotal = errorTotal;
	}
	public void setErrors(List<List<String>> errors) {
		this.errors = errors;
	}
	public void setDocumentIds(List<String> documentIds) {
		this.documentIds = documentIds;
	}
	
	public synchronized  List<String> addDocumentId( String id ){
		if( this.documentIds == null ) {
			this.documentIds = new ArrayList<>();
		}
		this.documentIds.add( id );
		return this.documentIds;
	}
	
	public synchronized void increaseErrorTotal( Integer count ) {
		if( count == null ) {
			count = 1;
		}
		if( this.processTotal == null ) {
			this.processTotal = 0;
		}
		this.processTotal = processTotal + count;
		this.errorTotal = errorTotal + count;
	}
	
	public synchronized void increaseSuccessTotal( Integer count ) {
		if( count == null ) {
			count = 1;
		}
		if( this.processTotal == null ) {
			this.processTotal = 0;
		}
		if( this.successTotal == null ) {
			this.successTotal = 0;
		}
		this.processTotal = processTotal + count;
		this.successTotal = successTotal + count;
	}
	
	public synchronized void increaseDataTotal( Integer count ) {
		if( count == null ) {
			count = 1;
		}
		if( this.dataTotal == null ) {
			this.dataTotal = 0;
		}
		this.dataTotal = dataTotal + count;
	}
	
	public synchronized void appendErorrData( List<String> data ) {
		if( errors == null ) {
			errors = new ArrayList<>();
		}
		errors.add( data );
	}
}
