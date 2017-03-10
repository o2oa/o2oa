package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;
import java.util.Map;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.form.WrapOutForm;
import com.x.cms.assemble.control.jaxrs.log.WrapOutLog;

@Wrap
public class WrapOutDocumentComplex extends GsonPropertyObject {

	@EntityFieldDescribe( "作为输出的CMS文档数据对象." )
	private WrapOutDocument document;

	@EntityFieldDescribe( "作为输出的CMS文档附件文件信息数据对象." )
	private List<WrapOutDocumentComplexFile> attachmentList;

	@EntityFieldDescribe( "作为输出的CMS文档操作日志." )
	private List<WrapOutLog> documentLogList;

	@EntityFieldDescribe( "文档所有数据信息." )
	private Map<?, ?> data;
	
	@EntityFieldDescribe( "作为输出的CMS文档表单." )
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
