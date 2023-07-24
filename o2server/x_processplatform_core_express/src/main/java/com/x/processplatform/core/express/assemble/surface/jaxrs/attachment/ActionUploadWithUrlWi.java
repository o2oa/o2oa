package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionUploadWithUrlWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6298368255152314235L;

	@FieldDescribe("*Work或WorkCompleted的id.")
	@Schema(description = "*Work或WorkCompleted的id.")
	private String workId;

	@FieldDescribe("*文件名称,带扩展名的文件名.")
	@Schema(description = "*文件名称,带扩展名的文件名.")
	private String fileName;

	@FieldDescribe("*附件来源url地址.")
	@Schema(description = "*附件来源url地址.")
	private String fileUrl;

	@FieldDescribe("*附件分类.")
	@Schema(description = "*附件分类.")
	private String site;

	@FieldDescribe("上传人员（仅对管理员生效）.")
	@Schema(description = "上传人员（仅对管理员生效）.")
	private String person;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}
}