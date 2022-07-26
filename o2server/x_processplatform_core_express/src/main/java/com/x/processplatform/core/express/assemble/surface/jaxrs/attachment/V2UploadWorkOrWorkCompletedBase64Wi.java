package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2UploadWorkOrWorkCompletedBase64Wi extends GsonPropertyObject {

	private static final long serialVersionUID = 5544072979836413166L;

	@FieldDescribe("附件位置.")
	@Schema(description = "附件位置.")
	private String site;

	@FieldDescribe("附件内容,base64编码文本.")
	@Schema(description = "附件内容,base64编码文本.")
	private String content;

	@FieldDescribe("文件名.")
	@Schema(description = "文件名.")
	private String fileName;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}