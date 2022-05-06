package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;

public class ActionAssignCreateWi extends GsonPropertyObject {

	private static final long serialVersionUID = -797953382184336448L;

	@FieldDescribe("应用标识")
	private String application;
	@FieldDescribe("流程标识")
	private String process;
	@FieldDescribe("身份标识")
	private String identity;
	@FieldDescribe("标题")
	private String title;
	@FieldDescribe("业务数据")
	private Data data;
	@FieldDescribe("附件")
	private List<WiAttachment> attachmentList = new ArrayList<>();
	@FieldDescribe("是否软拷贝附件，true表示不拷贝真实存储附件，只拷贝附件记录，共用附件.")
	private Boolean attachmentSoftCopy;
	@FieldDescribe("自动流转")
	private Boolean processing;
	@FieldDescribe("在调用子流程的情况下记录父工作id")
	private String parentWork;
	@FieldDescribe("在调用子流程的情况下记录父工作job")
	private String parentJob;

	public String getParentWork() {
		return parentWork;
	}

	public void setParentWork(String parentWork) {
		this.parentWork = parentWork;
	}

	public String getParentJob() {
		return parentJob;
	}

	public void setParentJob(String parentJob) {
		this.parentJob = parentJob;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public List<WiAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<WiAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public Boolean getProcessing() {
		return processing;
	}

	public void setProcessing(Boolean processing) {
		this.processing = processing;
	}

	public Boolean getAttachmentSoftCopy() {
		return attachmentSoftCopy;
	}

	public void setAttachmentSoftCopy(Boolean attachmentSoftCopy) {
		this.attachmentSoftCopy = attachmentSoftCopy;
	}

	public static class WiAttachment extends Attachment {

		private static final long serialVersionUID = 1954637399762611493L;

		public static final WrapCopier<WiAttachment, Attachment> copier = WrapCopierFactory.wi(WiAttachment.class,
				Attachment.class, null, JpaObject.FieldsUnmodify);

	}
}