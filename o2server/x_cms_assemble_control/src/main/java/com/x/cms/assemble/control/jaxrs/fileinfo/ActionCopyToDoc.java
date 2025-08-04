package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.wrapin.WiAttachment;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.processplatform.core.entity.content.Attachment;
import java.util.ArrayList;
import java.util.List;

class ActionCopyToDoc extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCopyToDoc.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, docId:{}.", effectivePerson::getDistinguishedName, () -> docId);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = documentQueryService.get(docId);
			if (null == document) {
				throw new ExceptionDocumentNotExists(docId);
			}
			if(!business.isDocumentEditor(effectivePerson, null, null, document)){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<Attachment> attachmentList = checkAttachment(wi.getAttachmentList(), effectivePerson.getDistinguishedName(), business);
			List<FileInfo> fileList = checkFileInfo(wi.getFileInfoList(), effectivePerson.getDistinguishedName(), business);
			StorageMapping mapping = ThisApplication.context().storageMappings().random(FileInfo.class);
			for (Attachment o : attachmentList) {
				StorageMapping attMapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
				byte[] bytes = o.readContent(attMapping);
				FileInfo fileInfo = creteFileInfo(effectivePerson.getDistinguishedName(), document, mapping, o.getName(), o.getSite());
				fileInfo.saveContent(mapping, bytes, o.getName(), Config.general().getStorageEncrypt());
				fileInfo.setSeqNumber(o.getOrderNumber());
				emc.beginTransaction(FileInfo.class);
				emc.persist(fileInfo, CheckPersistType.all);
				emc.commit();
			}
			for (FileInfo o : fileList) {
				StorageMapping attMapping = ThisApplication.context().storageMappings().get(FileInfo.class, o.getStorage());
				byte[] bytes = o.readContent(attMapping);
				FileInfo fileInfo = creteFileInfo(effectivePerson.getDistinguishedName(), document, mapping, o.getName(), o.getSite());
				fileInfo.saveContent(mapping, bytes, o.getName(), Config.general().getStorageEncrypt());
				fileInfo.setSeqNumber(o.getSeqNumber());
				emc.beginTransaction(FileInfo.class);
				emc.persist(fileInfo, CheckPersistType.all);
				emc.commit();
			}
		}
		CacheManager.notify(FileInfo.class);
		CacheManager.notify(Document.class);
		result.setData(new Wo());
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 4382689061793305054L;

		@FieldDescribe("流程附件对象.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiAttachment", fieldValue = "[{'id':'附件id','name':'附件名称','site':'附件位置'}]")
		private List<WiAttachment> attachmentList = new ArrayList<>();

		@FieldDescribe("内容管理附件对象.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiAttachment", fieldValue = "[{'id':'附件id','name':'附件名称','site':'附件位置'}]")
		private List<WiAttachment> fileInfoList = new ArrayList<>();

		public List<WiAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WiAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

		public List<WiAttachment> getFileInfoList() {
			return fileInfoList;
		}

		public void setFileInfoList(
				List<WiAttachment> fileInfoList) {
			this.fileInfoList = fileInfoList;
		}
	}

	public static class Wo extends WrapBoolean {
		private static final long serialVersionUID = -5986602289699981815L;
		public Wo() {
			this.setValue(true);
		}
	}

}
