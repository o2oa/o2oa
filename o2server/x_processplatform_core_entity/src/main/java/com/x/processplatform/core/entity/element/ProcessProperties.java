package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.List;

public class ProcessProperties extends JsonProperties {

	private static final long serialVersionUID = 1L;

	@FieldDescribe("待办执行前脚本.")
	private String manualBeforeTaskScript;

	@FieldDescribe("待办执行前脚本文本.")
	private String manualBeforeTaskScriptText;

	@FieldDescribe("待办执行后脚本.")
	private String manualAfterTaskScript;

	@FieldDescribe("待办执行后脚本文本.")
	private String manualAfterTaskScriptText;

	@FieldDescribe("工作流转后执行脚本.")
	private String manualAfterProcessingScript;

	@FieldDescribe("工作流转后执行脚本文本.")
	private String manualAfterProcessingScriptText;

	@FieldDescribe("人工活动有停留脚本.")
	private String manualStayScript;

	@FieldDescribe("人工活动有停留脚本文本.")
	private String manualStayScriptText;

	@FieldDescribe("启用同步到自建表.")
	private Boolean updateTableEnable;

	@FieldDescribe("同步到自建表.")
	private List<String> updateTableList;

	@FieldDescribe("流程维护身份,如果无法找到处理身份默认的流程处理身份.")
	private String maintenanceIdentity;

	@FieldDescribe("数据脚本.")
	private String targetAssignDataScript;

	@FieldDescribe("数据脚本文本.")
	private String targetAssignDataScriptText;

	@FieldDescribe("可编辑权限脚本,返回组织专用标识.")
	private String permissionWriteScript;

	@FieldDescribe("可编辑权限脚本文本,返回组织专用标识.")
	private String permissionWriteScriptText;

	@FieldDescribe("需要记录数据变化的字段.")
	private List<String> dataTraceFieldList;

	@FieldDescribe("需要记录数据变化的字段配置方式：all|所有、custom|依据dataTraceFieldList配置的字段.")
	private String dataTraceFieldType;

	public String getPermissionWriteScript() {
		return permissionWriteScript;
	}

	public void setPermissionWriteScript(String permissionWriteScript) {
		this.permissionWriteScript = permissionWriteScript;
	}

	public String getPermissionWriteScriptText() {
		return permissionWriteScriptText;
	}

	public void setPermissionWriteScriptText(String permissionWriteScriptText) {
		this.permissionWriteScriptText = permissionWriteScriptText;
	}

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
	}

	public Boolean getUpdateTableEnable() {
		return updateTableEnable;
	}

	public void setUpdateTableEnable(Boolean updateTableEnable) {
		this.updateTableEnable = updateTableEnable;
	}

	public List<String> getUpdateTableList() {
		return updateTableList;
	}

	public void setUpdateTableList(List<String> updateTableList) {
		this.updateTableList = updateTableList;
	}

	public String getManualBeforeTaskScript() {
		return manualBeforeTaskScript;
	}

	public void setManualBeforeTaskScript(String manualBeforeTaskScript) {
		this.manualBeforeTaskScript = manualBeforeTaskScript;
	}

	public String getManualBeforeTaskScriptText() {
		return manualBeforeTaskScriptText;
	}

	public void setManualBeforeTaskScriptText(String manualBeforeTaskScriptText) {
		this.manualBeforeTaskScriptText = manualBeforeTaskScriptText;
	}

	public String getManualAfterTaskScript() {
		return manualAfterTaskScript;
	}

	public void setManualAfterTaskScript(String manualAfterTaskScript) {
		this.manualAfterTaskScript = manualAfterTaskScript;
	}

	public String getManualAfterTaskScriptText() {
		return manualAfterTaskScriptText;
	}

	public void setManualAfterTaskScriptText(String manualAfterTaskScriptText) {
		this.manualAfterTaskScriptText = manualAfterTaskScriptText;
	}

	public String getManualStayScript() {
		return manualStayScript;
	}

	public void setManualStayScript(String manualStayScript) {
		this.manualStayScript = manualStayScript;
	}

	public String getManualStayScriptText() {
		return manualStayScriptText;
	}

	public void setManualStayScriptText(String manualStayScriptText) {
		this.manualStayScriptText = manualStayScriptText;
	}

	public String getTargetAssignDataScript() {
		return targetAssignDataScript;
	}

	public void setTargetAssignDataScript(String targetAssignDataScript) {
		this.targetAssignDataScript = targetAssignDataScript;
	}

	public String getTargetAssignDataScriptText() {
		return targetAssignDataScriptText;
	}

	public void setTargetAssignDataScriptText(String targetAssignDataScriptText) {
		this.targetAssignDataScriptText = targetAssignDataScriptText;
	}

	public List<String> getDataTraceFieldList() {
		return dataTraceFieldList;
	}

	public void setDataTraceFieldList(List<String> dataTraceFieldList) {
		this.dataTraceFieldList = dataTraceFieldList;
	}

	public String getManualAfterProcessingScript() {
		return manualAfterProcessingScript;
	}

	public void setManualAfterProcessingScript(String manualAfterProcessingScript) {
		this.manualAfterProcessingScript = manualAfterProcessingScript;
	}

	public String getManualAfterProcessingScriptText() {
		return manualAfterProcessingScriptText;
	}

	public void setManualAfterProcessingScriptText(String manualAfterProcessingScriptText) {
		this.manualAfterProcessingScriptText = manualAfterProcessingScriptText;
	}

	public String getDataTraceFieldType() {
		return dataTraceFieldType;
	}

	public void setDataTraceFieldType(String dataTraceFieldType) {
		this.dataTraceFieldType = dataTraceFieldType;
	}
}
