package com.x.pan.assemble.control.jaxrs.attachment3;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.OriginFile;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.AttachmentVersion;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class ActionSaveByOnlyOffice extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSaveByOnlyOffice.class);

	String execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		logger.info("{}用户通过onlyOffice保存文档:{}", effectivePerson.getDistinguishedName(), wi.getFileId());
		Map<String, Object> hashMap = new HashMap<>(2);
		byte[] bytes = CipherConnectionAction.getBinary(false, wi.getDownLoadUrl());
		if(bytes == null) {
			hashMap.put("result", Business.ONLY_OFFICE_ERROR_CODE);
			hashMap.put("msg", "onlyOffice文件下载失败");
			return gson.toJson(hashMap);
		}
		String diff = "";
		if(StringUtils.isNotBlank(wi.getDiffUrl())) {
			byte[] diffBytes = CipherConnectionAction.getBinary(false, wi.getDiffUrl());
			if(diffBytes != null){
				diff = Base64.encodeBase64String(diffBytes);
			}
		}
		this.saveFile(id, bytes, wi.getChanges(), diff, effectivePerson, hashMap);
		return gson.toJson(hashMap);
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("接入应用Id.")
		private String appId;
		@FieldDescribe("文件Id.")
		private String fileId;
		@FieldDescribe("编辑后文件下载地址.")
		private String downLoadUrl;
		@FieldDescribe("onlyOffice文件修改记录.")
		private String changes;
		@FieldDescribe("onlyOffice文件修改详细信息下载地址(名称为diff.zip).")
		private String diffUrl;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getFileId() {
			return fileId;
		}

		public void setFileId(String fileId) {
			this.fileId = fileId;
		}

		public String getDownLoadUrl() {
			return downLoadUrl;
		}

		public void setDownLoadUrl(String downLoadUrl) {
			this.downLoadUrl = downLoadUrl;
		}

		public String getChanges() {
			return changes;
		}

		public void setChanges(String changes) {
			this.changes = changes;
		}

		public String getDiffUrl() {
			return diffUrl;
		}

		public void setDiffUrl(String diffUrl) {
			this.diffUrl = diffUrl;
		}

	}

	public static class Wo extends WoId {

	}
}
