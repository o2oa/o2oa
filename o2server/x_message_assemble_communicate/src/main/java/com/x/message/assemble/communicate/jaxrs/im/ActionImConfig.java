package com.x.message.assemble.communicate.jaxrs.im;

import java.util.Map;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 2022/2/18. Copyright © 2022 O2. All rights reserved.
 */
public class ActionImConfig extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionImConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setEnableClearMsg(false);
		wo.setEnableRevokeMsg(false);
		for (Map.Entry<String, JsonElement> en : Config.web().entrySet()) {
			if (en.getKey().equals(IM_CONFIG_KEY_NAME)) {
				JsonElement je = en.getValue();
				wo = this.convertToWrapIn(je, Wo.class);
			}
		}
		if (wo.getRevokeOutMinute() == null || wo.getRevokeOutMinute() <= 0) {
			wo.setRevokeOutMinute(2); // 默认2分钟
		}
		wo.setVersionNo(300);
		wo.setChangelog("新增转发、收藏等功能！");
		result.setData(wo);
		return result;
	}

	static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 6217331123000062134L;

		@FieldDescribe("是否开启清空聊天记录的功能.")
		private Boolean enableClearMsg;
		@FieldDescribe("是否开启撤回聊天消息的功能.")
		private Boolean enableRevokeMsg;
		@FieldDescribe("是否使用onlyOffice预览文件(需要先安装onlyOffice扩展应用).")
		private Boolean enableOnlyOfficePreview;
		@FieldDescribe("撤回时效（分钟数）")
		private Integer revokeOutMinute;
		@FieldDescribe("会话检查脚本.")
		private String conversationCheckInvoke;
		@FieldDescribe("版本号.")
		private int versionNo;
		@FieldDescribe("更新内容.")
		private String changelog;

		public Boolean getEnableOnlyOfficePreview() {
			return enableOnlyOfficePreview;
		}

		public void setEnableOnlyOfficePreview(Boolean enableOnlyOfficePreview) {
			this.enableOnlyOfficePreview = enableOnlyOfficePreview;
		}

		public String getConversationCheckInvoke() {
			return conversationCheckInvoke;
		}

		public void setConversationCheckInvoke(String conversationCheckInvoke) {
			this.conversationCheckInvoke = conversationCheckInvoke;
		}

		public Integer getRevokeOutMinute() {
			return revokeOutMinute;
		}

		public void setRevokeOutMinute(Integer revokeOutMinute) {
			this.revokeOutMinute = revokeOutMinute;
		}

		public int getVersionNo() {
			return versionNo;
		}

		public void setVersionNo(int versionNo) {
			this.versionNo = versionNo;
		}

		public String getChangelog() {
			return changelog;
		}

		public void setChangelog(String changelog) {
			this.changelog = changelog;
		}

		public Boolean getEnableClearMsg() {
			return enableClearMsg;
		}

		public void setEnableClearMsg(Boolean enableClearMsg) {
			this.enableClearMsg = enableClearMsg;
		}

		public Boolean getEnableRevokeMsg() {
			return enableRevokeMsg;
		}

		public void setEnableRevokeMsg(Boolean enableRevokeMsg) {
			this.enableRevokeMsg = enableRevokeMsg;
		}
	}
}
