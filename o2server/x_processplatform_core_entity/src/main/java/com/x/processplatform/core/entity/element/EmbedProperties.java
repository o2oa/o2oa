package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class EmbedProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("活动自定义数据")
	private JsonElement customData;

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

	@FieldDescribe("嵌入子流程在结束事件(包含cancel和end活动)脚本,.")
	private String completedScript;

	@FieldDescribe("嵌入子流程在结束事件包含cancel和end活动脚本文本.")
	private String completedScriptText;

	@FieldDescribe("嵌入子流程在End节点结束事件脚本.")
	private String completedEndScript;

	@FieldDescribe("嵌入子流程在End节点结束事件脚本文本.")
	private String completedEndScriptText;

	@FieldDescribe("嵌入子流程在Cancel节点结束事件脚本.")
	private String completedCancelScript;

	@FieldDescribe("嵌入子流程在Cancel节点结束事件脚本文本.")
	private String completedCancelScriptText;

	public String getCompletedEndScript() {
		return completedEndScript;
	}

	public void setCompletedEndScript(String completedEndScript) {
		this.completedEndScript = completedEndScript;
	}

	public String getCompletedEndScriptText() {
		return completedEndScriptText;
	}

	public void setCompletedEndScriptText(String completedEndScriptText) {
		this.completedEndScriptText = completedEndScriptText;
	}

	public String getCompletedCancelScript() {
		return completedCancelScript;
	}

	public void setCompletedCancelScript(String completedCancelScript) {
		this.completedCancelScript = completedCancelScript;
	}

	public String getCompletedCancelScriptText() {
		return completedCancelScriptText;
	}

	public void setCompletedCancelScriptText(String completedCancelScriptText) {
		this.completedCancelScriptText = completedCancelScriptText;
	}

	public String getCompletedScript() {
		return completedScript;
	}

	public void setCompletedScript(String completedScript) {
		this.completedScript = completedScript;
	}

	public String getCompletedScriptText() {
		return completedScriptText;
	}

	public void setCompletedScriptText(String completedScriptText) {
		this.completedScriptText = completedScriptText;
	}

}
