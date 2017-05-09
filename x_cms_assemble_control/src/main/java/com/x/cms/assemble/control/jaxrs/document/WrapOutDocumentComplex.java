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
	
	@EntityFieldDescribe( "作为编辑的CMS文档表单." )
	private WrapOutForm form;
	
	@EntityFieldDescribe( "作为查看的CMS文档表单." )
	private WrapOutForm readForm;
	
	private Boolean isAppAdmin = false;
	private Boolean isCategoryAdmin = false;
	private Boolean isManager = false;

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

	public WrapOutForm getReadForm() {
		return readForm;
	}

	public void setReadForm(WrapOutForm readForm) {
		this.readForm = readForm;
	}

	public Boolean getIsAppAdmin() {
		return isAppAdmin;
	}

	public Boolean getIsCategoryAdmin() {
		return isCategoryAdmin;
	}

	public Boolean getIsManager() {
		return isManager;
	}

	public void setIsAppAdmin(Boolean isAppAdmin) {
		this.isAppAdmin = isAppAdmin;
	}

	public void setIsCategoryAdmin(Boolean isCategoryAdmin) {
		this.isCategoryAdmin = isCategoryAdmin;
	}

	public void setIsManager(Boolean isManager) {
		this.isManager = isManager;
	}

}
