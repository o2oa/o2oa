package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionUpdateContentWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4802496811328343606L;

	@FieldDescribe("文件名称,不带扩展名的文件名.")
	@Schema(description = "文件名称,不带扩展名的文件名.")
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
