package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionHtmlToImageWi extends GsonPropertyObject {

	private static final long serialVersionUID = 8371465627589051014L;

	@FieldDescribe("*待转换html.")
	@Schema(description = "*待转换html.")
	private String workHtml;
	@FieldDescribe("图片标题")
	@Schema(description = "图片标题")
	private String title;
	@FieldDescribe("html正文宽度，允许为空.")
	@Schema(description = "html正文宽度，允许为空.")
	private Double htmlWidth;
	@FieldDescribe("html正文高度，允许为空.")
	@Schema(description = "html正文高度，允许为空.")
	private Double htmlHeight;
	@FieldDescribe("html的X轴开始位置，允许为空.")
	@Schema(description = "html的X轴开始位置，允许为空.")
	private Double startX;
	@FieldDescribe("html的Y轴开始位置，允许为空.")
	@Schema(description = "html的Y轴开始位置，允许为空.")
	private Double startY;
	@FieldDescribe("背景是否透明，默认为false.")
	@Schema(description = "背景是否透明，默认为false.")
	private Boolean omitBackground;
	@FieldDescribe("工作标识，把图片保存到工单的附件中，非必填")
	@Schema(description = "工作标识，把图片保存到工单的附件中，非必填")
	private String workId;
	@FieldDescribe("位置，工作标识不为空的时候必填")
	@Schema(description = "位置，工作标识不为空的时候必填")
	private String site;

	public String getWorkHtml() {
		return workHtml;
	}

	public void setWorkHtml(String workHtml) {
		this.workHtml = workHtml;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Double getStartX() {
		return startX == null ? 0D : startX;
	}

	public void setStartX(Double startX) {
		this.startX = startX;
	}

	public Double getStartY() {
		return startY == null ? 0D : startX;
	}

	public void setStartY(Double startY) {
		this.startY = startY;
	}

	public Double getHtmlWidth() {
		return htmlWidth;
	}

	public void setHtmlWidth(Double htmlWidth) {
		this.htmlWidth = htmlWidth;
	}

	public Double getHtmlHeight() {
		return htmlHeight;
	}

	public void setHtmlHeight(Double htmlHeight) {
		this.htmlHeight = htmlHeight;
	}

	public Boolean getOmitBackground() {
		return omitBackground;
	}

	public void setOmitBackground(Boolean omitBackground) {
		this.omitBackground = omitBackground;
	}
}