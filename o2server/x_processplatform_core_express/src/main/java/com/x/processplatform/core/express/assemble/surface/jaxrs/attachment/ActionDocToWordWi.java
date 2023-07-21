package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionDocToWordWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8435497625233483358L;

	@FieldDescribe("转换文件名.")
	@Schema(description = "转换文件名.")
	private String fileName;
	@FieldDescribe("附件site.")
	@Schema(description = "附件site.")
	private String site;
	@FieldDescribe("内容.")
	@Schema(description = "内容.")
	private String content;

	public String getFileName() throws Exception {
		return StringUtils.isEmpty(fileName) ? Config.processPlatform().getDocToWordDefaultFileName() : fileName;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSite() throws Exception {
		return StringUtils.isEmpty(site) ? Config.processPlatform().getDocToWordDefaultSite() : site;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}