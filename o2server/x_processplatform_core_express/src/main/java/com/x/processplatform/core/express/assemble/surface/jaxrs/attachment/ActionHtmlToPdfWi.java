package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionHtmlToPdfWi extends GsonPropertyObject {

	private static final long serialVersionUID = 8513838302143557434L;

	@FieldDescribe("待转换html.")
	@Schema(description = "待转换html.")
	private String workHtml;

	@FieldDescribe("转pdf页面宽度，默认A4.")
	@Schema(description = "转pdf页面宽度，默认A4.")
	private Float pageWidth;

	@FieldDescribe("pdf标题.")
	@Schema(description = "pdf标题.")
	private String title;

	public String getWorkHtml() {
		return workHtml;
	}

	public void setWorkHtml(String workHtml) {
		this.workHtml = workHtml;
	}

	public Float getPageWidth() {
		return pageWidth;
	}

	public void setPageWidth(Float pageWidth) {
		this.pageWidth = pageWidth;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}