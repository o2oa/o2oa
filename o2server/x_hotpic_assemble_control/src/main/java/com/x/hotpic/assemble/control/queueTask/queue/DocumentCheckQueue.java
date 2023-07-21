package com.x.hotpic.assemble.control.queueTask.queue;

public class DocumentCheckQueue {
	private String docId = null;
	private String docType = null;
	private String docTitle = null;
	private Integer idx = 0;
	public DocumentCheckQueue() {
	}
	public DocumentCheckQueue( Integer idx, String id, String application, String title) {
		this.idx = idx;
		this.docId = id;
		this.docType = application;
		this.docTitle = title;
	}
	public String getDocId() {
		return docId;
	}
	public String getDocType() {
		return docType;
	}
	public Integer getIdx() {
		return idx;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public void setIdx(Integer idx) {
		this.idx = idx;
	}
	public String getDocTitle() {
		return docTitle;
	}
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	
}
