package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.WiAttachment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

public class ActionCreateWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5156711320925350432L;

	@FieldDescribe("直接打开指定人员已经有的草稿,草稿判断:工作没有已办,只有一条此人的待办.")
	@Schema(description = "直接打开指定人员已经有的草稿,草稿判断:工作没有已办,只有一条此人的待办.")
	private Boolean latest;

	@FieldDescribe("标题.")
	@Schema(description = "标题.")
	private String title;

	@FieldDescribe("启动人员身份.")
	@Schema(description = "启动人员身份.")
	private String identity;

	@FieldDescribe("工作数据.")
	@Schema(description = "工作数据.")
	private JsonElement data;

	@FieldDescribe("父工作标识.")
	@Schema(description = "父工作标识.")
	private String parentWork;

	@FieldDescribe("允许启动非当但版本流程,默认否并自动升级到当前版本流程.")
	@Schema(description = "允许启动非当但版本流程,默认否并自动升级到当前版本流程.")
	private Boolean allowEdition;

	@FieldDescribe("是否跳过新建检查(默认根据流程的新建检查配置，设置true则不进行新建检查).")
	@Schema(description = "是否跳过新建检查(默认根据流程的新建检查配置，设置true则不进行新建检查).")
	private Boolean skipDraftCheck = false;

	@FieldDescribe("附件列表")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiAttachment",
			fieldValue = "{'id':'附件id','name':'附件名称','site':'附件框分类','isSoftCopy':'是否软拷贝，默认false，true表示不拷贝真实存储附件，共用附件，仅支持流程附件','copyFrom':'附件来源(cms或processPlatform，默认为processPlatform)'}")
	private List<WiAttachment> attachmentList = new ArrayList<>();

	@FieldDescribe("关联文档列表.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "TargetWi",
			fieldValue = "{'type':'关联目标类型(cms或processPlatform)','bundle':'关联目标标识','site':'关联内容框标识','view':'来源视图'}")
	private List<TargetWi> correlationTargetList;

	public List<WiAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(
			List<WiAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getParentWork() {
		return parentWork;
	}

	public void setParentWork(String parentWork) {
		this.parentWork = parentWork;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

	public Boolean getLatest() {
		return latest;
	}

	public void setLatest(Boolean latest) {
		this.latest = latest;
	}

	public Boolean getAllowEdition() {
		return allowEdition;
	}

	public void setAllowEdition(Boolean allowEdition) {
		this.allowEdition = allowEdition;
	}

	public Boolean getSkipDraftCheck() {
		return skipDraftCheck;
	}

	public void setSkipDraftCheck(Boolean skipDraftCheck) {
		this.skipDraftCheck = skipDraftCheck;
	}

	public List<TargetWi> getCorrelationTargetList() {
		return correlationTargetList;
	}

	public void setCorrelationTargetList(
			List<TargetWi> correlationTargetList) {
		this.correlationTargetList = correlationTargetList;
	}
}
