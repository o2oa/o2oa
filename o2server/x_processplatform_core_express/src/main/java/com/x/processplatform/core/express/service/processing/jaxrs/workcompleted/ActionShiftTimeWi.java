package com.x.processplatform.core.express.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionShiftTimeWi extends GsonPropertyObject {

	private static final long serialVersionUID = 2040132891703254119L;

	@FieldDescribe("已完成工作标识")
	private String id;

	@FieldDescribe("调整分钟数,负数向前调整")
	private Integer adjustMinutes;

	@FieldDescribe("是否调整已完成工作")
	private Boolean workCompletedEnable = true;

	@FieldDescribe("是否调整Data")
	private Boolean dataEnable = true;

	@FieldDescribe("是否调整工作日志")
	private Boolean recordEnable = true;

	@FieldDescribe("是否调整已办")
	private Boolean taskCompletedEnable = true;

	@FieldDescribe("是否调整待阅")
	private Boolean readEnable = true;

	@FieldDescribe("是否调整已阅")
	private Boolean readCompletedEnable = true;

	@FieldDescribe("是否调整参阅")
	private Boolean reviewEnable = true;

	@FieldDescribe("是否调整附件")
	private Boolean attachmentEnable = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAdjustMinutes() {
		return adjustMinutes;
	}

	public void setAdjustMinutes(Integer adjustMinutes) {
		this.adjustMinutes = adjustMinutes;
	}

	public Boolean getWorkCompletedEnable() {
		return workCompletedEnable;
	}

	public void setWorkCompletedEnable(Boolean workCompletedEnable) {
		this.workCompletedEnable = workCompletedEnable;
	}

	public Boolean getDataEnable() {
		return dataEnable;
	}

	public void setDataEnable(Boolean dataEnable) {
		this.dataEnable = dataEnable;
	}

	public Boolean getRecordEnable() {
		return recordEnable;
	}

	public void setRecordEnable(Boolean recordEnable) {
		this.recordEnable = recordEnable;
	}

	public Boolean getTaskCompletedEnable() {
		return taskCompletedEnable;
	}

	public void setTaskCompletedEnable(Boolean taskCompletedEnable) {
		this.taskCompletedEnable = taskCompletedEnable;
	}

	public Boolean getReadEnable() {
		return readEnable;
	}

	public void setReadEnable(Boolean readEnable) {
		this.readEnable = readEnable;
	}

	public Boolean getReadCompletedEnable() {
		return readCompletedEnable;
	}

	public void setReadCompletedEnable(Boolean readCompletedEnable) {
		this.readCompletedEnable = readCompletedEnable;
	}

	public Boolean getReviewEnable() {
		return reviewEnable;
	}

	public void setReviewEnable(Boolean reviewEnable) {
		this.reviewEnable = reviewEnable;
	}

	public Boolean getAttachmentEnable() {
		return attachmentEnable;
	}

	public void setAttachmentEnable(Boolean attachmentEnable) {
		this.attachmentEnable = attachmentEnable;
	}

}
