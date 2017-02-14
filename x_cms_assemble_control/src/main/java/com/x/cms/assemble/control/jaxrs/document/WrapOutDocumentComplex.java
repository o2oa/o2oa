package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;
import java.util.Map;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.form.WrapOutForm;
import com.x.cms.assemble.control.jaxrs.log.WrapOutLog;

@Wrap
public class WrapOutDocumentComplex extends GsonPropertyObject {

	private WrapOutDocument document;

	private List<WrapOutDocumentComplexFile> attachmentList;

	private List<WrapOutLog> documentLogList;

	private Map<?, ?> data;
	
	private WrapOutForm form;

	public WrapOutDocument getDocument() {
		return document;
	}

	public void setDocument(WrapOutDocument document) {
		this.document = document;
	}

	public List<WrapOutDocumentComplexFile> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<WrapOutDocumentComplexFile> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<WrapOutLog> getDocumentLogList() {
		return documentLogList;
	}

	public void setDocumentLogList(List<WrapOutLog> documentLogList) {
		this.documentLogList = documentLogList;
	}

	public Map<?, ?> getData() {
		return data;
	}

	public void setData(Map<?, ?> data) {
		this.data = data;
	}

	public WrapOutForm getForm() {
		return form;
	}

	public void setForm(WrapOutForm form) {
		this.form = form;
	}

}
